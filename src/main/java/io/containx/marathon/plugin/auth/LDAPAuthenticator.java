package io.containx.marathon.plugin.auth;

import akka.dispatch.ExecutionContexts;
import akka.dispatch.Futures;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.containx.marathon.plugin.auth.type.AuthKey;
import io.containx.marathon.plugin.auth.type.Configuration;
import io.containx.marathon.plugin.auth.type.UserIdentity;
import io.containx.marathon.plugin.auth.util.HTTPHelper;
import io.containx.marathon.plugin.auth.util.LDAPHelper;
import mesosphere.marathon.plugin.auth.Authenticator;
import mesosphere.marathon.plugin.auth.Identity;
import mesosphere.marathon.plugin.http.HttpRequest;
import mesosphere.marathon.plugin.http.HttpResponse;
import mesosphere.marathon.plugin.plugin.PluginConfiguration;
import play.api.libs.json.JsObject;
import play.api.libs.json.JsValue;
import scala.Option;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.naming.NamingException;

public class LDAPAuthenticator implements Authenticator, PluginConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(LDAPAuthenticator.class);
    private AccessRulesUpdaterTask accessRulesUpdaterTask;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final long DEFAULT_INTERVAL_IN_SECONDS = 60;
    private long refreshInterval = DEFAULT_INTERVAL_IN_SECONDS;

    private final ExecutionContext EC = ExecutionContexts
        .fromExecutorService(
            Executors.newSingleThreadExecutor(r -> new Thread(r, "Ldap-ExecutorThread"))
        );

    private LoadingCache<AuthKey, UserIdentity> USERS;

    private Configuration config;

    @Override
    public void initialize(scala.collection.immutable.Map<String, Object> map, JsObject jsObject) {
        Map<String, JsValue> conf = scala.collection.JavaConverters.mapAsJavaMap(jsObject.value());
        String intervalKey = "refresh-interval-seconds";
        if(conf.containsKey(intervalKey)){
            refreshInterval = Long.parseLong(conf.get(intervalKey).toString());
            if(refreshInterval <= 0) {
                refreshInterval = DEFAULT_INTERVAL_IN_SECONDS;
            }
        }
        USERS = CacheBuilder.newBuilder()
                .maximumSize(2000)
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .refreshAfterWrite(refreshInterval, TimeUnit.SECONDS)
                .build(
                        CacheLoader.asyncReloading(
                                new CacheLoader<AuthKey, UserIdentity>() {
                                    @Override
                                    public UserIdentity load(AuthKey key) throws Exception {
                                        return (UserIdentity) doAuth(key.getUsername(), key.getPassword());
                                    }
                                }
                                , Executors.newSingleThreadExecutor(
                                        r -> new Thread(r, "Ldap-CacheLoaderExecutorThread")
                                )
                        )
                );
        try {
            config = new ObjectMapper().readValue(jsObject.toString(), Configuration.class);
            if(config.getLdap().getRulesUpdaterBindUser() != null ) {
                accessRulesUpdaterTask = new AccessRulesUpdaterTask(config);
                scheduler.scheduleAtFixedRate(accessRulesUpdaterTask, 0, refreshInterval, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            LOGGER.error("Error reading configuration JSON: {}", e.getMessage(), e);
        }
    }

    @Override
    public Future<Option<Identity>> authenticate(HttpRequest request) {
        return Futures.future(() -> Option.apply(doAuth(request)), EC);
    }

    private Identity doAuth(HttpRequest request) {
        try {
            if(accessRulesUpdaterTask != null) {
                config.setAuthorization(accessRulesUpdaterTask.getAccessRules());
            }
            AuthKey ak = HTTPHelper.authKeyFromHeaders(request);
            if (ak != null) {

                // If a user is matched in the static list within the config - validate
                if (config.hasStaticUser(ak.getUsername())) {
                    UserIdentity su = config.getStaticUser(ak.getUsername());
                    if (ak.getPassword().equals(su.getPassword())) {
                        return su;
                    }
                    return null;
                }

                // Use LDAP for non-static users
                UserIdentity id = USERS.get(ak);

                if (id != null) {
                    return id.applyResolvePermissions(config);
                }
            }
        } catch (Exception ex) {
            LOGGER.error("LDAP error validating user: {}", ex);
        }
        return null;
    }

    /**
     * Authenticate, if the username matches the password.
     */
    private Identity doAuth(String username, String password) throws NamingException {
        int count = 0;
        int maxTries = 5;

        if(accessRulesUpdaterTask != null) {
            config.setAuthorization(accessRulesUpdaterTask.getAccessRules());
        }

        while(true) {
            try {
                Set<String> memberships = LDAPHelper.validate(username, password, config.getLdap());
                if (memberships != null) {
                    return new UserIdentity(username, memberships).applyResolvePermissions(config);
                } else {
                    return null;
                }
            } catch (Exception ex) {
                LOGGER.error("LDAP error Exception: {}", ex);
                if (++count == maxTries) throw ex;
            }
        }
    }


    @Override
    public void handleNotAuthenticated(HttpRequest request, HttpResponse response) {
        HTTPHelper.applyNotAuthenticatedToResponse(response);
    }
}
