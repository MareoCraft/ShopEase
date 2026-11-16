
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ShopEase | Register Your Shop</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/register.css">
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

    <form action="${pageContext.request.contextPath}/register" method="post">

        <h2>Shop Information</h2>

        <div class="form-grid">

            <div class="form-group">
                <label>Shop Name *</label>
                <input type="text" name="shopName"
                       placeholder="Enter shop name" required>
            </div>

            <div class="form-group">
                <label>Shop Category</label>
                <select name="shopCategory">
                    <option value="">Select Category</option>
                    <option value="Grocery">Grocery</option>
                    <option value="Clothing">Clothing</option>
                    <option value="Mobile">Mobile Shop</option>
                    <option value="Electronics">Electronics</option>
                    <option value="Medical">Medical</option>
                    <option value="Other">Other</option>
                </select>
            </div>

            <div class="form-group">
                <label>Mobile Number *</label>
                <input type="tel" name="mobile"
                       placeholder="Enter mobile number"
                       maxlength="15" required>
            </div>

            <div class="form-group">
                <label>Email Address</label>
                <input type="email" name="email"
                       placeholder="Enter email address">
            </div>

            <div class="form-group full-width">
                <label>Shop Address</label>
                <input type="text" name="address"
                       placeholder="Enter shop address">
            </div>

            <div class="form-group">
                <label>City</label>
                <input type="text" name="city"
                       placeholder="Enter city">
            </div>

            <div class="form-group">
                <label>State</label>
                <input type="text" name="state"
                       placeholder="Enter state">
            </div>

            <div class="form-group">
                <label>PIN Code</label>
                <input type="text" name="pincode"
                       placeholder="Enter PIN code" maxlength="10">
            </div>

        </div>

        <div class="section-divider"></div>

        <h2>Owner Information</h2>

        <div class="form-grid">

            <div class="form-group">
                <label>Owner Full Name *</label>
                <input type="text" name="ownerName"
                       placeholder="Enter owner name" required>
            </div>

            <div class="form-group">
                <label>Username *</label>
                <input type="text" name="username"
                       placeholder="Create username" required>
            </div>

            <div class="form-group">
                <label>Password *</label>
                <input type="password" name="password"
                       placeholder="Create password" required>
            </div>

            <div class="form-group">
                <label>Confirm Password *</label>
                <input type="password" name="confirmPassword"
                       placeholder="Confirm password" required>
            </div>

        </div>

        <button type="submit">Register Shop</button>

        <p class="login-link">
            Already registered?
            <a href="login.jsp">Login here</a>
        </p>

    </form>

    <%
        String errorMessage =
            (String) request.getAttribute("errorMessage");

        if (errorMessage != null) {
    %>
        <div class="error-message">
            <%= errorMessage %>
        </div>
    <%
        }
    %>

</div>

</body>
</html>