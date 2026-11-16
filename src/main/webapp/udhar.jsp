
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Udhar Management | ShopEase</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/udhar.css?v=2">

    <!-- Lucide Icons -->
    <script src="https://unpkg.com/lucide@latest"></script>
</head>

<body>

<div class="udhar-container">

    <!-- HEADER -->
    <header class="page-header">
        <div>
            <h1>
                <i data-lucide="wallet" class="heading-icon"></i>
                Udhar Management
            </h1>
            <p>Track outstanding customer credit and collect payments.</p>
        </div>

        <a href="${pageContext.request.contextPath}/dashboard"
           class="back-btn">
            <i data-lucide="layout-dashboard"></i>
            Dashboard
        </a>
    </header>


    <!-- MESSAGES -->
    <c:if test="${param.success eq 'collected'}">
        <div class="success-message">
            <i data-lucide="check-circle"></i>
            Payment collected successfully and outstanding balance updated.
        </div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="error-message">
            <i data-lucide="alert-circle"></i>
            <c:out value="${error}"/>
        </div>
    </c:if>


    <!-- SUMMARY -->
    <section class="summary-grid">

        <div class="summary-card">
            <div class="summary-icon">
                <i data-lucide="wallet"></i>
            </div>

            <span>Total Outstanding Udhar</span>

            <h2>
                ₹ <fmt:formatNumber value="${totalOutstanding}"
                                    minFractionDigits="2"
                                    maxFractionDigits="2"/>
            </h2>

            <p>Pending customer payments</p>
        </div>

        <div class="summary-card">
            <div class="summary-icon bills-icon">
                <i data-lucide="receipt-text"></i>
            </div>

            <span>Outstanding Bills</span>

            <h2>
                <c:out value="${outstandingSales.size()}"/>
            </h2>

            <p>Unpaid and partially paid bills</p>
        </div>

    </section>


    <!-- OUTSTANDING BILLS -->
    <section class="udhar-card">

        <div class="section-heading">
            <div>
                <h2>Outstanding Credit Bills</h2>
                <p>Collect payments against existing bills.</p>
            </div>

            <!-- LIVE SEARCH -->
                    <div class="udhar-search">

                        <i data-lucide="search"></i>

                        <input
                            type="search"
                            id="udharSearch"
                            placeholder="Search by customer name or mobile number..."
                            autocomplete="off">

                        <button type="button" id="clearUdharSearch">
                            Clear
                        </button>

                    </div>
        </div>

        <div class="table-wrapper">

            <table class="udhar-table">

                <thead>
                <tr>
                    <th>Bill Number</th>
                    <th>Customer</th>
                    <th>Mobile</th>
                    <th>Bill Date</th>
                    <th>Bill Amount</th>
                    <th>Paid Amount</th>
                    <th>Balance Due</th>
                    <th>Status</th>
                    <th>Collect Payment</th>
                    <th>Invoice</th>
                </tr>
                </thead>

                <tbody>

                <c:forEach var="sale" items="${outstandingSales}">

                    <tr data-udhar-row
                        data-customer-name="<c:out value='${sale.customerName}'/>"
                        data-customer-mobile="<c:out value='${sale.customerMobile}'/>">

                        <td class="bill-number">
                            <c:out value="${sale.billNumber}"/>
                        </td>

                        <td>
                            <strong class="udhar-customer-name">
                                <c:out value="${sale.customerName}"
                                       default="Unknown Customer"/>
                            </strong>
                        </td>

                        <td>
                            <c:out value="${sale.customerMobile}"
                                   default="Not provided"/>
                        </td>

                        <td>
                            <c:out value="${sale.createdDate}"/>
                        </td>

                        <td>
                            ₹ <fmt:formatNumber value="${sale.grandTotal}"
                                                minFractionDigits="2"
                                                maxFractionDigits="2"/>
                        </td>

                        <td class="paid-amount">
                            ₹ <fmt:formatNumber value="${sale.paidAmount}"
                                                minFractionDigits="2"
                                                maxFractionDigits="2"/>
                        </td>

                        <td class="balance-amount">
                            ₹ <fmt:formatNumber value="${sale.balanceDue}"
                                                minFractionDigits="2"
                                                maxFractionDigits="2"/>
                        </td>

                        <td>
                            <span class="status-badge">
                                <c:out value="${sale.paymentStatus}"/>
                            </span>
                        </td>

                        <td>
                            <form action="${pageContext.request.contextPath}/udhar"
                                  method="post"
                                  class="collection-form">

                                <input type="hidden"
                                       name="saleId"
                                       value="${sale.saleId}">

                                <input type="number"
                                       name="collectionAmount"
                                       min="0.01"
                                       max="${sale.balanceDue}"
                                       step="0.01"
                                       placeholder="Amount"
                                       required>

                                <select name="paymentMethod" required>
                                    <option value="">Payment Method</option>
                                    <option value="CASH">Cash</option>
                                    <option value="UPI">UPI</option>
                                    <option value="CARD">Card</option>
                                </select>

                                <input type="text"
                                       name="paymentReference"
                                       maxlength="150"
                                       placeholder="Reference (optional)">

                                <button type="submit" class="collect-btn">
                                    <i data-lucide="hand-coins"></i>
                                    Collect
                                </button>

                            </form>
                        </td>

                        <td>
                            <a class="view-btn"
                               href="${pageContext.request.contextPath}/sale-details?id=${sale.saleId}">
                                <i data-lucide="eye"></i>
                                View
                            </a>
                        </td>

                    </tr>

                </c:forEach>


                <!-- NO OUTSTANDING BILLS -->
                <c:if test="${empty outstandingSales}">
                    <tr>
                        <td colspan="10" class="empty-state">
                            <i data-lucide="check-circle"></i>
                            <h3>No Outstanding Udhar</h3>
                            <p>All customer credit bills are paid.</p>
                        </td>
                    </tr>
                </c:if>


                <!-- NO SEARCH RESULTS -->
                <tr id="noUdharResults" style="display:none;">
                    <td colspan="10" class="empty-state">
                        <i data-lucide="search-x"></i>
                        <h3>No Matching Bills Found</h3>
                        <p>Try another customer name or mobile number.</p>
                    </td>
                </tr>

                </tbody>

            </table>

        </div>

    </section>

