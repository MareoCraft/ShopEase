
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>ShopEase | Shop Profile</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/profile.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>

<body>

<div class="container">
    <div class="form-header">
        <div class="brand-logo">
            <span class="brand-icon">S</span>
            <span>ShopEase</span>
        </div>

        <div class="brand-content">
            <span class="eyebrow">SMART BUSINESS MANAGEMENT</span>

            <h1>Everything your shop needs. All in one place.</h1>

            <p>
                Simplify daily operations, manage your inventory
                and grow your business with confidence.
            </p>

            <div class="service-list">

                <div class="service-item">
                    <span class="service-icon">▦</span>
                    <div>
                        <strong>Inventory Management</strong>
                        <small>Products, pricing and stock tracking</small>
                    </div>
                </div>

                <div class="service-item">
                    <span class="service-icon">₹</span>
                    <div>
                        <strong>Sales & Billing</strong>
                        <small>Easy billing and sales records</small>
                    </div>
                </div>

                <div class="service-item">
                    <span class="service-icon">♙</span>
                    <div>
                        <strong>Customer Management</strong>
                        <small>Organize customer information</small>
                    </div>
                </div>

                <div class="service-item">
                    <span class="service-icon">◷</span>
                    <div>
                        <strong>Udhar Management</strong>
                        <small>Track credit and pending payments</small>
                    </div>
                </div>

                <div class="service-item">
                    <span class="service-icon">▥</span>
                    <div>
                        <strong>Business Analytics</strong>
                        <small>Understand your shop performance</small>
                    </div>
                </div>

            </div>
        </div>

        <div class="brand-footer">
            YOUR BUSINESS. YOUR CONTROL. ONE PLATFORM.
        </div>
    </div>


    <c:if test="${not empty errorMessage}">
        <div class="error-message">
            <c:out value="${errorMessage}"/>
        </div>
    </c:if>

    <c:if test="${param.updated == 'success'}">
        <div class="success-message">
            Shop profile updated successfully!
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/profile"
          method="post">

        <h2>Shop Information</h2>

        <div class="form-grid">

            <div class="form-group">
                <label>Shop ID</label>
                <input type="text"
                       value="<c:out value='${shop.shopId}'/>"
                       readonly>
            </div>

            <div class="form-group">
                <label>Shop Name *</label>
                <input type="text"
                       name="shopName"
                       value="<c:out value='${shop.shopName}'/>"
                       required>
            </div>

            <div class="form-group">
                <label>Shop Category</label>
                <input type="text"
                       name="shopCategory"
                       value="<c:out value='${shop.shopCategory}'/>">
            </div>

            <div class="form-group">
                <label>Owner Name *</label>
                <input type="text"
                       name="ownerName"
                       value="<c:out value='${shop.ownerName}'/>"
                       required>
            </div>

            <div class="form-group">
                <label>Mobile Number *</label>
                <input type="tel"
                       name="mobile"
                       value="<c:out value='${shop.mobile}'/>"
                       required>
            </div>

            <div class="form-group">
                <label>Email Address</label>
                <input type="email"
                       name="email"
                       value="<c:out value='${shop.email}'/>">
            </div>

            <div class="form-group full-width">
                <label>Shop Address</label>
                <input type="text"
                       name="address"
                       value="<c:out value='${shop.address}'/>">
            </div>

            <div class="form-group">
                <label>City</label>
                <input type="text"
                       name="city"
                       value="<c:out value='${shop.city}'/>">
            </div>

            <div class="form-group">
                <label>State</label>
                <input type="text"
                       name="state"
                       value="<c:out value='${shop.state}'/>">
            </div>

            <div class="form-group">
                <label>PIN Code</label>
                <input type="text"
                       name="pincode"
                       value="<c:out value='${shop.pincode}'/>">
            </div>

        </div>

        <button type="submit">Update Shop Profile</button> <br> <br>
        <p class="login-link">
                <a href="${pageContext.request.contextPath}/dashboard.jsp">
                    Back to Dashboard
                </a>
        </p>

    </form>
</div>

</body>
</html>