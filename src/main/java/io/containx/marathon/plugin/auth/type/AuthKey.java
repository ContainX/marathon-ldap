package io.containx.marathon.plugin.auth.type;

import java.util.Objects;

public class AuthKey {

    private final String username;
    private final String password;

    private AuthKey(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static AuthKey with(String username, String password) {
        return new AuthKey(username, password);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthKey key = (AuthKey) o;
        return Objects.equals(username, key.username) &&
            Objects.equals(password, key.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

    @Override
    public String toString() {
        return com.google.common.base.MoreObjects.toStringHelper(this)
            .add("username", username)
            .toString();
    }
}
