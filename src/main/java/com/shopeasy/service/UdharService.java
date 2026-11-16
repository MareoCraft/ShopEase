
package com.shopeasy.service;

import com.shopeasy.dao.UdharDAO;
import com.shopeasy.model.Sale;
import com.shopeasy.model.UdharCollection;
import com.shopeasy.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UdharService {

    private final UdharDAO udharDAO = new UdharDAO();

    // Collect payment against an existing credit bill
    public void collectPayment(int shopId, int saleId,
                               double amount, String paymentMethod,
                               String paymentReference)
            throws SQLException {

        // Basic validation
        if (shopId <= 0 || saleId <= 0) {
            throw new IllegalArgumentException("Invalid shop or bill.");
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException(
                    "Collection amount must be greater than zero.");
        }

        if (paymentMethod == null) {
            throw new IllegalArgumentException(
                    "Please select a payment method.");
        }

        paymentMethod = paymentMethod.trim().toUpperCase();

        if (!paymentMethod.equals("CASH")
                && !paymentMethod.equals("UPI")
                && !paymentMethod.equals("CARD")) {
            throw new IllegalArgumentException(
                    "Invalid payment method.");
        }

        if (paymentReference != null) {
            paymentReference = paymentReference.trim();

            if (paymentReference.length() > 150) {
                throw new IllegalArgumentException(
                        "Payment reference cannot exceed 150 characters.");
            }

            if (paymentReference.isEmpty()) {
                paymentReference = null;
            }
        }

        amount = roundMoney(amount);

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                // Lock the bill so simultaneous collections cannot overpay it.
                Sale sale = udharDAO.getSaleForUpdate(con, saleId, shopId);

                if (sale == null) {
                    throw new IllegalArgumentException(
                            "Bill not found for this shop.");
                }

                if (sale.getBalanceDue() <= 0) {
                    throw new IllegalArgumentException(
                            "This bill has no outstanding balance.");
                }

                if (amount > sale.getBalanceDue()) {
                    throw new IllegalArgumentException(
                            "Collection amount cannot exceed outstanding balance.");
                }

                // Calculate updated amounts
                double newPaidAmount = roundMoney(
                        sale.getPaidAmount() + amount);

                double newBalanceDue = roundMoney(
                        sale.getBalanceDue() - amount);

                String newPaymentStatus;

                if (newBalanceDue == 0) {
                    newPaymentStatus = "PAID";
                } else if (newPaidAmount > 0) {
                    newPaymentStatus = "PARTIAL";
                } else {
                    newPaymentStatus = "UNPAID";
                }

                // Create collection record
                UdharCollection collection = new UdharCollection();

                collection.setShopId(shopId);
                collection.setSaleId(saleId);
                collection.setCustomerId(sale.getCustomerId());
                collection.setCollectionAmount(amount);
                collection.setPaymentMethod(paymentMethod);
                collection.setPaymentReference(paymentReference);
                collection.setStatus("SUCCESS");

                udharDAO.addCollection(con, collection);

                // Update original sale balance
                udharDAO.updateSaleBalance(
                        con,
                        saleId,
                        shopId,
                        newPaidAmount,
                        newBalanceDue,
                        newPaymentStatus
                );

                con.commit();

            } catch (SQLException | RuntimeException e) {

                con.rollback();
                throw e;

            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // Get collection history for a bill
    public List<UdharCollection> getCollections(
            int shopId, int saleId) throws SQLException {

        if (shopId <= 0 || saleId <= 0) {
            throw new IllegalArgumentException("Invalid shop or bill.");
        }

        try (Connection con = DBConnection.getConnection()) {
            return udharDAO.getCollectionsBySaleId(
                    con, saleId, shopId);
        }
    }

    private double roundMoney(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }

    public List<Sale> getOutstandingSales(int shopId) throws SQLException {

        if (shopId <= 0) {
            throw new IllegalArgumentException("Invalid shop ID.");
        }

        try (Connection con = DBConnection.getConnection()) {
            return udharDAO.getOutstandingSalesByShopId(con, shopId);
        }
    }

    public double getTotalOutstanding(int shopId) throws SQLException {

        if (shopId <= 0) {
            throw new IllegalArgumentException("Invalid shop ID.");
        }

        try (Connection con = DBConnection.getConnection()) {
            return udharDAO.getTotalOutstanding(con, shopId);
        }
    }
}
