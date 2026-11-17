
package com.shopeasy.service;

import com.shopeasy.dao.StockDAO;
import com.shopeasy.model.Product;
import com.shopeasy.model.StockHistory;
import com.shopeasy.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
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

                Product product = stockDAO.getProductForUpdate(
                        con, productId, shopId
                );

                if (product == null) {
                    throw new IllegalArgumentException("Product not found.");
                }

                double previousQuantity = product.getStockQuantity();
                double newQuantity;
                double quantityChange;

                if ("STOCK_IN".equals(movementType)) {

                    newQuantity = previousQuantity + quantity;
                    quantityChange = quantity;

                } else if ("STOCK_OUT".equals(movementType)) {

                    if (quantity > previousQuantity) {
                        throw new IllegalArgumentException(
                                "Insufficient stock available.");
                    }

                    newQuantity = previousQuantity - quantity;
                    quantityChange = -quantity;

                } else if ("ADJUSTMENT".equals(movementType)) {

                    newQuantity = quantity;
                    quantityChange = newQuantity - previousQuantity;

                } else {
                    throw new IllegalArgumentException(
                            "Invalid stock movement type.");
                }

                boolean updated = stockDAO.updateStockQuantity(
                        con, productId, shopId, newQuantity
                );

                if (!updated) {
                    throw new Exception("Unable to update product stock.");
                }

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

                int historyId = stockDAO.addStockHistory(con, history);

                if (historyId <= 0) {
                    throw new Exception("Unable to save stock history.");
                }

                con.commit();

                return true;

            } catch (Exception e) {

                try {
                    con.rollback();
                } catch (Exception rollbackException) {
                    e.addSuppressed(rollbackException);
                }

                throw e;
            }
        }
    }

    // GET ALL STOCK HISTORY
    public List<StockHistory> getStockHistory(int shopId)
            throws Exception {

        return getStockHistory(shopId, null, null);
    }

    // GET STOCK HISTORY WITH DATE FILTER
    public List<StockHistory> getStockHistory(
            int shopId,
            Date fromDate,
            Date toDate) throws Exception {

        if (shopId <= 0) {
            throw new IllegalArgumentException("Invalid shop ID.");
        }

        if ((fromDate == null) != (toDate == null)) {
            throw new IllegalArgumentException(
                    "Both start date and end date are required.");
        }

        if (fromDate != null && fromDate.after(toDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date.");
        }

        try (Connection con = DBConnection.getConnection()) {

            return stockDAO.getStockHistoryByShopId(
                    con,
                    shopId,
                    fromDate,
                    toDate
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

        if (movementType == null || movementType.trim().isEmpty()) {
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