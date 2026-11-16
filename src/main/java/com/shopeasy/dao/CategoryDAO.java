
package com.shopeasy.dao;

import com.shopeasy.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    // ADD CATEGORY
    public int addCategory(Connection con, Category category)
            throws SQLException {

        String sequenceSQL =
                "SELECT CATEGORY_SEQ.NEXTVAL FROM DUAL";

        String insertSQL =
                "INSERT INTO CATEGORIES (CATEGORY_ID, SHOP_ID, CATEGORY_NAME, DESCRIPTION, STATUS) VALUES (?, ?, ?, ?, 'ACTIVE')";

        int categoryId;

        try (PreparedStatement ps =
                     con.prepareStatement(sequenceSQL);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            categoryId = rs.getInt(1);
        }

        try (PreparedStatement ps =
                     con.prepareStatement(insertSQL)) {

            ps.setInt(1, categoryId);
            ps.setInt(2, category.getShopId());
            ps.setString(3, category.getCategoryName());
            ps.setString(4, category.getDescription());

            int result = ps.executeUpdate();

            return result > 0 ? categoryId : 0;
        }
    }

    // UPDATE CATEGORY
    public boolean updateCategory(Connection con, Category category)
            throws SQLException {

        String sql =
                "UPDATE CATEGORIES SET CATEGORY_NAME = ?, DESCRIPTION = ? WHERE CATEGORY_ID = ? AND SHOP_ID = ? AND STATUS = 'ACTIVE'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, category.getCategoryName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, category.getCategoryId());
            ps.setInt(4, category.getShopId());

            return ps.executeUpdate() > 0;
        }
    }

    // SOFT DELETE CATEGORY
    public boolean deleteCategory(Connection con, int categoryId,
                                  int shopId)
            throws SQLException {

        String sql =
                "UPDATE CATEGORIES SET STATUS = 'INACTIVE' WHERE CATEGORY_ID = ? AND SHOP_ID = ? AND STATUS = 'ACTIVE'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ps.setInt(2, shopId);

            return ps.executeUpdate() > 0;
        }
    }

    // GET ALL ACTIVE CATEGORIES OF A SHOP
    public List<Category> getCategoriesByShopId(
            Connection con, int shopId) throws SQLException {

        List<Category> categories = new ArrayList<>();

        String sql =
                "SELECT CATEGORY_ID, SHOP_ID, CATEGORY_NAME, DESCRIPTION, STATUS, CREATED_DATE FROM CATEGORIES WHERE SHOP_ID = ? AND STATUS = 'ACTIVE' ORDER BY CATEGORY_ID DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Category category = new Category();

                    category.setCategoryId(
                            rs.getInt("CATEGORY_ID"));

                    category.setShopId(
                            rs.getInt("SHOP_ID"));

                    category.setCategoryName(
                            rs.getString("CATEGORY_NAME"));

                    category.setDescription(
                            rs.getString("DESCRIPTION"));

                    category.setStatus(
                            rs.getString("STATUS"));

                    category.setCreatedDate(
                            rs.getDate("CREATED_DATE"));

                    categories.add(category);
                }
            }
        }

        return categories;
    }

    // GET CATEGORY BY ID
    public Category getCategoryById(Connection con,
                                    int categoryId,
                                    int shopId)
            throws SQLException {

        String sql =
                "SELECT CATEGORY_ID, SHOP_ID, CATEGORY_NAME, DESCRIPTION, STATUS, CREATED_DATE FROM CATEGORIES WHERE CATEGORY_ID = ? AND SHOP_ID = ? AND STATUS = 'ACTIVE'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ps.setInt(2, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Category category = new Category();

                    category.setCategoryId(rs.getInt("CATEGORY_ID"));
                    category.setShopId(rs.getInt("SHOP_ID"));
                    category.setCategoryName(rs.getString("CATEGORY_NAME"));
                    category.setDescription(rs.getString("DESCRIPTION"));
                    category.setStatus(rs.getString("STATUS"));
                    category.setCreatedDate(rs.getDate("CREATED_DATE"));

                    return category;
                }
            }
        }

        return null;
    }

    // CHECK DUPLICATE CATEGORY NAME
    public boolean isCategoryNameExists(Connection con,
                                        int shopId,
                                        String categoryName,
                                        int excludeCategoryId)
            throws SQLException {

        String sql =
                "SELECT COUNT(*) FROM CATEGORIES WHERE SHOP_ID = ? AND UPPER(CATEGORY_NAME) = UPPER(?) AND STATUS = 'ACTIVE' AND CATEGORY_ID <> ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);
            ps.setString(2, categoryName);
            ps.setInt(3, excludeCategoryId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }
}
