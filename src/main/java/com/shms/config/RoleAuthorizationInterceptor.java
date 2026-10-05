package com.shms.config;

import com.shms.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

public class RoleAuthorizationInterceptor implements HandlerInterceptor {

    private static final Set<String> PUBLIC_PREFIXES = Set.of(
            "/css/", "/js/", "/images/", "/uploads/", "/webjars/"
    );

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String path = request.getRequestURI();
        if (path.startsWith("/error") || path.equals("/") || path.equals("/home")
                || path.equals("/login") || path.equals("/register")
                || path.equals("/contact") || PUBLIC_PREFIXES.stream().anyMatch(path::startsWith)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("currentUser");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        String role = user.getRole();
        boolean allowed = switch (path) {
            case "/admin-dashboard" -> "ADMIN".equals(role);
            case "/reception-dashboard" -> "RECEPTIONIST".equals(role);
            case "/patient-dashboard" -> "PATIENT".equals(role);
            case "/staff-dashboard" -> Set.of("NURSE","PHARMACIST","LAB_TECHNICIAN").contains(role);
            default -> isAllowedPath(path, request.getMethod(), role);
        };

        if (!allowed) {
            response.sendRedirect(request.getContextPath() + dashboardFor(role));
            return false;
        }

        return true;
    }

    private boolean isAllowedPath(String path, String method, String role) {
        if (path.equals("/doctors") && "GET".equalsIgnoreCase(method)) {
            return true; // public doctor directory for authenticated users
        }

        if (path.startsWith("/doctors/dashboard")
                || path.startsWith("/doctors/mark-attended")
                || path.startsWith("/doctors/cancel-appointment")
                || path.startsWith("/doctors/toggle-slot")) {
            return "DOCTOR".equals(role);
        }

        if (path.startsWith("/doctors/addDoctor")
                || path.startsWith("/doctors/edit/")
                || path.startsWith("/doctors/updateDoctor/")
                || path.startsWith("/doctors/delete/")) {
            return "ADMIN".equals(role);
        }

        if (path.startsWith("/patients")) {
            return "ADMIN".equals(role) || "RECEPTIONIST".equals(role);
        }

        if (path.startsWith("/appointments/book") || path.startsWith("/appointments/my")
                || path.startsWith("/appointments/cancel")) {
            return "PATIENT".equals(role);
        }

        if (path.startsWith("/billing/my")) {
            return "PATIENT".equals(role);
        }

        if (path.startsWith("/billing/listBills")
                || path.startsWith("/billing/view/")
                || path.startsWith("/billing/updateStatus/")) {
            return "ADMIN".equals(role) || "RECEPTIONIST".equals(role);
        }

        return true;
    }

    private String dashboardFor(String role) {
        return switch (role) {
            case "ADMIN" -> "/admin-dashboard";
            case "DOCTOR" -> "/doctors/dashboard";
            case "RECEPTIONIST" -> "/reception-dashboard";
            case "NURSE", "PHARMACIST", "LAB_TECHNICIAN" -> "/staff-dashboard";
            default -> "/patient-dashboard";
        };
    }
}
