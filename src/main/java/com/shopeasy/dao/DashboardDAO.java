
package com.shopeasy.dao;

import com.shopeasy.model.Customer;
import com.shopeasy.model.Dashboard;
import com.shopeasy.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    public Dashboard getDashboardData(Connection con, int shopId)
            throws SQLException {

        Dashboard dashboard = new Dashboard();

        String sql =
                "SELECT " +
                        "S.SHOP_ID, " +
                        "S.SHOP_NAME, " +
                        "S.OWNER_NAME, " +
                        "COUNT(U.USER_ID) AS TOTAL_USERS, " +

                        "(SELECT NVL(SUM(GRAND_TOTAL), 0) " +
                        "FROM SALES " +
                        "WHERE SHOP_ID = S.SHOP_ID " +
                        "AND SALE_STATUS = 'COMPLETED' " +
                        "AND CREATED_DATE >= TRUNC(SYSDATE) " +
                        "AND CREATED_DATE < TRUNC(SYSDATE) + 1 " +
                        ") AS TODAY_SALES, " +

                        "(SELECT NVL(SUM(GRAND_TOTAL), 0) " +
                        "FROM SALES " +
                        "WHERE SHOP_ID = S.SHOP_ID " +
                        "AND SALE_STATUS = 'COMPLETED' " +
                        ") AS TOTAL_SALES, " +

                        "(SELECT COUNT(*) " +
                        "FROM PRODUCTS " +
                        "WHERE SHOP_ID = S.SHOP_ID " +
                        "AND STATUS = 'ACTIVE' " +
                        ") AS TOTAL_PRODUCTS, " +

                        "(SELECT COUNT(*) " +
                        "FROM PRODUCTS " +
                        "WHERE SHOP_ID = S.SHOP_ID " +
                        "AND STATUS = 'ACTIVE' " +
                        "AND STOCK_QUANTITY <= MIN_STOCK_LEVEL " +
                        ") AS LOW_STOCK_PRODUCTS, " +

                        "(SELECT COUNT(*) " +
                        "FROM CUSTOMERS " +
                        "WHERE SHOP_ID = S.SHOP_ID " +
                        "AND STATUS = 'ACTIVE' " +
                        ") AS TOTAL_CUSTOMERS, " +

                        "(SELECT NVL(SUM(BALANCE_DUE), 0) " +
                        "FROM SALES " +
                        "WHERE SHOP_ID = S.SHOP_ID " +
                        "AND SALE_STATUS = 'COMPLETED' " +
                        "AND BALANCE_DUE > 0 " +
                        ") AS PENDING_UDHAR " +

                        "FROM SHOPS S " +

                        "LEFT JOIN USERS U " +
                        "ON S.SHOP_ID = U.SHOP_ID " +
                        "AND U.STATUS = 'ACTIVE' " +

                        "WHERE S.SHOP_ID = ? " +
                        "AND S.STATUS = 'ACTIVE' " +

                        "GROUP BY S.SHOP_ID, S.SHOP_NAME, S.OWNER_NAME";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    dashboard.setShopId(rs.getInt("SHOP_ID"));
                    dashboard.setShopName(rs.getString("SHOP_NAME"));
                    dashboard.setOwnerName(rs.getString("OWNER_NAME"));
                    dashboard.setTotalUsers(rs.getInt("TOTAL_USERS"));

                    dashboard.setTodaySales(
                            rs.getDouble("TODAY_SALES"));

                    dashboard.setTotalSales(
                            rs.getDouble("TOTAL_SALES"));

                    dashboard.setTotalProducts(
                            rs.getInt("TOTAL_PRODUCTS"));

                    dashboard.setLowStockProducts(
                            rs.getInt("LOW_STOCK_PRODUCTS"));

                    dashboard.setTotalCustomers(
                            rs.getInt("TOTAL_CUSTOMERS"));

                    dashboard.setPendingUdhar(
                            rs.getDouble("PENDING_UDHAR"));
                }
            }
        }

        // Load dashboard detail lists using the same connection
        dashboard.setPendingUdharCustomers(
                getPendingUdharCustomers(con, shopId));

        dashboard.setLowStockProductList(
                getLowStockProducts(con, shopId));

        return dashboard;
    }


    // TOP 5 CUSTOMERS WITH HIGHEST PENDING UDHAR
    private List<Customer> getPendingUdharCustomers(
            Connection con, int shopId) throws SQLException {

        List<Customer> customers = new ArrayList<>();

        String sql =
                "SELECT " +
                        "C.CUSTOMER_ID, " +
                        "C.SHOP_ID, " +
                        "C.CUSTOMER_NAME, " +
                        "C.MOBILE, " +
                        "SUM(S.BALANCE_DUE) AS PENDING_UDHAR " +

                        "FROM CUSTOMERS C " +

                        "INNER JOIN SALES S " +
                        "ON C.CUSTOMER_ID = S.CUSTOMER_ID " +
                        "AND C.SHOP_ID = S.SHOP_ID " +

                        "WHERE C.SHOP_ID = ? " +
                        "AND C.STATUS = 'ACTIVE' " +
                        "AND S.SALE_STATUS = 'COMPLETED' " +
                        "AND S.BALANCE_DUE > 0 " +

                        "GROUP BY C.CUSTOMER_ID, C.SHOP_ID, " +
                        "C.CUSTOMER_NAME, C.MOBILE " +

                        "ORDER BY PENDING_UDHAR DESC " +
                        "FETCH FIRST 5 ROWS ONLY";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Customer customer = new Customer();

                    customer.setCustomerId(
                            rs.getInt("CUSTOMER_ID"));

                    customer.setShopId(
                            rs.getInt("SHOP_ID"));

                    customer.setCustomerName(
                            rs.getString("CUSTOMER_NAME"));

                    customer.setMobile(
                            rs.getString("MOBILE"));

                    customer.setPendingUdhar(
                            rs.getDouble("PENDING_UDHAR"));

                    customers.add(customer);
                }
            }
        }

        return customers;
    }


    // TOP 5 LOW STOCK PRODUCTS
    private List<Product> getLowStockProducts(
            Connection con, int shopId) throws SQLException {

        List<Product> products = new ArrayList<>();

        String sql =
                "SELECT " +
                        "PRODUCT_ID, " +
                        "SHOP_ID, " +
                        "PRODUCT_NAME, " +
                        "SKU, " +
                        "STOCK_QUANTITY, " +
                        "MIN_STOCK_LEVEL, " +
                        "UNIT " +

                        "FROM PRODUCTS " +

                        "WHERE SHOP_ID = ? " +
                        "AND STATUS = 'ACTIVE' " +
                        "AND STOCK_QUANTITY <= MIN_STOCK_LEVEL " +

                        "ORDER BY STOCK_QUANTITY ASC " +
                        "FETCH FIRST 5 ROWS ONLY";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Product product = new Product();

                    product.setProductId(
                            rs.getInt("PRODUCT_ID"));

                    product.setShopId(
                            rs.getInt("SHOP_ID"));

                    product.setProductName(
                            rs.getString("PRODUCT_NAME"));

                    product.setSku(
                            rs.getString("SKU"));

                    product.setStockQuantity(
                            rs.getDouble("STOCK_QUANTITY"));

                    product.setMinStockLevel(
                            rs.getDouble("MIN_STOCK_LEVEL"));

                    product.setUnit(
                            rs.getString("UNIT"));

                    products.add(product);
                }
            }
        }

        return products;
    }
}