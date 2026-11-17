
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
if (request.getAttribute("dashboard") == null) {
    response.sendRedirect(request.getContextPath() + "/dashboard");
    return;
}
%>

<!DOCTYPE html>
<html>
<head>
    <title>ShopEase | Dashboard</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

   <link rel="stylesheet"
         href="${pageContext.request.contextPath}/css/dashboard.css?v=2">

    <!-- Lucide Icons -->
    <script src="https://unpkg.com/lucide@latest"></script>
</head>

<body>

<div class="layout">

    <!-- SIDEBAR -->
    <aside class="sidebar">

        <div class="logo">
             <img width="90%" src="./images/logo.png">
        </div>

        <!-- SHOP INFO -->
        <div class="shop-info">
            <div class="shop-avatar">
                <i data-lucide="store"></i>
            </div>

            <div>
                <h3><c:out value="${dashboard.shopName}"/></h3>
                <p>Shop Name</p>
            </div>
        </div>

        <!-- NAVIGATION -->
        <nav>

            <a href="${pageContext.request.contextPath}/dashboard"
               class="active">
                <i data-lucide="layout-dashboard"></i>
                <span>Dashboard</span>
            </a>

            <a href="${pageContext.request.contextPath}/categories">
                <i data-lucide="folder"></i>
                <span>Categories</span>
            </a>

            <a href="${pageContext.request.contextPath}/products">
                <i data-lucide="package"></i>
                <span>Products</span>
            </a>

            <a href="${pageContext.request.contextPath}/stock">
                <i data-lucide="boxes"></i>
                <span>Stock Management</span>
            </a>

            <a href="${pageContext.request.contextPath}/sales">
                <i data-lucide="receipt"></i>
                <span>Sales & Billing</span>
            </a>

            <a href="${pageContext.request.contextPath}/sales-history">
                <i data-lucide="history"></i>
                <span>Sales History</span>
            </a>

            <a href="${pageContext.request.contextPath}/customers">
                <i data-lucide="users"></i>
                <span>Customer Management</span>
            </a>

            <a href="${pageContext.request.contextPath}/udhar">
                <i data-lucide="wallet"></i>
                <span>Udhar Management</span>
            </a>

            <a href="${pageContext.request.contextPath}/expenses"
               class="menu-item">

                <i data-lucide="wallet"></i>
                <span>Expense Management</span>
            </a>

            <a href="${pageContext.request.contextPath}/reports">
                <i data-lucide="chart-no-axes-combined"></i>
                <span>Reports & Analytics</span>
            </a>

            <a href="${pageContext.request.contextPath}/profile">
                <i data-lucide="settings"></i>
                <span>Shop Profile</span>
            </a>

        </nav>

        <!-- LOGOUT -->
        <div class="sidebar-bottom">

            <form action="${pageContext.request.contextPath}/logout"
                  method="post">

                <button type="submit" class="logout-btn">
                    <i data-lucide="log-out"></i>
                    <span>Logout</span>
                </button>

            </form>

        </div>

    </aside>


    <!-- MAIN CONTENT -->
    <main class="main-content">

        <!-- TOPBAR -->
        <header class="topbar">

            <div>
                <h2>Dashboard</h2>
                <p>
                    Welcome back,
                    <c:out value="${dashboard.ownerName}"/>!
                </p>
            </div>

            <div class="user-profile">

                <div class="user-avatar">
                    <i data-lucide="user-round"></i>
                </div>

                <div>
                    <strong>
                        <c:out value="${dashboard.ownerName}"/>
                    </strong>
                    <small>Shop Owner</small>
                </div>

            </div>

        </header>


        <!-- SHOP OVERVIEW -->
        <section class="welcome-card">

            <div>
                <p>YOUR SHOP OVERVIEW</p>

                <h2>
                    <c:out value="${dashboard.shopName}"/>
                </h2>

                <span>
                    Shop ID:
                    <c:out value="${dashboard.shopCode}"/>
                </span>
            </div>

            <div class="welcome-icon">
                <i data-lucide="store"></i>
            </div>

        </section>


        <!-- BUSINESS OVERVIEW -->
        <section class="section">

            <div class="section-heading">
                <h2>Business Overview</h2>
                <p>Your business performance at a glance</p>
            </div>

            <div class="stats-grid">

                <div class="stat-card">
                    <div class="stat-icon sales-icon">
                        <i data-lucide="indian-rupee"></i>
                    </div>

                    <p>Today's Sales</p>

                    <h2>
                        ₹<fmt:formatNumber
                            value="${dashboard.todaySales}"
                            minFractionDigits="2"
                            maxFractionDigits="2"/>
                    </h2>

                    <small>Today's completed sales</small>
                </div>


                <div class="stat-card">
                    <div class="stat-icon product-icon">
                        <i data-lucide="package"></i>
                    </div>

                    <p>Total Products</p>

                    <h2>
                        <c:out value="${dashboard.totalProducts}"/>
                    </h2>

                    <small>Active products</small>
                </div>


                <div class="stat-card">
                    <div class="stat-icon customer-icon">
                        <i data-lucide="users"></i>
                    </div>

                    <p>Total Customers</p>

                    <h2>
                        <c:out value="${dashboard.totalCustomers}"/>
                    </h2>

                    <small>Registered customers</small>
                </div>


                <div class="stat-card">
                    <div class="stat-icon udhar-icon">
                        <i data-lucide="wallet"></i>
                    </div>

                    <p>Pending Udhar</p>

                    <h2>
                        ₹<fmt:formatNumber
                            value="${dashboard.pendingUdhar}"
                            minFractionDigits="2"
                            maxFractionDigits="2"/>
                    </h2>

                    <small>Outstanding customer credit</small>
                </div>

            </div>

        </section>


        <!-- SHOP INFORMATION -->
        <section class="section">

            <div class="section-heading">

                <h2>Shop Information</h2>

                <a href="${pageContext.request.contextPath}/profile">
                    Edit Profile
                    <i data-lucide="arrow-up-right"></i>
                </a>

            </div>

            <div class="details-card">

                <div>
                    <label>Shop Name</label>
                    <strong>
                        <c:out value="${dashboard.shopName}"/>
                    </strong>
                </div>

                <div>
                    <label>Owner Name</label>
                    <strong>
                        <c:out value="${dashboard.ownerName}"/>
                    </strong>
                </div>

                <div>
                    <label>Shop ID</label>
                    <strong>
                        <c:out value="${dashboard.shopCode}"/>
                    </strong>
                </div>

                <div>
                    <label>Active Users</label>
                    <strong>
                        <c:out value="${dashboard.totalUsers}"/>
                    </strong>
                </div>

            </div>

        </section>


        <!-- QUICK ACTIONS -->
        <section class="section">

            <div class="section-heading">
                <h2>Quick Actions</h2>
                <p>Quickly access your daily shop operations</p>
            </div>

            <div class="quick-grid">

                <a href="${pageContext.request.contextPath}/products"
                   class="quick-card">
                    <div class="quick-icon">
                        <i data-lucide="package-plus"></i>
                    </div>
                    <h3>Add Product</h3>
                    <p>Add and manage shop products</p>
                    <span class="quick-arrow">
                        <i data-lucide="arrow-up-right"></i>
                    </span>
                </a>


                <a href="${pageContext.request.contextPath}/sales"
                   class="quick-card">
                    <div class="quick-icon">
                        <i data-lucide="file-plus"></i>
                    </div>
                    <h3>Create Bill</h3>
                    <p>Generate a new customer bill</p>
                    <span class="quick-arrow">
                        <i data-lucide="arrow-up-right"></i>
                    </span>
                </a>


                <a href="${pageContext.request.contextPath}/customers"
                   class="quick-card">
                    <div class="quick-icon">
                        <i data-lucide="user-plus"></i>
                    </div>
                    <h3>Add Customer</h3>
                    <p>Register a new customer</p>
                    <span class="quick-arrow">
                        <i data-lucide="arrow-up-right"></i>
                    </span>
                </a>


                <a href="${pageContext.request.contextPath}/udhar"
                   class="quick-card">
                    <div class="quick-icon">
                        <i data-lucide="wallet"></i>
                    </div>
                    <h3>Collect Udhar</h3>
                    <p>Manage pending customer payments</p>
                    <span class="quick-arrow">
                        <i data-lucide="arrow-up-right"></i>
                    </span>
                </a>


                <a href="${pageContext.request.contextPath}/stock"
                   class="quick-card">
                    <div class="quick-icon">
                        <i data-lucide="boxes"></i>
                    </div>
                    <h3>Update Stock</h3>
                    <p>Manage stock movements</p>
                    <span class="quick-arrow">
                        <i data-lucide="arrow-up-right"></i>
                    </span>
                </a>


                <a href="${pageContext.request.contextPath}/sales-history"
                   class="quick-card">
                    <div class="quick-icon">
                        <i data-lucide="history"></i>
                    </div>
                    <h3>Sales History</h3>
                    <p>View previous transactions</p>
                    <span class="quick-arrow">
                        <i data-lucide="arrow-up-right"></i>
                    </span>
                </a>


                <a href="${pageContext.request.contextPath}/reports"
                   class="quick-card">
                    <div class="quick-icon">
                        <i data-lucide="chart-no-axes-combined"></i>
                    </div>
                    <h3>View Reports</h3>
                    <p>Analyze business performance</p>
                    <span class="quick-arrow">
                        <i data-lucide="arrow-up-right"></i>
                    </span>
                </a>


                <a href="${pageContext.request.contextPath}/categories"
                   class="quick-card">
                    <div class="quick-icon">
                        <i data-lucide="folder-plus"></i>
                    </div>
                    <h3>Manage Categories</h3>
                    <p>Organize product categories</p>
                    <span class="quick-arrow">
                        <i data-lucide="arrow-up-right"></i>
                    </span>
                </a>

            </div>

        </section>


        <!-- PENDING UDHAR AND LOW STOCK -->
        <section class="section">

            <div class="dashboard-details-grid">

                <!-- PENDING UDHAR CUSTOMERS -->
                <div class="dashboard-list-card">

                    <div class="section-heading">
                        <div>
                            <h2>Pending Udhar Customers</h2>
                            <p>Customers with outstanding payments</p>
                        </div>

                        <a href="${pageContext.request.contextPath}/udhar">
                            View All
                            <i data-lucide="arrow-up-right"></i>
                        </a>
                    </div>

                    <div class="dashboard-table-wrapper">

                        <table class="dashboard-table">

                            <thead>
                                <tr>
                                    <th>Customer</th>
                                    <th>Mobile</th>
                                    <th>Pending Amount</th>
                                </tr>
                            </thead>

                            <tbody>

                                <c:choose>
                                    <c:when test="${not empty dashboard.pendingUdharCustomers}">

                                        <c:forEach var="customer"
                                                   items="${dashboard.pendingUdharCustomers}">

                                            <tr>
                                                <td>
                                                    <strong>
                                                        <c:out value="${customer.customerName}"/>
                                                    </strong>
                                                </td>

                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty customer.mobile}">
                                                            <c:out value="${customer.mobile}"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            -
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>

                                                <td>
                                                    <span class="dashboard-amount">
                                                        ₹<fmt:formatNumber
                                                            value="${customer.pendingUdhar}"
                                                            minFractionDigits="2"
                                                            maxFractionDigits="2"/>
                                                    </span>
                                                </td>
                                            </tr>

                                        </c:forEach>

                                    </c:when>

                                    <c:otherwise>
                                        <tr>
                                            <td colspan="3" class="dashboard-empty">
                                                No pending Udhar customers
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>

                            </tbody>

                        </table>

                    </div>

                </div>


                <!-- LOW STOCK PRODUCTS -->
                <div class="dashboard-list-card">

                    <div class="section-heading">
                        <div>
                            <h2>Low Stock Products</h2>
                            <p>Products that need stock attention</p>
                        </div>

                        <a href="${pageContext.request.contextPath}/stock">
                            View All
                            <i data-lucide="arrow-up-right"></i>
                        </a>
                    </div>

                    <div class="dashboard-table-wrapper">

                        <table class="dashboard-table">

                            <thead>
                                <tr>
                                    <th>Product</th>
                                    <th>Available</th>
                                    <th>Minimum</th>
                                </tr>
                            </thead>

                            <tbody>

                                <c:choose>
                                    <c:when test="${not empty dashboard.lowStockProductList}">

                                        <c:forEach var="product"
                                                   items="${dashboard.lowStockProductList}">

                                            <tr>
                                                <td>
                                                    <strong>
                                                        <c:out value="${product.productName}"/>
                                                    </strong>

                                                    <small class="dashboard-sku">
                                                        SKU:
                                                        <c:choose>
                                                            <c:when test="${not empty product.sku}">
                                                                <c:out value="${product.sku}"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                -
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </small>
                                                </td>

                                                <td>
                                                    <span class="stock-warning">
                                                        <fmt:formatNumber
                                                            value="${product.stockQuantity}"
                                                            maxFractionDigits="2"/>
                                                        <c:out value="${product.unit}"/>
                                                    </span>
                                                </td>

                                                <td>
                                                    <fmt:formatNumber
                                                        value="${product.minStockLevel}"
                                                        maxFractionDigits="2"/>
                                                    <c:out value="${product.unit}"/>
                                                </td>
                                            </tr>

                                        </c:forEach>

                                    </c:when>

                                    <c:otherwise>
                                        <tr>
                                            <td colspan="3" class="dashboard-empty">
                                                All products have sufficient stock
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>

                            </tbody>

                        </table>

                    </div>

                </div>

            </div>

        </section>

    </main>

</div>

<!-- INITIALIZE LUCIDE ICONS -->
<script>
    document.addEventListener("DOMContentLoaded", function () {
        if (window.lucide) {
            lucide.createIcons();
        }
    });
</script>

</body>
</html>