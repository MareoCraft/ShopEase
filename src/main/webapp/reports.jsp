
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${empty reportSummary}">
    <c:redirect url="/reports"/>
</c:if>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Reports & Analytics | ShopEasy</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/reports.css">

    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>

<body>

<div class="reports-container">

    <!-- PAGE HEADER -->
    <header class="page-header">

        <div>
            <h1>Reports & Analytics</h1>
            <p>Track your shop's performance and business growth.</p>
        </div>

        <a href="${pageContext.request.contextPath}/dashboard"
           class="back-btn">
            Back to Dashboard
        </a>

    </header>


<!-- REPORT DATE FILTER -->
<section class="report-filter-card">

    <div class="filter-heading">
        <div>
            <h2>Filter Reports</h2>
            <p>Select a period to analyze your shop performance.</p>
        </div>
    </div>

    <form action="${pageContext.request.contextPath}/reports"
          method="GET"
          id="reportFilterForm">

        <div class="filter-controls">

            <div class="filter-group">

                <label for="period">Reporting Period</label>

                <select name="period" id="period"
                        onchange="toggleCustomDates()">

                    <option value="TODAY"
                        ${reportFilter.period == 'TODAY' ? 'selected' : ''}>
                        Today
                    </option>

                    <option value="LAST_7_DAYS"
                        ${reportFilter.period == 'LAST_7_DAYS' ? 'selected' : ''}>
                        Last 7 Days
                    </option>

                    <option value="THIS_MONTH"
                        ${reportFilter.period == 'THIS_MONTH' ? 'selected' : ''}>
                        This Month
                    </option>

                    <option value="LAST_MONTH"
                        ${reportFilter.period == 'LAST_MONTH' ? 'selected' : ''}>
                        Last Month
                    </option>

                    <option value="CUSTOM"
                        ${reportFilter.period == 'CUSTOM' ? 'selected' : ''}>
                        Custom Range
                    </option>

                </select>

            </div>


            <div class="filter-group custom-date-group">

                <label for="startDate">Start Date</label>

                <input type="date"
                       id="startDate"
                       name="startDate"
                       value="${startDate}"
                       max="${startDate}">

            </div>


            <div class="filter-group custom-date-group">

                <label for="endDate">End Date</label>

                <input type="date"
                       id="endDate"
                       name="endDate"
                       value="${endDate}"
                       max="${endDate}">

            </div>


            <div class="filter-actions">

                <button type="submit" class="apply-filter-btn">
                    Apply Filter
                </button>

                <a href="${pageContext.request.contextPath}/reports"
                   class="reset-filter-btn">
                    Reset
                </a>

            </div>

        </div>

    </form>

    <div class="active-date-range">
        Showing reports from
        <strong>${startDate}</strong>
        to
        <strong>${endDate}</strong>
    </div>

</section>

    <!-- BUSINESS OVERVIEW -->
    <section class="report-section">

        <h2>Business Overview</h2>

        <div class="report-grid">

            <div class="report-card blue">
                <span class="card-label">Total Bills</span>
                <h3>
                    <c:out value="${reportSummary.totalBills}"/>
                </h3>
                <p>Completed sales</p>
            </div>


            <div class="report-card green">
                <span class="card-label">Total Sales</span>
                <h3>
                    ₹ <fmt:formatNumber value="${reportSummary.totalSales}"
                        minFractionDigits="2" maxFractionDigits="2"/>
                </h3>
                <p>Total completed sale value</p>
            </div>


            <div class="report-card orange">
                <span class="card-label">Total Discounts</span>
                <h3>
                    ₹ <fmt:formatNumber value="${reportSummary.totalDiscount}"
                        minFractionDigits="2" maxFractionDigits="2"/>
                </h3>
                <p>Discount given to customers</p>
            </div>


            <div class="report-card purple">
                <span class="card-label">Total Collected</span>
                <h3>
                    ₹ <fmt:formatNumber value="${reportSummary.totalCollected}"
                        minFractionDigits="2" maxFractionDigits="2"/>
                </h3>
                <p>Payments received</p>
            </div>


            <div class="report-card red">
                <span class="card-label">Outstanding Udhar</span>
                <h3>
                    ₹ <fmt:formatNumber value="${reportSummary.totalOutstanding}"
                        minFractionDigits="2" maxFractionDigits="2"/>
                </h3>
                <p>Pending customer payments</p>
            </div>


            <div class="report-card blue">
                <span class="card-label">Active Products</span>
                <h3>
                    <c:out value="${reportSummary.totalProducts}"/>
                </h3>
                <p>Products in inventory</p>
            </div>


            <div class="report-card orange">
                <span class="card-label">Low Stock Products</span>
                <h3>
                    <c:out value="${reportSummary.lowStockProducts}"/>
                </h3>
                <p>Products requiring restock</p>
            </div>


            <div class="report-card green">
                <span class="card-label">Total Customers</span>
                <h3>
                    <c:out value="${reportSummary.totalCustomers}"/>
                </h3>
                <p>Active registered customers</p>
            </div>

        </div>

    </section>


    <!-- SALES ANALYTICS -->
    <section class="report-section">

        <h2>Sales Analytics</h2>

        <div class="chart-card">

            <div class="chart-header">

                <div>
                    <h3>Daily Sales Trend</h3>
                    <p>Sales performance over the last 7 days</p>
                </div>

                <span class="chart-badge">Last 7 Days</span>

            </div>


            <!-- IMPORTANT: CHART CANVAS -->
            <div class="chart-container">

                <canvas id="salesTrendChart"></canvas>

            </div>

        </div>

    </section>


