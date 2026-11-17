
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Stock Management | ShopEase</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/stock.css?v=3">
</head>

<body>

<div class="page-container">

    <!-- PAGE HEADER -->
    <header class="page-header">
        <div>
            <h1>Stock Management</h1>
            <p>Track stock movements and manage your inventory.</p>
        </div>

        <a href="${pageContext.request.contextPath}/dashboard"
           class="back-button">
            Back to Dashboard
        </a>
    </header>


    <!-- SUCCESS MESSAGE -->
    <c:if test="${param.success == 'updated'}">
        <div class="success-message">
            Stock updated successfully and history recorded!
        </div>
    </c:if>


    <!-- ERROR MESSAGE -->
    <c:if test="${not empty error}">
        <div class="error-message">
            <c:out value="${error}"/>
        </div>
    </c:if>


    <!-- STOCK SUMMARY -->
    <section class="summary-section">

        <div class="summary-card">
            <span class="summary-label">Total Products</span>
            <h2>${products.size()}</h2>
            <p>Active products</p>
        </div>

        <div class="summary-card">
            <span class="summary-label">Stock Movements</span>
            <h2>${historyList.size()}</h2>
            <p>Movements in selected period</p>
        </div>

    </section>


    <!-- STOCK MANAGEMENT FORM -->
    <section class="form-card">

        <div class="section-heading">
            <div>
                <h2>Update Stock</h2>
                <p>Record stock additions, removals or corrections.</p>
            </div>
        </div>

        <c:choose>

            <c:when test="${empty products}">
                <div class="warning-message">
                    No active products found.
                    <a href="${pageContext.request.contextPath}/products">
                        Add a product first.
                    </a>
                </div>
            </c:when>

            <c:otherwise>

                <form action="${pageContext.request.contextPath}/stock"
                      method="post">

                    <input type="hidden" name="action" value="adjust">

                    <div class="stock-form-grid">

                        <!-- PRODUCT -->
                        <div class="form-group">
                            <label for="productId">Select Product *</label>

                            <select name="productId"
                                    id="productId"
                                    required>

                                <option value="">Choose Product</option>

                                <c:forEach var="product" items="${products}">

                                    <option value="${product.productId}">
                                        <c:out value="${product.productName}"/>
                                        (Available:
                                        <c:out value="${product.stockQuantity}"/>
                                        <c:out value="${product.unit}"/>)
                                    </option>

                                </c:forEach>

                            </select>
                        </div>


                        <!-- MOVEMENT TYPE -->
                        <div class="form-group">
                            <label for="movementType">Movement Type *</label>

                            <select name="movementType"
                                    id="movementType"
                                    required>

                                <option value="">Select Operation</option>
                                <option value="STOCK_IN">Stock In (+)</option>
                                <option value="STOCK_OUT">Stock Out (-)</option>
                                <option value="ADJUSTMENT">Stock Adjustment</option>

                            </select>
                        </div>


                        <!-- QUANTITY -->
                        <div class="form-group">
                            <label for="quantity" id="quantityLabel">
                                Quantity *
                            </label>

                            <input type="number"
                                   name="quantity"
                                   id="quantity"
                                   min="0"
                                   step="0.01"
                                   placeholder="Enter quantity"
                                   required>
                        </div>


                        <!-- NOTES -->
                        <div class="form-group">
                            <label for="notes">Notes</label>

                            <input type="text"
                                   name="notes"
                                   id="notes"
                                   maxlength="500"
                                   placeholder="Reason for stock movement">
                        </div>

                    </div>

                    <div class="form-info" id="operationInfo">
                        Select a stock operation to see its description.
                    </div>

                    <button type="submit" class="primary-button">
                        Update Stock
                    </button>

                </form>

            </c:otherwise>

        </c:choose>

    </section>

    <!-- DATE FILTER -->
            <div class="history-filter-card">
                <form action="${pageContext.request.contextPath}/stock" method="get" id="stockFilterForm">

                    <div class="history-filter-grid">

                        <!-- PERIOD -->
                        <div class="form-group">
                            <select name="period" id="period">

                                <option value="all"
                                    ${selectedPeriod == 'all' ? 'selected' : ''}>
                                    All History
                                </option>

                                <option value="today"
                                    ${selectedPeriod == 'today' ? 'selected' : ''}>
                                    Today
                                </option>

                                <option value="yesterday"
                                    ${selectedPeriod == 'yesterday' ? 'selected' : ''}>
                                    Yesterday
                                </option>

                                <option value="last7days"
                                    ${selectedPeriod == 'last7days' ? 'selected' : ''}>
                                    Last 7 Days
                                </option>

                                <option value="thismonth"
                                    ${selectedPeriod == 'thismonth' ? 'selected' : ''}>
                                    This Month
                                </option>

                                <option value="lastmonth"
                                    ${selectedPeriod == 'lastmonth' ? 'selected' : ''}>
                                    Last Month
                                </option>

                                <option value="custom"
                                    ${selectedPeriod == 'custom' ? 'selected' : ''}>
                                    Custom Date Range
                                </option>

                            </select>
                        </div>


                        <!-- FROM DATE -->
                        <div class="form-group custom-date-field"
                             id="fromDateGroup">

                            <label for="fromDate">From Date</label>

                            <input type="date"
                                   name="fromDate"
                                   id="fromDate"
                                   value="<c:out value='${fromDate}'/>">

                        </div>


                        <!-- TO DATE -->
                        <div class="form-group custom-date-field"
                             id="toDateGroup">

                            <label for="toDate">To Date</label>

                            <input type="date"
                                   name="toDate"
                                   id="toDate"
                                   value="<c:out value='${toDate}'/>">

                        </div>


                        <!-- FILTER BUTTONS -->
                        <div class="filter-actions">

                            <button type="submit" class="filter-button">
                                Apply Filter
                            </button>

                            <a href="${pageContext.request.contextPath}/stock"
                               class="reset-filter-button">
                                Reset
                            </a>

                        </div>

                    </div>

                </form>

                <div class="filter-result">
                    Showing
                    <strong>${historyCount}</strong>
                    stock movement records
                </div>

            </div>


    <!-- STOCK HISTORY -->
    <section class="table-card">

        <div class="table-header">
            <div>
                <h2>Stock Movement History</h2>
                <p>View and filter your inventory movement records.</p>
            </div>

            <span class="history-count">
                ${historyCount} Records
            </span>
        </div>

        <!-- HISTORY TABLE -->
        <div class="table-wrapper">

            <table>

                <thead>
                <tr>
                    <th>#</th>
                    <th>Date</th>
                    <th>Product</th>
                    <th>Movement</th>
                    <th>Previous Stock</th>
                    <th>Quantity Change</th>
                    <th>New Stock</th>
                    <th>Notes</th>
                </tr>
                </thead>

                <tbody>

                <c:forEach var="history"
                           items="${historyList}"
                           varStatus="status">

                    <tr>

                        <td>${status.count}</td>

                        <td>
                            <c:out value="${history.createdDate}"/>
                        </td>

                        <td>
                            <strong>
                                <c:out value="${history.productName}"/>
                            </strong>
                        </td>


                        <!-- MOVEMENT BADGE -->
                        <td>

                            <c:choose>

                                <c:when test="${history.movementType == 'OPENING_STOCK'}">
                                    <span class="movement-badge opening-stock">
                                        Opening Stock
                                    </span>
                                </c:when>

                                <c:when test="${history.movementType == 'STOCK_IN'}">
                                    <span class="movement-badge stock-in">
                                        Stock In
                                    </span>
                                </c:when>

                                <c:when test="${history.movementType == 'STOCK_OUT'}">
                                    <span class="movement-badge stock-out">
                                        Stock Out
                                    </span>
                                </c:when>

                                <c:otherwise>
                                    <span class="movement-badge adjustment">
                                        Adjustment
                                    </span>
                                </c:otherwise>

                            </c:choose>

                        </td>


                        <!-- PREVIOUS STOCK -->
                        <td>
                            <c:out value="${history.previousQuantity}"/>
                        </td>


                        <!-- QUANTITY CHANGE -->
                        <td>

                            <c:choose>

                                <c:when test="${history.quantityChange > 0}">
                                    <span class="positive-quantity">
                                        +<c:out value="${history.quantityChange}"/>
                                    </span>
                                </c:when>

                                <c:when test="${history.quantityChange < 0}">
                                    <span class="negative-quantity">
                                        <c:out value="${history.quantityChange}"/>
                                    </span>
                                </c:when>

                                <c:otherwise>
                                    <span class="neutral-quantity">0</span>
                                </c:otherwise>

                            </c:choose>

                        </td>


                        <!-- NEW STOCK -->
                        <td>
                            <strong>
                                <c:out value="${history.newQuantity}"/>
                            </strong>
                        </td>


                        <!-- NOTES -->
                        <td>

                            <c:choose>

                                <c:when test="${not empty history.notes}">
                                    <c:out value="${history.notes}"/>
                                </c:when>

                                <c:otherwise>
                                    <span class="muted">No notes</span>
                                </c:otherwise>

                            </c:choose>

                        </td>

                    </tr>

                </c:forEach>


                <!-- EMPTY HISTORY -->
                <c:if test="${empty historyList}">
                    <tr>
                        <td colspan="8" class="empty-state">
                            No stock movements found for this period.
                        </td>
                    </tr>
                </c:if>

                </tbody>

            </table>

        </div>

    </section>

