
package com.shopeasy.service;

import com.shopeasy.dao.StockDAO;
import com.shopeasy.model.Product;
import com.shopeasy.model.StockHistory;
import com.shopeasy.util.DBConnection;

import java.sql.Connection;
import java.util.List;

public class StockService {

    private final StockDAO stockDAO = new StockDAO();

    // STOCK IN / STOCK OUT / STOCK ADJUSTMENT
    public boolean adjustStock(int shopId,
                               int productId,
                               String movementType,
                               double quantity,
                               String notes) throws Exception {

        validateInput(shopId, productId, movementType, quantity);

        movementType = movementType.trim().toUpperCase();

        if (notes != null) {
            notes = notes.trim();

            if (notes.isEmpty()) {
                notes = null;
            }

            if (notes != null && notes.length() > 500) {
                throw new IllegalArgumentException(
                        "Notes cannot exceed 500 characters.");
            }
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                // Lock product row to prevent simultaneous stock updates
                Product product = stockDAO.getProductForUpdate(
                        con,
                        productId,
                        shopId
                );

                if (product == null) {
                    throw new IllegalArgumentException(
                            "Product not found.");
                }

                double previousQuantity = product.getStockQuantity();
                double newQuantity;
                double quantityChange;

                // STOCK IN
                if ("STOCK_IN".equals(movementType)) {

                    newQuantity = previousQuantity + quantity;
                    quantityChange = quantity;

                }

                // STOCK OUT
                else if ("STOCK_OUT".equals(movementType)) {

                    if (quantity > previousQuantity) {
                        throw new IllegalArgumentException(
                                "Insufficient stock available.");
                    }

                    newQuantity = previousQuantity - quantity;
                    quantityChange = -quantity;

                }

                // STOCK ADJUSTMENT
                else if ("ADJUSTMENT".equals(movementType)) {

                    // For adjustment, quantity represents actual stock
                    newQuantity = quantity;
                    quantityChange = newQuantity - previousQuantity;

                }

                else {
                    throw new IllegalArgumentException(
                            "Invalid stock movement type.");
                }

                // Update product quantity
                boolean updated = stockDAO.updateStockQuantity(
                        con,
                        productId,
                        shopId,
                        newQuantity
                );

                if (!updated) {
                    throw new Exception(
                            "Unable to update product stock.");
                }

                // Prepare stock history
                StockHistory history = new StockHistory();

                history.setShopId(shopId);
                history.setProductId(productId);
                history.setMovementType(movementType);
                history.setQuantityChange(quantityChange);
                history.setPreviousQuantity(previousQuantity);
                history.setNewQuantity(newQuantity);
                history.setReferenceType("MANUAL");
                history.setReferenceId(0);
                history.setNotes(notes);

                // Insert stock history
                int historyId = stockDAO.addStockHistory(
                        con,
                        history
                );

                if (historyId <= 0) {
                    throw new Exception(
                            "Unable to save stock history.");
                }

                // Both operations succeeded
                con.commit();

                return true;

            } catch (Exception e) {

                // Roll back both stock update and history insertion
                try {
                    con.rollback();
                } catch (Exception rollbackException) {
                    e.addSuppressed(rollbackException);
                }

                throw e;
            }
        }
    }

    // GET STOCK HISTORY
    public List<StockHistory> getStockHistory(int shopId)
            throws Exception {

        if (shopId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid shop ID.");
        }

        try (Connection con = DBConnection.getConnection()) {

            return stockDAO.getStockHistoryByShopId(
                    con,
                    shopId
            );
        }
    }

    // INPUT VALIDATION
    private void validateInput(int shopId,
                               int productId,
                               String movementType,
                               double quantity) {

        if (shopId <= 0 || productId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid shop or product information.");
        }

        if (movementType == null ||
                movementType.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Stock movement type is required.");
        }

        String type = movementType.trim().toUpperCase();

        if (!"STOCK_IN".equals(type) &&
                !"STOCK_OUT".equals(type) &&
                !"ADJUSTMENT".equals(type)) {

            throw new IllegalArgumentException(
                    "Invalid stock movement type.");
        }

        if (!Double.isFinite(quantity) || quantity < 0) {
            throw new IllegalArgumentException(
                    "Quantity must be a valid non-negative number.");
        }

        if (("STOCK_IN".equals(type) ||
                "STOCK_OUT".equals(type)) && quantity <= 0) {

            throw new IllegalArgumentException(
                    "Stock In and Stock Out quantities must be greater than zero.");
        }
    }
}
