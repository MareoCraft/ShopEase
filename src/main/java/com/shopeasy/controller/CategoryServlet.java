
package com.shopeasy.controller;

import com.shopeasy.model.Category;
import com.shopeasy.service.CategoryService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {

    private final CategoryService categoryService = new CategoryService();

    // DISPLAY CATEGORIES
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("shopId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int shopId = (Integer) session.getAttribute("shopId");

        try {
            List<Category> categories =
                    categoryService.getCategories(shopId);

            request.setAttribute("categories", categories);

            request.getRequestDispatcher("/categories.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException(
                    "Unable to load categories.", e);
        }
    }

    // HANDLE CATEGORY OPERATIONS
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("shopId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int shopId = (Integer) session.getAttribute("shopId");
        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {

                Category category = new Category();
                category.setShopId(shopId);
                category.setCategoryName(
                        request.getParameter("categoryName"));
                category.setDescription(
                        request.getParameter("description"));

                categoryService.addCategory(category);

                response.sendRedirect(
                        request.getContextPath()
                                + "/categories?success=added");
                return;

            } else if ("update".equals(action)) {

                int categoryId = Integer.parseInt(
                        request.getParameter("categoryId"));

                Category category = new Category();
                category.setCategoryId(categoryId);
                category.setShopId(shopId);
                category.setCategoryName(
                        request.getParameter("categoryName"));
                category.setDescription(
                        request.getParameter("description"));

                categoryService.updateCategory(category);

                response.sendRedirect(
                        request.getContextPath()
                                + "/categories?success=updated");
                return;

            } else if ("delete".equals(action)) {

                int categoryId = Integer.parseInt(
                        request.getParameter("categoryId"));

                categoryService.deleteCategory(categoryId, shopId);

                response.sendRedirect(
                        request.getContextPath()
                                + "/categories?success=deleted");
                return;

            } else {
                throw new IllegalArgumentException(
                        "Invalid category action.");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error",
                    "Invalid category ID.");

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());

        } catch (Exception e) {
            throw new ServletException(
                    "Unable to process category operation.", e);
        }

        // Reload categories when an operation fails
        try {
            List<Category> categories =
                    categoryService.getCategories(shopId);

            request.setAttribute("categories", categories);

            request.getRequestDispatcher("/categories.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException(
                    "Unable to reload categories.", e);
        }
    }
}
