
package com.shopeasy.service;

import com.shopeasy.dao.ProductDAO;
import com.shopeasy.model.Product;
import com.shopeasy.util.DBConnection;
import com.shopeasy.dao.StockDAO;
import com.shopeasy.model.StockHistory;

import java.sql.Connection;
import java.util.List;

public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();
    private final StockDAO stockDAO = new StockDAO();

    // ADD PRODUCT

    public int addProduct(Product product) throws Exception {

        validateProduct(product);

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                // CHECK CATEGORY
                if (!productDAO.isCategoryValid(
                        con,
                        product.getCategoryId(),
                        product.getShopId())) {

                    throw new IllegalArgumentException(
                            "Please select a valid category.");
                }

                // CHECK DUPLICATE SKU
                if (productDAO.isSkuExists(
                        con,
                        product.getShopId(),
                        product.getSku(),
                        0)) {

                    throw new IllegalArgumentException(
                            "This SKU already exists.");
                }

                // CHECK DUPLICATE BARCODE
                if (productDAO.isBarcodeExists(
                        con,
                        product.getShopId(),
                        product.getBarcode(),
                        0)) {

                    throw new IllegalArgumentException(
                            "This barcode already exists.");
                }

                // INSERT PRODUCT
                int productId = productDAO.addProduct(con, product);

                if (productId <= 0) {
                    throw new Exception("Unable to register product.");
                }

                // CREATE OPENING STOCK HISTORY
                StockHistory history = new StockHistory();

                history.setShopId(product.getShopId());
                history.setProductId(productId);
                history.setMovementType("OPENING_STOCK");

                history.setQuantityChange(product.getStockQuantity());
                history.setPreviousQuantity(0);
                history.setNewQuantity(product.getStockQuantity());

                history.setReferenceType("PRODUCT_CREATION");
                history.setReferenceId(productId);

                history.setNotes("Initial stock recorded during product registration.");

                // INSERT HISTORY
                int historyId = stockDAO.addStockHistory(con, history);

                if (historyId <= 0) {
                    throw new Exception(
                            "Unable to record opening stock history.");
                }

                // COMMIT BOTH OPERATIONS
                con.commit();

                return productId;

            } catch (Exception e) {

                // ROLLBACK PRODUCT AND HISTORY TOGETHER
                try {
                    con.rollback();
                } catch (Exception rollbackException) {
                    e.addSuppressed(rollbackException);
                }

                throw e;
            }
        }
    }

    // UPDATE PRODUCT
    public boolean updateProduct(Product product) throws Exception {

        validateProduct(product);

        if (product.getProductId() <= 0) {
            throw new IllegalArgumentException(
                    "Invalid product ID.");
        }

        try (Connection con = DBConnection.getConnection()) {

            // Verify product belongs to this shop
            Product existing = productDAO.getProductById(
                    con,
                    product.getProductId(),
                    product.getShopId()
            );

            if (existing == null) {
                throw new IllegalArgumentException(
                        "Product not found.");
            }

            // Verify category belongs to this shop
            if (!productDAO.isCategoryValid(
                    con,
                    product.getCategoryId(),
                    product.getShopId())) {

                throw new IllegalArgumentException(
                        "Please select a valid category.");
            }

            // Check duplicate SKU
            if (productDAO.isSkuExists(
                    con,
                    product.getShopId(),
                    product.getSku(),
                    product.getProductId())) {

                throw new IllegalArgumentException(
                        "This SKU already exists.");
            }

            // Check duplicate barcode
            if (productDAO.isBarcodeExists(
                    con,
                    product.getShopId(),
                    product.getBarcode(),
                    product.getProductId())) {

                throw new IllegalArgumentException(
                        "This barcode already exists.");
            }

            return productDAO.updateProduct(con, product);
        }
    }

    // DELETE PRODUCT (SOFT DELETE)
    public boolean deleteProduct(int productId, int shopId)
            throws Exception {

        if (productId <= 0 || shopId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid product information.");
        }

        try (Connection con = DBConnection.getConnection()) {

            return productDAO.deleteProduct(
                    con,
                    productId,
                    shopId
            );
        }
    }

    // GET ALL PRODUCTS
    public List<Product> getProducts(int shopId) throws Exception {

        if (shopId <= 0) {
            throw new IllegalArgumentException("Invalid shop ID.");
        }

        try (Connection con = DBConnection.getConnection()) {

            return productDAO.getProductsByShopId(con, shopId);
        }
    }

    // GET PRODUCT BY ID
    public Product getProduct(int productId, int shopId)
            throws Exception {

        if (productId <= 0 || shopId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid product information.");
        }

        try (Connection con = DBConnection.getConnection()) {

            return productDAO.getProductById(
                    con,
                    productId,
                    shopId
            );
        }
    }

    // VALIDATION
    private void validateProduct(Product product) {

        if (product == null) {
            throw new IllegalArgumentException(
                    "Product information is required.");
        }

        if (product.getShopId() <= 0) {
            throw new IllegalArgumentException("Invalid shop ID.");
        }

        if (product.getCategoryId() <= 0) {
            throw new IllegalArgumentException(
                    "Please select a category.");
        }

        if (product.getProductName() == null ||
                product.getProductName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Product name is required.");
        }

        product.setProductName(
                product.getProductName().trim()
        );

        if (product.getProductName().length() > 150) {
            throw new IllegalArgumentException(
                    "Product name cannot exceed 150 characters.");
        }

        // Optional SKU
        if (product.getSku() != null) {
            product.setSku(product.getSku().trim());

            if (product.getSku().isEmpty()) {
                product.setSku(null);
            } else if (product.getSku().length() > 50) {
                throw new IllegalArgumentException(
                        "SKU cannot exceed 50 characters.");
            }
        }

        // Optional barcode
        if (product.getBarcode() != null) {
            product.setBarcode(product.getBarcode().trim());

            if (product.getBarcode().isEmpty()) {
                product.setBarcode(null);
            } else if (product.getBarcode().length() > 100) {
                throw new IllegalArgumentException(
                        "Barcode cannot exceed 100 characters.");
            }
        }

        // Price validation
        if (!Double.isFinite(product.getPurchasePrice()) ||
                product.getPurchasePrice() < 0) {

            throw new IllegalArgumentException(
                    "Purchase price must be a valid non-negative amount.");
        }

        if (!Double.isFinite(product.getSellingPrice()) ||
                product.getSellingPrice() <= 0) {

            throw new IllegalArgumentException(
                    "Selling price must be greater than zero.");
        }

        // Stock validation
        if (!Double.isFinite(product.getStockQuantity()) ||
                product.getStockQuantity() < 0) {

            throw new IllegalArgumentException(
                    "Stock quantity cannot be negative.");
        }

        if (!Double.isFinite(product.getMinStockLevel()) ||
                product.getMinStockLevel() < 0) {

            throw new IllegalArgumentException(
                    "Minimum stock level cannot be negative.");
        }

        // Unit validation
        if (product.getUnit() == null ||
                product.getUnit().trim().isEmpty()) {

            product.setUnit("PCS");

        } else {
            product.setUnit(product.getUnit().trim());

            if (product.getUnit().length() > 30) {
                throw new IllegalArgumentException(
                        "Unit cannot exceed 30 characters.");
            }
        }

        // Description validation
        if (product.getDescription() != null) {

            product.setDescription(
                    product.getDescription().trim()
            );

            if (product.getDescription().isEmpty()) {
                product.setDescription(null);
            } else if (product.getDescription().length() > 500) {
                throw new IllegalArgumentException(
                        "Description cannot exceed 500 characters.");
            }
        }
    }
}