</div>


<!-- JAVASCRIPT -->
<script>

    /* =====================================
       MOVEMENT TYPE INFORMATION
    ===================================== */

    const movementType = document.getElementById("movementType");
    const quantityLabel = document.getElementById("quantityLabel");
    const operationInfo = document.getElementById("operationInfo");
    const quantityInput = document.getElementById("quantity");

    if (movementType) {

        movementType.addEventListener("change", function () {

            const value = this.value;

            if (value === "STOCK_IN") {

                quantityLabel.textContent = "Quantity to Add *";

                operationInfo.textContent =
                    "Stock In: The entered quantity will be added to your current stock.";

                quantityInput.placeholder = "Example: 10";

            } else if (value === "STOCK_OUT") {

                quantityLabel.textContent = "Quantity to Remove *";

                operationInfo.textContent =
                    "Stock Out: The entered quantity will be removed from your current stock.";

                quantityInput.placeholder = "Example: 5";

            } else if (value === "ADJUSTMENT") {

                quantityLabel.textContent = "Actual Stock Quantity *";

                operationInfo.textContent =
                    "Adjustment: Enter the actual physical stock quantity. This will replace the current stock.";

                quantityInput.placeholder = "Example: 25";

            } else {

                quantityLabel.textContent = "Quantity *";

                operationInfo.textContent =
                    "Select a stock operation to see its description.";

                quantityInput.placeholder = "Enter quantity";
            }
        });
    }


    /* =====================================
       STOCK HISTORY DATE FILTER
    ===================================== */

    const periodSelect = document.getElementById("period");

    const fromDateGroup = document.getElementById("fromDateGroup");
    const toDateGroup = document.getElementById("toDateGroup");

    const fromDateInput = document.getElementById("fromDate");
    const toDateInput = document.getElementById("toDate");

    const stockFilterForm = document.getElementById("stockFilterForm");


    function toggleCustomDateFields() {

        const isCustom = periodSelect.value === "custom";

        fromDateGroup.style.display = isCustom ? "flex" : "none";
        toDateGroup.style.display = isCustom ? "flex" : "none";

        fromDateInput.required = isCustom;
        toDateInput.required = isCustom;
    }


    periodSelect.addEventListener("change", toggleCustomDateFields);


    stockFilterForm.addEventListener("submit", function (event) {

        if (periodSelect.value === "custom") {

            if (!fromDateInput.value || !toDateInput.value) {

                event.preventDefault();
                alert("Please select both From Date and To Date.");
                return;
            }

            if (fromDateInput.value > toDateInput.value) {

                event.preventDefault();
                alert("From Date cannot be after To Date.");
                return;
            }
        }

    });


    toggleCustomDateFields();

</script>

</body>
</html>