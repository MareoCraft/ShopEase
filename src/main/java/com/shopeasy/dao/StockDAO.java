
package com.shopeasy.dao;

import com.shopeasy.model.Product;
import com.shopeasy.model.StockHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    // GET PRODUCT AND LOCK ROW FOR STOCK UPDATE
    public Product getProductForUpdate(Connection con,
                                       int productId,
                                       int shopId) throws Exception {

        String sql = "SELECT PRODUCT_ID, SHOP_ID, PRODUCT_NAME, SELLING_PRICE, STOCK_QUANTITY, MIN_STOCK_LEVEL, UNIT FROM PRODUCTS WHERE PRODUCT_ID = ? AND SHOP_ID = ? AND STATUS = 'ACTIVE' FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.setInt(2, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Product product = new Product();

                    product.setProductId(rs.getInt("PRODUCT_ID"));
                    product.setShopId(rs.getInt("SHOP_ID"));
                    product.setProductName(rs.getString("PRODUCT_NAME"));
                    product.setStockQuantity(
                            rs.getDouble("STOCK_QUANTITY"));
                    product.setMinStockLevel(
                            rs.getDouble("MIN_STOCK_LEVEL"));
                    product.setUnit(rs.getString("UNIT"));
                    product.setSellingPrice(rs.getDouble("SELLING_PRICE"));

                    return product;
                }
            }
        }

        return null;
    }

    // UPDATE PRODUCT STOCK QUANTITY
    public boolean updateStockQuantity(Connection con,
                                       int productId,
                                       int shopId,
                                       double newQuantity)
            throws Exception {

        String sql = "UPDATE PRODUCTS SET STOCK_QUANTITY = ?, UPDATED_DATE = SYSDATE WHERE PRODUCT_ID = ? AND SHOP_ID = ? AND STATUS = 'ACTIVE'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, newQuantity);
            ps.setInt(2, productId);
            ps.setInt(3, shopId);

            return ps.executeUpdate() > 0;
        }
    }

    // INSERT STOCK HISTORY
    public int addStockHistory(Connection con,
                               StockHistory history) throws Exception {

        int historyId = 0;

        String sequenceSql =
                "SELECT STOCK_HISTORY_SEQ.NEXTVAL FROM DUAL";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sequenceSql)) {

            if (rs.next()) {
                historyId = rs.getInt(1);
            }
        }

        String sql = "INSERT INTO STOCK_HISTORY (HISTORY_ID, SHOP_ID, PRODUCT_ID, MOVEMENT_TYPE, QUANTITY_CHANGE, PREVIOUS_QUANTITY, NEW_QUANTITY, REFERENCE_TYPE, REFERENCE_ID, NOTES, CREATED_DATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, historyId);
            ps.setInt(2, history.getShopId());
            ps.setInt(3, history.getProductId());
            ps.setString(4, history.getMovementType());
            ps.setDouble(5, history.getQuantityChange());
            ps.setDouble(6, history.getPreviousQuantity());
            ps.setDouble(7, history.getNewQuantity());
            ps.setString(8, history.getReferenceType());

            if (history.getReferenceId() > 0) {
                ps.setInt(9, history.getReferenceId());
            } else {
                ps.setNull(9, Types.NUMERIC);
            }

            ps.setString(10, history.getNotes());

            return ps.executeUpdate() > 0 ? historyId : 0;
        }
    }

    // GET STOCK HISTORY FOR A SHOP
    public List<StockHistory> getStockHistoryByShopId(
            Connection con, int shopId) throws Exception {

        List<StockHistory> historyList = new ArrayList<>();

        String sql = "SELECT H.HISTORY_ID, H.SHOP_ID, H.PRODUCT_ID, P.PRODUCT_NAME, H.MOVEMENT_TYPE, H.QUANTITY_CHANGE, H.PREVIOUS_QUANTITY, H.NEW_QUANTITY, H.REFERENCE_TYPE, H.REFERENCE_ID, H.NOTES, H.CREATED_DATE FROM STOCK_HISTORY H INNER JOIN PRODUCTS P ON H.PRODUCT_ID = P.PRODUCT_ID AND H.SHOP_ID = P.SHOP_ID WHERE H.SHOP_ID = ? ORDER BY H.HISTORY_ID DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    StockHistory history = new StockHistory();

                    history.setHistoryId(rs.getInt("HISTORY_ID"));
                    history.setShopId(rs.getInt("SHOP_ID"));
                    history.setProductId(rs.getInt("PRODUCT_ID"));
                    history.setProductName(
                            rs.getString("PRODUCT_NAME"));
                    history.setMovementType(
                            rs.getString("MOVEMENT_TYPE"));
                    history.setQuantityChange(
                            rs.getDouble("QUANTITY_CHANGE"));
                    history.setPreviousQuantity(
                            rs.getDouble("PREVIOUS_QUANTITY"));
                    history.setNewQuantity(
                            rs.getDouble("NEW_QUANTITY"));
                    history.setReferenceType(
                            rs.getString("REFERENCE_TYPE"));
                    history.setReferenceId(
                            rs.getInt("REFERENCE_ID"));
                    history.setNotes(rs.getString("NOTES"));
                    history.setCreatedDate(
                            rs.getDate("CREATED_DATE"));

                    historyList.add(history);
                }
            }
        }

        return historyList;
    }
}
