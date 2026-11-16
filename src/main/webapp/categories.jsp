
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Category Management | ShopEase</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/categories.css">
</head>

<body>

<div class="page-container">

    <!-- HEADER -->
    <header class="page-header">
        <div>
            <h1>Category Management</h1>
            <p>Organize your products into categories.</p>
        </div>

        <a href="${pageContext.request.contextPath}/dashboard"
           class="back-button">
            Back to Dashboard
        </a>
    </header>

    <!-- SUCCESS MESSAGE -->
    <c:if test="${param.success == 'added'}">
        <div class="success-message">
            Category added successfully!
        </div>
    </c:if>

    <c:if test="${param.success == 'updated'}">
        <div class="success-message">
            Category updated successfully!
        </div>
    </c:if>

    <c:if test="${param.success == 'deleted'}">
        <div class="success-message">
            Category deleted successfully!
        </div>
    </c:if>

    <!-- ERROR MESSAGE -->
    <c:if test="${not empty error}">
        <div class="error-message">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <div class="category-layout">

        <!-- ADD CATEGORY FORM -->
        <section class="form-card">

            <h2>Add New Category</h2>
            <p class="section-description">
                Enter the details of your new category.
            </p>

            <form action="${pageContext.request.contextPath}/categories"
                  method="post">

                <input type="hidden" name="action" value="add">

                <div class="form-group">
                    <label for="categoryName">Category Name *</label>

                    <input type="text"
                           id="categoryName"
                           name="categoryName"
                           placeholder="Example: Groceries"
                           maxlength="100"
                           required>
                </div>

                <div class="form-group">
                    <label for="description">Description</label>

                    <textarea id="description"
                              name="description"
                              placeholder="Enter category description"
                              maxlength="250"
                              rows="4"></textarea>
                </div>

                <button type="submit" class="primary-button">
                    Add Category
                </button>

            </form>
        </section>

        <!-- CATEGORY LIST -->
        <section class="table-card">

            <div class="table-header">
                <div>
                    <h2>Your Categories</h2>
                    <p class="section-description">
                        Manage your existing categories.
                    </p>
                </div>

                <span class="category-count">
                    ${categories.size()} Categories
                </span>
            </div>

            <div class="table-wrapper">

                <table>
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Category Name</th>
                        <th>Description</th>
                        <th>Created Date</th>
                        <th>Actions</th>
                    </tr>
                    </thead>

                    <tbody>

                    <c:forEach var="category"
                               items="${categories}"
                               varStatus="status">

                        <tr>
                            <td>${status.count}</td>

                            <td>
                                <strong>
                                    <c:out value="${category.categoryName}"/>
                                </strong>
                            </td>

                            <td>
                                <c:choose>
                                    <c:when test="${not empty category.description}">
                                        <c:out value="${category.description}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="muted">No description</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td>
                                <c:out value="${category.createdDate}"/>
                            </td>

                            <td>
                                <div class="action-buttons">

                                    <button type="button"
                                            class="edit-button"
                                            onclick="openEditModal(
                                                '${category.categoryId}',
                                                this
                                            )"
                                            data-name="<c:out value='${category.categoryName}'/>"
                                            data-description="<c:out value='${category.description}'/>">
                                        Edit
                                    </button>

                                    <form action="${pageContext.request.contextPath}/categories"
                                          method="post"
                                          onsubmit="return confirm('Are you sure you want to delete this category?');">

                                        <input type="hidden"
                                               name="action"
                                               value="delete">

                                        <input type="hidden"
                                               name="categoryId"
                                               value="${category.categoryId}">

                                        <button type="submit"
                                                class="delete-button">
                                            Delete
                                        </button>
                                    </form>

                                </div>
                            </td>
                        </tr>

                    </c:forEach>

                    <c:if test="${empty categories}">
                        <tr>
                            <td colspan="5" class="empty-state">
                                No categories found. Add your first category.
                            </td>
                        </tr>
                    </c:if>

                    </tbody>
                </table>

            </div>
        </section>

    </div>
</div>

<!-- EDIT CATEGORY MODAL -->
<div class="modal-overlay" id="editModal">

    <div class="modal-card">

        <div class="modal-header">
            <h2>Edit Category</h2>

            <button type="button"
                    class="close-button"
                    onclick="closeEditModal()">
                &times;
            </button>
        </div>

        <form action="${pageContext.request.contextPath}/categories"
              method="post">

            <input type="hidden" name="action" value="update">

            <input type="hidden"
                   name="categoryId"
                   id="editCategoryId">

            <div class="form-group">
                <label for="editCategoryName">Category Name *</label>

                <input type="text"
                       id="editCategoryName"
                       name="categoryName"
                       maxlength="100"
                       required>
            </div>

            <div class="form-group">
                <label for="editDescription">Description</label>

                <textarea id="editDescription"
                          name="description"
                          maxlength="250"
                          rows="4"></textarea>
            </div>

            <div class="modal-actions">
                <button type="button"
                        class="cancel-button"
                        onclick="closeEditModal()">
                    Cancel
                </button>

                <button type="submit" class="primary-button">
                    Update Category
                </button>
            </div>

        </form>

    </div>
</div>

<script>
    function openEditModal(categoryId, button) {

        document.getElementById("editCategoryId").value = categoryId;

        document.getElementById("editCategoryName").value =
            button.dataset.name || "";

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