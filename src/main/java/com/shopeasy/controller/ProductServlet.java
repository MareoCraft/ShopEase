
package com.shopeasy.controller;

import com.shopeasy.model.Category;
import com.shopeasy.model.Product;
import com.shopeasy.service.CategoryService;
import com.shopeasy.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    // DISPLAY PRODUCTS
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
            loadProductPage(request, shopId);

            request.getRequestDispatcher("/products.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Unable to load products.", e);
        }
    }

    // ADD, UPDATE AND DELETE PRODUCTS
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

                Product product = createProductFromRequest(request, shopId);

                productService.addProduct(product);

                response.sendRedirect(
                        request.getContextPath() + "/products?success=added");
                return;

            } else if ("update".equals(action)) {

                Product product = createProductFromRequest(request, shopId);

                product.setProductId(
                        Integer.parseInt(request.getParameter("productId"))
                );

                productService.updateProduct(product);

                response.sendRedirect(
                        request.getContextPath() + "/products?success=updated");
                return;

            } else if ("delete".equals(action)) {

                int productId = Integer.parseInt(
                        request.getParameter("productId")
                );

                productService.deleteProduct(productId, shopId);

                response.sendRedirect(
                        request.getContextPath() + "/products?success=deleted");
                return;

            } else {
                throw new IllegalArgumentException(
                        "Invalid product action.");
            }

        } catch (NumberFormatException e) {

            request.setAttribute("error",
                    "Please enter valid numeric values.");

        } catch (IllegalArgumentException e) {

            request.setAttribute("error", e.getMessage());

        } catch (Exception e) {

            throw new ServletException(
                    "Unable to process product operation.", e);
        }

        // Reload page after validation error
        try {
            loadProductPage(request, shopId);

            request.getRequestDispatcher("/products.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException(
                    "Unable to reload product page.", e);
        }
    }

    // CREATE PRODUCT FROM FORM DATA
    private Product createProductFromRequest(
            HttpServletRequest request, int shopId) {

        Product product = new Product();

        product.setShopId(shopId);

        product.setCategoryId(
                Integer.parseInt(request.getParameter("categoryId"))
        );

        product.setProductName(
                request.getParameter("productName")
        );

        product.setSku(request.getParameter("sku"));

        product.setBarcode(request.getParameter("barcode"));

        product.setPurchasePrice(
                parseAmount(request.getParameter("purchasePrice"), 0)
        );

        product.setSellingPrice(
                parseAmount(request.getParameter("sellingPrice"), 0)
        );

        product.setStockQuantity(
                parseAmount(request.getParameter("stockQuantity"), 0)
        );

        product.setMinStockLevel(
                parseAmount(request.getParameter("minStockLevel"), 5)
        );

        product.setUnit(request.getParameter("unit"));

        product.setDescription(
                request.getParameter("description")
        );

        return product;
    }

    // PARSE OPTIONAL NUMERIC VALUES
    private double parseAmount(String value, double defaultValue) {

        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        return Double.parseDouble(value.trim());
    }

    // LOAD PRODUCTS AND CATEGORIES
    private void loadProductPage(HttpServletRequest request, int shopId)
            throws Exception {

        List<Product> products =
                productService.getProducts(shopId);

        List<Category> categories =
                categoryService.getCategories(shopId);

        request.setAttribute("products", products);
        request.setAttribute("categories", categories);
    }
}
