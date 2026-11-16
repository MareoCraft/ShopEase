
package com.shopeasy.controller;

import com.shopeasy.model.Product;
import com.shopeasy.model.StockHistory;
import com.shopeasy.service.ProductService;
import com.shopeasy.service.StockService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/stock")
public class StockServlet extends HttpServlet {

    private final StockService stockService = new StockService();
    private final ProductService productService = new ProductService();

    // DISPLAY STOCK MANAGEMENT PAGE
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("shopId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int shopId = (Integer) session.getAttribute("shopId");

        try {
            loadStockPage(request, shopId);

            request.getRequestDispatcher("/stock.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException(
                    "Unable to load stock management page.", e);
        }
    }

    // HANDLE STOCK OPERATIONS
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("shopId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int shopId = (Integer) session.getAttribute("shopId");

        String action = request.getParameter("action");

        try {

            if ("adjust".equals(action)) {

                int productId = Integer.parseInt(
                        request.getParameter("productId"));

                String movementType =
                        request.getParameter("movementType");

                double quantity = Double.parseDouble(
                        request.getParameter("quantity"));

                String notes = request.getParameter("notes");

                stockService.adjustStock(
                        shopId,
                        productId,
                        movementType,
                        quantity,
                        notes
                );

                response.sendRedirect(
                        request.getContextPath() + "/stock?success=updated");

                return;

            } else {

                throw new IllegalArgumentException(
                        "Invalid stock operation.");
            }

        } catch (NumberFormatException e) {

            request.setAttribute(
                    "error",
                    "Please enter valid product and quantity values.");

        } catch (IllegalArgumentException e) {

            request.setAttribute("error", e.getMessage());

        } catch (Exception e) {

            throw new ServletException(
                    "Unable to process stock operation.", e);
        }

        // RELOAD PAGE AFTER VALIDATION ERROR
        try {

            loadStockPage(request, shopId);

            request.getRequestDispatcher("/stock.jsp")
                    .forward(request, response);

        } catch (Exception e) {

            throw new ServletException(
                    "Unable to reload stock page.", e);
        }
    }

    // LOAD PRODUCTS AND STOCK HISTORY
    private void loadStockPage(HttpServletRequest request, int shopId)
            throws Exception {

        List<Product> products =
                productService.getProducts(shopId);

        List<StockHistory> historyList =
                stockService.getStockHistory(shopId);

        request.setAttribute("products", products);
        request.setAttribute("historyList", historyList);
    }
}
