
package com.shopeasy.dao;

import com.shopeasy.model.BestSellingProduct;
import com.shopeasy.model.PaymentMethodSummary;
import com.shopeasy.model.ReportSummary;

import java.sql.*;

import com.shopeasy.model.SalesTrend;

import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public ReportSummary getReportSummary(Connection con, int shopId)
            throws SQLException {

        ReportSummary summary = new ReportSummary();

        String sql =
                "SELECT " +
                        "(SELECT COUNT(*) FROM SALES WHERE SHOP_ID = ? AND SALE_STATUS = 'COMPLETED') AS TOTAL_BILLS, " +
                        "(SELECT NVL(SUM(GRAND_TOTAL), 0) FROM SALES WHERE SHOP_ID = ? AND SALE_STATUS = 'COMPLETED') AS TOTAL_SALES, " +
                        "(SELECT NVL(SUM(DISCOUNT_AMOUNT), 0) FROM SALES WHERE SHOP_ID = ? AND SALE_STATUS = 'COMPLETED') AS TOTAL_DISCOUNT, " +
                        "(SELECT NVL(SUM(PAID_AMOUNT), 0) FROM SALES WHERE SHOP_ID = ? AND SALE_STATUS = 'COMPLETED') AS TOTAL_COLLECTED, " +
                        "(SELECT NVL(SUM(BALANCE_DUE), 0) FROM SALES WHERE SHOP_ID = ? AND SALE_STATUS = 'COMPLETED') AS TOTAL_OUTSTANDING, " +
                        "(SELECT COUNT(*) FROM PRODUCTS WHERE SHOP_ID = ? AND STATUS = 'ACTIVE') AS TOTAL_PRODUCTS, " +
                        "(SELECT COUNT(*) FROM PRODUCTS WHERE SHOP_ID = ? AND STATUS = 'ACTIVE' AND STOCK_QUANTITY <= MIN_STOCK_LEVEL) AS LOW_STOCK_PRODUCTS, " +
                        "(SELECT COUNT(*) FROM CUSTOMERS WHERE SHOP_ID = ? AND STATUS = 'ACTIVE') AS TOTAL_CUSTOMERS " +
                        "FROM DUAL";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);
            ps.setInt(2, shopId);
            ps.setInt(3, shopId);
            ps.setInt(4, shopId);
            ps.setInt(5, shopId);
            ps.setInt(6, shopId);
            ps.setInt(7, shopId);
            ps.setInt(8, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    summary.setTotalBills(
                            rs.getInt("TOTAL_BILLS"));

                    summary.setTotalSales(
                            rs.getDouble("TOTAL_SALES"));

                    summary.setTotalDiscount(
                            rs.getDouble("TOTAL_DISCOUNT"));

                    summary.setTotalCollected(
                            rs.getDouble("TOTAL_COLLECTED"));

                    summary.setTotalOutstanding(
                            rs.getDouble("TOTAL_OUTSTANDING"));

                    summary.setTotalProducts(
                            rs.getInt("TOTAL_PRODUCTS"));

                    summary.setLowStockProducts(
                            rs.getInt("LOW_STOCK_PRODUCTS"));

                    summary.setTotalCustomers(
                            rs.getInt("TOTAL_CUSTOMERS"));
                }
            }
        }

        return summary;
    }


    public List<SalesTrend> getDailySalesTrend(Connection con, int shopId)
            throws SQLException {

        List<SalesTrend> trends = new ArrayList<>();

        String sql = "SELECT TRUNC(CREATED_DATE) AS SALE_DATE, " +
                "NVL(SUM(GRAND_TOTAL), 0) AS TOTAL_SALES, " +
                "COUNT(*) AS TOTAL_BILLS " +
                "FROM SALES " +
                "WHERE SHOP_ID = ? " +
                "AND SALE_STATUS = 'COMPLETED' " +
                "AND CREATED_DATE >= TRUNC(SYSDATE) - 6 " +
                "AND CREATED_DATE < TRUNC(SYSDATE) + 1 " +
                "GROUP BY TRUNC(CREATED_DATE) " +
                "ORDER BY SALE_DATE";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    SalesTrend trend = new SalesTrend();

                    trend.setSaleDate(rs.getDate("SALE_DATE"));
                    trend.setTotalSales(rs.getDouble("TOTAL_SALES"));
                    trend.setTotalBills(rs.getInt("TOTAL_BILLS"));

                    trends.add(trend);
                }
            }
        }

        return trends;
    }


    public List<PaymentMethodSummary> getPaymentMethodSummary(
            Connection con, int shopId) throws SQLException {

        List<PaymentMethodSummary> summaries = new ArrayList<>();

        String sql =
                "SELECT PAYMENT_METHOD, SUM(TOTAL_AMOUNT) AS TOTAL_AMOUNT, " +
                        "SUM(TOTAL_TRANSACTIONS) AS TOTAL_TRANSACTIONS " +
                        "FROM ( " +
                        "SELECT PAYMENT_METHOD, PAYMENT_AMOUNT AS TOTAL_AMOUNT, " +
                        "1 AS TOTAL_TRANSACTIONS " +
                        "FROM PAYMENTS " +
                        "WHERE SHOP_ID = ? " +
                        "AND PAYMENT_STATUS = 'SUCCESS' " +
                        "AND PAYMENT_METHOD IN ('CASH', 'UPI', 'CARD') " +
                        "AND PAYMENT_AMOUNT > 0 " +

                        "UNION ALL " +

                        "SELECT PAYMENT_METHOD, COLLECTION_AMOUNT AS TOTAL_AMOUNT, " +
                        "1 AS TOTAL_TRANSACTIONS " +
                        "FROM UDHAR_COLLECTIONS " +
                        "WHERE SHOP_ID = ? " +
                        "AND STATUS = 'SUCCESS' " +
                        "AND PAYMENT_METHOD IN ('CASH', 'UPI', 'CARD') " +
                        ") " +
                        "GROUP BY PAYMENT_METHOD " +
                        "ORDER BY PAYMENT_METHOD";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);
            ps.setInt(2, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    PaymentMethodSummary summary =
                            new PaymentMethodSummary();

                    summary.setPaymentMethod(
                            rs.getString("PAYMENT_METHOD"));

                    summary.setTotalAmount(
                            rs.getDouble("TOTAL_AMOUNT"));

                    summary.setTotalTransactions(
                            rs.getInt("TOTAL_TRANSACTIONS"));

                    summaries.add(summary);
                }
            }
        }

        return summaries;
    }


    public List<BestSellingProduct> getBestSellingProducts(
            Connection con, int shopId) throws SQLException {

        List<BestSellingProduct> products = new ArrayList<>();

        String sql =
                "SELECT PRODUCT_ID, PRODUCT_NAME, SKU, " +
                        "SUM(QUANTITY) AS TOTAL_QUANTITY_SOLD, " +
                        "SUM(LINE_TOTAL) AS TOTAL_SALES_AMOUNT, " +
                        "COUNT(DISTINCT SALE_ID) AS TOTAL_BILLS " +
                        "FROM SALE_ITEMS " +
                        "WHERE SHOP_ID = ? " +
                        "AND SALE_ID IN ( " +
                        "SELECT SALE_ID FROM SALES " +
                        "WHERE SHOP_ID = ? AND SALE_STATUS = 'COMPLETED' " +
                        ") " +
                        "GROUP BY PRODUCT_ID, PRODUCT_NAME, SKU " +
                        "ORDER BY TOTAL_QUANTITY_SOLD DESC " +
                        "FETCH FIRST 10 ROWS ONLY";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);
            ps.setInt(2, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    BestSellingProduct product =
                            new BestSellingProduct();

                    product.setProductId(rs.getInt("PRODUCT_ID"));
                    product.setProductName(rs.getString("PRODUCT_NAME"));
                    product.setSku(rs.getString("SKU"));

                    product.setTotalQuantitySold(
                            rs.getDouble("TOTAL_QUANTITY_SOLD"));

                    product.setTotalSalesAmount(
                            rs.getDouble("TOTAL_SALES_AMOUNT"));

                    product.setTotalBills(
                            rs.getInt("TOTAL_BILLS"));

                    products.add(product);
                }
            }
        }

        return products;
    }


    public ReportSummary getReportSummary(
            Connection con,
            int shopId,
            Date startDate,
            Date endDate) throws SQLException {

        ReportSummary summary = new ReportSummary();

        String sql =
                "SELECT " +

                        "(SELECT COUNT(*) FROM SALES " +
                        "WHERE SHOP_ID = ? AND SALE_STATUS = 'COMPLETED' " +
                        "AND CREATED_DATE >= ? AND CREATED_DATE < ? + 1) AS TOTAL_BILLS, " +

                        "(SELECT NVL(SUM(GRAND_TOTAL), 0) FROM SALES " +
                        "WHERE SHOP_ID = ? AND SALE_STATUS = 'COMPLETED' " +
                        "AND CREATED_DATE >= ? AND CREATED_DATE < ? + 1) AS TOTAL_SALES, " +

                        "(SELECT NVL(SUM(DISCOUNT_AMOUNT), 0) FROM SALES " +
                        "WHERE SHOP_ID = ? AND SALE_STATUS = 'COMPLETED' " +
                        "AND CREATED_DATE >= ? AND CREATED_DATE < ? + 1) AS TOTAL_DISCOUNT, " +

                        "(SELECT NVL(SUM(PAID_AMOUNT), 0) FROM SALES " +
                        "WHERE SHOP_ID = ? AND SALE_STATUS = 'COMPLETED' " +
                        "AND CREATED_DATE >= ? AND CREATED_DATE < ? + 1) AS TOTAL_COLLECTED, " +

                        "(SELECT NVL(SUM(BALANCE_DUE), 0) FROM SALES " +
                        "WHERE SHOP_ID = ? AND SALE_STATUS = 'COMPLETED' " +
                        "AND CREATED_DATE >= ? AND CREATED_DATE < ? + 1) AS TOTAL_OUTSTANDING, " +

                        "(SELECT COUNT(*) FROM PRODUCTS " +
                        "WHERE SHOP_ID = ? AND STATUS = 'ACTIVE') AS TOTAL_PRODUCTS, " +

                        "(SELECT COUNT(*) FROM PRODUCTS " +
                        "WHERE SHOP_ID = ? AND STATUS = 'ACTIVE' " +
                        "AND STOCK_QUANTITY <= MIN_STOCK_LEVEL) AS LOW_STOCK_PRODUCTS, " +

                        "(SELECT COUNT(*) FROM CUSTOMERS " +
                        "WHERE SHOP_ID = ? AND STATUS = 'ACTIVE') AS TOTAL_CUSTOMERS " +

                        "FROM DUAL";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            int index = 1;

            // Total bills
            ps.setInt(index++, shopId);
            ps.setDate(index++, startDate);
            ps.setDate(index++, endDate);

            // Total sales
            ps.setInt(index++, shopId);
            ps.setDate(index++, startDate);
            ps.setDate(index++, endDate);

            // Total discounts
            ps.setInt(index++, shopId);
            ps.setDate(index++, startDate);
            ps.setDate(index++, endDate);

            // Total collected
            ps.setInt(index++, shopId);
            ps.setDate(index++, startDate);
            ps.setDate(index++, endDate);

            // Total outstanding
            ps.setInt(index++, shopId);
            ps.setDate(index++, startDate);
            ps.setDate(index++, endDate);

            // Current active products
            ps.setInt(index++, shopId);

            // Current low-stock products
            ps.setInt(index++, shopId);

            // Current active customers
            ps.setInt(index, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    summary.setTotalBills(
                            rs.getInt("TOTAL_BILLS"));

                    summary.setTotalSales(
                            rs.getDouble("TOTAL_SALES"));

                    summary.setTotalDiscount(
                            rs.getDouble("TOTAL_DISCOUNT"));

                    summary.setTotalCollected(
                            rs.getDouble("TOTAL_COLLECTED"));

                    summary.setTotalOutstanding(
                            rs.getDouble("TOTAL_OUTSTANDING"));

                    summary.setTotalProducts(
                            rs.getInt("TOTAL_PRODUCTS"));

                    summary.setLowStockProducts(
                            rs.getInt("LOW_STOCK_PRODUCTS"));

                    summary.setTotalCustomers(
                            rs.getInt("TOTAL_CUSTOMERS"));
                }
            }
        }

        return summary;
    }


    public List<SalesTrend> getDailySalesTrend(
            Connection con,
            int shopId,
            Date startDate,
            Date endDate) throws SQLException {

        List<SalesTrend> trends = new ArrayList<>();

        String sql =
                "SELECT TRUNC(CREATED_DATE) AS SALE_DATE, " +
                        "NVL(SUM(GRAND_TOTAL), 0) AS TOTAL_SALES, " +
                        "COUNT(*) AS TOTAL_BILLS " +
                        "FROM SALES " +
                        "WHERE SHOP_ID = ? " +
                        "AND SALE_STATUS = 'COMPLETED' " +
                        "AND CREATED_DATE >= ? " +
                        "AND CREATED_DATE < ? + 1 " +
                        "GROUP BY TRUNC(CREATED_DATE) " +
                        "ORDER BY SALE_DATE";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    SalesTrend trend = new SalesTrend();

                    trend.setSaleDate(rs.getDate("SALE_DATE"));
                    trend.setTotalSales(rs.getDouble("TOTAL_SALES"));
                    trend.setTotalBills(rs.getInt("TOTAL_BILLS"));

                    trends.add(trend);
                }
            }
        }

        return trends;
    }


    public List<PaymentMethodSummary> getPaymentMethodSummary(
            Connection con,
            int shopId,
            Date startDate,
            Date endDate) throws SQLException {

        List<PaymentMethodSummary> summaries = new ArrayList<>();

        String sql =
                "SELECT PAYMENT_METHOD, " +
                        "SUM(TOTAL_AMOUNT) AS TOTAL_AMOUNT, " +
                        "SUM(TOTAL_TRANSACTIONS) AS TOTAL_TRANSACTIONS " +
                        "FROM ( " +

                        "SELECT PAYMENT_METHOD, " +
                        "PAYMENT_AMOUNT AS TOTAL_AMOUNT, " +
                        "1 AS TOTAL_TRANSACTIONS " +
                        "FROM PAYMENTS " +
                        "WHERE SHOP_ID = ? " +
                        "AND PAYMENT_STATUS = 'SUCCESS' " +
                        "AND PAYMENT_METHOD IN ('CASH', 'UPI', 'CARD') " +
                        "AND PAYMENT_AMOUNT > 0 " +
                        "AND PAYMENT_DATE >= ? " +
                        "AND PAYMENT_DATE < ? + 1 " +

                        "UNION ALL " +

                        "SELECT PAYMENT_METHOD, " +
                        "COLLECTION_AMOUNT AS TOTAL_AMOUNT, " +
                        "1 AS TOTAL_TRANSACTIONS " +
                        "FROM UDHAR_COLLECTIONS " +
                        "WHERE SHOP_ID = ? " +
                        "AND STATUS = 'SUCCESS' " +
                        "AND PAYMENT_METHOD IN ('CASH', 'UPI', 'CARD') " +
                        "AND COLLECTION_AMOUNT > 0 " +
                        "AND COLLECTION_DATE >= ? " +
                        "AND COLLECTION_DATE < ? + 1 " +

                        ") " +
                        "GROUP BY PAYMENT_METHOD " +
                        "ORDER BY PAYMENT_METHOD";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);

            ps.setInt(4, shopId);
            ps.setDate(5, startDate);
            ps.setDate(6, endDate);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    PaymentMethodSummary summary =
                            new PaymentMethodSummary();

                    summary.setPaymentMethod(
                            rs.getString("PAYMENT_METHOD"));

                    summary.setTotalAmount(
                            rs.getDouble("TOTAL_AMOUNT"));

                    summary.setTotalTransactions(
                            rs.getInt("TOTAL_TRANSACTIONS"));

                    summaries.add(summary);
                }
            }
        }

        return summaries;
    }

    public List<BestSellingProduct> getBestSellingProducts(
            Connection con,
            int shopId,
            Date startDate,
            Date endDate) throws SQLException {

        List<BestSellingProduct> products = new ArrayList<>();

        String sql =
                "SELECT SI.PRODUCT_ID, SI.PRODUCT_NAME, SI.SKU, " +
                        "SUM(SI.QUANTITY) AS TOTAL_QUANTITY_SOLD, " +
                        "SUM(SI.LINE_TOTAL) AS TOTAL_SALES_AMOUNT, " +
                        "COUNT(DISTINCT SI.SALE_ID) AS TOTAL_BILLS " +
                        "FROM SALE_ITEMS SI " +
                        "INNER JOIN SALES S " +
                        "ON SI.SALE_ID = S.SALE_ID " +
                        "AND SI.SHOP_ID = S.SHOP_ID " +
                        "WHERE SI.SHOP_ID = ? " +
                        "AND S.SHOP_ID = ? " +
                        "AND S.SALE_STATUS = 'COMPLETED' " +
                        "AND S.CREATED_DATE >= ? " +
                        "AND S.CREATED_DATE < ? + 1 " +
                        "GROUP BY SI.PRODUCT_ID, SI.PRODUCT_NAME, SI.SKU " +
                        "ORDER BY TOTAL_QUANTITY_SOLD DESC " +
                        "FETCH FIRST 10 ROWS ONLY";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);
            ps.setInt(2, shopId);
            ps.setDate(3, startDate);
            ps.setDate(4, endDate);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    BestSellingProduct product =
                            new BestSellingProduct();

                    product.setProductId(rs.getInt("PRODUCT_ID"));
                    product.setProductName(rs.getString("PRODUCT_NAME"));
                    product.setSku(rs.getString("SKU"));

                    product.setTotalQuantitySold(
                            rs.getDouble("TOTAL_QUANTITY_SOLD"));

                    product.setTotalSalesAmount(
                            rs.getDouble("TOTAL_SALES_AMOUNT"));

                    product.setTotalBills(
                            rs.getInt("TOTAL_BILLS"));

                    products.add(product);
                }
            }
        }

        return products;
    }

}