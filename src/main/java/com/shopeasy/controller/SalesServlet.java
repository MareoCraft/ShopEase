
package com.shopeasy.controller;

import com.shopeasy.model.Payment;
import com.shopeasy.model.Sale;
import com.shopeasy.model.SaleItem;
import com.shopeasy.service.CustomerService;
import com.shopeasy.service.ProductService;
import com.shopeasy.service.SalesService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/sales")
public class SalesServlet extends HttpServlet {

    private final SalesService salesService = new SalesService();
    private final CustomerService customerService = new CustomerService();
    private final ProductService productService = new ProductService();

    // DISPLAY BILLING PAGE
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

            loadBillingData(request, shopId);

            request.getRequestDispatcher("/sales.jsp")
                    .forward(request, response);

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to load billing information.", e
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // PROCESS NEW SALE
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("shopId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int shopId = (Integer) session.getAttribute("shopId");

        try {

            int customerId = Integer.parseInt(
                    request.getParameter("customerId")
            );

            double discount = Double.parseDouble(
                    request.getParameter("discountAmount")
            );

            String paymentMethod = request.getParameter("paymentMethod");

            String paymentReference =
                    request.getParameter("paymentReference");

            String[] productIds =
                    request.getParameterValues("productId");

            String[] quantities =
                    request.getParameterValues("quantity");

            if (productIds == null || quantities == null
                    || productIds.length != quantities.length) {

                throw new IllegalArgumentException(
                        "Please add valid products to the bill."
                );
            }

            // CREATE SALE OBJECT
            Sale sale = new Sale();

            sale.setShopId(shopId);
            sale.setCustomerId(customerId);
            sale.setDiscountAmount(discount);

            // CREATE SALE ITEMS
            List<SaleItem> items = new ArrayList<>();

            for (int i = 0; i < productIds.length; i++) {

                SaleItem item = new SaleItem();

                item.setProductId(
                        Integer.parseInt(productIds[i])
                );

                item.setQuantity(
                        Double.parseDouble(quantities[i])
                );

                items.add(item);
            }

            // CREATE PAYMENT OBJECT
            Payment payment = new Payment();

            payment.setShopId(shopId);
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentReference(paymentReference);

            // PROCESS COMPLETE SALE
            Sale completedSale = salesService.createSale(
                    shopId,
                    sale,
                    items,
                    payment
            );

            // REDIRECT TO BILL DETAILS
            response.sendRedirect(
                    request.getContextPath()
                            + "/sale-details?id="
                            + completedSale.getSaleId()
            );

        } catch (IllegalArgumentException e) {

            request.setAttribute("error", e.getMessage());

            try {

                loadBillingData(request, shopId);

                request.getRequestDispatcher("/sales.jsp")
                        .forward(request, response);

            } catch (SQLException ex) {

                throw new ServletException(
                        "Unable to reload billing page.", ex
                );
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to complete sales transaction.", e
            );
        }
    }

    // LOAD CUSTOMERS AND PRODUCTS
    private void loadBillingData(HttpServletRequest request,
                                 int shopId)
            throws Exception {

        request.setAttribute(
                "customers",
                customerService.getCustomers(shopId)
        );

        request.setAttribute(
                "products",
                productService.getProducts(shopId)
        );
    }
}
