
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Product Management | ShopEase</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/products.css">
</head>

<body>

<div class="page-container">

    <!-- HEADER -->
    <header class="page-header">
        <div>
            <h1>Product Management</h1>
            <p>Manage your shop products, prices and inventory.</p>
        </div>

        <a href="${pageContext.request.contextPath}/dashboard"
           class="back-button">
            Back to Dashboard
        </a>
    </header>

    <!-- SUCCESS MESSAGES -->
    <c:if test="${param.success == 'added'}">
        <div class="success-message">Product added successfully!</div>
    </c:if>

    <c:if test="${param.success == 'updated'}">
        <div class="success-message">Product updated successfully!</div>
    </c:if>

    <c:if test="${param.success == 'deleted'}">
        <div class="success-message">Product deleted successfully!</div>
    </c:if>

    <!-- ERROR MESSAGE -->
    <c:if test="${not empty error}">
        <div class="error-message">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <div class="product-layout">

        <!-- ADD PRODUCT FORM -->
        <section class="form-card">

            <h2>Add New Product</h2>
            <p class="section-description">
                Enter product details to add it to your inventory.
            </p>

            <c:choose>
                <c:when test="${empty categories}">
                    <div class="warning-message">
                        No active categories found.
                        <a href="${pageContext.request.contextPath}/categories">
                            Create a category first.
                        </a>
                    </div>
                </c:when>

                <c:otherwise>

                    <form action="${pageContext.request.contextPath}/products"
                          method="post">

                        <input type="hidden" name="action" value="add">

                        <div class="form-group">
                            <label for="categoryId">Category *</label>

                            <select id="categoryId"
                                    name="categoryId"
                                    required>

                                <option value="">Select Category</option>

                                <c:forEach var="category" items="${categories}">
                                    <option value="${category.categoryId}">
                                        <c:out value="${category.categoryName}"/>
                                    </option>
                                </c:forEach>

                            </select>
                        </div>

                        <div class="form-group">
                            <label for="productName">Product Name *</label>

                            <input type="text"
                                   id="productName"
                                   name="productName"
                                   placeholder="Example: Rice 1kg"
                                   maxlength="150"
                                   required>
                        </div>

                        <div class="form-group">
                            <label for="sku">SKU</label>

                            <input type="text"
                                   id="sku"
                                   name="sku"
                                   placeholder="Example: RICE001"
                                   maxlength="50">
                        </div>

                        <div class="form-group">
                            <label for="barcode">Barcode</label>

                            <input type="text"
                                   id="barcode"
                                   name="barcode"
                                   placeholder="Enter barcode"
                                   maxlength="100">
                        </div>

                        <div class="form-grid">

                            <div class="form-group">
                                <label for="purchasePrice">Purchase Price (₹)</label>

                                <input type="number"
                                       id="purchasePrice"
                                       name="purchasePrice"
                                       min="0"
                                       step="0.01"
                                       value="0">
                            </div>

                            <div class="form-group">
                                <label for="sellingPrice">Selling Price (₹) *</label>

                                <input type="number"
                                       id="sellingPrice"
                                       name="sellingPrice"
                                       min="0.01"
                                       step="0.01"
                                       placeholder="0.00"
                                       required>
                            </div>

                            <div class="form-group">
                                <label for="stockQuantity">Opening Stock *</label>

                                <input type="number"
                                       id="stockQuantity"
                                       name="stockQuantity"
                                       min="0"
                                       step="0.01"
                                       value="0"
                                       required>
                            </div>

                            <div class="form-group">
                                <label for="minStockLevel">Minimum Stock</label>

                                <input type="number"
                                       id="minStockLevel"
                                       name="minStockLevel"
                                       min="0"
                                       step="0.01"
                                       value="5">
                            </div>

                        </div>

                        <div class="form-group">
                            <label for="unit">Unit</label>

                            <select id="unit" name="unit">
                                <option value="PCS">Pieces (PCS)</option>
                                <option value="KG">Kilogram (KG)</option>
                                <option value="GRAM">Gram (GRAM)</option>
                                <option value="LITRE">Litre (LITRE)</option>
                                <option value="ML">Millilitre (ML)</option>
                                <option value="PACK">Packet (PACK)</option>
                                <option value="BOX">Box (BOX)</option>
                                <option value="DOZEN">Dozen (DOZEN)</option>
                                <option value="METER">Meter (METER)</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="description">Description</label>

                            <textarea id="description"
                                      name="description"
                                      rows="3"
                                      maxlength="500"
                                      placeholder="Optional product description"></textarea>
                        </div>

                        <button type="submit" class="primary-button">
                            Add Product
                        </button>

                    </form>

                </c:otherwise>
            </c:choose>

        </section>

        <!-- PRODUCT TABLE -->
        <section class="table-card">

            <div class="table-header">
                <div>
                    <h2>Your Products</h2>
                    <p class="section-description">
                        View and manage your inventory.
                    </p>
                </div>

                <span class="product-count">
                    ${products.size()} Products
                </span>
            </div>

            <div class="table-wrapper">

                <table>

                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Product</th>
                        <th>Category</th>
                        <th>SKU</th>
                        <th>Purchase Price</th>
                        <th>Selling Price</th>
                        <th>Stock</th>
                        <th>Actions</th>
                    </tr>
                    </thead>

                    <tbody>

                    <c:forEach var="product"
                               items="${products}"
                               varStatus="status">

                        <tr>

                            <td>${status.count}</td>

                            <td>
                                <strong>
                                    <c:out value="${product.productName}"/>
                                </strong>
                                <div class="sub-text">
                                    <c:out value="${product.unit}"/>
                                </div>
                            </td>

                            <td>
                                <c:out value="${product.categoryName}"/>
                            </td>

                            <td>
                                <c:choose>
                                    <c:when test="${not empty product.sku}">
                                        <c:out value="${product.sku}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="muted">—</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td>
                                ₹<c:out value="${product.purchasePrice}"/>
                            </td>

                            <td>
                                <strong>
                                    ₹<c:out value="${product.sellingPrice}"/>
                                </strong>
                            </td>

                            <td>
                                <c:choose>
                                    <c:when test="${product.stockQuantity <= product.minStockLevel}">
                                        <span class="stock-badge low-stock">
                                            <c:out value="${product.stockQuantity}"/>
                                            <c:out value="${product.unit}"/>
                                        </span>
                                    </c:when>

                                    <c:otherwise>
                                        <span class="stock-badge in-stock">
                                            <c:out value="${product.stockQuantity}"/>
                                            <c:out value="${product.unit}"/>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td>
                                <div class="action-buttons">

                                    <button type="button"
                                            class="edit-button"
                                            onclick="openEditModal(this)"

                                            data-id="<c:out value='${product.productId}'/>"
                                            data-category="<c:out value='${product.categoryId}'/>"
                                            data-name="<c:out value='${product.productName}'/>"
                                            data-sku="<c:out value='${product.sku}'/>"
                                            data-barcode="<c:out value='${product.barcode}'/>"
                                            data-purchase="<c:out value='${product.purchasePrice}'/>"
                                            data-selling="<c:out value='${product.sellingPrice}'/>"
                                            data-stock="<c:out value='${product.stockQuantity}'/>"
                                            data-minstock="<c:out value='${product.minStockLevel}'/>"
                                            data-unit="<c:out value='${product.unit}'/>"
                                            data-description="<c:out value='${product.description}'/>">

                                        Edit
                                    </button>

                                    <form action="${pageContext.request.contextPath}/products"
                                          method="post"
                                          onsubmit="return confirm('Are you sure you want to delete this product?');">

                                        <input type="hidden"
                                               name="action"
                                               value="delete">

                                        <input type="hidden"
                                               name="productId"
                                               value="${product.productId}">

                                        <button type="submit"
                                                class="delete-button">
                                            Delete
                                        </button>

                                    </form>

                                </div>
                            </td>

                        </tr>

                    </c:forEach>

                    <c:if test="${empty products}">
                        <tr>
                            <td colspan="8" class="empty-state">
                                No products found. Add your first product.
                            </td>
                        </tr>
                    </c:if>

                    </tbody>

                </table>

            </div>

        </section>

    </div>

