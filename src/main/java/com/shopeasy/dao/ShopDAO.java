
package com.shopeasy.dao;

import com.shopeasy.model.Shop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShopDAO {

    // Insert shop and return generated SHOP_ID
    public int insertShop(Connection con, Shop shop)
            throws SQLException {

        String idQuery = "SELECT SHOP_SEQ.NEXTVAL FROM DUAL";

        int shopId;

        try (PreparedStatement ps = con.prepareStatement(idQuery);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            shopId = rs.getInt(1);
        }

        String sql = "INSERT INTO SHOPS ( SHOP_ID, SHOP_NAME, SHOP_CATEGORY, OWNER_NAME, MOBILE, EMAIL, ADDRESS, CITY, STATE, PINCODE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);
            ps.setString(2, shop.getShopName());
            ps.setString(3, shop.getShopCategory());
            ps.setString(4, shop.getOwnerName());
            ps.setString(5, shop.getMobile());
            ps.setString(6, shop.getEmail());
            ps.setString(7, shop.getAddress());
            ps.setString(8, shop.getCity());
            ps.setString(9, shop.getState());
            ps.setString(10, shop.getPincode());

            ps.executeUpdate();
        }

        return shopId;
    }

    // Find shop by SHOP_ID
    public Shop getShopById(Connection con, int shopId)
            throws SQLException {

        String sql = "SELECT * FROM SHOPS WHERE SHOP_ID = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Shop shop = new Shop();

                    shop.setShopId(rs.getInt("SHOP_ID"));
                    shop.setShopName(rs.getString("SHOP_NAME"));
                    shop.setShopCategory(rs.getString("SHOP_CATEGORY"));
                    shop.setOwnerName(rs.getString("OWNER_NAME"));
                    shop.setMobile(rs.getString("MOBILE"));
                    shop.setEmail(rs.getString("EMAIL"));
                    shop.setAddress(rs.getString("ADDRESS"));
                    shop.setCity(rs.getString("CITY"));
                    shop.setState(rs.getString("STATE"));
                    shop.setPincode(rs.getString("PINCODE"));
                    shop.setCreatedDate(rs.getDate("CREATED_DATE"));
                    shop.setStatus(rs.getString("STATUS"));

                    return shop;
                }
            }
        }

        return null;
    }

    // Update shop details
    public boolean updateShop(Connection con, Shop shop)
            throws SQLException {

        String sql = "UPDATE SHOPS SET SHOP_NAME = ?, SHOP_CATEGORY = ?, OWNER_NAME = ?, MOBILE = ?, EMAIL = ?, ADDRESS = ?, CITY = ?, STATE = ?, PINCODE = ? WHERE SHOP_ID = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, shop.getShopName());
            ps.setString(2, shop.getShopCategory());
            ps.setString(3, shop.getOwnerName());
            ps.setString(4, shop.getMobile());
            ps.setString(5, shop.getEmail());
            ps.setString(6, shop.getAddress());
            ps.setString(7, shop.getCity());
            ps.setString(8, shop.getState());
            ps.setString(9, shop.getPincode());
            ps.setInt(10, shop.getShopId());

            return ps.executeUpdate() > 0;
        }
    }
}
