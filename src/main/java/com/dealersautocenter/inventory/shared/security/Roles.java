package com.dealersautocenter.inventory.shared.security;

/**
 * Role constants — single source of truth to avoid magic strings.
 */
public final class Roles {
    private Roles() {}

    public static final String GLOBAL_ADMIN = "GLOBAL_ADMIN";
    public static final String DEALER_ADMIN  = "DEALER_ADMIN";
    public static final String VIEWER        = "VIEWER";

    public static final String ROLE_GLOBAL_ADMIN = "ROLE_" + GLOBAL_ADMIN;
    public static final String ROLE_DEALER_ADMIN  = "ROLE_" + DEALER_ADMIN;
    public static final String ROLE_VIEWER        = "ROLE_" + VIEWER;
}