<!-- PAYMENT METHOD ANALYTICS -->
<section class="report-section">

    <h2>Payment Analytics</h2>

    <div class="payment-analytics-grid">

        <!-- DOUGHNUT CHART -->
        <div class="chart-card">

            <div class="chart-header">
                <div>
                    <h3>Payment Distribution</h3>
                    <p>Collected amount by payment method</p>
                </div>
            </div>

            <div class="payment-chart-container">
                <canvas id="paymentMethodChart"></canvas>
            </div>

        </div>


        <!-- PAYMENT SUMMARY -->
        <div class="chart-card">

            <div class="chart-header">
                <div>
                    <h3>Payment Summary</h3>
                    <p>Collection details by payment method</p>
                </div>
            </div>

            <div class="payment-summary-list">

                <c:forEach var="payment" items="${paymentSummary}">

                    <div class="payment-summary-item">

                        <div class="payment-info">
                            <div class="payment-name">
                                <c:out value="${payment.paymentMethod}"/>
                            </div>

                            <div class="payment-transactions">
                                <c:out value="${payment.totalTransactions}"/>
                                transactions
                            </div>
                        </div>

                        <div class="payment-amount">
                            ₹ <fmt:formatNumber
                                    value="${payment.totalAmount}"
                                    minFractionDigits="2"
                                    maxFractionDigits="2"/>
                        </div>

                    </div>

                </c:forEach>

                <c:if test="${empty paymentSummary}">
                    <div class="empty-payment">
                        No payment transactions available yet.
                    </div>
                </c:if>

            </div>

        </div>

    </div>

</section>


<!-- BEST SELLING PRODUCTS -->
<section class="report-section">

    <div class="section-heading">
        <div>
            <h2>Best-Selling Products</h2>
            <p>Top 10 products ranked by quantity sold.</p>
        </div>

        <span class="chart-badge">Top 10 Products</span>
    </div>

    <div class="table-card">

        <div class="table-responsive">

            <table class="products-report-table">

                <thead>
                    <tr>
                        <th>Rank</th>
                        <th>Product Name</th>
                        <th>SKU</th>
                        <th>Quantity Sold</th>
                        <th>Total Bills</th>
                        <th>Revenue</th>
                    </tr>
                </thead>

                <tbody>

                    <c:forEach var="product"
                               items="${bestSellingProducts}"
                               varStatus="status">

                        <tr>

                            <td>
                                <span class="rank-badge">
                                    #${status.count}
                                </span>
                            </td>

                            <td class="product-name">
                                <c:out value="${product.productName}"/>
                            </td>

                            <td>
                                <c:out value="${product.sku}"
                                       default="-"/>
                            </td>

                            <td>
                                <fmt:formatNumber
                                    value="${product.totalQuantitySold}"
                                    maxFractionDigits="2"/>
                            </td>

                            <td>
                                <c:out value="${product.totalBills}"/>
                            </td>

                            <td class="revenue-value">
                                ₹ <fmt:formatNumber
                                    value="${product.totalSalesAmount}"
                                    minFractionDigits="2"
                                    maxFractionDigits="2"/>
                            </td>

                        </tr>

                    </c:forEach>

                    <c:if test="${empty bestSellingProducts}">
                        <tr>
                            <td colspan="6" class="empty-table">
                                No completed product sales available yet.
                            </td>
                        </tr>
                    </c:if>

                </tbody>

            </table>

        </div>

    </div>

</section>

</div>


<!-- SALES CHART JAVASCRIPT -->
<script>

