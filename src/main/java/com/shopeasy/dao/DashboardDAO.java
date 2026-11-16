
package com.shopeasy.dao;

import com.shopeasy.model.Dashboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

                    // SHOP INFORMATION
                    dashboard.setShopId(
                            rs.getInt("SHOP_ID"));

                    dashboard.setShopName(
                            rs.getString("SHOP_NAME"));

                    dashboard.setOwnerName(
                            rs.getString("OWNER_NAME"));

                    dashboard.setTotalUsers(
                            rs.getInt("TOTAL_USERS"));


                    // BUSINESS OVERVIEW
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

        return dashboard;
    }
}