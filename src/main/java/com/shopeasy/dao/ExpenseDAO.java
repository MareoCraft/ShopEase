
package com.shopeasy.dao;

import com.shopeasy.model.Expense;
import com.shopeasy.model.ExpenseSummary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    // ADD NEW EXPENSE
    public int addExpense(Connection con, Expense expense)
            throws SQLException {

        String idQuery = "SELECT EXPENSE_SEQ.NEXTVAL FROM DUAL";

        int expenseId;

        try (PreparedStatement ps = con.prepareStatement(idQuery);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                expenseId = rs.getInt(1);
            } else {
                throw new SQLException("Unable to generate Expense ID.");
            }
        }

        String sql =
                "INSERT INTO SHOP_EXPENSES (" +
                        "EXPENSE_ID, SHOP_ID, EXPENSE_TITLE, CATEGORY, " +
                        "AMOUNT, PAYMENT_METHOD, EXPENSE_DATE, DESCRIPTION" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, expenseId);
            ps.setInt(2, expense.getShopId());
            ps.setString(3, expense.getExpenseTitle());
            ps.setString(4, expense.getCategory());
            ps.setDouble(5, expense.getAmount());
            ps.setString(6, expense.getPaymentMethod());
            ps.setDate(7, expense.getExpenseDate());
            ps.setString(8, expense.getDescription());

            ps.executeUpdate();
        }

        return expenseId;
    }


    // GET ALL ACTIVE EXPENSES OF A SHOP
    public List<Expense> getExpensesByShopId(
            Connection con, int shopId) throws SQLException {

        List<Expense> expenses = new ArrayList<>();

        String sql =
                "SELECT * FROM SHOP_EXPENSES " +
                        "WHERE SHOP_ID = ? " +
                        "AND STATUS = 'ACTIVE' " +
                        "ORDER BY EXPENSE_DATE DESC, EXPENSE_ID DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    expenses.add(mapExpense(rs));
                }
            }
        }

        return expenses;
    }


    // GET ONE EXPENSE BY ID
    public Expense getExpenseById(
            Connection con, int expenseId, int shopId)
            throws SQLException {

        String sql =
                "SELECT * FROM SHOP_EXPENSES " +
                        "WHERE EXPENSE_ID = ? " +
                        "AND SHOP_ID = ? " +
                        "AND STATUS = 'ACTIVE'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, expenseId);
            ps.setInt(2, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapExpense(rs);
                }
            }
        }

        return null;
    }


    // UPDATE EXPENSE
    public boolean updateExpense(
            Connection con, Expense expense) throws SQLException {

        String sql =
                "UPDATE SHOP_EXPENSES SET " +
                        "EXPENSE_TITLE = ?, " +
                        "CATEGORY = ?, " +
                        "AMOUNT = ?, " +
                        "PAYMENT_METHOD = ?, " +
                        "EXPENSE_DATE = ?, " +
                        "DESCRIPTION = ?, " +
                        "UPDATED_DATE = SYSDATE " +
                        "WHERE EXPENSE_ID = ? " +
                        "AND SHOP_ID = ? " +
                        "AND STATUS = 'ACTIVE'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, expense.getExpenseTitle());
            ps.setString(2, expense.getCategory());
            ps.setDouble(3, expense.getAmount());
            ps.setString(4, expense.getPaymentMethod());
            ps.setDate(5, expense.getExpenseDate());
            ps.setString(6, expense.getDescription());
            ps.setInt(7, expense.getExpenseId());
            ps.setInt(8, expense.getShopId());

            return ps.executeUpdate() > 0;
        }
    }


    // SOFT DELETE EXPENSE
    public boolean deleteExpense(
            Connection con, int expenseId, int shopId)
            throws SQLException {

        String sql =
                "UPDATE SHOP_EXPENSES SET " +
                        "STATUS = 'INACTIVE', " +
                        "UPDATED_DATE = SYSDATE " +
                        "WHERE EXPENSE_ID = ? " +
                        "AND SHOP_ID = ? " +
                        "AND STATUS = 'ACTIVE'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, expenseId);
            ps.setInt(2, shopId);

            return ps.executeUpdate() > 0;
        }
    }


    // MAP RESULT SET TO EXPENSE MODEL
    private Expense mapExpense(ResultSet rs) throws SQLException {

        Expense expense = new Expense();

        expense.setExpenseId(rs.getInt("EXPENSE_ID"));
        expense.setShopId(rs.getInt("SHOP_ID"));
        expense.setExpenseTitle(rs.getString("EXPENSE_TITLE"));
        expense.setCategory(rs.getString("CATEGORY"));
        expense.setAmount(rs.getDouble("AMOUNT"));
        expense.setPaymentMethod(rs.getString("PAYMENT_METHOD"));
        expense.setExpenseDate(rs.getDate("EXPENSE_DATE"));
        expense.setDescription(rs.getString("DESCRIPTION"));
        expense.setCreatedDate(rs.getDate("CREATED_DATE"));
        expense.setUpdatedDate(rs.getDate("UPDATED_DATE"));
        expense.setStatus(rs.getString("STATUS"));

        return expense;
    }

    public List<Expense> getExpensesByShopId(
            Connection connection,
            int shopId,
            Date fromDate,
            Date toDate) throws SQLException {

        if (fromDate == null && toDate == null) {
            return getExpensesByShopId(connection, shopId);
        }

        List<Expense> expenses = new ArrayList<>();

        String sql = "SELECT * FROM SHOP_EXPENSES " +
                "WHERE SHOP_ID = ? AND STATUS = 'ACTIVE' " +
                "AND EXPENSE_DATE >= ? " +
                "AND EXPENSE_DATE < ? + 1 " +
                "ORDER BY EXPENSE_DATE DESC, EXPENSE_ID DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, shopId);
            ps.setDate(2, fromDate);
            ps.setDate(3, toDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapExpense(rs));
                }
            }
        }

        return expenses;
    }

    public ExpenseSummary getExpenseSummary(
            Connection connection,
            int shopId) throws SQLException {

        ExpenseSummary summary = new ExpenseSummary();

        String sql = "SELECT " +
                "NVL(SUM(CASE WHEN EXPENSE_DATE >= TRUNC(SYSDATE) " +
                "AND EXPENSE_DATE < TRUNC(SYSDATE) + 1 " +
                "THEN AMOUNT ELSE 0 END), 0) AS TODAY_EXPENSES, " +

                "NVL(SUM(CASE WHEN EXPENSE_DATE >= TRUNC(SYSDATE, 'MM') " +
                "AND EXPENSE_DATE < ADD_MONTHS(TRUNC(SYSDATE, 'MM'), 1) " +
                "THEN AMOUNT ELSE 0 END), 0) AS MONTHLY_EXPENSES, " +

                "NVL(SUM(AMOUNT), 0) AS TOTAL_EXPENSES " +

                "FROM SHOP_EXPENSES " +
                "WHERE SHOP_ID = ? AND STATUS = 'ACTIVE'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    summary.setTodayExpenses(rs.getDouble("TODAY_EXPENSES"));
                    summary.setMonthlyExpenses(rs.getDouble("MONTHLY_EXPENSES"));
                    summary.setTotalExpenses(rs.getDouble("TOTAL_EXPENSES"));
                }
            }
        }

        return summary;
    }
}