</div>

<!-- EDIT PRODUCT MODAL -->
<div class="modal-overlay" id="editModal">

    <div class="modal-card">

        <div class="modal-header">
            <div>
                <h2>Edit Product</h2>
                <p>Update product information.</p>
            </div>

            <button type="button"
                    class="close-button"
                    onclick="closeEditModal()">
                &times;
            </button>
        </div>

        <form action="${pageContext.request.contextPath}/products"
              method="post">

            <input type="hidden" name="action" value="update">

            <input type="hidden"
                   name="productId"
                   id="editProductId">

            <div class="form-group">
                <label for="editCategoryId">Category *</label>

                <select name="categoryId"
                        id="editCategoryId"
                        required>

                    <c:forEach var="category" items="${categories}">
                        <option value="${category.categoryId}">
                            <c:out value="${category.categoryName}"/>
                        </option>
                    </c:forEach>

                </select>
            </div>

            <div class="form-group">
                <label for="editProductName">Product Name *</label>

                <input type="text"
                       name="productName"
                       id="editProductName"
                       maxlength="150"
                       required>
            </div>

            <div class="form-group">
                <label for="editSku">SKU</label>

                <input type="text"
                       name="sku"
                       id="editSku"
                       maxlength="50">
            </div>

            <div class="form-group">
                <label for="editBarcode">Barcode</label>

                <input type="text"
                       name="barcode"
                       id="editBarcode"
                       maxlength="100">
            </div>

            <div class="form-grid">

                <div class="form-group">
                    <label for="editPurchasePrice">Purchase Price (₹)</label>

                    <input type="number"
                           name="purchasePrice"
                           id="editPurchasePrice"
                           min="0"
                           step="0.01"
                           required>
                </div>

                <div class="form-group">
                    <label for="editSellingPrice">Selling Price (₹)</label>

                    <input type="number"
                           name="sellingPrice"
                           id="editSellingPrice"
                           min="0.01"
                           step="0.01"
                           required>
                </div>

                <div class="form-group">
                    <label for="editStockQuantity">Current Stock</label>

                    <input type="number"
                           name="stockQuantity"
                           id="editStockQuantity"
                           readonly>
                    <small>Stock changes are managed separately.</small>
                </div>

                <div class="form-group">
                    <label for="editMinStockLevel">Minimum Stock</label>

                    <input type="number"
                           name="minStockLevel"
                           id="editMinStockLevel"
                           min="0"
                           step="0.01"
                           required>
                </div>

            </div>

            <div class="form-group">
                <label for="editUnit">Unit</label>

                <select name="unit" id="editUnit">
                    <option value="PCS">Pieces (PCS)</option>
                    <option value="KG">Kilogram (KG)</option>
                    <option value="GRAM">Gram (GRAM)</option>
                    <option value="LITRE">Litre (LITRE)</option>
                    <option value="ML">Millilitre (ML)</option>
                    <option value="PACK">Packet (PACK)</option>
                    <option value="BOX">Box (BOX)</option>
                    <option value="DOZEN">Dozen (DOZEN)</option>
                    <option value="METER">Meter (METER)</option>
                </select>
            </div>

            <div class="form-group">
                <label for="editDescription">Description</label>

                <textarea name="description"
                          id="editDescription"
                          rows="3"
                          maxlength="500"></textarea>
            </div>

            <div class="modal-actions">

                <button type="button"
                        class="cancel-button"
                        onclick="closeEditModal()">
                    Cancel
                </button>

                <button type="submit" class="primary-button">
                    Update Product
                </button>

            </div>

        </form>

    </div>

</div>

<script>

    function openEditModal(button) {

        document.getElementById("editProductId").value =
            button.dataset.id;

        document.getElementById("editCategoryId").value =
            button.dataset.category;

        document.getElementById("editProductName").value =
            button.dataset.name || "";

        document.getElementById("editSku").value =
            button.dataset.sku || "";

        document.getElementById("editBarcode").value =
            button.dataset.barcode || "";

        document.getElementById("editPurchasePrice").value =
            button.dataset.purchase;

        document.getElementById("editSellingPrice").value =
            button.dataset.selling;

        document.getElementById("editStockQuantity").value =
            button.dataset.stock;

        document.getElementById("editMinStockLevel").value =
            button.dataset.minstock;

        document.getElementById("editUnit").value =
            button.dataset.unit || "PCS";

        document.getElementById("editDescription").value =
            button.dataset.description || "";

        document.getElementById("editModal").classList.add("active");
    }

    function closeEditModal() {
        document.getElementById("editModal").classList.remove("active");
    }

    document.getElementById("editModal").addEventListener("click", function(event) {
        if (event.target === this) {
            closeEditModal();
        }
    });

</script>

</body>
</html>