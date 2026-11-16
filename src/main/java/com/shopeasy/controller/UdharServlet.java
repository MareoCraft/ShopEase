
package com.shopeasy.controller;

import com.shopeasy.model.Sale;
import com.shopeasy.service.UdharService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/udhar")
public class UdharServlet extends HttpServlet {

    private final UdharService udharService = new UdharService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("shopId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int shopId = (Integer) session.getAttribute("shopId");

        loadUdharData(request, shopId);

        request.getRequestDispatcher("/udhar.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("shopId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int shopId = (Integer) session.getAttribute("shopId");

        try {
            int saleId = Integer.parseInt(request.getParameter("saleId"));

            double amount = Double.parseDouble(
                    request.getParameter("collectionAmount"));

            String paymentMethod = request.getParameter("paymentMethod");

            String paymentReference =
                    request.getParameter("paymentReference");

            udharService.collectPayment(
                    shopId,
                    saleId,
                    amount,
                    paymentMethod,
                    paymentReference
            );

            response.sendRedirect(
                    request.getContextPath() + "/udhar?success=collected"
            );

        } catch (IllegalArgumentException e) {

            request.setAttribute("error", e.getMessage());

            loadUdharData(request, shopId);

            request.getRequestDispatcher("/udhar.jsp")
                    .forward(request, response);

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to collect Udhar payment.", e);
        }
    }

    private void loadUdharData(HttpServletRequest request, int shopId)
            throws ServletException {

        try {
            List<Sale> outstandingSales =
                    udharService.getOutstandingSales(shopId);

            double totalOutstanding =
                    udharService.getTotalOutstanding(shopId);

            request.setAttribute("outstandingSales", outstandingSales);
            request.setAttribute("totalOutstanding", totalOutstanding);

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to load Udhar records.", e);
        }
    }
}