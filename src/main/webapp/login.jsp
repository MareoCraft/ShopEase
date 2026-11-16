
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>ShopEase | Login</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
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
    <%
        String errorMessage =
            (String) request.getAttribute("errorMessage");

        String registered =
            request.getParameter("registered");

        if (errorMessage != null) {
    %>
        <div class="error-message">
            <%= errorMessage %>
        </div>
    <%
        }

        if ("success".equals(registered)) {
    %>
        <div class="success-message">
            Shop registered successfully! Please login.
        </div>
    <%
        }
    %>

    <%
        String logout = request.getParameter("logout");

        if ("success".equals(logout)) {
    %>

        <div class="success-message">
            You have logged out successfully.
        </div>

    <%
        }
    %>

    <form action="${pageContext.request.contextPath}/login"
          method="post">

        <h2>Owner Login</h2>

        <div class="form-group">

            <label>Username</label>

            <input type="text"
                   name="username"
                   placeholder="Enter username"
                   autocomplete="username"
                   required>

        </div>

        <br>

        <div class="form-group">

            <label>Password</label>

            <input type="password"
                   name="password"
                   placeholder="Enter password"
                   autocomplete="current-password"
                   required>

        </div>

        <button type="submit">Login</button>

        <p class="login-link">
            Don't have an account?
            <a href="register.jsp">Register your shop</a>
        </p>

    </form>

</div>

</body>
</html>