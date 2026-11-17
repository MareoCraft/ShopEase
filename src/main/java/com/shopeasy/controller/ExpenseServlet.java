
package com.shopeasy.controller;

import com.shopeasy.model.Expense;
import com.shopeasy.model.ExpenseSummary;
import com.shopeasy.service.ExpenseService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet("/expenses")
public class ExpenseServlet extends HttpServlet {

    private ExpenseService expenseService;

    @Override
    public void init() throws ServletException {
        expenseService = new ExpenseService();
    }


    // GET: DISPLAY EXPENSES
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        Integer shopId = getShopId(request);

        if (shopId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {

            String period = request.getParameter("period");

            if (period == null || period.trim().isEmpty()) {
                period = "ALL";
            }

            LocalDate today = LocalDate.now();
            LocalDate from = null;
            LocalDate to = null;

            switch (period.toUpperCase()) {

                case "TODAY":
                    from = today;
                    to = today;
                    break;

                case "YESTERDAY":
                    from = today.minusDays(1);
                    to = today.minusDays(1);
                    break;

                case "LAST_7_DAYS":
                    from = today.minusDays(6);
                    to = today;
                    break;

                case "THIS_MONTH":
                    from = today.withDayOfMonth(1);
                    to = today;
                    break;

                case "LAST_MONTH":
                    LocalDate previousMonth = today.minusMonths(1);
                    from = previousMonth.withDayOfMonth(1);
                    to = previousMonth.withDayOfMonth(
                            previousMonth.lengthOfMonth());
                    break;

                case "CUSTOM":

                    String fromValue = request.getParameter("fromDate");
                    String toValue = request.getParameter("toDate");

                    if (fromValue == null || fromValue.isEmpty()
                            || toValue == null || toValue.isEmpty()) {

                        throw new IllegalArgumentException(
                                "Please select both start and end dates.");
                    }

                    from = LocalDate.parse(fromValue);
                    to = LocalDate.parse(toValue);
                    break;

                case "ALL":
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Invalid date filter.");
            }

            Date sqlFrom = from == null ? null : Date.valueOf(from);
            Date sqlTo = to == null ? null : Date.valueOf(to);

            if (from != null && from.isAfter(to)) {
                throw new IllegalArgumentException(
                        "Start date cannot be after end date.");
            }

            // =====================================
            // DEBUG: EXPENSE RETRIEVAL
            // =====================================

            List<Expense> expenseList =
                    expenseService.getExpenses(shopId, sqlFrom, sqlTo);

            request.setAttribute("expenses", expenseList);


            // LOAD EXPENSE SUMMARY
            ExpenseSummary summary =
                    expenseService.getExpenseSummary(shopId);

            request.setAttribute("expenseSummary", summary);
            request.setAttribute("selectedPeriod", period.toUpperCase());
            request.setAttribute("fromDate", from);
            request.setAttribute("toDate", to);


            // LOAD EXPENSE FOR EDITING
            String editId = request.getParameter("editId");

            if (editId != null && !editId.trim().isEmpty()) {

                Expense expense = expenseService.getExpenseById(
                        Integer.parseInt(editId), shopId);

                if (expense != null) {
                    request.setAttribute("editExpense", expense);
                }
            }


            request.getRequestDispatcher("/expenses.jsp")
                    .forward(request, response);

        } catch (NumberFormatException | DateTimeParseException e) {

            request.setAttribute(
                    "errorMessage",
                    "Invalid date or expense ID.");

            loadExpensePage(request, response, shopId);

        } catch (IllegalArgumentException e) {

            request.setAttribute("errorMessage", e.getMessage());

            loadExpensePage(request, response, shopId);

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to load expenses.", e);
        }
    }


    // POST: ADD, UPDATE OR DELETE EXPENSE
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        Integer shopId = getShopId(request);

        if (shopId == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        try {

            // ADD EXPENSE
            if ("add".equals(action)) {

                Expense expense = createExpenseFromRequest(
                        request, shopId);

                expenseService.addExpense(expense);

                response.sendRedirect(
                        request.getContextPath()
                                + "/expenses?success=added");

                return;
            }


            // UPDATE EXPENSE
            if ("update".equals(action)) {

                int expenseId = Integer.parseInt(
                        request.getParameter("expenseId"));

                Expense expense = createExpenseFromRequest(
                        request, shopId);

                expense.setExpenseId(expenseId);

                boolean updated =
                        expenseService.updateExpense(expense);

                if (updated) {

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/expenses?success=updated");

                } else {

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/expenses?error=notfound");
                }

                return;
            }


            // DELETE EXPENSE
            if ("delete".equals(action)) {

                int expenseId = Integer.parseInt(
                        request.getParameter("expenseId"));

                boolean deleted =
                        expenseService.deleteExpense(
                                expenseId, shopId);

                if (deleted) {

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/expenses?success=deleted");

                } else {

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/expenses?error=notfound");
                }

                return;
            }


            response.sendRedirect(
                    request.getContextPath()
                            + "/expenses?error=invalid");

        } catch (IllegalArgumentException e) {

            loadExpensePage(request, response, shopId,
                    e.getMessage());

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to process expense.", e);
        }
    }


    // CREATE EXPENSE OBJECT FROM FORM DATA
    private Expense createExpenseFromRequest(
            HttpServletRequest request, int shopId) {

        String title = request.getParameter("expenseTitle");
        String category = request.getParameter("category");
        String amountValue = request.getParameter("amount");
        String paymentMethod = request.getParameter("paymentMethod");
        String expenseDateValue = request.getParameter("expenseDate");
        String description = request.getParameter("description");

        if (amountValue == null || amountValue.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Expense amount is required.");
        }

        if (expenseDateValue == null ||
                expenseDateValue.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Expense date is required.");
        }

        double amount = Double.parseDouble(amountValue);

        Date expenseDate = Date.valueOf(expenseDateValue);

        Expense expense = new Expense();

        // ALWAYS USE AUTHENTICATED SESSION SHOP ID
        expense.setShopId(shopId);

        expense.setExpenseTitle(title);
        expense.setCategory(category);
        expense.setAmount(amount);
        expense.setPaymentMethod(paymentMethod);
        expense.setExpenseDate(expenseDate);
        expense.setDescription(description);

        return expense;
    }


    // LOAD EXPENSE PAGE AFTER VALIDATION ERROR
    private void loadExpensePage(
            HttpServletRequest request,
            HttpServletResponse response,
            int shopId) throws ServletException, IOException {

        try {

            request.setAttribute(
                    "expenses",
                    expenseService.getExpenses(shopId));

            ExpenseSummary summary =
                    expenseService.getExpenseSummary(shopId);

            request.setAttribute("expenseSummary", summary);
            request.setAttribute("selectedPeriod", "ALL");

            request.getRequestDispatcher("/expenses.jsp")
                    .forward(request, response);

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to reload expenses.", e);
        }
    }


    // LOAD EXPENSE PAGE AFTER VALIDATION ERROR WITH MESSAGE
    private void loadExpensePage(
            HttpServletRequest request,
            HttpServletResponse response,
            int shopId,
            String errorMessage) throws ServletException, IOException {

        request.setAttribute("errorMessage", errorMessage);

        loadExpensePage(request, response, shopId);
    }


    // GET AUTHENTICATED SHOP ID FROM SESSION
    private Integer getShopId(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        Object shopId = session.getAttribute("shopId");

        if (shopId instanceof Number) {
            return ((Number) shopId).intValue();
        }

        return null;
    }
}