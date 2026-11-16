package com.shopeasy.service;

import com.shopeasy.dao.CategoryDAO;
import com.shopeasy.model.Category;
import com.shopeasy.util.DBConnection;

import java.sql.Connection;
import java.util.List;

public class CategoryService {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    // ADD CATEGORY
    public int addCategory(Category category) throws Exception {

        validateCategory(category);

        try (Connection con = DBConnection.getConnection()) {

            boolean exists = categoryDAO.isCategoryNameExists(
                    con,
                    category.getShopId(),
                    category.getCategoryName(),
                    0
            );

            if (exists) {
                throw new IllegalArgumentException(
                        "Category already exists.");
            }

            return categoryDAO.addCategory(con, category);
        }
    }

    // UPDATE CATEGORY
    public boolean updateCategory(Category category) throws Exception {

        validateCategory(category);

        if (category.getCategoryId() <= 0) {
            throw new IllegalArgumentException("Invalid category ID.");
        }

        try (Connection con = DBConnection.getConnection()) {

            Category existing = categoryDAO.getCategoryById(
                    con,
                    category.getCategoryId(),
                    category.getShopId()
            );

            if (existing == null) {
                throw new IllegalArgumentException(
                        "Category not found.");
            }

            boolean exists = categoryDAO.isCategoryNameExists(
                    con,
                    category.getShopId(),
                    category.getCategoryName(),
                    category.getCategoryId()
            );

            if (exists) {
                throw new IllegalArgumentException(
                        "Another category with this name already exists.");
            }

            return categoryDAO.updateCategory(con, category);
        }
    }

    // DELETE CATEGORY (SOFT DELETE)
    public boolean deleteCategory(int categoryId, int shopId)
            throws Exception {

        if (categoryId <= 0 || shopId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid category information.");
        }

        try (Connection con = DBConnection.getConnection()) {

            return categoryDAO.deleteCategory(
                    con,
                    categoryId,
                    shopId
            );
        }
    }

    // GET ALL CATEGORIES
    public List<Category> getCategories(int shopId) throws Exception {

        if (shopId <= 0) {
            throw new IllegalArgumentException("Invalid shop ID.");
        }

        try (Connection con = DBConnection.getConnection()) {

            return categoryDAO.getCategoriesByShopId(con, shopId);
        }
    }

    // GET CATEGORY BY ID
    public Category getCategory(int categoryId, int shopId)
            throws Exception {

        if (categoryId <= 0 || shopId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid category information.");
        }

        try (Connection con = DBConnection.getConnection()) {

            return categoryDAO.getCategoryById(
                    con,
                    categoryId,
                    shopId
            );
        }
    }

    // VALIDATION
    private void validateCategory(Category category) {

        if (category == null) {
            throw new IllegalArgumentException(
                    "Category information is required.");
        }

        if (category.getShopId() <= 0) {
            throw new IllegalArgumentException("Invalid shop ID.");
        }

        if (category.getCategoryName() == null ||
                category.getCategoryName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Category name is required.");
        }

        category.setCategoryName(
                category.getCategoryName().trim()
        );

        if (category.getCategoryName().length() > 100) {
            throw new IllegalArgumentException(
                    "Category name cannot exceed 100 characters.");
        }

        if (category.getDescription() != null &&
                category.getDescription().length() > 250) {

            throw new IllegalArgumentException(
                    "Description cannot exceed 250 characters.");
        }
    }
}
