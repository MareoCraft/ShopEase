
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    if (request.getAttribute("expenses") == null) {
        response.sendRedirect(request.getContextPath() + "/expenses");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>ShopEase | Expense Management</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/expenses.css?v=2">

    <script src="https://unpkg.com/lucide@latest"></script>
</head>

<body>

<div class="expense-container">

    <!-- PAGE HEADER -->
    <header class="expense-header">

        <div>
            <h1>Expense Management</h1>
            <p>Track and manage your shop expenses</p>
        </div>

        <a href="${pageContext.request.contextPath}/dashboard"
           class="back-btn">
            <i data-lucide="arrow-left"></i>
            Dashboard
        </a>

    </header>


    <!-- SUCCESS MESSAGES -->
    <c:if test="${param.success == 'added'}">
        <div class="alert alert-success">
            <i data-lucide="check-circle"></i>
            Expense added successfully.
        </div>
    </c:if>

    <c:if test="${param.success == 'updated'}">
        <div class="alert alert-success">
            <i data-lucide="check-circle"></i>
            Expense updated successfully.
        </div>
    </c:if>

    <c:if test="${param.success == 'deleted'}">
        <div class="alert alert-success">
            <i data-lucide="check-circle"></i>
            Expense deleted successfully.
        </div>
    </c:if>

    <c:if test="${param.error == 'notfound'}">
        <div class="alert alert-error">
            <i data-lucide="alert-circle"></i>
            Expense not found or already deleted.
        </div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">
            <i data-lucide="alert-circle"></i>
            <c:out value="${errorMessage}"/>
        </div>
    </c:if>


    <!-- EXPENSE SUMMARY -->
    <section class="expense-summary">

        <div class="summary-card">

            <div class="summary-icon">
                <i data-lucide="calendar-days"></i>
            </div>

            <div>
                <p>Today's Expenses</p>
                <h2>
                    ₹<fmt:formatNumber
                        value="${expenseSummary.todayExpenses}"
                        minFractionDigits="2"
                        maxFractionDigits="2"/>
                </h2>
            </div>

        </div>


        <div class="summary-card">

            <div class="summary-icon">
                <i data-lucide="calendar-range"></i>
            </div>

            <div>
                <p>This Month</p>
                <h2>
                    ₹<fmt:formatNumber
                        value="${expenseSummary.monthlyExpenses}"
                        minFractionDigits="2"
                        maxFractionDigits="2"/>
                </h2>
            </div>

        </div>


        <div class="summary-card">

            <div class="summary-icon">
                <i data-lucide="wallet"></i>
            </div>

            <div>
                <p>Total Expenses</p>
                <h2>
                    ₹<fmt:formatNumber
                        value="${expenseSummary.totalExpenses}"
                        minFractionDigits="2"
                        maxFractionDigits="2"/>
                </h2>
            </div>

        </div>

    </section>


    <!-- EXPENSE DATE FILTER -->
    <section class="expense-filter-card">

        <form action="${pageContext.request.contextPath}/expenses"
              method="get"
              class="expense-filter-form">

            <div class="filter-group">

                <label for="period">Filter Expenses</label>

                <select name="period"
                        id="period"
                        onchange="toggleCustomDates()">

                    <option value="ALL"
                        ${selectedPeriod == 'ALL' ? 'selected' : ''}>
                        All Expenses
                    </option>

                    <option value="TODAY"
                        ${selectedPeriod == 'TODAY' ? 'selected' : ''}>
                        Today
                    </option>

                    <option value="YESTERDAY"
                        ${selectedPeriod == 'YESTERDAY' ? 'selected' : ''}>
                        Yesterday
                    </option>

                    <option value="LAST_7_DAYS"
                        ${selectedPeriod == 'LAST_7_DAYS' ? 'selected' : ''}>
                        Last 7 Days
                    </option>

                    <option value="THIS_MONTH"
                        ${selectedPeriod == 'THIS_MONTH' ? 'selected' : ''}>
                        This Month
                    </option>

                    <option value="LAST_MONTH"
                        ${selectedPeriod == 'LAST_MONTH' ? 'selected' : ''}>
                        Last Month
                    </option>

                    <option value="CUSTOM"
                        ${selectedPeriod == 'CUSTOM' ? 'selected' : ''}>
                        Custom Date Range
                    </option>

                </select>

            </div>


            <!-- CUSTOM DATE FIELDS -->
            <div class="custom-date-fields" id="customDateFields">

                <div class="filter-group">

                    <label for="fromDate">From Date</label>

                    <input type="date"
                           name="fromDate"
                           id="fromDate"
                           value="${fromDate}">

                </div>


                <div class="filter-group">

                    <label for="toDate">To Date</label>

                    <input type="date"
                           name="toDate"
                           id="toDate"
                           value="${toDate}">

                </div>

            </div>


            <button type="submit" class="filter-btn">
                <i data-lucide="filter"></i>
                Apply Filter
            </button>


            <a href="${pageContext.request.contextPath}/expenses"
               class="reset-filter-btn">

                <i data-lucide="rotate-ccw"></i>
                Reset

            </a>

        </form>

    </section>


    <!-- ADD / EDIT EXPENSE -->
    <section class="expense-form-card">

        <div class="card-heading">

            <div>

                <h2>
                    <c:choose>
                        <c:when test="${not empty editExpense}">
                            Edit Expense
                        </c:when>

                        <c:otherwise>
                            Add New Expense
                        </c:otherwise>
                    </c:choose>
                </h2>

                <p>Enter your shop expense details below</p>

            </div>

            <div class="heading-icon">
                <i data-lucide="receipt"></i>
            </div>

        </div>


        <form action="${pageContext.request.contextPath}/expenses"
              method="post">

            <input type="hidden"
                   name="action"
                   value="${not empty editExpense ? 'update' : 'add'}">


            <c:if test="${not empty editExpense}">
                <input type="hidden"
                       name="expenseId"
                       value="${editExpense.expenseId}">
            </c:if>


            <div class="expense-form-grid">


                <!-- EXPENSE TITLE -->
                <div class="form-group">

                    <label for="expenseTitle">
                        Expense Title *
                    </label>

                    <input type="text"
                           id="expenseTitle"
                           name="expenseTitle"
                           maxlength="150"
                           placeholder="e.g. Electricity Bill"
                           value="<c:out value='${editExpense.expenseTitle}'/>"
                           required>

                </div>


                <!-- CATEGORY -->
                <div class="form-group">

                    <label for="category">
                        Expense Category *
                    </label>

                    <select id="category"
                            name="category"
                            required>

                        <option value="">Select Category</option>

                        <option value="Shop Rent"
                            ${editExpense.category == 'Shop Rent' ? 'selected' : ''}>
                            Shop Rent
                        </option>

                        <option value="Electricity Bill"
                            ${editExpense.category == 'Electricity Bill' ? 'selected' : ''}>
                            Electricity Bill
                        </option>

                        <option value="Water Bill"
                            ${editExpense.category == 'Water Bill' ? 'selected' : ''}>
                            Water Bill
                        </option>

                        <option value="Internet / Wi-Fi"
                            ${editExpense.category == 'Internet / Wi-Fi' ? 'selected' : ''}>
                            Internet / Wi-Fi
                        </option>

                        <option value="Employee Salary"
                            ${editExpense.category == 'Employee Salary' ? 'selected' : ''}>
                            Employee Salary
                        </option>

                        <option value="Transportation"
                            ${editExpense.category == 'Transportation' ? 'selected' : ''}>
                            Transportation
                        </option>

                        <option value="Packaging Materials"
                            ${editExpense.category == 'Packaging Materials' ? 'selected' : ''}>
                            Packaging Materials
                        </option>

                        <option value="Maintenance & Repair"
                            ${editExpense.category == 'Maintenance & Repair' ? 'selected' : ''}>
                            Maintenance & Repair
                        </option>

                        <option value="Tea & Refreshments"
                            ${editExpense.category == 'Tea & Refreshments' ? 'selected' : ''}>
                            Tea & Refreshments
                        </option>

                        <option value="Marketing & Advertising"
                            ${editExpense.category == 'Marketing & Advertising' ? 'selected' : ''}>
                            Marketing & Advertising
                        </option>

                        <option value="Office Supplies"
                            ${editExpense.category == 'Office Supplies' ? 'selected' : ''}>
                            Office Supplies
                        </option>

                        <option value="Other Expenses"
                            ${editExpense.category == 'Other Expenses' ? 'selected' : ''}>
                            Other Expenses
                        </option>

                    </select>

                </div>


                <!-- AMOUNT -->
                <div class="form-group">

                    <label for="amount">
                        Amount (₹) *
                    </label>

                    <input type="number"
                           id="amount"
                           name="amount"
                           min="0.01"
                           step="0.01"
                           placeholder="Enter amount"
                           value="${editExpense.amount}"
                           required>

                </div>


                <!-- PAYMENT METHOD -->
                <div class="form-group">

                    <label for="paymentMethod">
                        Payment Method *
                    </label>

                    <select id="paymentMethod"
                            name="paymentMethod"
                            required>

                        <option value="">Select Payment Method</option>

                        <option value="CASH"
                            ${editExpense.paymentMethod == 'CASH' ? 'selected' : ''}>
                            Cash
                        </option>

                        <option value="UPI"
                            ${editExpense.paymentMethod == 'UPI' ? 'selected' : ''}>
                            UPI
                        </option>

                        <option value="CARD"
                            ${editExpense.paymentMethod == 'CARD' ? 'selected' : ''}>
                            Card
                        </option>

                        <option value="BANK"
                            ${editExpense.paymentMethod == 'BANK' ? 'selected' : ''}>
                            Bank Transfer
                        </option>

                    </select>

                </div>


                <!-- EXPENSE DATE -->
                <div class="form-group">

                    <label for="expenseDate">
                        Expense Date *
                    </label>

                    <input type="date"
                           id="expenseDate"
                           name="expenseDate"
                           value="<fmt:formatDate value='${editExpense.expenseDate}' pattern='yyyy-MM-dd'/>"
                           required>

                </div>


                <!-- DESCRIPTION -->
                <div class="form-group full-width">

                    <label for="description">
                        Description
                    </label>

                    <textarea id="description"
                              name="description"
                              maxlength="500"
                              rows="3"
                              placeholder="Additional expense details..."><c:out value="${editExpense.description}"/></textarea>

                </div>

            </div>


            <!-- FORM ACTIONS -->
            <div class="form-actions">

                <c:if test="${not empty editExpense}">

                    <a href="${pageContext.request.contextPath}/expenses"
                       class="cancel-btn">
                        Cancel
                    </a>

                </c:if>


                <button type="submit" class="submit-btn">

                    <i data-lucide="${not empty editExpense ? 'save' : 'plus-circle'}"></i>

                    <c:choose>
                        <c:when test="${not empty editExpense}">
                            Update Expense
                        </c:when>

                        <c:otherwise>
                            Add Expense
                        </c:otherwise>
                    </c:choose>

                </button>

            </div>

        </form>

    </section>


    <!-- EXPENSE HISTORY -->
    <section class="expense-history-card">

        <div class="history-heading">

            <div>
                <h2>Expense History</h2>
                <p>View and manage your recorded expenses</p>
            </div>

            <div class="expense-count">

                <span id="expenseCount">
                    <c:out value="${expenses.size()}"/>
                </span>

                Records

            </div>

        </div>


        <!-- SEARCH -->
        <div class="expense-search">

            <i data-lucide="search"></i>

            <input type="text"
                   id="expenseSearch"
                   placeholder="Search expense title or category..."
                   oninput="filterExpenses()">

            <button type="button"
                    id="clearSearch"
                    onclick="clearExpenseSearch()"
                    title="Clear search">

                <i data-lucide="x"></i>

            </button>

        </div>


        <!-- TABLE -->
        <div class="expense-table-wrapper">

            <table class="expense-table">

                <thead>
                    <tr>
                        <th>#</th>
                        <th>Expense Details</th>
                        <th>Category</th>
                        <th>Date</th>
                        <th>Payment</th>
                        <th>Amount</th>
                        <th>Actions</th>
                    </tr>
                </thead>


                <tbody id="expenseTableBody">

                    <c:choose>

                        <c:when test="${not empty expenses}">

                            <c:forEach var="expense"
                                       items="${expenses}"
                                       varStatus="loop">

                                <tr data-expense-row
                                    data-title="${expense.expenseTitle}"
                                    data-category="${expense.category}">

                                    <td>
                                        ${loop.index + 1}
                                    </td>


                                    <td>

                                        <strong>
                                            <c:out value="${expense.expenseTitle}"/>
                                        </strong>

                                        <c:if test="${not empty expense.description}">

                                            <small>
                                                <c:out value="${expense.description}"/>
                                            </small>

                                        </c:if>

                                    </td>


                                    <td>

                                        <span class="category-badge">
                                            <c:out value="${expense.category}"/>
                                        </span>

                                    </td>


                                    <td>

                                        <fmt:formatDate
                                                value="${expense.expenseDate}"
                                                pattern="dd MMM yyyy"/>

                                    </td>


                                    <td>

                                        <span class="payment-badge">
                                            <c:out value="${expense.paymentMethod}"/>
                                        </span>

                                    </td>


                                    <td>

                                        <strong class="expense-amount">

                                            ₹<fmt:formatNumber
                                                value="${expense.amount}"
                                                minFractionDigits="2"
                                                maxFractionDigits="2"/>

                                        </strong>

                                    </td>


                                    <td>

                                        <div class="action-buttons">

                                            <!-- EDIT -->
                                            <a href="${pageContext.request.contextPath}/expenses?editId=${expense.expenseId}"
                                               class="edit-btn"
                                               title="Edit Expense">

                                                <i data-lucide="pencil"></i>

                                            </a>


                                            <!-- DELETE -->
                                            <form action="${pageContext.request.contextPath}/expenses"
                                                  method="post"
                                                  onsubmit="return confirm('Are you sure you want to delete this expense?');">

                                                <input type="hidden"
                                                       name="action"
                                                       value="delete">

                                                <input type="hidden"
                                                       name="expenseId"
                                                       value="${expense.expenseId}">

                                                <button type="submit"
                                                        class="delete-btn"
                                                        title="Delete Expense">

                                                    <i data-lucide="trash-2"></i>

                                                </button>

                                            </form>

                                        </div>

                                    </td>

                                </tr>

                            </c:forEach>

                        </c:when>


                        <c:otherwise>

                            <tr>
                                <td colspan="7" class="empty-state">

                                    <i data-lucide="receipt-text"></i>

                                    <strong>No Expenses Found</strong>

                                    <p>No expenses found for the selected period.</p>

                                </td>
                            </tr>

                        </c:otherwise>

                    </c:choose>


                    <!-- SEARCH EMPTY STATE -->
                    <tr id="noSearchResults" style="display:none;">

                        <td colspan="7" class="empty-state">
                            No matching expenses found.
                        </td>

                    </tr>

                </tbody>

            </table>

        </div>

    </section>

</div>


<!-- JAVASCRIPT -->
<script>

    // CUSTOM DATE FILTER
    function toggleCustomDates() {

        const period = document.getElementById("period").value;

        const customFields =
            document.getElementById("customDateFields");

        customFields.style.display =
            period === "CUSTOM" ? "flex" : "none";

        const fromDate = document.getElementById("fromDate");
        const toDate = document.getElementById("toDate");

        fromDate.required = period === "CUSTOM";
        toDate.required = period === "CUSTOM";
    }


    // EXPENSE SEARCH
    function filterExpenses() {

        const searchInput =
            document.getElementById("expenseSearch");

        const searchValue =
            searchInput.value.toLowerCase().trim();

        const rows =
            document.querySelectorAll("[data-expense-row]");

        const count =
            document.getElementById("expenseCount");

        const noResults =
            document.getElementById("noSearchResults");

        let visibleCount = 0;

        rows.forEach(function(row) {

            const title =
                (row.dataset.title || "").toLowerCase();

            const category =
                (row.dataset.category || "").toLowerCase();

            const matches =
                title.includes(searchValue) ||
                category.includes(searchValue);

            row.style.display = matches ? "" : "none";

            if (matches) {
                visibleCount++;
            }

        });

        count.textContent = visibleCount;

        if (noResults) {
            noResults.style.display =
                visibleCount === 0 && rows.length > 0 ? "" : "none";
        }

    }


    // CLEAR SEARCH
    function clearExpenseSearch() {

        document.getElementById("expenseSearch").value = "";

        filterExpenses();

    }


    // INITIALIZE PAGE
    document.addEventListener("DOMContentLoaded", function() {

        toggleCustomDates();

        if (window.lucide) {
            lucide.createIcons();
        }

    });

</script>

</body>
</html>