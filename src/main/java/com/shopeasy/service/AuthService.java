
package com.shopeasy.service;

import com.shopeasy.dao.ShopDAO;
import com.shopeasy.dao.UserDAO;
import com.shopeasy.model.Shop;
import com.shopeasy.model.User;
import com.shopeasy.util.DBConnection;
import com.shopeasy.util.PasswordUtil;

import java.sql.Connection;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final ShopDAO shopDAO = new ShopDAO();

    public User authenticate(String username, String password)
            throws Exception {

        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {
            return null;
        }

        try (Connection con = DBConnection.getConnection()) {

            // Find user by username
            User user = userDAO.getUserByUsername(con, username);

            if (user == null) {
                return null;
            }

            // Verify password hash
            boolean validPassword = PasswordUtil.verifyPassword(
                    password,
                    user.getPassword()
            );

            if (!validPassword) {
                return null;
            }

            // Verify shop status
            Shop shop = shopDAO.getShopById(con, user.getShopId());

            if (shop == null ||
                    !"ACTIVE".equalsIgnoreCase(shop.getStatus())) {
                return null;
            }

            return user;
        }
    }
}
