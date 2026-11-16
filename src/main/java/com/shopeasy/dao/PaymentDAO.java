
package com.shopeasy.dao;

import com.shopeasy.model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    // ADD PAYMENT
    public boolean addPayment(Connection con, Payment payment)
            throws SQLException {

        String sql = "INSERT INTO PAYMENTS (PAYMENT_ID, SHOP_ID, SALE_ID, PAYMENT_METHOD, PAYMENT_AMOUNT, PAYMENT_REFERENCE, PAYMENT_DATE, PAYMENT_STATUS) VALUES (PAYMENT_SEQ.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, payment.getShopId());
            ps.setInt(2, payment.getSaleId());
            ps.setString(3, payment.getPaymentMethod());
            ps.setDouble(4, payment.getPaymentAmount());
            ps.setString(5, payment.getPaymentReference());
            ps.setString(6, payment.getPaymentStatus());

            return ps.executeUpdate() > 0;
        }
    }

    // GET PAYMENT BY SALE ID
    public Payment getPaymentBySaleId(Connection con,
                                      int saleId,
                                      int shopId)
            throws SQLException {

        String sql = "SELECT * FROM PAYMENTS WHERE SALE_ID = ? AND SHOP_ID = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);
            ps.setInt(2, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapPayment(rs);
                }
            }
        }

        return null;
    }

    // GET ALL PAYMENTS BY SHOP
    public List<Payment> getPaymentsByShopId(Connection con, int shopId)
            throws SQLException {

        List<Payment> payments = new ArrayList<>();

        String sql = "SELECT * FROM PAYMENTS WHERE SHOP_ID = ? ORDER BY PAYMENT_DATE DESC, PAYMENT_ID DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    payments.add(mapPayment(rs));
                }
            }
        }

        return payments;
    }

    // MAP RESULT SET
    private Payment mapPayment(ResultSet rs) throws SQLException {

        Payment payment = new Payment();

        payment.setPaymentId(rs.getInt("PAYMENT_ID"));
        payment.setShopId(rs.getInt("SHOP_ID"));
        payment.setSaleId(rs.getInt("SALE_ID"));
        payment.setPaymentMethod(rs.getString("PAYMENT_METHOD"));
        payment.setPaymentAmount(rs.getDouble("PAYMENT_AMOUNT"));
        payment.setPaymentReference(rs.getString("PAYMENT_REFERENCE"));
        payment.setPaymentDate(rs.getDate("PAYMENT_DATE"));
        payment.setPaymentStatus(rs.getString("PAYMENT_STATUS"));

        return payment;
    }
}
