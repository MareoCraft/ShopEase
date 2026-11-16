
package com.shopeasy.controller;

import com.shopeasy.model.User;
import com.shopeasy.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() {
        authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/login.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {

            User user = authService.authenticate(username, password);

            if (user == null) {

                request.setAttribute(
                        "errorMessage",
                        "Invalid username or password, or account inactive."
                );

                request.getRequestDispatcher("/login.jsp")
                        .forward(request, response);

                return;
            }

            // Create or renew session after successful login
            HttpSession session = request.getSession(true);
            request.changeSessionId();

            session.setAttribute("userId", user.getUserId());
            session.setAttribute("shopId", user.getShopId());
            session.setAttribute("fullName", user.getFullName());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());

            // Session timeout: 30 minutes
            session.setMaxInactiveInterval(30 * 60);

            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (Exception e) {

            getServletContext().log("Login failed", e);

            request.setAttribute(
                    "errorMessage",
                    "Unable to process login. Please try again."
            );

            request.getRequestDispatcher("/login.jsp")
                    .forward(request, response);
        }
    }
}