</div>


<!-- LIVE SEARCH -->
<script>

    const udharSearch = document.getElementById("udharSearch");
    const clearUdharSearch = document.getElementById("clearUdharSearch");
    const visibleBillCount = document.getElementById("visibleBillCount");
    const noUdharResults = document.getElementById("noUdharResults");

    const udharRows = document.querySelectorAll("[data-udhar-row]");

    function filterUdharBills() {

        const searchValue = udharSearch.value.trim().toLowerCase();

        let visibleCount = 0;

        udharRows.forEach(function(row) {

            const customerName =
                (row.dataset.customerName || "").toLowerCase();

            const customerMobile =
                (row.dataset.customerMobile || "").toLowerCase();

            const matches =
                customerName.includes(searchValue) ||
                customerMobile.includes(searchValue);

            row.style.display = matches ? "" : "none";

            if (matches) {
                visibleCount++;
            }
        });

        visibleBillCount.textContent =
            visibleCount + (visibleCount === 1 ? " Bill" : " Bills");

        noUdharResults.style.display =
            searchValue !== "" && visibleCount === 0 ? "" : "none";
    }

    udharSearch.addEventListener("input", filterUdharBills);

    clearUdharSearch.addEventListener("click", function() {

        udharSearch.value = "";

        filterUdharBills();

        udharSearch.focus();
    });


    // INITIALIZE LUCIDE ICONS
    document.addEventListener("DOMContentLoaded", function() {

        if (window.lucide) {
            lucide.createIcons();
        }

    });

</script>

</body>
</html>