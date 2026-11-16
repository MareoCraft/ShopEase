
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Invoice | ShopEasy</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/sale-details.css">
</head>

<body>

<div class="invoice-page">

    <div class="invoice-actions">
        <a href="${pageContext.request.contextPath}/sales" class="back-btn">
            Create New Bill
        </a>

        <button type="button" onclick="window.print()" class="print-btn">
            Print Invoice
        </button>
    </div>

    <c:if test="${not empty sale}">

        <div class="invoice-container">

            <!-- INVOICE HEADER -->
            <div class="invoice-header">

                <div>
                    <h1>SHOP<span>EASY</span></h1>
                    <p>Shop Management & Billing System</p>
                </div>

                <div class="invoice-title">
                    <h2>INVOICE</h2>
                    <p>Original Customer Copy</p>
                </div>

            </div>

            <div class="invoice-divider"></div>

            <!-- BILL INFORMATION -->
            <div class="invoice-info">

                <div class="info-block">
                    <h3>Bill To</h3>

                    <c:if test="${not empty customer}">
                        <p class="customer-name">
                            <c:out value="${customer.customerName}"/>
                        </p>

                        <p>
                            <c:out value="${customer.mobile}"/>
                        </p>

                        <p>
                            <c:out value="${customer.email}"/>
                        </p>

                        <p>
                            <c:out value="${customer.address}"/>
                        </p>
                    </c:if>
                </div>

                <div class="info-block invoice-meta">
                    <h3>Invoice Details</h3>

                    <p>
                        <strong>Bill Number:</strong>
                        <c:out value="${sale.billNumber}"/>
                    </p>

                    <p>
                        <strong>Bill Date:</strong>
                        <c:out value="${sale.createdDate}"/>
                    </p>

                    <p>
                        <strong>Payment Status:</strong>
                        <span class="status">
                            <c:out value="${sale.paymentStatus}"/>
                        </span>
                    </p>

                    <p>
                        <strong>Sale Status:</strong>
                        <c:out value="${sale.saleStatus}"/>
                    </p>
                </div>

            </div>

            <!-- PRODUCT TABLE -->
            <div class="invoice-table-wrapper">

                <table class="invoice-table">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Product</th>
                        <th>SKU</th>
                        <th>Unit</th>
                        <th>Qty</th>
                        <th>Price</th>
                        <th>Total</th>
                    </tr>
                    </thead>

                    <tbody>

                    <c:forEach var="item" items="${items}" varStatus="loop">
                        <tr>
                            <td><c:out value="${loop.count}"/></td>

                            <td class="product-name">
                                <c:out value="${item.productName}"/>
                            </td>

                            <td>
                                <c:out value="${item.sku}"/>
                            </td>

                            <td>
                                <c:out value="${item.unit}"/>
                            </td>

                            <td>
                                <c:out value="${item.quantity}"/>
                            </td>

                            <td>
                                ₹ <c:out value="${item.unitPrice}"/>
                            </td>

                            <td>
                                ₹ <c:out value="${item.lineTotal}"/>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty items}">
                        <tr>
                            <td colspan="7" class="empty-row">
                                No invoice items found.
                            </td>
                        </tr>
                    </c:if>

                    </tbody>
                </table>

            </div>

            <!-- BILL TOTALS -->
            <div class="invoice-bottom">

                <div class="thank-you">
                    <h3>Thank You!</h3>
                    <p>Thank you for shopping with us.</p>
                    <p>We appreciate your business.</p>
                </div>

                <div class="totals-box">

                    <div class="total-row">
                        <span>Subtotal</span>
                        <strong>₹ <c:out value="${sale.subtotal}"/></strong>
                    </div>

                    <div class="total-row">
                        <span>Discount</span>
                        <strong>₹ <c:out value="${sale.discountAmount}"/></strong>
                    </div>

                    <div class="total-row grand-total">
                        <span>Grand Total</span>
                        <strong>₹ <c:out value="${sale.grandTotal}"/></strong>
                    </div>

                    <div class="total-row">
                        <span>Paid Amount</span>
                        <strong>₹ <c:out value="${sale.paidAmount}"/></strong>
                    </div>

                    <div class="total-row balance">
                        <span>Balance Due</span>
                        <strong>₹ <c:out value="${sale.balanceDue}"/></strong>
                    </div>

                </div>

            </div>

            <!-- PAYMENT DETAILS -->
            <div class="payment-details">
                <h3>Payment Information</h3>

                <c:choose>
                    <c:when test="${not empty payment}">
                        <p>
                            <strong>Payment Method:</strong>
                            <c:out value="${payment.paymentMethod}"/>
                        </p>

                        <p>
                            <strong>Payment Reference:</strong>
                            <c:out value="${payment.paymentReference}"/>
                        </p>

                        <p>
                            <strong>Payment Record Status:</strong>
                            <c:out value="${payment.paymentStatus}"/>
                        </p>
                    </c:when>

                    <c:otherwise>
                        <p>No payment record available.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- FOOTER -->
            <div class="invoice-footer">
                <p>This is a computer-generated invoice.</p>
                <p>Thank you for your business!</p>
            </div>

        </div>

    </c:if>

</div>

</body>
</html>