document.addEventListener("DOMContentLoaded", function () {

    const chartCanvas = document.getElementById("salesTrendChart");

    if (!chartCanvas) {
        console.error("Sales chart canvas not found.");
        return;
    }

    if (typeof Chart === "undefined") {
        console.error("Chart.js library is not loaded.");
        return;
    }

    const ctx = chartCanvas.getContext("2d");


    // SALES DATES
    const salesLabels = [
        <c:forEach var="item" items="${salesTrend}" varStatus="status">
            "${item.saleDate}"
            <c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];


    // DAILY SALES AMOUNT
    const salesValues = [
        <c:forEach var="item" items="${salesTrend}" varStatus="status">
            ${item.totalSales}
            <c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];


    // DAILY BILL COUNT
    const billValues = [
        <c:forEach var="item" items="${salesTrend}" varStatus="status">
            ${item.totalBills}
            <c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];


    // CREATE CHART
    new Chart(ctx, {

        type: "bar",

        data: {

            labels: salesLabels,

            datasets: [

                {
                    label: "Sales Amount (₹)",
                    data: salesValues,
                    backgroundColor: "#3b82f6",
                    borderRadius: 7,
                    yAxisID: "y"
                },

                {
                    label: "Total Bills",
                    data: billValues,
                    type: "line",
                    borderColor: "#16a34a",
                    backgroundColor: "#16a34a",
                    pointBackgroundColor: "#16a34a",
                    pointRadius: 5,
                    tension: 0.35,
                    yAxisID: "y1"
                }

            ]

        },


        options: {

            responsive: true,
            maintainAspectRatio: false,

            interaction: {
                mode: "index",
                intersect: false
            },

            plugins: {

                legend: {
                    position: "top"
                },

                tooltip: {
                    enabled: true
                }

            },

            scales: {

                y: {

                    beginAtZero: true,
                    position: "left",

                    title: {
                        display: true,
                        text: "Sales Amount (₹)"
                    }

                },

                y1: {

                    beginAtZero: true,
                    position: "right",

                    grid: {
                        drawOnChartArea: false
                    },

                    title: {
                        display: true,
                        text: "Number of Bills"
                    }

                }

            }

        }

    });

});


document.addEventListener("DOMContentLoaded", function () {

    const paymentCanvas =
        document.getElementById("paymentMethodChart");

    if (!paymentCanvas) {
        console.error("Payment chart canvas not found.");
        return;
    }

    if (typeof Chart === "undefined") {
        console.error("Chart.js library is not loaded.");
        return;
    }

    const paymentLabels = [
        <c:forEach var="payment" items="${paymentSummary}" varStatus="status">
            "${payment.paymentMethod}"
            <c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];

    const paymentValues = [
        <c:forEach var="payment" items="${paymentSummary}" varStatus="status">
            ${payment.totalAmount}
            <c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];

    new Chart(paymentCanvas.getContext("2d"), {

        type: "doughnut",

        data: {
            labels: paymentLabels,

            datasets: [{
                data: paymentValues,
                backgroundColor: [
                    "#3b82f6",
                    "#16a34a",
                    "#f59e0b"
                ],
                borderWidth: 3,
                borderColor: "#ffffff",
                hoverOffset: 8
            }]
        },

        options: {

            responsive: true,
            maintainAspectRatio: false,

            cutout: "65%",

            plugins: {

                legend: {
                    position: "bottom"
                },

                tooltip: {
                    callbacks: {
                        label: function (context) {
                            return context.label + ": ₹" +
                                Number(context.raw).toLocaleString("en-IN", {
                                    minimumFractionDigits: 2
                                });
                        }
                    }
                }

            }

        }

    });

});


function toggleCustomDates() {

    const period = document.getElementById("period").value;

    const customGroups =
        document.querySelectorAll(".custom-date-group");

    const startDate = document.getElementById("startDate");
    const endDate = document.getElementById("endDate");

    const isCustom = period === "CUSTOM";

    customGroups.forEach(function (group) {
        group.style.display = isCustom ? "flex" : "none";
    });

    startDate.required = isCustom;
    endDate.required = isCustom;
}

document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("reportFilterForm");

    toggleCustomDates();

    form.addEventListener("submit", function (event) {

        const period = document.getElementById("period").value;

        if (period === "CUSTOM") {

            const start = document.getElementById("startDate").value;
            const end = document.getElementById("endDate").value;

            if (!start || !end) {
                event.preventDefault();
                alert("Please select both start and end dates.");
                return;
            }

            if (end < start) {
                event.preventDefault();
                alert("End date cannot be before start date.");
            }
        }
    });

});


</script>

</body>
</html>