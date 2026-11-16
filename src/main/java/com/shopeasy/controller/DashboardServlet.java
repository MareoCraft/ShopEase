
package com.shopeasy.controller;

import com.shopeasy.model.Dashboard;
import com.shopeasy.service.DashboardService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private DashboardService dashboardService;

    @Override
    public void init() throws ServletException {
        dashboardService = new DashboardService();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null ||
                session.getAttribute("shopId") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        int shopId = (Integer) session.getAttribute("shopId");

        try {

            Dashboard dashboard =
                    dashboardService.getDashboard(shopId);

            if (dashboard == null || dashboard.getShopId() == 0) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Shop not found");
                return;
            }

            request.setAttribute("dashboard", dashboard);

            request.getRequestDispatcher("/dashboard.jsp")
                    .forward(request, response);

        } catch (Exception e) {

            getServletContext().log(
                    "Dashboard loading error", e);

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load dashboard");
        }
    }
}
