package io.containx.marathon.plugin.auth.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.containx.marathon.plugin.auth.predicates.IdentityPredicates;
import org.testng.annotations.Test;

import java.util.Optional;
import java.util.Set;

import static io.containx.marathon.plugin.auth.predicates.PermissionPredicates.matchesAction;
import static io.containx.marathon.plugin.auth.predicates.PermissionPredicates.pathContains;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ConfigurationTest {

    private static final String CONFIG_FILE = "/config.json";
    private static final String GROUP_ADMIN = "admin";
    private static final String GROUP_DEVELOPERS = "developers";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testThatLDAPIsCorrect() throws Exception {
        Configuration config = getConfig();
        assertEquals(config.getLdap().getUrl(), "ldap://localhost:389/");
        assertEquals(config.getLdap().getBase(), "dc=containx,dc=io");
        assertEquals(config.getLdap().getUserSearch(), "uid={username}");
        assertEquals(config.getLdap().getUserSubTree(), "ou=People");
    }

    @Test
    public void testStaticUsersDefined() throws Exception {
        Configuration config = getConfig();
        assertNotNull(config.getStaticUsers());
        assertEquals(config.getStaticUsers().size(), 3);
        assertTrue(config.hasStaticUser("super"));
        assertTrue(config.hasStaticUser("admin"));
        assertTrue(config.hasStaticUser("user"));
    }

    @Test
    public void testAccessCreation() throws Exception {
        Configuration config = getConfig();
        assertNotNull(config.getAuthorization());
        assertEquals(config.getAuthorization().getAccess().size(), 2);
    }

    @Test
    public void testActionIsDeniedOrAllowedOnDevGroup() throws Exception {
        Configuration config = getConfig();
        Optional<Access> access = config.getAuthorization().accessFor(GROUP_DEVELOPERS);
        assertTrue(access.isPresent());

        Set<Permission> perms = access.get().getPermissions();
        assertTrue(perms.stream().anyMatch(matchesAction(Action.CREATE_APP)));
        assertFalse(perms.stream().anyMatch(matchesAction(Action.DELETE_APP)));


        /* Test Denied Access : path = "/" or "/other/other" */
        assertFalse(perms.stream().filter(matchesAction(Action.CREATE_APP)).anyMatch(pathContains("/")));
        assertFalse(perms.stream().filter(matchesAction(Action.CREATE_APP)).anyMatch(pathContains("/other/other")));

        /* Test Granted Access : path = "/dev/some/other" */
        assertTrue(perms.stream().filter(matchesAction(Action.CREATE_APP)).anyMatch(pathContains("/dev/some/other")));
        assertTrue(perms.stream().filter(matchesAction(Action.CREATE_APP)).anyMatch(pathContains("/other/dev/something")));
    }

    @Test
      public void testUserIdentityAsDeveloper() throws Exception {
        Configuration config = getConfig();
        Optional<UserIdentity> matches = config.getStaticUsers().stream().filter(IdentityPredicates.matchByName("user")).findFirst();
        assertTrue(matches.isPresent());

        UserIdentity user = matches.get().applyResolvePermissions(config);

        assertFalse(user.isAuthorized(Action.CREATE_APP, "/"));
        assertFalse(user.isAuthorized(Action.CREATE_APP, "/other/other"));

        assertTrue(user.isAuthorized(Action.CREATE_APP, "/dev/some/other"));
        assertTrue(user.isAuthorized(Action.CREATE_APP, "/other/dev/something"));
    }

    @Test
    public void testUserIdentityAsSuper() throws Exception {
        Configuration config = getConfig();
        Optional<UserIdentity> matches = config.getStaticUsers().stream().filter(IdentityPredicates.matchByName("super")).findFirst();
        assertTrue(matches.isPresent());

        UserIdentity user = matches.get().applyResolvePermissions(config);

        assertTrue(user.isAuthorized(Action.CREATE_APP, "/"));
        assertTrue(user.isAuthorized(Action.CREATE_APP, "/other/other"));
        assertTrue(user.isAuthorized(Action.CREATE_APP, "/dev/some/other"));
        assertTrue(user.isAuthorized(Action.CREATE_APP, "/other/dev/something"));
        assertFalse(user.isAuthorized(Action.DELETE_GROUP, "/"));
    }


    @Test
    public void testActionIsDeniedOrAllowedOnAdminGroup() throws Exception {
        Configuration config = getConfig();
        Optional<Access> access = config.getAuthorization().accessFor(GROUP_ADMIN);
        assertTrue(access.isPresent());

        Set<Permission> perms = access.get().getPermissions();


        for (Action a : Action.values()) {
            assertTrue(perms.stream().anyMatch(matchesAction(a)), "Was expecting true for Action: " + a);
        }

        /* Admin on App should be able to access "anything" */
        assertTrue(perms.stream().filter(matchesAction(Action.CREATE_APP)).anyMatch(pathContains("/dev/some/other")));
        assertTrue(perms.stream().filter(matchesAction(Action.CREATE_APP)).anyMatch(pathContains("/")));
        assertTrue(perms.stream().filter(matchesAction(Action.DELETE_APP)).anyMatch(pathContains("/")));
        assertTrue(perms.stream().filter(matchesAction(Action.VIEW_APP)).anyMatch(pathContains("/")));
        assertTrue(perms.stream().filter(matchesAction(Action.UPDATE_APP)).anyMatch(pathContains("/")));

        /* Admin should be able to do anything EXCEPT for delete a outside of the /dev context in a Group */
        assertTrue(perms.stream().filter(matchesAction(Action.CREATE_GROUP)).anyMatch(pathContains("/dev/some/other")));
        assertTrue(perms.stream().filter(matchesAction(Action.CREATE_GROUP)).anyMatch(pathContains("/")));
        assertFalse(perms.stream().filter(matchesAction(Action.DELETE_GROUP)).anyMatch(pathContains("/")));
        assertTrue(perms.stream().filter(matchesAction(Action.DELETE_GROUP)).anyMatch(pathContains("/dev/something/else")));
        assertTrue(perms.stream().filter(matchesAction(Action.VIEW_GROUP)).anyMatch(pathContains("/")));
        assertTrue(perms.stream().filter(matchesAction(Action.UPDATE_GROUP)).anyMatch(pathContains("/")));
    }

    private Configuration getConfig() throws Exception {
        return MAPPER.readValue(getClass().getResourceAsStream(CONFIG_FILE), Configuration.class);
    }

}
