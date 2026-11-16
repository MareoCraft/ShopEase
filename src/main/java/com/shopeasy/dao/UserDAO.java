
package com.shopeasy.dao;

import com.shopeasy.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // Insert owner account
    public int insertUser(Connection con, User user)
            throws SQLException {

        String sql = "INSERT INTO USERS ( USER_ID, SHOP_ID, FULL_NAME, USERNAME, PASSWORD, ROLE) VALUES (USER_SEQ.NEXTVAL, ?, ?, ?, ?, ?) ";

        try (PreparedStatement ps = con.prepareStatement(
                sql, new String[]{"USER_ID"})) {

            ps.setInt(1, user.getShopId());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getPassword());
            ps.setString(5, "OWNER");

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }

        return 0;
    }

    // Check whether username already exists
    public boolean isUsernameExists(
            Connection con, String username)
            throws SQLException {

        String sql = "SELECT COUNT(*) FROM USERS WHERE USERNAME = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    // Find user by username
    public User getUserByUsername(
            Connection con, String username)
            throws SQLException {

        String sql = "SELECT * FROM USERS WHERE USERNAME = ? AND STATUS = 'ACTIVE'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    User user = new User();

                    user.setUserId(rs.getInt("USER_ID"));
                    user.setShopId(rs.getInt("SHOP_ID"));
                    user.setFullName(rs.getString("FULL_NAME"));
                    user.setUsername(rs.getString("USERNAME"));
                    user.setPassword(rs.getString("PASSWORD"));
                    user.setRole(rs.getString("ROLE"));
                    user.setCreatedDate(rs.getDate("CREATED_DATE"));
                    user.setStatus(rs.getString("STATUS"));

                    return user;
                }
            }
        }

        return null;
    }
}
