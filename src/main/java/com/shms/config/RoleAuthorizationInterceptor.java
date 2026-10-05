package com.shms.config;

import com.shms.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * Lightweight session-based authorization used by the server-rendered application.
 * Authentication remains email/password based; authorization is deny-by-default.
 */
public class RoleAuthorizationInterceptor implements HandlerInterceptor {
    private static final Set<String> PUBLIC_PREFIXES = Set.of(
            "/css/", "/js/", "/images/", "/uploads/", "/webjars/", "/favicon.ico"
    );
    private static final Set<String> STAFF = Set.of("ADMIN","DOCTOR","RECEPTIONIST","NURSE","PHARMACIST","LAB_TECHNICIAN");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        if (path.startsWith("/error") || path.equals("/actuator/health") || path.equals("/") || path.equals("/home") || path.equals("/login")
                || path.equals("/logout") || path.equals("/register") || path.equals("/contact")
                || (path.equals("/doctors") && "GET".equalsIgnoreCase(request.getMethod()))
                || PUBLIC_PREFIXES.stream().anyMatch(path::startsWith)) return true;

        HttpSession session=request.getSession(false);
        Object value=session==null?null:session.getAttribute("currentUser");
        if(!(value instanceof User user)){response.sendRedirect(request.getContextPath()+"/login");return false;}

        String role=user.getRole();
        boolean allowed=isAllowed(path,request.getMethod(),role);
        if(!allowed){response.sendRedirect(request.getContextPath()+dashboardFor(role));return false;}
        return true;
    }

    private boolean isAllowed(String path,String method,String role){
        if(path.equals("/profile")) return true;
        if(path.equals("/admin-dashboard")) return "ADMIN".equals(role);
        if(path.equals("/patient-dashboard") || path.startsWith("/appointments/book") || path.startsWith("/appointments/my") || path.startsWith("/appointments/cancel") || path.startsWith("/billing/my")) return "PATIENT".equals(role);
        if(path.equals("/reception-dashboard")) return "RECEPTIONIST".equals(role);
        if(path.equals("/staff-dashboard")) return Set.of("NURSE","PHARMACIST","LAB_TECHNICIAN").contains(role);

        if(path.equals("/doctors") && "GET".equalsIgnoreCase(method)) return true;
        if(path.startsWith("/doctors/dashboard") || path.startsWith("/doctors/start-appointment") || path.startsWith("/doctors/appointment/") || path.startsWith("/doctors/mark-attended") || path.startsWith("/doctors/cancel-appointment")) return "DOCTOR".equals(role);
        if(path.startsWith("/doctors/addDoctor") || path.startsWith("/doctors/edit/") || path.startsWith("/doctors/updateDoctor/") || path.startsWith("/doctors/delete/")) return "ADMIN".equals(role);

        if(path.startsWith("/patients")) return Set.of("ADMIN","RECEPTIONIST","DOCTOR","NURSE").contains(role);
        if(path.startsWith("/billing/listBills") || path.startsWith("/billing/updateStatus/")) return Set.of("ADMIN","RECEPTIONIST").contains(role);
        if(path.startsWith("/billing/view/") || path.startsWith("/billing/pay/")) return Set.of("ADMIN","RECEPTIONIST","PATIENT").contains(role);

        if(path.startsWith("/admin/api/") || path.startsWith("/admin/staff")) return "ADMIN".equals(role);
        if(path.startsWith("/medical-records")) return Set.of("ADMIN","DOCTOR","NURSE","PATIENT").contains(role);
        if(path.startsWith("/prescriptions")) return Set.of("ADMIN","DOCTOR","PHARMACIST","PATIENT").contains(role);
        if(path.startsWith("/lab")) return Set.of("ADMIN","DOCTOR","LAB_TECHNICIAN","PATIENT").contains(role);
        if(path.startsWith("/admissions")) return Set.of("ADMIN","DOCTOR","NURSE","RECEPTIONIST","PATIENT").contains(role);
        if(path.startsWith("/notifications")) return STAFF.contains(role) || "PATIENT".equals(role);

        // Unknown application routes are denied instead of being implicitly public to authenticated users.
        return false;
    }

    private String dashboardFor(String role){return switch(role){case "ADMIN"->"/admin-dashboard";case "DOCTOR"->"/doctors/dashboard";case "RECEPTIONIST"->"/reception-dashboard";case "NURSE","PHARMACIST","LAB_TECHNICIAN"->"/staff-dashboard";default->"/patient-dashboard";};}
}
