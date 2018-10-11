package io.containx.marathon.plugin.auth;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import io.containx.marathon.plugin.auth.type.*;
import io.containx.marathon.plugin.auth.util.LDAPHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConfigUpdaterTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUpdaterTask.class);
    private AtomicReference<Configuration> config = new AtomicReference<>();
    private Set<Access> staticAccessRules;

    ConfigUpdaterTask() {}

    public Configuration getConfig() {
        return config.get();
    }

    void initialize(Configuration config) {
        this.config.set(config);
        staticAccessRules = config.getAuthorization().getAccess();
    }

    private Authorization parse() throws Exception {
        Authorization auth = new Authorization();
        auth.setAccess(LDAPHelper.getAccessRules(config.get().getLdap(),staticAccessRules));
        LOGGER.debug(String.format("Authorization - %s ", auth.toString()));
        return auth;
    }

    @Override
    public void run() {
        try {
            config.set(config.get().setAuthorizationAndGet(parse()));
            LOGGER.debug(String.format("Updated Configuration - %s ", config.toString()));
        } catch (Exception e) {
            LOGGER.error(String.format("Cannot get configuration from ldap - %s ", e));
        }
    }
}
