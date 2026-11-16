
package com.shopeasy.dao;

import com.shopeasy.model.SaleItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleItemDAO {

    // ADD SALE ITEM
    public boolean addSaleItem(Connection con, SaleItem item)
            throws SQLException {

        String sql = "INSERT INTO SALE_ITEMS (SALE_ITEM_ID, SHOP_ID, SALE_ID, PRODUCT_ID, PRODUCT_NAME, SKU, QUANTITY, UNIT, UNIT_PRICE, LINE_TOTAL) VALUES (SALE_ITEM_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, item.getShopId());
            ps.setInt(2, item.getSaleId());
            ps.setInt(3, item.getProductId());
            ps.setString(4, item.getProductName());
            ps.setString(5, item.getSku());
            ps.setDouble(6, item.getQuantity());
            ps.setString(7, item.getUnit());
            ps.setDouble(8, item.getUnitPrice());
            ps.setDouble(9, item.getLineTotal());

            return ps.executeUpdate() > 0;
        }
    }

    // GET SALE ITEMS BY SALE ID
    public List<SaleItem> getItemsBySaleId(Connection con,
                                           int saleId,
                                           int shopId)
            throws SQLException {

        List<SaleItem> items = new ArrayList<>();

        String sql = "SELECT SALE_ITEM_ID, SHOP_ID, SALE_ID, PRODUCT_ID, PRODUCT_NAME, SKU, QUANTITY, UNIT, UNIT_PRICE, LINE_TOTAL FROM SALE_ITEMS WHERE SALE_ID = ? AND SHOP_ID = ? ORDER BY SALE_ITEM_ID";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);
            ps.setInt(2, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    SaleItem item = new SaleItem();

                    item.setSaleItemId(rs.getInt("SALE_ITEM_ID"));
                    item.setShopId(rs.getInt("SHOP_ID"));
                    item.setSaleId(rs.getInt("SALE_ID"));
                    item.setProductId(rs.getInt("PRODUCT_ID"));

                    item.setProductName(rs.getString("PRODUCT_NAME"));
                    item.setSku(rs.getString("SKU"));
                    item.setQuantity(rs.getDouble("QUANTITY"));
                    item.setUnit(rs.getString("UNIT"));
                    item.setUnitPrice(rs.getDouble("UNIT_PRICE"));
                    item.setLineTotal(rs.getDouble("LINE_TOTAL"));

                    items.add(item);
                }
            }
        }

        return items;
    }
}
