
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Management | ShopEase</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/customers.css?v=2">

    <!-- Lucide Icons -->
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body>

<div class="page-container">

    <!-- PAGE HEADER -->
    <header class="page-header">
        <div>
            <h1>Customer Management</h1>
            <p>Manage your shop customers and their information.</p>
        </div>

        <a href="${pageContext.request.contextPath}/dashboard"
           class="back-button">Back to Dashboard</a>
    </header>

    <!-- MESSAGES -->
    <c:if test="${param.success == 'added'}">
        <div class="success-message">Customer added successfully!</div>
    </c:if>

    <c:if test="${param.success == 'updated'}">
        <div class="success-message">Customer updated successfully!</div>
    </c:if>

    <c:if test="${param.success == 'deleted'}">
        <div class="success-message">Customer deactivated successfully!</div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="error-message">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <!-- ADD CUSTOMER -->
    <section class="form-card">

        <div class="section-heading">
            <div>
                <h2>Add New Customer</h2>
                <p>Enter customer contact information.</p>
            </div>
        </div>

        <form action="${pageContext.request.contextPath}/customers"
              method="post">

            <input type="hidden" name="action" value="add">

            <div class="form-grid">

                <div class="form-group">
                    <label>Customer Name *</label>
                    <input type="text"
                           name="customerName"
                           maxlength="150"
                           placeholder="Enter customer name"
                           required>
                </div>

                <div class="form-group">
                    <label>Mobile Number</label>
                    <input type="tel"
                           name="mobile"
                           maxlength="15"
                           pattern="[0-9]{10,15}"
                           placeholder="Enter mobile number">
                </div>

                <div class="form-group">
                    <label>Email Address</label>
                    <input type="email"
                           name="email"
                           maxlength="150"
                           placeholder="Enter email address">
                </div>

                <div class="form-group">
                    <label>Address</label>
                    <input type="text"
                           name="address"
                           maxlength="500"
                           placeholder="Enter customer address">
                </div>

            </div>

            <button type="submit" class="primary-button">
                Add Customer
            </button>

        </form>
    </section>

    <!-- CUSTOMER LIST -->
    <section class="table-card">

        <div class="table-header">
            <div>
                <h2>Customer List</h2>
                <p>All active customers registered in your shop.</p>
            </div>

            <!-- CUSTOMER SEARCH -->
                    <div class="customer-search">
                        <span class="search-symbol"><i data-lucide="search"></i></span>

                        <input
                            type="search"
                            id="customerSearch"
                            placeholder="Search by customer name or mobile number..."
                            autocomplete="off">

                        <button type="button" id="clearCustomerSearch">
                            Clear
                        </button>
                    </div>
        </div>

        <div class="table-wrapper">

            <table>

                <thead>
                    <tr>
                        <th>#</th>
                        <th>Customer Name</th>
                        <th>Mobile</th>
                        <th>Pending Udhar</th>
                        <th>Email</th>
                        <th>Address</th>
                        <th>Registered Date</th>
                        <th>Actions</th>
                    </tr>
                </thead>

                <tbody>

                    <c:forEach var="customer"
                               items="${customers}"
                               varStatus="status">

                        <tr data-customer-row
                            data-name="<c:out value='${customer.customerName}'/>"
                            data-mobile="<c:out value='${customer.mobile}'/>">

                            <td>${status.count}</td>

                            <td>
                                <strong>
                                    <c:out value="${customer.customerName}"/>
                                </strong>
                            </td>

                            <td>
                                <c:out value="${customer.mobile}"
                                       default="Not provided"/>
                            </td>

                            <!-- CUSTOMER PENDING UDHAR -->
                            <td>
                                <c:choose>
                                    <c:when test="${customer.pendingUdhar > 0}">
                                        <span class="pending-amount">
                                            ₹<fmt:formatNumber
                                                value="${customer.pendingUdhar}"
                                                pattern="#,##0.00"/>
                                        </span>
                                    </c:when>

                                    <c:otherwise>
                                        <span class="no-pending">
                                            No Pending
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td>
                                <c:out value="${customer.email}"
                                       default="Not provided"/>
                            </td>

                            <td>
                                <c:out value="${customer.address}"
                                       default="Not provided"/>
                            </td>

                            <td>
                                <c:out value="${customer.createdDate}"/>
                            </td>

                            <td class="action-cell">

                                <button type="button"
                                        class="edit-button"
                                        onclick="openEditModal(
                                            this.dataset.id,
                                            this.dataset.name,
                                            this.dataset.mobile,
                                            this.dataset.email,
                                            this.dataset.address
                                        )"
                                        data-id="<c:out value='${customer.customerId}'/>"
                                        data-name="<c:out value='${customer.customerName}'/>"
                                        data-mobile="<c:out value='${customer.mobile}'/>"
                                        data-email="<c:out value='${customer.email}'/>"
                                        data-address="<c:out value='${customer.address}'/>">
                                    Edit
                                </button>

                                <form action="${pageContext.request.contextPath}/customers"
                                      method="post"
                                      onsubmit="return confirm('Are you sure you want to deactivate this customer?');">

                                    <input type="hidden" name="action" value="delete">

                                    <input type="hidden"
                                           name="customerId"
                                           value="${customer.customerId}">

                                    <button type="submit" class="delete-button">
                                        Delete
                                    </button>
                                </form>

                            </td>

                        </tr>

                    </c:forEach>

                    <c:if test="${empty customers}">
                        <tr>
                            <td colspan="8" class="empty-state">
                                No customers registered yet.
                            </td>
                        </tr>
                    </c:if>

                    <!-- NO SEARCH RESULTS -->
                    <tr id="noSearchResults" style="display: none;">
                        <td colspan="8" class="empty-state">
                            No customers found matching your search.
                        </td>
                    </tr>

                </tbody>

            </table>

        </div>
    </section>

