package com.example.school.tenant;

import com.example.school.entity.Establishment;
import com.example.school.repository.EstablishmentRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

/**
 * Resolves the tenant (establishment) based on the Host header / accessUrl and stores it in TenantContext.
 */
@Component
@Order(1)
public class TenantResolverFilter extends OncePerRequestFilter {

    private final EstablishmentRepository establishmentRepository;
    private final Environment environment;

    public TenantResolverFilter(EstablishmentRepository establishmentRepository, Environment environment) {
        this.establishmentRepository = establishmentRepository;
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            boolean tenantScoped = isTenantRequest(request);
            if (tenantScoped) {
                boolean isSuperAdmin = isSuperAdmin();
                Object lockedAttr = request.getSession(false) != null
                        ? request.getSession(false).getAttribute("TENANT_LOCKED")
                        : null;
                boolean tenantLocked = !isSuperAdmin && lockedAttr instanceof Boolean && (Boolean) lockedAttr;
                if (tenantLocked) {
                    Object sessionTenant = request.getSession(false) != null
                            ? request.getSession(false).getAttribute("TENANT_ID")
                            : null;
                    Long lockedId = null;
                    if (sessionTenant instanceof Long id) {
                        lockedId = id;
                    } else if (sessionTenant instanceof String idText) {
                        try {
                            lockedId = Long.parseLong(idText);
                        } catch (NumberFormatException ignored) {}
                    }
                    if (lockedId != null) {
                        TenantContext.setCurrentTenantId(lockedId);
                        filterChain.doFilter(request, response);
                        return;
                    }
                }

                Long resolvedTenantId = null;
                String tenantParam = request.getParameter("tenantId");
                if (tenantParam != null && !tenantParam.isBlank()) {
                    try {
                        resolvedTenantId = Long.parseLong(tenantParam);
                    } catch (NumberFormatException ignored) {}
                }
                if (resolvedTenantId == null) {
                    String tenantSlug = request.getParameter("tenant");
                    if (tenantSlug != null && !tenantSlug.isBlank()) {
                        resolvedTenantId = resolveTenantBySlug(tenantSlug).orElse(null);
                    }
                }

                if (resolvedTenantId == null) {
                    String host = request.getServerName();
                    if (host != null && !host.isBlank()) {
                        resolvedTenantId = resolveTenantByHost(host).orElse(null);
                    }
                }

                if (resolvedTenantId == null) {
                    Object sessionTenant = request.getSession(false) != null
                            ? request.getSession(false).getAttribute("TENANT_ID")
                            : null;
                    if (sessionTenant instanceof Long id) {
                        resolvedTenantId = id;
                    } else if (sessionTenant instanceof String idText) {
                        try {
                            resolvedTenantId = Long.parseLong(idText);
                        } catch (NumberFormatException ignored) {}
                    }
                } else {
                    request.getSession(true).setAttribute("TENANT_ID", resolvedTenantId);
                    if (isSuperAdmin && request.getSession(false) != null) {
                        request.getSession(false).removeAttribute("TENANT_LOCKED");
                    }
                }

                if (resolvedTenantId != null) {
                    TenantContext.setCurrentTenantId(resolvedTenantId);
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private Optional<Long> resolveTenantByHost(String host) {
        // Try exact accessUrl, then by host contains
        Optional<Establishment> exact = establishmentRepository.findByAccessUrlIgnoreCase("http://" + host)
                .or(() -> establishmentRepository.findByAccessUrlIgnoreCase("https://" + host));
        if (exact.isPresent()) {
            return exact.map(Establishment::getId);
        }

        // Fallback: scan by host extracted from stored URL
        return establishmentRepository.findAll().stream()
                .filter(e -> {
                    if (e.getAccessUrl() == null || e.getAccessUrl().isBlank()) return false;
                    try {
                        URI uri = URI.create(e.getAccessUrl());
                        return host.equalsIgnoreCase(uri.getHost());
                    } catch (Exception ex) {
                        return false;
                    }
                })
                .findFirst()
                .map(Establishment::getId);
    }

    private Optional<Long> resolveTenantBySlug(String slug) {
        return establishmentRepository.findBySlugIgnoreCase(slug.trim())
                .map(Establishment::getId);
    }

    private boolean isTenantRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path == null) return false;
        if (path.startsWith("/tenant")) return true;
        if (path.startsWith("/api/tenant")) return true;
        String loginType = request.getParameter("loginType");
        return "/login".equals(path) && "tenant".equalsIgnoreCase(loginType);
    }

    private boolean isLocalhost(String host) {
        return host != null && (host.contains("localhost") || host.startsWith("127."));
    }

    private boolean isDevProfileActive() {
        return Arrays.asList(environment.getActiveProfiles()).contains("dev");
    }

    private boolean isSuperAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_SUPER_ADMIN".equals(a.getAuthority()));
    }
}
