
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sales History | ShopEasy</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/sales-history.css">
</head>

<body>

<div class="history-container">

    <header class="page-header">
        <div>
            <h1>Sales History</h1>
            <p>View and manage your shop's previous bills.</p>
        </div>

        <div class="header-actions">
            <a href="${pageContext.request.contextPath}/dashboard"
               class="back-btn">Dashboard</a>

            <a href="${pageContext.request.contextPath}/sales"
               class="new-sale-btn">+ New Sale</a>
        </div>
    </header>

    <c:if test="${not empty param.success}">
        <div class="success-message">Sale operation completed successfully.</div>
    </c:if>

    <!-- SUMMARY -->
    <section class="summary-grid">

        <div class="summary-card">
            <span>Total Bills</span>
            <strong><c:out value="${sales.size()}"/></strong>
        </div>

        <div class="summary-card">
            <span>Completed Bills</span>
            <strong>
                <c:set var="completedCount" value="0"/>
                <c:forEach var="sale" items="${sales}">
                    <c:if test="${sale.saleStatus eq 'COMPLETED'}">
                        <c:set var="completedCount"
                               value="${completedCount + 1}"/>
                    </c:if>
                </c:forEach>
                <c:out value="${completedCount}"/>
            </strong>
        </div>

        <div class="summary-card">
            <span>Cancelled Bills</span>
            <strong>
                <c:set var="cancelledCount" value="0"/>
                <c:forEach var="sale" items="${sales}">
                    <c:if test="${sale.saleStatus eq 'CANCELLED'}">
                        <c:set var="cancelledCount"
                               value="${cancelledCount + 1}"/>
                    </c:if>
                </c:forEach>
                <c:out value="${cancelledCount}"/>
            </strong>
        </div>

    </section>

    <!-- SALES TABLE -->
    <section class="history-card">

        <div class="table-heading">
            <div>
                <h2>All Transactions</h2>
                <p>Recent sales and invoice records</p>
            </div>
        </div>

        <div class="table-wrapper">

            <table class="history-table">

                <thead>
                <tr>
                    <th>Bill Number</th>
                    <th>Date</th>
                    <th>Customer ID</th>
                    <th>Subtotal</th>
                    <th>Discount</th>
                    <th>Grand Total</th>
                    <th>Paid</th>
                    <th>Balance</th>
                    <th>Payment</th>
                    <th>Status</th>
                    <th>Invoice</th>
                </tr>
                </thead>

                <tbody>

                <c:forEach var="sale" items="${sales}">
                    <tr>
                        <td class="bill-number">
                            <c:out value="${sale.billNumber}"/>
                        </td>

                        <td><c:out value="${sale.createdDate}"/></td>

                        <td><c:out value="${sale.customerId}"/></td>

                        <td>₹ <c:out value="${sale.subtotal}"/></td>

                        <td>₹ <c:out value="${sale.discountAmount}"/></td>

                        <td class="grand-total">
                            ₹ <c:out value="${sale.grandTotal}"/>
                        </td>

                        <td>₹ <c:out value="${sale.paidAmount}"/></td>

                        <td class="balance">
                            ₹ <c:out value="${sale.balanceDue}"/>
                        </td>

                        <td>
                            <span class="payment-status
                                <c:out value='${sale.paymentStatus}'/>">
                                <c:out value="${sale.paymentStatus}"/>
                            </span>
                        </td>

                        <td>
                            <span class="sale-status
                                <c:out value='${sale.saleStatus}'/>">
                                <c:out value="${sale.saleStatus}"/>
                            </span>
                        </td>

                        <td>
                            <a class="view-btn"
                               href="${pageContext.request.contextPath}/sale-details?id=${sale.saleId}">
                                View
                            </a>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty sales}">
                    <tr>
                        <td colspan="11" class="empty-state">
                            <div class="empty-content">
                                <h3>No Sales Found</h3>
                                <p>Your generated bills will appear here.</p>
                                <a href="${pageContext.request.contextPath}/sales">
                                    Create Your First Bill
                                </a>
                            </div>
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