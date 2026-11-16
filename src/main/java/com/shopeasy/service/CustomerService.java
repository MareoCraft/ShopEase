
package com.shopeasy.service;

import com.shopeasy.dao.CustomerDAO;
import com.shopeasy.model.Customer;
import com.shopeasy.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CustomerService {

    private final CustomerDAO customerDAO = new CustomerDAO();

    // ADD CUSTOMER
    public boolean addCustomer(Customer customer) throws SQLException {

        validateCustomer(customer);

        if (customer.getShopId() <= 0) {
            throw new IllegalArgumentException("Invalid shop.");
        }

        try (Connection con = DBConnection.getConnection()) {

            if (hasDuplicateMobile(con, customer.getShopId(),
                    customer.getMobile(), 0)) {
                throw new IllegalArgumentException(
                        "This mobile number is already registered."
                );
            }

            return customerDAO.addCustomer(con, customer);
        }
    }

    // UPDATE CUSTOMER
    public boolean updateCustomer(Customer customer) throws SQLException {

        validateCustomer(customer);

        if (customer.getShopId() <= 0 || customer.getCustomerId() <= 0) {
            throw new IllegalArgumentException("Invalid customer or shop.");
        }

        try (Connection con = DBConnection.getConnection()) {

            Customer existing = customerDAO.getCustomerById(
                    con,
                    customer.getCustomerId(),
                    customer.getShopId()
            );

            if (existing == null) {
                throw new IllegalArgumentException("Customer not found.");
            }

            if (hasDuplicateMobile(con, customer.getShopId(),
                    customer.getMobile(), customer.getCustomerId())) {
                throw new IllegalArgumentException(
                        "This mobile number is already registered."
                );
            }

            return customerDAO.updateCustomer(con, customer);
        }
    }

    // DELETE CUSTOMER
    public boolean deleteCustomer(int customerId, int shopId)
            throws SQLException {

        if (customerId <= 0 || shopId <= 0) {
            throw new IllegalArgumentException("Invalid customer.");
        }

        try (Connection con = DBConnection.getConnection()) {

            return customerDAO.deleteCustomer(con, customerId, shopId);
        }
    }

    // GET ALL CUSTOMERS
    public List<Customer> getCustomers(int shopId) throws SQLException {

        if (shopId <= 0) {
            throw new IllegalArgumentException("Invalid shop.");
        }

        try (Connection con = DBConnection.getConnection()) {

            return customerDAO.getCustomersByShopId(con, shopId);
        }
    }

    // GET CUSTOMER BY ID
    public Customer getCustomer(int customerId, int shopId)
            throws SQLException {

        if (customerId <= 0 || shopId <= 0) {
            throw new IllegalArgumentException("Invalid customer.");
        }

        try (Connection con = DBConnection.getConnection()) {

            return customerDAO.getCustomerById(
                    con, customerId, shopId
            );
        }
    }

    // CHECK DUPLICATE MOBILE
    private boolean hasDuplicateMobile(Connection con, int shopId,
                                       String mobile, int excludeCustomerId)
            throws SQLException {

        if (mobile == null || mobile.trim().isEmpty()) {
            return false;
        }

        return customerDAO.isMobileExists(
                con,
                shopId,
                mobile.trim(),
                excludeCustomerId
        );
    }

    // CUSTOMER VALIDATION
    private void validateCustomer(Customer customer) {

        if (customer == null) {
            throw new IllegalArgumentException("Customer details are required.");
        }

        if (customer.getCustomerName() == null ||
                customer.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required.");
        }

        if (customer.getCustomerName().trim().length() > 150) {
            throw new IllegalArgumentException("Customer name is too long.");
        }

        customer.setCustomerName(customer.getCustomerName().trim());

        if (customer.getMobile() != null) {

            String mobile = customer.getMobile().trim();

            if (!mobile.isEmpty() && !mobile.matches("[0-9]{10,15}")) {
                throw new IllegalArgumentException(
                        "Enter a valid mobile number (10 to 15 digits)."
                );
            }

            customer.setMobile(mobile.isEmpty() ? null : mobile);
        }

        if (customer.getEmail() != null) {

            String email = customer.getEmail().trim();

            if (!email.isEmpty() &&
                    !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                throw new IllegalArgumentException(
                        "Enter a valid email address."
                );
            }

            if (email.length() > 150) {
                throw new IllegalArgumentException("Email address is too long.");
            }

            customer.setEmail(email.isEmpty() ? null : email);
        }

        if (customer.getAddress() != null) {

            String address = customer.getAddress().trim();

            if (address.length() > 500) {
                throw new IllegalArgumentException("Address is too long.");
            }

            customer.setAddress(address.isEmpty() ? null : address);
        }
    }
}