</div>


<!-- EDIT CUSTOMER MODAL -->
<div class="modal-overlay" id="editModal">

    <div class="modal-card">

        <div class="modal-header">
            <div>
                <h2>Edit Customer</h2>
                <p>Update customer information.</p>
            </div>

            <button type="button"
                    class="close-button"
                    onclick="closeEditModal()">&times;</button>
        </div>

        <form action="${pageContext.request.contextPath}/customers"
              method="post">

            <input type="hidden" name="action" value="update">
            <input type="hidden" name="customerId" id="editCustomerId">

            <div class="form-group">
                <label>Customer Name *</label>
                <input type="text"
                       name="customerName"
                       id="editCustomerName"
                       maxlength="150"
                       required>
            </div>

            <div class="form-group">
                <label>Mobile Number</label>
                <input type="tel"
                       name="mobile"
                       id="editMobile"
                       maxlength="15"
                       pattern="[0-9]{10,15}">
            </div>

            <div class="form-group">
                <label>Email Address</label>
                <input type="email"
                       name="email"
                       id="editEmail"
                       maxlength="150">
            </div>

            <div class="form-group">
                <label>Address</label>
                <input type="text"
                       name="address"
                       id="editAddress"
                       maxlength="500">
            </div>

            <div class="modal-actions">
                <button type="button"
                        class="cancel-button"
                        onclick="closeEditModal()">Cancel</button>

                <button type="submit" class="primary-button">
                    Save Changes
                </button>
            </div>

        </form>

    </div>
</div>


<script>

// =====================================
// EDIT CUSTOMER MODAL
// =====================================

function openEditModal(id, name, mobile, email, address) {

    document.getElementById("editCustomerId").value = id;
    document.getElementById("editCustomerName").value = name || "";
    document.getElementById("editMobile").value = mobile || "";
    document.getElementById("editEmail").value = email || "";
    document.getElementById("editAddress").value = address || "";

    document.getElementById("editModal").classList.add("active");
}

function closeEditModal() {
    document.getElementById("editModal").classList.remove("active");
}

document.getElementById("editModal").addEventListener("click", function(e) {
    if (e.target === this) {
        closeEditModal();
    }
});


// =====================================
// CUSTOMER LIVE SEARCH
// =====================================

const customerSearch = document.getElementById("customerSearch");
const clearCustomerSearch = document.getElementById("clearCustomerSearch");
const customerCount = document.getElementById("customerCount");
const noSearchResults = document.getElementById("noSearchResults");

const customerRows = document.querySelectorAll("[data-customer-row]");

function filterCustomers() {

    const searchValue = customerSearch.value.trim().toLowerCase();

    let visibleCount = 0;

    customerRows.forEach(function(row) {

        const name = (row.dataset.name || "").toLowerCase();
        const mobile = (row.dataset.mobile || "").toLowerCase();

        const matches =
            name.includes(searchValue) ||
            mobile.includes(searchValue);

        row.style.display = matches ? "" : "none";

        if (matches) {
            visibleCount++;
        }
    });

    customerCount.textContent =
        visibleCount + (visibleCount === 1 ? " Customer" : " Customers");

    noSearchResults.style.display =
        searchValue !== "" && visibleCount === 0 ? "" : "none";
}

customerSearch.addEventListener("input", filterCustomers);

clearCustomerSearch.addEventListener("click", function() {

    customerSearch.value = "";

    filterCustomers();

    customerSearch.focus();
});

</script>

</body>
</html>