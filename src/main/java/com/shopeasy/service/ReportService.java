
package com.shopeasy.service;

import com.shopeasy.dao.ReportDAO;
import com.shopeasy.model.ReportSummary;
import com.shopeasy.model.SalesTrend;
import com.shopeasy.model.PaymentMethodSummary;
import com.shopeasy.model.BestSellingProduct;
import com.shopeasy.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;

public class ReportService {

    private final ReportDAO reportDAO = new ReportDAO();

    // 1. Overall Business Summary
    public ReportSummary getReportSummary(int shopId)
            throws SQLException {

        try (Connection con = DBConnection.getConnection()) {
            return reportDAO.getReportSummary(con, shopId);
        }
    }

    // 2. Date-filtered Business Summary
    public ReportSummary getReportSummary(
            int shopId, Date startDate, Date endDate)
            throws SQLException {

        validateFilter(shopId, startDate, endDate);

        try (Connection con = DBConnection.getConnection()) {
            return reportDAO.getReportSummary(
                    con, shopId, startDate, endDate);
        }
    }

    // 3. Daily Sales Trend
    public List<SalesTrend> getDailySalesTrend(int shopId)
            throws SQLException {

        try (Connection con = DBConnection.getConnection()) {
            return reportDAO.getDailySalesTrend(con, shopId);
        }
    }

    // 4. Date-filtered Daily Sales Trend
    public List<SalesTrend> getDailySalesTrend(
            int shopId, Date startDate, Date endDate)
            throws SQLException {

        validateFilter(shopId, startDate, endDate);

        try (Connection con = DBConnection.getConnection()) {
            return reportDAO.getDailySalesTrend(
                    con, shopId, startDate, endDate);
        }
    }

    // 5. Payment Method Summary
    public List<PaymentMethodSummary> getPaymentMethodSummary(
            int shopId) throws SQLException {

        try (Connection con = DBConnection.getConnection()) {
            return reportDAO.getPaymentMethodSummary(con, shopId);
        }
    }

    // 6. Date-filtered Payment Method Summary
    public List<PaymentMethodSummary> getPaymentMethodSummary(
            int shopId, Date startDate, Date endDate)
            throws SQLException {

        validateFilter(shopId, startDate, endDate);

        try (Connection con = DBConnection.getConnection()) {
            return reportDAO.getPaymentMethodSummary(
                    con, shopId, startDate, endDate);
        }
    }

    // 7. Best-Selling Products
    public List<BestSellingProduct> getBestSellingProducts(
            int shopId) throws SQLException {

        try (Connection con = DBConnection.getConnection()) {
            return reportDAO.getBestSellingProducts(con, shopId);
        }
    }

    // 8. Date-filtered Best-Selling Products
    public List<BestSellingProduct> getBestSellingProducts(
            int shopId, Date startDate, Date endDate)
            throws SQLException {

        validateFilter(shopId, startDate, endDate);

        try (Connection con = DBConnection.getConnection()) {
            return reportDAO.getBestSellingProducts(
                    con, shopId, startDate, endDate);
        }
    }

    // Common Date Validation
    private void validateFilter(
            int shopId, Date startDate, Date endDate) {

        if (shopId <= 0) {
            throw new IllegalArgumentException("Invalid shop ID.");
        }

        if (startDate == null || endDate == null ||
                endDate.before(startDate)) {

            throw new IllegalArgumentException(
                    "Invalid report date range.");
        }
    }
}