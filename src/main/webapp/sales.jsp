
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create New Bill | ShopEasy</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sales.css">
</head>

<body>

<div class="sales-container">

    <div class="page-header">
        <div>
            <h1>Create New Bill</h1>
            <p>Create a customer bill and record the payment.</p>
        </div>
        <a href="${pageContext.request.contextPath}/dashboard" class="back-btn">
            Back to Dashboard
        </a>
    </div>

    <c:if test="${not empty param.success}">
        <div class="success-message">Bill created successfully.</div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="error-message"><c:out value="${error}"/></div>
    </c:if>

    <form action="${pageContext.request.contextPath}/sales"
          method="post" id="salesForm">

        <!-- CUSTOMER DETAILS -->
        <section class="sales-card">
            <h2>Customer Details</h2>

            <label for="customerId">Select Customer <span>*</span></label>

            <select name="customerId" id="customerId" required>
                <option value="">Choose registered customer</option>

                <c:forEach var="customer" items="${customers}">
                    <option value="${customer.customerId}">
                        <c:out value="${customer.customerName}"/>
                        <c:if test="${not empty customer.mobile}">
                            - <c:out value="${customer.mobile}"/>
                        </c:if>
                    </option>
                </c:forEach>
            </select>

            <c:if test="${empty customers}">
                <p class="field-note">
                    No registered customers found. Please add a customer first.
                    <a href="${pageContext.request.contextPath}/customers">Add Customer</a>
                </p>
            </c:if>
        </section>

        <!-- PRODUCT DETAILS -->
        <section class="sales-card">
            <div class="section-heading">
                <div>
                    <h2>Products</h2>
                    <p>Add one or more products to this bill.</p>
                </div>

                <button type="button" id="addProductBtn" class="add-btn">
                    + Add Product
                </button>
            </div>

            <div class="table-wrapper">
                <table class="sales-table">
                    <thead>
                    <tr>
                        <th>Product</th>
                        <th>Available Stock</th>
                        <th>Unit Price</th>
                        <th>Quantity</th>
                        <th>Line Total</th>
                        <th>Action</th>
                    </tr>
                    </thead>

                    <tbody id="productRows">

                    <tr class="product-row">
                        <td>
                            <select name="productId" class="product-select" required>
                                <option value="">Select Product</option>

                                <c:forEach var="product" items="${products}">
                                    <option
                                        value="${product.productId}"
                                        data-price="${product.sellingPrice}"
                                        data-stock="${product.stockQuantity}"
                                        data-unit="${product.unit}">
                                        <c:out value="${product.productName}"/>
                                        <c:if test="${not empty product.sku}">
                                            (<c:out value="${product.sku}"/>)
                                        </c:if>
                                    </option>
                                </c:forEach>
                            </select>
                        </td>

                        <td class="stock-cell">-</td>

                        <td>
                            <input type="text" class="price-input" value="0.00" readonly>
                        </td>

                        <td>
                            <input type="number" name="quantity"
                                   class="quantity-input"
                                   min="0.01" step="0.01"
                                   placeholder="Qty" required>
                        </td>

                        <td>
                            <input type="text" class="line-total" value="0.00" readonly>
                        </td>

                        <td>
                            <button type="button" class="remove-btn"
                                    onclick="removeProductRow(this)" disabled>
                                Remove
                            </button>
                        </td>
                    </tr>

                    </tbody>
                </table>
            </div>

            <c:if test="${empty products}">
                <p class="field-note">
                    No active products found. Please add products first.
                    <a href="${pageContext.request.contextPath}/products">Add Product</a>
                </p>
            </c:if>
        </section>

        <!-- BILL SUMMARY -->
        <section class="sales-card">
            <h2>Bill Summary</h2>

            <div class="summary-row">
                <span>Subtotal</span>
                <strong>₹ <span id="subtotal">0.00</span></strong>
            </div>

            <div class="discount-row">
                <label for="discountAmount">Discount Amount (₹)</label>
                <input type="number" name="discountAmount"
                       id="discountAmount" value="0"
                       min="0" step="0.01">
            </div>

            <div class="summary-row grand-total">
                <span>Grand Total</span>
                <strong>₹ <span id="grandTotal">0.00</span></strong>
            </div>
        </section>

        <!-- PAYMENT DETAILS -->
        <section class="sales-card">
            <h2>Payment Details</h2>

            <div class="payment-grid">
                <div>
                    <label for="paymentMethod">Payment Method <span>*</span></label>

                    <select name="paymentMethod" id="paymentMethod" required>
                        <option value="">Select Payment Method</option>
                        <option value="CASH">Cash</option>
                        <option value="UPI">UPI</option>
                        <option value="CARD">Card</option>
                        <option value="CREDIT">Credit / Udhar</option>
                    </select>
                </div>

                <div>
                    <label for="paymentReference">Payment Reference</label>

                    <input type="text" name="paymentReference"
                           id="paymentReference"
                           maxlength="150"
                           placeholder="Optional transaction reference">
                </div>
            </div>

            <p class="payment-note" id="paymentNote">
                Select a payment method to see payment information.
            </p>
        </section>

        <!-- SUBMIT -->
        <div class="form-actions">
            <button type="reset" class="cancel-btn" id="resetBtn">
                Reset
            </button>

            <button type="submit" class="submit-btn"
                    <c:if test="${empty customers or empty products}">disabled</c:if>>
                Generate Bill
            </button>
        </div>

    </form>
