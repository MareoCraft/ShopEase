
package com.shopeasy.controller;

import com.shopeasy.model.ReportSummary;
import com.shopeasy.model.SalesTrend;
import com.shopeasy.model.PaymentMethodSummary;
import com.shopeasy.model.BestSellingProduct;
import com.shopeasy.model.ReportFilter;
import com.shopeasy.service.ReportService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet("/reports")
public class ReportsServlet extends HttpServlet {

    private final ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("shopId") == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        int shopId = (Integer) session.getAttribute("shopId");

        try {

            String period = request.getParameter("period");

            if (period == null || period.trim().isEmpty()) {
                period = "LAST_7_DAYS";
            }

            period = period.toUpperCase();

            LocalDate today = LocalDate.now();
            LocalDate startDate;
            LocalDate endDate;

            switch (period) {

                case "TODAY":
                    startDate = today;
                    endDate = today;
                    break;

                case "THIS_MONTH":
                    startDate = today.withDayOfMonth(1);
                    endDate = today;
                    break;

                case "LAST_MONTH":
                    LocalDate previousMonth = today.minusMonths(1);
                    startDate = previousMonth.withDayOfMonth(1);
                    endDate = previousMonth.withDayOfMonth(
                            previousMonth.lengthOfMonth());
                    break;

                case "CUSTOM":
                    try {
                        startDate = LocalDate.parse(
                                request.getParameter("startDate"));

                        endDate = LocalDate.parse(
                                request.getParameter("endDate"));

                        if (endDate.isBefore(startDate)) {
                            throw new IllegalArgumentException(
                                    "End date cannot be before start date.");
                        }

                        if (endDate.isAfter(today)) {
                            throw new IllegalArgumentException(
                                    "Future dates are not allowed.");
                        }

                    } catch (DateTimeParseException |
                             NullPointerException e) {

                        throw new IllegalArgumentException(
                                "Please select valid start and end dates.");
                    }
                    break;

                case "LAST_7_DAYS":
                    startDate = today.minusDays(6);
                    endDate = today;
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Invalid reporting period.");
            }

            // Convert LocalDate to SQL Date
            Date sqlStartDate = Date.valueOf(startDate);
            Date sqlEndDate = Date.valueOf(endDate);

            // Create filter model
            ReportFilter filter = new ReportFilter();
            filter.setPeriod(period);
            filter.setStartDate(sqlStartDate);
            filter.setEndDate(sqlEndDate);

            // 1. Date-filtered business summary
            ReportSummary summary =
                    reportService.getReportSummary(
                            shopId, sqlStartDate, sqlEndDate);

            // 2. Date-filtered daily sales trend
            List<SalesTrend> salesTrend =
                    reportService.getDailySalesTrend(
                            shopId, sqlStartDate, sqlEndDate);

            // 3. Date-filtered payment analytics
            List<PaymentMethodSummary> paymentSummary =
                    reportService.getPaymentMethodSummary(
                            shopId, sqlStartDate, sqlEndDate);

            // 4. Date-filtered best-selling products
            List<BestSellingProduct> bestSellingProducts =
                    reportService.getBestSellingProducts(
                            shopId, sqlStartDate, sqlEndDate);

            // Send report data to JSP
            request.setAttribute("reportSummary", summary);
            request.setAttribute("salesTrend", salesTrend);
            request.setAttribute("paymentSummary", paymentSummary);
            request.setAttribute("bestSellingProducts", bestSellingProducts);

            request.setAttribute("reportFilter", filter);
            request.setAttribute("startDate", startDate.toString());
            request.setAttribute("endDate", endDate.toString());

            request.getRequestDispatcher("/reports.jsp")
                    .forward(request, response);

        } catch (IllegalArgumentException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage());

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to load business reports.", e);
        }
    }
}