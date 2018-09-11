package io.containx.marathon.plugin.auth;

import java.util.concurrent.atomic.AtomicReference;

import io.containx.marathon.plugin.auth.type.Authorization;
import io.containx.marathon.plugin.auth.type.LDAPConfig;
import io.containx.marathon.plugin.auth.util.LDAPHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AccessRulesUpdaterTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessRulesUpdaterTask.class);
    private LDAPConfig ldapConfig;
    private AtomicReference<Authorization> accessRules = new AtomicReference<>();


    AccessRulesUpdaterTask(LDAPConfig ldapconfig) throws Exception {
        this.ldapConfig = ldapconfig;
        this.accessRules.set(parse());
    }

    Authorization getAccessRules() {
        return accessRules.get();
    }

    private Authorization parse() throws Exception {
        Authorization auth = new Authorization();
        auth.setAccess(LDAPHelper.getAccessRules(ldapConfig));
        LOGGER.debug(String.format("Authorization - %s ", auth.toString()));
        return auth;
    }

    @Override
    public void run() {
        try {
            this.accessRules.set(parse());
        } catch (Exception e) {
            LOGGER.error(String.format("Cannot get configuration from ldap - %s ", e));
        }
    }
}
