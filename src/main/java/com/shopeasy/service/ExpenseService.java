
package com.shopeasy.service;

import com.shopeasy.dao.ExpenseDAO;
import com.shopeasy.model.Expense;
import com.shopeasy.model.ExpenseSummary;
import com.shopeasy.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class ExpenseService {

    private final ExpenseDAO expenseDAO = new ExpenseDAO();

    private static final String[] CATEGORIES = {
            "Shop Rent",
            "Electricity Bill",
            "Water Bill",
            "Internet / Wi-Fi",
            "Employee Salary",
            "Transportation",
            "Packaging Materials",
            "Maintenance & Repair",
            "Tea & Refreshments",
            "Marketing & Advertising",
            "Office Supplies",
            "Other Expenses"
    };

    private static final String[] PAYMENT_METHODS = {
            "CASH",
            "UPI",
            "CARD",
            "BANK"
    };


    // ADD EXPENSE
    public int addExpense(Expense expense) throws SQLException {

        validateExpense(expense);

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                int expenseId = expenseDAO.addExpense(con, expense);

                con.commit();

                return expenseId;

            } catch (SQLException | RuntimeException e) {

                con.rollback();
                throw e;
            }
        }
    }


    // GET ALL SHOP EXPENSES
    public List<Expense> getExpenses(int shopId)
            throws SQLException {

        validateShopId(shopId);

        try (Connection con = DBConnection.getConnection()) {

            return expenseDAO.getExpensesByShopId(con, shopId);
        }
    }


    // GET EXPENSE BY ID
    public Expense getExpenseById(int expenseId, int shopId)
            throws SQLException {

        validateShopId(shopId);

        if (expenseId <= 0) {
            throw new IllegalArgumentException("Invalid expense ID.");
        }

        try (Connection con = DBConnection.getConnection()) {

            return expenseDAO.getExpenseById(
                    con, expenseId, shopId);
        }
    }


    // UPDATE EXPENSE
    public boolean updateExpense(Expense expense)
            throws SQLException {

        validateExpense(expense);
        validateShopId(expense.getShopId());

        if (expense.getExpenseId() <= 0) {
            throw new IllegalArgumentException("Invalid expense ID.");
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                boolean updated = expenseDAO.updateExpense(
                        con, expense);

                con.commit();

                return updated;

            } catch (SQLException | RuntimeException e) {

                con.rollback();
                throw e;
            }
        }
    }


    // DELETE EXPENSE (SOFT DELETE)
    public boolean deleteExpense(int expenseId, int shopId)
            throws SQLException {

        validateShopId(shopId);

        if (expenseId <= 0) {
            throw new IllegalArgumentException("Invalid expense ID.");
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                boolean deleted = expenseDAO.deleteExpense(
                        con, expenseId, shopId);

                con.commit();

                return deleted;

            } catch (SQLException | RuntimeException e) {

                con.rollback();
                throw e;
            }
        }
    }


    // VALIDATE EXPENSE DETAILS
    private void validateExpense(Expense expense) {

        if (expense == null) {
            throw new IllegalArgumentException(
                    "Expense details are required.");
        }

        validateShopId(expense.getShopId());

        if (expense.getExpenseTitle() == null ||
                expense.getExpenseTitle().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Expense title is required.");
        }

        if (expense.getExpenseTitle().trim().length() > 150) {
            throw new IllegalArgumentException(
                    "Expense title cannot exceed 150 characters.");
        }

        if (expense.getCategory() == null ||
                !isValidCategory(expense.getCategory())) {

            throw new IllegalArgumentException(
                    "Please select a valid expense category.");
        }

        if (expense.getAmount() <= 0 ||
                Double.isNaN(expense.getAmount()) ||
                Double.isInfinite(expense.getAmount())) {

            throw new IllegalArgumentException(
                    "Expense amount must be greater than zero.");
        }

        if (expense.getPaymentMethod() == null ||
                !isValidPaymentMethod(expense.getPaymentMethod())) {

            throw new IllegalArgumentException(
                    "Please select a valid payment method.");
        }

        if (expense.getExpenseDate() == null) {
            throw new IllegalArgumentException(
                    "Expense date is required.");
        }

        if (expense.getDescription() != null &&
                expense.getDescription().length() > 500) {

            throw new IllegalArgumentException(
                    "Description cannot exceed 500 characters.");
        }
    }


    // VALIDATE SHOP ID
    private void validateShopId(int shopId) {

        if (shopId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid shop session.");
        }
    }


    // CHECK CATEGORY
    private boolean isValidCategory(String category) {

        for (String validCategory : CATEGORIES) {

            if (validCategory.equals(category.trim())) {
                return true;
            }
        }

        return false;
    }


    // CHECK PAYMENT METHOD
    private boolean isValidPaymentMethod(String paymentMethod) {

        for (String validMethod : PAYMENT_METHODS) {

            if (validMethod.equalsIgnoreCase(paymentMethod.trim())) {
                return true;
            }
        }

        return false;
    }

    public List<Expense> getExpenses(
            int shopId,
            Date fromDate,
            Date toDate) throws SQLException {

        if (shopId <= 0) {
            throw new IllegalArgumentException("Invalid shop.");
        }

        if ((fromDate == null && toDate != null) ||
                (fromDate != null && toDate == null)) {
            throw new IllegalArgumentException("Both dates are required.");
        }

        if (fromDate != null && fromDate.after(toDate)) {
            throw new IllegalArgumentException("From date cannot be after to date.");
        }

        try (Connection connection = DBConnection.getConnection()) {
            return expenseDAO.getExpensesByShopId(
                    connection, shopId, fromDate, toDate);
        }
    }

    public ExpenseSummary getExpenseSummary(int shopId) throws SQLException {

        if (shopId <= 0) {
            throw new IllegalArgumentException("Invalid shop.");
        }

        try (Connection connection = DBConnection.getConnection()) {
            return expenseDAO.getExpenseSummary(connection, shopId);
        }
    }
}