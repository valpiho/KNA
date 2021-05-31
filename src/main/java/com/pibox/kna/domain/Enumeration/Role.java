package com.pibox.kna.domain.Enumeration;

import static com.pibox.kna.constants.Authority.*;

public enum Role {
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_DRIVER(DRIVER_AUTHORITIES),
    ROLE_CLIENT(CLIENT_AUTHORITIES);

    private String[] authorities;

    Role(String... authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}
