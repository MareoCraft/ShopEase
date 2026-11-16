
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Udhar Management | ShopEasy</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/udhar.css">
</head>

<body>

<div class="udhar-container">

    <!-- HEADER -->
    <header class="page-header">
        <div>
            <h1>Udhar Management</h1>
            <p>Track outstanding customer credit and collect payments.</p>
        </div>

        <a href="${pageContext.request.contextPath}/dashboard"
           class="back-btn">Dashboard</a>
    </header>

    <!-- MESSAGES -->
    <c:if test="${param.success eq 'collected'}">
        <div class="success-message">
            Payment collected successfully and outstanding balance updated.
        </div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="error-message">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <!-- SUMMARY -->
    <section class="summary-grid">

        <div class="summary-card">
            <span>Total Outstanding Udhar</span>
            <h2>
                ₹ <c:out value="${totalOutstanding}"/>
            </h2>
            <p>Pending customer payments</p>
        </div>

        <div class="summary-card">
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
        </div>

        <div class="table-wrapper">

            <table class="udhar-table">

                <thead>
                <tr>
                    <th>Bill Number</th>
                    <th>Customer ID</th>
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

                    <tr>
                        <td class="bill-number">
                            <c:out value="${sale.billNumber}"/>
                        </td>

                        <td>
                            <c:out value="${sale.customerId}"/>
                        </td>

                        <td>
                            <c:out value="${sale.createdDate}"/>
                        </td>

                        <td>
                            ₹ <c:out value="${sale.grandTotal}"/>
                        </td>

                        <td class="paid-amount">
                            ₹ <c:out value="${sale.paidAmount}"/>
                        </td>

                        <td class="balance-amount">
                            ₹ <c:out value="${sale.balanceDue}"/>
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

                                <input type="hidden" name="saleId"
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
                                    Collect
                                </button>

                            </form>
                        </td>

                        <td>
                            <a class="view-btn"
                               href="${pageContext.request.contextPath}/sale-details?id=${sale.saleId}">
                                View
                            </a>
                        </td>
                    </tr>

                </c:forEach>

                <c:if test="${empty outstandingSales}">
                    <tr>
                        <td colspan="9" class="empty-state">
                            <h3>No Outstanding Udhar</h3>
                            <p>All customer credit bills are paid.</p>
                        </td>
                    </tr>
                </c:if>

                </tbody>

            </table>

        </div>
    </section>

</div>

</body>
</html>