</div>

<!-- PRODUCT OPTIONS TEMPLATE -->
<template id="productOptionsTemplate">
    <option value="">Select Product</option>

    <c:forEach var="product" items="${products}">
        <option
            value="${product.productId}"
            data-price="${product.sellingPrice}"
            data-stock="${product.stockQuantity}"
            data-unit="${product.unit}">

            <c:out value="${product.productName}"/>

            <c:if test="${not empty product.sku}">
                (<c:out value="${product.sku}"/>)
            </c:if>

        </option>
    </c:forEach>
</template>

<script>
    const productRows = document.getElementById("productRows");
    const addProductBtn = document.getElementById("addProductBtn");
    const discountInput = document.getElementById("discountAmount");
    const paymentMethod = document.getElementById("paymentMethod");
    const paymentNote = document.getElementById("paymentNote");

    function updateRow(row) {
        const select = row.querySelector(".product-select");
        const quantityInput = row.querySelector(".quantity-input");
        const priceInput = row.querySelector(".price-input");
        const stockCell = row.querySelector(".stock-cell");
        const totalInput = row.querySelector(".line-total");

        const option = select.options[select.selectedIndex];

        if (!option || !option.value) {
            priceInput.value = "0.00";
            stockCell.textContent = "-";
            quantityInput.removeAttribute("max");
            totalInput.value = "0.00";
            calculateBill();
            return;
        }

        const price = Number(option.dataset.price);
        const stock = Number(option.dataset.stock);
        const unit = option.dataset.unit || "";

        priceInput.value = price.toFixed(2);
        stockCell.textContent = stock + " " + unit;
        quantityInput.max = stock;

        if (Number(quantityInput.value) > stock) {
            quantityInput.value = stock;
        }

        calculateBill();
    }

    function calculateBill() {
        let subtotal = 0;

        document.querySelectorAll(".product-row").forEach(row => {
            const select = row.querySelector(".product-select");
            const quantity = Number(row.querySelector(".quantity-input").value) || 0;
            const option = select.options[select.selectedIndex];

            const price = option && option.value
                ? Number(option.dataset.price)
                : 0;

            const lineTotal = price * quantity;

            row.querySelector(".line-total").value = lineTotal.toFixed(2);
            subtotal += lineTotal;
        });

        let discount = Number(discountInput.value) || 0;

        if (discount < 0) discount = 0;

        const grandTotal = Math.max(0, subtotal - discount);

        document.getElementById("subtotal").textContent = subtotal.toFixed(2);
        document.getElementById("grandTotal").textContent = grandTotal.toFixed(2);
    }

    function addProductRow() {
        const firstRow = document.querySelector(".product-row");
        const newRow = firstRow.cloneNode(true);

        const select = newRow.querySelector(".product-select");
        const quantity = newRow.querySelector(".quantity-input");

        select.selectedIndex = 0;
        quantity.value = "";
        newRow.querySelector(".price-input").value = "0.00";
        newRow.querySelector(".stock-cell").textContent = "-";
        newRow.querySelector(".line-total").value = "0.00";
        newRow.querySelector(".remove-btn").disabled = false;

        productRows.appendChild(newRow);
        updateRemoveButtons();
        calculateBill();
    }

    function removeProductRow(button) {
        if (document.querySelectorAll(".product-row").length <= 1) {
            return;
        }

        button.closest(".product-row").remove();
        updateRemoveButtons();
        calculateBill();
    }

    function updateRemoveButtons() {
        const rows = document.querySelectorAll(".product-row");

        rows.forEach(row => {
            row.querySelector(".remove-btn").disabled = rows.length === 1;
        });
    }

    addProductBtn.addEventListener("click", addProductRow);

    productRows.addEventListener("change", function(event) {
        if (event.target.classList.contains("product-select")) {
            updateRow(event.target.closest(".product-row"));
        }
    });

    productRows.addEventListener("input", function(event) {
        if (event.target.classList.contains("quantity-input")) {
            const row = event.target.closest(".product-row");
            const select = row.querySelector(".product-select");
            const option = select.options[select.selectedIndex];

            if (option && option.value) {
                const stock = Number(option.dataset.stock);

                if (Number(event.target.value) > stock) {
                    event.target.value = stock;
                }
            }

            calculateBill();
        }
    });

    discountInput.addEventListener("input", calculateBill);

    paymentMethod.addEventListener("change", function() {
        const messages = {
            CASH: "Cash payment selected. Full bill amount will be recorded as paid.",
            UPI: "UPI payment selected. Full bill amount will be recorded as paid.",
            CARD: "Card payment selected. Full bill amount will be recorded as paid.",
            CREDIT: "Credit selected. No payment will be collected now; the amount will be recorded as outstanding."
        };

        paymentNote.textContent = messages[this.value] ||
            "Select a payment method to see payment information.";
    });

    document.getElementById("salesForm").addEventListener("submit", function(event) {
        const selectedProducts = [];

        let invalid = false;

        document.querySelectorAll(".product-row").forEach(row => {
            const select = row.querySelector(".product-select");
            const quantity = Number(row.querySelector(".quantity-input").value);
            const option = select.options[select.selectedIndex];

            if (!option || !option.value || !quantity || quantity <= 0) {
                invalid = true;
                return;
            }

            const stock = Number(option.dataset.stock);

            if (quantity > stock) {
                invalid = true;
                return;
            }

            if (selectedProducts.includes(select.value)) {
                invalid = true;
                alert("Do not add the same product more than once. Update its quantity instead.");
                return;
            }

            selectedProducts.push(select.value);
        });

        const subtotal = Number(document.getElementById("subtotal").textContent);
        const discount = Number(discountInput.value) || 0;

        if (invalid) {
            event.preventDefault();
            if (!selectedProducts.length) {
                alert("Please select at least one valid product and quantity.");
            } else {
                alert("Please check product quantities and selections.");
            }
            return;
        }

        if (discount < 0 || discount > subtotal) {
            event.preventDefault();
            alert("Discount cannot be negative or greater than the subtotal.");
            return;
        }

        if (!paymentMethod.value) {
            event.preventDefault();
            alert("Please select a payment method.");
        }
    });

    document.getElementById("resetBtn").addEventListener("click", function() {
        setTimeout(() => {
            document.querySelectorAll(".product-row").forEach((row, index) => {
                if (index > 0) row.remove();
                else updateRow(row);
            });

            updateRemoveButtons();
            calculateBill();
            paymentNote.textContent =
                "Select a payment method to see payment information.";
        }, 0);
    });

    calculateBill();
</script>

</body>
</html>