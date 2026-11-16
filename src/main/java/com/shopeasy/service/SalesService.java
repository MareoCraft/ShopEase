
package com.shopeasy.service;

import com.shopeasy.dao.CustomerDAO;
import com.shopeasy.dao.PaymentDAO;
import com.shopeasy.dao.SaleDAO;
import com.shopeasy.dao.SaleItemDAO;
import com.shopeasy.dao.StockDAO;

import com.shopeasy.model.Customer;
import com.shopeasy.model.Payment;
import com.shopeasy.model.Product;
import com.shopeasy.model.Sale;
import com.shopeasy.model.SaleItem;
import com.shopeasy.model.StockHistory;

import com.shopeasy.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SalesService {

    private final SaleDAO saleDAO = new SaleDAO();
    private final SaleItemDAO saleItemDAO = new SaleItemDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final StockDAO stockDAO = new StockDAO();

    // CREATE COMPLETE SALE
    public Sale createSale(int shopId, Sale sale,
                           List<SaleItem> items,
                           Payment payment) throws SQLException {

        validateSaleRequest(shopId, sale, items, payment);

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                // 1. VERIFY CUSTOMER
                Customer customer = customerDAO.getCustomerById(
                        con,
                        sale.getCustomerId(),
                        shopId
                );

                if (customer == null) {
                    throw new IllegalArgumentException(
                            "Please select a valid registered customer."
                    );
                }

                // 2. VALIDATE PAYMENT METHOD
                String method = payment.getPaymentMethod()
                        .trim().toUpperCase();

                if (!method.equals("CASH")
                        && !method.equals("UPI")
                        && !method.equals("CARD")
                        && !method.equals("CREDIT")) {

                    throw new IllegalArgumentException(
                            "Invalid payment method."
                    );
                }

                payment.setPaymentMethod(method);

                // 3. GENERATE BILL NUMBER
                String date = LocalDate.now().format(
                        DateTimeFormatter.ofPattern("yyyyMMdd")
                );

                String uniqueCode = UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

                String billNumber = "INV-" + shopId
                        + "-" + date + "-" + uniqueCode;

                // 4. CALCULATE BILL TOTAL
                double subtotal = 0;

                Set<Integer> processedProducts = new HashSet<>();

                for (SaleItem item : items) {

                    if (item == null || item.getProductId() <= 0) {
                        throw new IllegalArgumentException(
                                "Invalid product selected."
                        );
                    }

                    if (!processedProducts.add(item.getProductId())) {
                        throw new IllegalArgumentException(
                                "Duplicate products found in the bill. "
                                        + "Combine their quantities."
                        );
                    }

                    double quantity = item.getQuantity();

                    if (!Double.isFinite(quantity) || quantity <= 0) {
                        throw new IllegalArgumentException(
                                "Product quantity must be greater than zero."
                        );
                    }

                    // Lock product row and fetch current database values.
                    Product product = stockDAO.getProductForUpdate(
                            con,
                            item.getProductId(),
                            shopId
                    );

                    if (product == null) {
                        throw new IllegalArgumentException(
                                "Product not found or inactive."
                        );
                    }

                    if (product.getStockQuantity() < quantity) {
                        throw new IllegalArgumentException(
                                "Insufficient stock for "
                                        + product.getProductName()
                                        + ". Available stock: "
                                        + product.getStockQuantity()
                        );
                    }

                    double price = product.getSellingPrice();
                    double lineTotal = roundMoney(quantity * price);

                    if (!Double.isFinite(lineTotal)) {
                        throw new IllegalArgumentException(
                                "Invalid product total."
                        );
                    }

                    // Never trust product prices submitted by the browser.
                    item.setShopId(shopId);
                    item.setProductName(product.getProductName());
                    item.setSku(product.getSku());
                    item.setUnit(product.getUnit());
                    item.setUnitPrice(price);
                    item.setLineTotal(lineTotal);

                    subtotal = roundMoney(subtotal + lineTotal);
                }

                // 5. VALIDATE DISCOUNT
                double discount = sale.getDiscountAmount();

                if (!Double.isFinite(discount)
                        || discount < 0 || discount > subtotal) {

                    throw new IllegalArgumentException(
                            "Invalid discount amount."
                    );
                }

                discount = roundMoney(discount);

                double grandTotal = roundMoney(subtotal - discount);

                // 6. CALCULATE PAYMENT
                double paidAmount;

                if (method.equals("CREDIT")) {

                    paidAmount = 0;

                } else {

                    paidAmount = grandTotal;
                }

                double balanceDue = roundMoney(
                        grandTotal - paidAmount
                );

                String paymentStatus = balanceDue == 0
                        ? "PAID" : "UNPAID";

                // 7. PREPARE SALE
                sale.setShopId(shopId);
                sale.setBillNumber(billNumber);
                sale.setSubtotal(subtotal);
                sale.setDiscountAmount(discount);
                sale.setGrandTotal(grandTotal);
                sale.setPaidAmount(paidAmount);
                sale.setBalanceDue(balanceDue);
                sale.setPaymentStatus(paymentStatus);
                sale.setSaleStatus("COMPLETED");

                // 8. INSERT MAIN SALE
                if (!saleDAO.addSale(con, sale)) {
                    throw new SQLException(
                            "Unable to create sales bill."
                    );
                }

                int saleId = sale.getSaleId();

                // 9. INSERT SALE ITEMS
                for (SaleItem item : items) {

                    item.setSaleId(saleId);

                    if (!saleItemDAO.addSaleItem(con, item)) {
                        throw new SQLException(
                                "Unable to save sale items."
                        );
                    }
                }

                // 10. INSERT PAYMENT
                payment.setShopId(shopId);
                payment.setSaleId(saleId);
                payment.setPaymentAmount(paidAmount);
                payment.setPaymentStatus("SUCCESS");

                if (!paymentDAO.addPayment(con, payment)) {
                    throw new SQLException(
                            "Unable to save payment details."
                    );
                }

                // 11. REDUCE STOCK AND RECORD HISTORY
                for (SaleItem item : items) {

                    Product product = stockDAO.getProductForUpdate(
                            con,
                            item.getProductId(),
                            shopId
                    );

                    if (product == null) {
                        throw new IllegalArgumentException(
                                "Product is no longer available."
                        );
                    }

                    double previousQuantity =
                            product.getStockQuantity();

                    double quantitySold = item.getQuantity();

                    if (previousQuantity < quantitySold) {
                        throw new IllegalArgumentException(
                                "Insufficient stock for "
                                        + product.getProductName()
                        );
                    }

                    double newQuantity = previousQuantity
                            - quantitySold;

                    if (!stockDAO.updateStockQuantity(
                            con,
                            item.getProductId(),
                            shopId,
                            newQuantity)) {

                        throw new SQLException(
                                "Unable to update product stock."
                        );
                    }

                    StockHistory history = new StockHistory();

                    history.setShopId(shopId);
                    history.setProductId(item.getProductId());
                    history.setMovementType("STOCK_OUT");
                    history.setQuantityChange(-quantitySold);
                    history.setPreviousQuantity(previousQuantity);
                    history.setNewQuantity(newQuantity);
                    history.setReferenceType("SALE");
                    history.setReferenceId(saleId);
                    history.setNotes(
                            "Stock deducted for bill " + billNumber
                    );

                    stockDAO.addStockHistory(con, history);
                }

                // 12. COMMIT COMPLETE SALE
                con.commit();

                return sale;

            } catch (SQLException | RuntimeException e) {

                con.rollback();
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // REQUEST VALIDATION
    private void validateSaleRequest(int shopId,
                                     Sale sale,
                                     List<SaleItem> items,
                                     Payment payment) {

        if (shopId <= 0) {
            throw new IllegalArgumentException("Invalid shop.");
        }

        if (sale == null) {
            throw new IllegalArgumentException(
                    "Sales information is required."
            );
        }

        if (sale.getCustomerId() <= 0) {
            throw new IllegalArgumentException(
                    "Please select a customer."
            );
        }

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException(
                    "Add at least one product to the bill."
            );
        }

        if (payment == null
                || payment.getPaymentMethod() == null
                || payment.getPaymentMethod().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Please select a payment method."
            );
        }
    }

    // ROUND TO TWO DECIMAL PLACES
    private double roundMoney(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }
}
