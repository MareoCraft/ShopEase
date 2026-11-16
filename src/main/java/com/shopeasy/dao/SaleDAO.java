
package com.shopeasy.dao;

import com.shopeasy.model.Sale;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    // ADD NEW SALE
    public boolean addSale(Connection con, Sale sale)
            throws SQLException {

        // Generate sale ID
        String sequenceSql = "SELECT SALES_SEQ.NEXTVAL FROM DUAL";

        try (PreparedStatement ps = con.prepareStatement(sequenceSql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                sale.setSaleId(rs.getInt(1));
            }
        }

        String sql = "INSERT INTO SALES (SALE_ID, SHOP_ID, CUSTOMER_ID, BILL_NUMBER, SUBTOTAL, DISCOUNT_AMOUNT, GRAND_TOTAL, PAID_AMOUNT, BALANCE_DUE, PAYMENT_STATUS, SALE_STATUS, CREATED_DATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, sale.getSaleId());
            ps.setInt(2, sale.getShopId());
            ps.setInt(3, sale.getCustomerId());
            ps.setString(4, sale.getBillNumber());
            ps.setDouble(5, sale.getSubtotal());
            ps.setDouble(6, sale.getDiscountAmount());
            ps.setDouble(7, sale.getGrandTotal());
            ps.setDouble(8, sale.getPaidAmount());
            ps.setDouble(9, sale.getBalanceDue());
            ps.setString(10, sale.getPaymentStatus());
            ps.setString(11, sale.getSaleStatus());

            return ps.executeUpdate() > 0;
        }
    }

    // GET SALE BY ID
    public Sale getSaleById(Connection con, int saleId, int shopId)
            throws SQLException {

        String sql = "SELECT * FROM SALES WHERE SALE_ID = ? AND SHOP_ID = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);
            ps.setInt(2, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapSale(rs);
                }
            }
        }

        return null;
    }

    // GET ALL SALES BY SHOP
    public List<Sale> getSalesByShopId(Connection con, int shopId)
            throws SQLException {

        List<Sale> sales = new ArrayList<>();

        String sql = "SELECT * FROM SALES WHERE SHOP_ID = ? ORDER BY CREATED_DATE DESC, SALE_ID DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    sales.add(mapSale(rs));
                }
            }
        }

        return sales;
    }

    // MAP RESULT SET
    private Sale mapSale(ResultSet rs) throws SQLException {

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
