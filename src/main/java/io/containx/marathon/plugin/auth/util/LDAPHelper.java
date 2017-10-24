package io.containx.marathon.plugin.auth.util;

import io.containx.marathon.plugin.auth.type.LDAPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import static javax.naming.directory.SearchControls.SUBTREE_SCOPE;

public final class LDAPHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LDAPHelper.class);

    public static Set<String> validate(String username, String userPassword, LDAPConfig config) {

        if (config == null) {
            LOGGER.warn("LDAP Configuration not defined.  Skipping LDAP authentication");
            return null;
        }

        int count = 0;
        int maxTries = 5;

        do {
            DirContext context = null;
          
            try {
                String dn = "";
                String bindUser = config.getBindUser();
                String bindPassword = userPassword;

                if (bindUser != null) {
                    dn = bindUser.replace("{username}", username);
                    bindPassword = (config.getBindPassword() == null) ? userPassword : config.getBindPassword();
                } else {
                    if (config.useSimpleAuthentication()) {
                        dn = config.getDn().replace("{username}", username);
                    } else {
                        dn = new StringBuilder(config.getDn().replace("{username}", username))
                            .append(",")
                            .append(config.getUserSubTree() != null ? config.getUserSubTree() + "," : "")
                            .append(config.getBase())
                            .toString();
                    }
                }

                LOGGER.info("LDAP trying to connect as {} on {}", dn, config.getUrl());
                Hashtable<String, String> env = new Hashtable<>();
                env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                env.put(Context.PROVIDER_URL, config.getUrl());
                env.put(Context.SECURITY_PRINCIPAL, dn);
                env.put(Context.SECURITY_CREDENTIALS, bindPassword);
                env.put("com.sun.jndi.ldap.connect.timeout", config.getLdapConnectTimeout().toString());
                env.put("com.sun.jndi.ldap.read.timeout", config.getLdapReadTimeout().toString());

                context = new InitialDirContext(env);

                LOGGER.debug("getEnvironment: " + context.getEnvironment());

                // if an exception wasn't raised, then we managed to bind to the directory
                LOGGER.info("LDAP Bind succeeded for user {}", dn);

                SearchControls controls = new SearchControls();
                controls.setSearchScope(SUBTREE_SCOPE);
                // openLDAP needs the following (assumption).  Does anything else break if it's added?
                controls.setReturningAttributes(new String[]{"*", "+"});

                String searchString = config.getUserSearch().replace("{username}", username);
                String searchContext = config.getBase();
                if (config.getUserSubTree() != null) {
                    searchContext = new StringBuilder(config.getUserSubTree())
                        .append(",").append(searchContext)
                        .toString();
                }
                LOGGER.info("LDAP searching {} in {}", searchString, searchContext);
                NamingEnumeration<SearchResult> renum =
                    context.search(searchContext, searchString, controls);

                if (!renum.hasMore()) {
                    LOGGER.warn("LDAP cannot locate user information for {}", username);
                    return null;
                }

                SearchResult result = renum.next();
                LOGGER.info("LDAP user search found {}", result.toString());

                if (bindUser != null) {
                    dn = result.getNameInNamespace().toString();

                    if (userPassword == null || userPassword.isEmpty()) {
                        return null;
                    }

                    LOGGER.debug("Authenticate with DN {}", dn);
                    env.put(Context.SECURITY_PRINCIPAL, dn);
                    env.put(Context.SECURITY_CREDENTIALS, userPassword);

                    context = new InitialDirContext(env);

                    LOGGER.info("LDAP Auth succeeded for user {}", dn);
                }

                // search group memberships as user attributes
                Attribute memberOf = result.getAttributes().get("memberOf");
                Set<String> memberships = new HashSet<>();
                if (memberOf != null) {// null if this user belongs to no group at all
                    for (int i = 0; i < memberOf.size(); i++) {
                        try {
                            Attributes atts = context.getAttributes(memberOf.get(i).toString(), new String[]{"CN"});
                            Attribute att = atts.get("CN");
                            memberships.add(att.get().toString());
                        } catch (PartialResultException e) {
                            // ignore
                        }
                    }
                }

                // alternative LDAP group membership scheme involves a hierarchy of groups with zero to
                // many members, identified by an attribute name (typically 'memberUid' for posixGroup membership)
                if (config.getGroupSearch() != null) {
                    searchString = config.getGroupSearch().replace("{username}", username);
                    searchContext = config.getBase();
                    if (config.getUserSubTree() != null) {
                        searchContext = new StringBuilder(config.getGroupSubTree())
                            .append(",").append(searchContext)
                            .toString();
                    }
                    LOGGER.debug("LDAP searching for group membership {} in {}", searchString, searchContext);
                    renum = context.search(searchContext, searchString, controls);

                    while (renum.hasMore()) {
                        SearchResult group = renum.next();
                        String groupName = group.getAttributes().get("cn").get().toString();
                        memberships.add(groupName);
                        LOGGER.debug("LDAP found {} in group {}", username, groupName);
                    }

                }

                LOGGER.info("LDAP memberships for {} are {}", username, memberships);
                return memberships;

            } catch (NamingException e) {
                LOGGER.error("LDAP NamingException during authentication: {}", e.getMessage());
            } finally {
                try {
                    if (context != null) {
                        context.close();
                    }
                } catch (NamingException e) {
                    LOGGER.error("LDAP exception handling resource cleanup: {}", e.getMessage());
                }
            }
        }  while(++count < maxTries);

        return null;
    }
}
