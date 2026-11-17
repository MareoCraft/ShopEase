
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
import java.sql.Date;
import java.time.LocalDate;
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

            String period = request.getParameter("period");
            String fromDate = request.getParameter("fromDate");
            String toDate = request.getParameter("toDate");

            loadStockPage(
                    request,
                    shopId,
                    period,
                    fromDate,
                    toDate
            );

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

            loadStockPage(request, shopId, null, null, null);

            request.getRequestDispatcher("/stock.jsp")
                    .forward(request, response);

        } catch (Exception e) {

            throw new ServletException(
                    "Unable to reload stock page.", e);
        }
    }

    // LOAD PRODUCTS AND FILTERED STOCK HISTORY
    private void loadStockPage(HttpServletRequest request,
                               int shopId,
                               String selectedPeriod,
                               String customFromDate,
                               String customToDate)
            throws Exception {

        List<Product> products =
                productService.getProducts(shopId);

        String period = selectedPeriod;

        if (period == null || period.trim().isEmpty()) {
            period = "all";
        }

        period = period.trim().toLowerCase();

        LocalDate startDate = null;
        LocalDate endDate = null;

        LocalDate today = LocalDate.now();

        switch (period) {

            case "today":
                startDate = today;
                endDate = today;
                break;

            case "yesterday":
                startDate = today.minusDays(1);
                endDate = startDate;
                break;

            case "last7days":
                startDate = today.minusDays(6);
                endDate = today;
                break;

            case "thismonth":
                startDate = today.withDayOfMonth(1);
                endDate = today;
                break;

            case "lastmonth":
                LocalDate previousMonth = today.minusMonths(1);
                startDate = previousMonth.withDayOfMonth(1);
                endDate = previousMonth.withDayOfMonth(
                        previousMonth.lengthOfMonth());
                break;

            case "custom":

                if (customFromDate == null ||
                        customFromDate.trim().isEmpty() ||
                        customToDate == null ||
                        customToDate.trim().isEmpty()) {

                    throw new IllegalArgumentException(
                            "Please select both From Date and To Date.");
                }

                startDate = LocalDate.parse(customFromDate);
                endDate = LocalDate.parse(customToDate);

                if (startDate.isAfter(endDate)) {
                    throw new IllegalArgumentException(
                            "From Date cannot be after To Date.");
                }

                break;

            case "all":
                break;

            default:
                throw new IllegalArgumentException(
                        "Invalid stock history date filter.");
        }

        Date sqlFromDate = startDate == null
                ? null : Date.valueOf(startDate);

        Date sqlToDate = endDate == null
                ? null : Date.valueOf(endDate);

        List<StockHistory> historyList =
                stockService.getStockHistory(
                        shopId,
                        sqlFromDate,
                        sqlToDate
                );

        request.setAttribute("products", products);
        request.setAttribute("historyList", historyList);

        request.setAttribute("selectedPeriod", period);

        request.setAttribute(
                "fromDate",
                customFromDate == null ? "" : customFromDate
        );

        request.setAttribute(
                "toDate",
                customToDate == null ? "" : customToDate
        );

        request.setAttribute("historyCount", historyList.size());
    }
}