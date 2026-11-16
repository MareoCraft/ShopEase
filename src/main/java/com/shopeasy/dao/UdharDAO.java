
package com.shopeasy.dao;

import com.shopeasy.model.Sale;
import com.shopeasy.model.UdharCollection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UdharDAO {

    // Get a sale and lock it during payment collection
    public Sale getSaleForUpdate(Connection con, int saleId, int shopId)
            throws SQLException {

        String sql = "SELECT * FROM SALES WHERE SALE_ID = ? AND SHOP_ID = ? AND SALE_STATUS = 'COMPLETED' FOR UPDATE";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);
            ps.setInt(2, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Sale sale = new Sale();

                    sale.setSaleId(rs.getInt("SALE_ID"));
                    sale.setShopId(rs.getInt("SHOP_ID"));
                    sale.setCustomerId(rs.getInt("CUSTOMER_ID"));
                    sale.setBillNumber(rs.getString("BILL_NUMBER"));
                    sale.setSubtotal(rs.getDouble("SUBTOTAL"));
                    sale.setDiscountAmount(rs.getDouble("DISCOUNT_AMOUNT"));
                    sale.setGrandTotal(rs.getDouble("GRAND_TOTAL"));
                    sale.setPaidAmount(rs.getDouble("PAID_AMOUNT"));
                    sale.setBalanceDue(rs.getDouble("BALANCE_DUE"));
                    sale.setPaymentStatus(rs.getString("PAYMENT_STATUS"));
                    sale.setSaleStatus(rs.getString("SALE_STATUS"));
                    sale.setCreatedDate(rs.getDate("CREATED_DATE"));

                    return sale;
                }
            }
        }

        return null;
    }

    // Save a new Udhar collection
    public void addCollection(Connection con, UdharCollection collection)
            throws SQLException {

        String sql = "INSERT INTO UDHAR_COLLECTIONS (COLLECTION_ID, SHOP_ID, SALE_ID, CUSTOMER_ID, COLLECTION_AMOUNT, PAYMENT_METHOD, PAYMENT_REFERENCE, COLLECTION_DATE, STATUS) VALUES (UDHAR_COLLECTION_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, SYSDATE, 'SUCCESS')";

        try (PreparedStatement ps = con.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, collection.getShopId());
            ps.setInt(2, collection.getSaleId());
            ps.setInt(3, collection.getCustomerId());
            ps.setDouble(4, collection.getCollectionAmount());
            ps.setString(5, collection.getPaymentMethod());

            if (collection.getPaymentReference() == null ||
                    collection.getPaymentReference().trim().isEmpty()) {
                ps.setNull(6, java.sql.Types.VARCHAR);
            } else {
                ps.setString(6, collection.getPaymentReference().trim());
            }

            ps.executeUpdate();
        }
    }

    // Update paid amount and outstanding balance in SALES
    public void updateSaleBalance(Connection con, int saleId, int shopId,
                                  double paidAmount, double balanceDue,
                                  String paymentStatus)
            throws SQLException {

        String sql = "UPDATE SALES SET PAID_AMOUNT = ?, BALANCE_DUE = ?, PAYMENT_STATUS = ? WHERE SALE_ID = ? AND SHOP_ID = ? AND SALE_STATUS = 'COMPLETED'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, paidAmount);
            ps.setDouble(2, balanceDue);
            ps.setString(3, paymentStatus);
            ps.setInt(4, saleId);
            ps.setInt(5, shopId);

            int updated = ps.executeUpdate();

            if (updated != 1) {
                throw new SQLException("Unable to update the bill balance.");
            }
        }
    }

    // Get collection history for a particular bill
    public List<UdharCollection> getCollectionsBySaleId(
            Connection con, int saleId, int shopId) throws SQLException {

        List<UdharCollection> collections = new ArrayList<>();

        String sql = "SELECT * FROM UDHAR_COLLECTIONS WHERE SALE_ID = ? AND SHOP_ID = ? AND STATUS = 'SUCCESS' ORDER BY COLLECTION_DATE DESC, COLLECTION_ID DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);
            ps.setInt(2, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    UdharCollection collection = new UdharCollection();

                    collection.setCollectionId(rs.getInt("COLLECTION_ID"));
                    collection.setShopId(rs.getInt("SHOP_ID"));
                    collection.setSaleId(rs.getInt("SALE_ID"));
                    collection.setCustomerId(rs.getInt("CUSTOMER_ID"));
                    collection.setCollectionAmount(
                            rs.getDouble("COLLECTION_AMOUNT"));
                    collection.setPaymentMethod(
                            rs.getString("PAYMENT_METHOD"));
                    collection.setPaymentReference(
                            rs.getString("PAYMENT_REFERENCE"));
                    collection.setCollectionDate(
                            rs.getDate("COLLECTION_DATE"));
                    collection.setStatus(rs.getString("STATUS"));

                    collections.add(collection);
                }
            }
        }

        return collections;
    }

    public List<Sale> getOutstandingSalesByShopId(
            Connection con, int shopId) throws SQLException {

        List<Sale> sales = new ArrayList<>();

        String sql = "SELECT * FROM SALES WHERE SHOP_ID = ? AND SALE_STATUS = 'COMPLETED' AND PAYMENT_STATUS IN ('UNPAID','PARTIAL') AND BALANCE_DUE > 0 ORDER BY CREATED_DATE DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Sale sale = new Sale();

                    sale.setSaleId(rs.getInt("SALE_ID"));
                    sale.setShopId(rs.getInt("SHOP_ID"));
                    sale.setCustomerId(rs.getInt("CUSTOMER_ID"));
                    sale.setBillNumber(rs.getString("BILL_NUMBER"));
                    sale.setSubtotal(rs.getDouble("SUBTOTAL"));
                    sale.setDiscountAmount(rs.getDouble("DISCOUNT_AMOUNT"));
                    sale.setGrandTotal(rs.getDouble("GRAND_TOTAL"));
                    sale.setPaidAmount(rs.getDouble("PAID_AMOUNT"));
                    sale.setBalanceDue(rs.getDouble("BALANCE_DUE"));
                    sale.setPaymentStatus(rs.getString("PAYMENT_STATUS"));
                    sale.setSaleStatus(rs.getString("SALE_STATUS"));
                    sale.setCreatedDate(rs.getDate("CREATED_DATE"));

                    sales.add(sale);
                }
            }
        }

        return sales;
    }

    public double getTotalOutstanding(Connection con, int shopId)
            throws SQLException {

        String sql = "SELECT NVL(SUM(BALANCE_DUE), 0) AS TOTAL_OUTSTANDING FROM SALES WHERE SHOP_ID = ? AND SALE_STATUS = 'COMPLETED' AND PAYMENT_STATUS IN ('UNPAID','PARTIAL') AND BALANCE_DUE > 0";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getDouble("TOTAL_OUTSTANDING");
                }
            }
        }

        return 0;
    }
}
