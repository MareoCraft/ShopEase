
package com.shopeasy.controller;

import com.shopeasy.dao.CustomerDAO;
import com.shopeasy.dao.PaymentDAO;
import com.shopeasy.dao.SaleDAO;
import com.shopeasy.dao.SaleItemDAO;
import com.shopeasy.model.Customer;
import com.shopeasy.model.Payment;
import com.shopeasy.model.Sale;
import com.shopeasy.model.SaleItem;
import com.shopeasy.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/sale-details")
public class SaleDetailsServlet extends HttpServlet {

    private final SaleDAO saleDAO = new SaleDAO();
    private final SaleItemDAO saleItemDAO = new SaleItemDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

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

        try {
            int saleId = Integer.parseInt(request.getParameter("id"));

            if (saleId <= 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid bill ID.");
                return;
            }

            try (Connection con = DBConnection.getConnection()) {

                Sale sale = saleDAO.getSaleById(con, saleId, shopId);

                if (sale == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND,
                            "Bill not found.");
                    return;
                }

                Customer customer = customerDAO.getCustomerById(
                        con, sale.getCustomerId(), shopId);

                List<SaleItem> items = saleItemDAO.getItemsBySaleId(
                        con, saleId, shopId);

                Payment payment = paymentDAO.getPaymentBySaleId(
                        con, saleId, shopId);

                request.setAttribute("sale", sale);
                request.setAttribute("customer", customer);
                request.setAttribute("items", items);
                request.setAttribute("payment", payment);

                request.getRequestDispatcher("/sale-details.jsp")
                        .forward(request, response);
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid bill ID.");

        } catch (SQLException e) {
            throw new ServletException("Unable to load invoice details.", e);
        }
    }
}
