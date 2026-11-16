
package com.shopeasy.controller;

import com.shopeasy.dao.SaleDAO;
import com.shopeasy.model.Sale;
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

@WebServlet("/sales-history")
public class SalesHistoryServlet extends HttpServlet {

    private final SaleDAO saleDAO = new SaleDAO();

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

        try (Connection con = DBConnection.getConnection()) {

            List<Sale> sales = saleDAO.getSalesByShopId(con, shopId);

            request.setAttribute("sales", sales);

            request.getRequestDispatcher("/sales-history.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Unable to load sales history.", e);
        }
    }
}
