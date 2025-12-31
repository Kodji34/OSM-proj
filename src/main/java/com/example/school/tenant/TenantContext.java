package com.example.school.tenant;

/**
 * Simple ThreadLocal holder for the current tenant (establishment) id.
 */
public class TenantContext {
    private static final ThreadLocal<Long> CURRENT = new ThreadLocal<>();

    public static void setCurrentTenantId(Long id) {
        CURRENT.set(id);
    }

    public static Long getCurrentTenantId() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
