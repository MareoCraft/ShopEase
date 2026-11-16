
package com.shopeasy.service;

import com.shopeasy.dao.ShopDAO;
import com.shopeasy.dao.UserDAO;
import com.shopeasy.model.Shop;
import com.shopeasy.model.User;
import com.shopeasy.util.DBConnection;
import com.shopeasy.util.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class ShopService {

    private final ShopDAO shopDAO = new ShopDAO();
    private final UserDAO userDAO = new UserDAO();

    public int registerShop(Shop shop, User user)
            throws Exception {

        // Basic validation
        if (shop == null || user == null) {
            throw new IllegalArgumentException(
                    "Shop and owner information are required."
            );
        }

        if (shop.getShopName() == null ||
                shop.getShopName().isBlank()) {
            throw new IllegalArgumentException(
                    "Shop name is required."
            );
        }

        if (shop.getOwnerName() == null ||
                shop.getOwnerName().isBlank()) {
            throw new IllegalArgumentException(
                    "Owner name is required."
            );
        }

        if (user.getUsername() == null ||
                user.getUsername().isBlank()) {
            throw new IllegalArgumentException(
                    "Username is required."
            );
        }

        if (user.getPassword() == null ||
                user.getPassword().length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters."
            );
        }

        Connection con = null;

        try {
            con = DBConnection.getConnection();

            // Disable auto commit
            con.setAutoCommit(false);

            // Check duplicate username
            if (userDAO.isUsernameExists(
                    con, user.getUsername())) {

                throw new IllegalArgumentException(
                        "Username already exists."
                );
            }

            // Hash password
            String hashedPassword =
                    PasswordUtil.hashPassword(user.getPassword());

            user.setPassword(hashedPassword);

            // Insert shop and generate SHOP_ID
            int shopId = shopDAO.insertShop(con, shop);

            // Assign generated SHOP_ID to owner
            user.setShopId(shopId);
            user.setFullName(shop.getOwnerName());

            // Insert owner account
            int userId = userDAO.insertUser(con, user);

            if (userId <= 0) {
                throw new SQLException(
                        "Owner account creation failed."
                );
            }

            // Commit both insert operations
            con.commit();

            return shopId;

        } catch (Exception e) {

            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
            }

            throw e;

        } finally {

            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
