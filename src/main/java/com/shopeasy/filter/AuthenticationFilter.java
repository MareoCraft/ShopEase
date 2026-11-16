
package com.shopeasy.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {
        "/dashboard",
        "/dashboard.jsp",
        "/profile",
        "/profile/*",
        "/categories",
        "/products",
        "/stock",
        "/customers",
        "/sales",
        "/sale-details",
        "/sales-history",
        "/udhar",
        "/reports"
})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig)
            throws ServletException {
        // Filter initialization
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Prevent browser caching of protected pages
        res.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        // Get existing session without creating a new one
        HttpSession session = req.getSession(false);

        // Check authentication
        boolean loggedIn = session != null
                && session.getAttribute("userId") != null
                && session.getAttribute("shopId") != null;

        if (loggedIn) {

            // Allow request
            chain.doFilter(request, response);

        } else {

            // Redirect unauthenticated visitor
            res.sendRedirect(
                    req.getContextPath() + "/login.jsp"
            );
        }
    }

    @Override
    public void destroy() {
        // Filter cleanup
    }
}
