package com.example.school.config;

import com.example.school.entity.User;
import com.example.school.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                    .collect(Collectors.toList());
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.isEnabled(), true, true, true, authorities);
        };
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            AuthenticationSuccessHandler successHandler,
            AuthenticationFailureHandler failureHandler
    ) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(
                                "/favicon.ico",
                                "/theme.css",
                                "/data/**",
                                "/error",
                                "/error/**"
                        ).permitAll()
                        .requestMatchers("/login", "/login?error").permitAll()
                        .requestMatchers("/", "/index").permitAll()
                        .requestMatchers("/tenant/public", "/tenant/login", "/api/tenant").permitAll()
                        .requestMatchers("/tenant/app").hasAnyRole("USER","ADMIN","SUPER_ADMIN","DIRECTEUR","SECRETAIRE","COMPTABLE","DIRECTEUR_RH","ENSEIGNANT","APPRENANT")
                        .requestMatchers("/tenant/**").hasAnyRole("ADMIN","SUPER_ADMIN","DIRECTEUR","SECRETAIRE","COMPTABLE","DIRECTEUR_RH","ENSEIGNANT","APPRENANT","USER")
                        .requestMatchers("/api/admin/**").hasAnyRole("ADMIN","SUPER_ADMIN","DIRECTION","ADHESION")
                        .requestMatchers("/api/direction/**").hasRole("SUPER_ADMIN")
                        .requestMatchers("/dashboard", "/dashboard/**").hasAnyRole("SUPER_ADMIN","DIRECTION","ADHESION")
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/actuator/**").hasRole("SUPER_ADMIN")
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .failureHandler(failureHandler)
                        .successHandler(successHandler)
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            String loginType = request.getParameter("loginType");
            if ("tenant".equalsIgnoreCase(loginType)) {
                String tenantId = request.getParameter("tenantId");
                String tenantSlug = request.getParameter("tenant");
                String role = request.getParameter("role");
                StringBuilder redirect = new StringBuilder("/tenant/login?error");
                if (tenantSlug != null && !tenantSlug.isBlank()) {
                    redirect.append("&tenant=").append(URLEncoder.encode(tenantSlug, StandardCharsets.UTF_8));
                }
                if (tenantId != null && !tenantId.isBlank()) {
                    redirect.append("&tenantId=").append(URLEncoder.encode(tenantId, StandardCharsets.UTF_8));
                }
                if (role != null && !role.isBlank()) {
                    redirect.append("&role=").append(URLEncoder.encode(role, StandardCharsets.UTF_8));
                }
                response.sendRedirect(redirect.toString());
            } else {
                response.sendRedirect("/login?error");
            }
        };
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(UserRepository userRepository) {
        return (request, response, authentication) -> {
            boolean isSuperAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isDirection = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DIRECTION"));
            boolean isAdhesion = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADHESION"));
            boolean isUser = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
            boolean isDirecteur = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DIRECTEUR"));
            boolean isSecretaire = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_SECRETAIRE"));
            boolean isComptable = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_COMPTABLE"));
            boolean isRh = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DIRECTEUR_RH"));
            boolean isEnseignant = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ENSEIGNANT"));
            boolean isApprenant = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_APPRENANT"));
            boolean isTenantStaff = isUser || isDirecteur || isSecretaire || isComptable || isRh || isEnseignant || isApprenant;
            String username = authentication.getName();
            String tenantQuery = "";
            String tenantParam = request.getParameter("tenantId");
            String tenantSlugParam = request.getParameter("tenant");
            String loginType = request.getParameter("loginType");
            String roleParam = request.getParameter("role");
            Long tenantId = null;
            Long userTenantId = null;

            User user = null;
            if (username != null && !username.isBlank()) {
                user = userRepository.findByUsername(username).orElse(null);
            }
            if (user != null) {
                userTenantId = user.getEstablishmentId();
                user.setPreviousLoginAt(user.getLastLoginAt());
                user.setLastLoginAt(LocalDateTime.now());
                userRepository.save(user);
            }

            if (userTenantId != null && "tenant".equalsIgnoreCase(loginType)) {
                if (tenantParam != null && !tenantParam.isBlank()) {
                    if (!tenantParam.equals(userTenantId.toString())) {
                        if (request.getSession(false) != null) {
                            request.getSession(false).invalidate();
                        }
                        org.springframework.security.core.context.SecurityContextHolder.clearContext();
                        String slugPart = (tenantSlugParam != null && !tenantSlugParam.isBlank())
                                ? "tenant=" + URLEncoder.encode(tenantSlugParam, StandardCharsets.UTF_8)
                                : "tenantId=" + userTenantId;
                        response.sendRedirect("/tenant/login?error&" + slugPart);
                        return;
                    }
                }
                request.getSession(true).setAttribute("TENANT_ID", userTenantId);
                request.getSession(true).setAttribute("TENANT_LOCKED", Boolean.TRUE);
                tenantId = userTenantId;
            }

            if (tenantId == null && tenantParam != null && !tenantParam.isBlank()) {
                try {
                    tenantId = Long.parseLong(tenantParam);
                } catch (NumberFormatException ignored) {}
            }
            if (tenantId == null && request.getSession(false) != null) {
                Object sessionTenant = request.getSession(false).getAttribute("TENANT_ID");
                if (sessionTenant instanceof Long id) {
                    tenantId = id;
                } else if (sessionTenant instanceof String idText) {
                    try {
                        tenantId = Long.parseLong(idText);
                    } catch (NumberFormatException ignored) {}
                }
            }
            if (tenantSlugParam != null && !tenantSlugParam.isBlank()) {
                tenantQuery = "?tenant=" + URLEncoder.encode(tenantSlugParam, StandardCharsets.UTF_8);
            } else if (tenantId != null) {
                tenantQuery = "?tenantId=" + tenantId;
            }

            if (isSuperAdmin) {
                if (request.getSession(false) != null) {
                    request.getSession(false).removeAttribute("TENANT_LOCKED");
                    request.getSession(false).removeAttribute("TENANT_ID");
                }
            }

            if (isSuperAdmin || isAdmin || isDirection || isAdhesion) {
                if (isSuperAdmin && tenantId != null
                        && ("tenant".equalsIgnoreCase(loginType) || "SUPER_ADMIN".equalsIgnoreCase(roleParam))) {
                    response.sendRedirect("/tenant/dashboard" + tenantQuery);
                    return;
                }
                // If it's a tenant admin (pattern admin_est_<id>), route to tenant dashboard
                if (username != null && username.startsWith("admin_est_")) {
                    response.sendRedirect("/tenant/dashboard" + tenantQuery);
                } else {
                    response.sendRedirect("/dashboard");
                }
            } else if (isTenantStaff) {
                response.sendRedirect("/tenant/app" + tenantQuery);
            } else {
                response.sendRedirect("/");
            }
        };
    }
}
