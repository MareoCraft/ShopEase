
package com.shopeasy.dao;

import com.shopeasy.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // ADD CUSTOMER
    public boolean addCustomer(Connection con, Customer customer)
            throws SQLException {

        String sql = "INSERT INTO CUSTOMERS (CUSTOMER_ID, SHOP_ID, CUSTOMER_NAME, MOBILE, EMAIL, ADDRESS, CREATED_DATE, STATUS) VALUES (CUSTOMER_SEQ.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE, 'ACTIVE')";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customer.getShopId());
            ps.setString(2, customer.getCustomerName());
            ps.setString(3, customer.getMobile());
            ps.setString(4, customer.getEmail());
            ps.setString(5, customer.getAddress());

            return ps.executeUpdate() > 0;
        }
    }

    // GET ALL CUSTOMERS BY SHOP WITH PENDING UDHAR
    public List<Customer> getCustomersByShopId(Connection con, int shopId)
            throws SQLException {

        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT C.CUSTOMER_ID, C.SHOP_ID, C.CUSTOMER_NAME, C.MOBILE, C.EMAIL, C.ADDRESS, C.CREATED_DATE, C.STATUS, NVL((SELECT SUM(S.BALANCE_DUE) FROM SALES S WHERE S.CUSTOMER_ID = C.CUSTOMER_ID AND S.SHOP_ID = C.SHOP_ID AND S.SALE_STATUS = 'COMPLETED' AND S.BALANCE_DUE > 0), 0) AS PENDING_UDHAR FROM CUSTOMERS C WHERE C.SHOP_ID = ? AND C.STATUS = 'ACTIVE' ORDER BY PENDING_UDHAR DESC, C.CUSTOMER_ID DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Customer customer = mapCustomer(rs);
                    customers.add(customer);
                }
            }
        }

        return customers;
    }

    // GET CUSTOMER BY ID WITH PENDING UDHAR
    public Customer getCustomerById(Connection con, int customerId, int shopId)
            throws SQLException {

        String sql = "SELECT C.CUSTOMER_ID, C.SHOP_ID, C.CUSTOMER_NAME, C.MOBILE, C.EMAIL, C.ADDRESS, C.CREATED_DATE, C.STATUS, NVL((SELECT SUM(S.BALANCE_DUE) FROM SALES S WHERE S.CUSTOMER_ID = C.CUSTOMER_ID AND S.SHOP_ID = C.SHOP_ID AND S.SALE_STATUS = 'COMPLETED' AND S.BALANCE_DUE > 0), 0) AS PENDING_UDHAR FROM CUSTOMERS C WHERE C.CUSTOMER_ID = ? AND C.SHOP_ID = ? AND C.STATUS = 'ACTIVE'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.setInt(2, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }
        }

        return null;
    }

    // UPDATE CUSTOMER
    public boolean updateCustomer(Connection con, Customer customer)
            throws SQLException {

        String sql = "UPDATE CUSTOMERS SET CUSTOMER_NAME = ?, MOBILE = ?, EMAIL = ?, ADDRESS = ? WHERE CUSTOMER_ID = ? AND SHOP_ID = ? AND STATUS = 'ACTIVE'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, customer.getCustomerName());
            ps.setString(2, customer.getMobile());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getAddress());
            ps.setInt(5, customer.getCustomerId());
            ps.setInt(6, customer.getShopId());

            return ps.executeUpdate() > 0;
        }
    }

    // SOFT DELETE CUSTOMER
    public boolean deleteCustomer(Connection con, int customerId, int shopId)
            throws SQLException {

        String sql = "UPDATE CUSTOMERS SET STATUS = 'INACTIVE' WHERE CUSTOMER_ID = ? AND SHOP_ID = ? AND STATUS = 'ACTIVE'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.setInt(2, shopId);

            return ps.executeUpdate() > 0;
        }
    }

    // CHECK DUPLICATE MOBILE
    public boolean isMobileExists(Connection con, int shopId, String mobile,
                                  int excludeCustomerId)
            throws SQLException {

        String sql = "SELECT COUNT(*) FROM CUSTOMERS WHERE SHOP_ID = ? AND MOBILE = ? AND STATUS = 'ACTIVE' AND CUSTOMER_ID <> ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);
            ps.setString(2, mobile);
            ps.setInt(3, excludeCustomerId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    // MAP RESULT SET
    private Customer mapCustomer(ResultSet rs) throws SQLException {

        Customer customer = new Customer();

        customer.setCustomerId(rs.getInt("CUSTOMER_ID"));
        customer.setShopId(rs.getInt("SHOP_ID"));
        customer.setCustomerName(rs.getString("CUSTOMER_NAME"));
        customer.setMobile(rs.getString("MOBILE"));
        customer.setEmail(rs.getString("EMAIL"));
        customer.setAddress(rs.getString("ADDRESS"));
        customer.setCreatedDate(rs.getDate("CREATED_DATE"));
        customer.setStatus(rs.getString("STATUS"));

        // Map total pending Udhar
        customer.setPendingUdhar(rs.getDouble("PENDING_UDHAR"));

        return customer;
    }
}