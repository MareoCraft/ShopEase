
package com.shopeasy.controller;

import com.shopeasy.model.Customer;
import com.shopeasy.service.CustomerService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/customers")
public class CustomerServlet extends HttpServlet {

    private final CustomerService customerService = new CustomerService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        Integer shopId = (Integer) request.getSession(false).getAttribute("shopId");

        try {
            request.setAttribute(
                    "customers",
                    customerService.getCustomers(shopId)
            );

            request.getRequestDispatcher("/customers.jsp")
                    .forward(request, response);

        } catch (SQLException | IllegalArgumentException e) {

            throw new ServletException("Unable to load customers.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Integer shopId = (Integer) session.getAttribute("shopId");

        String action = request.getParameter("action");

        try {

            if ("add".equals(action)) {

                Customer customer = createCustomer(request, shopId);

                customerService.addCustomer(customer);

                response.sendRedirect(
                        request.getContextPath() + "/customers?success=added"
                );

            } else if ("update".equals(action)) {

                Customer customer = createCustomer(request, shopId);

                customer.setCustomerId(
                        Integer.parseInt(request.getParameter("customerId"))
                );

                customerService.updateCustomer(customer);

                response.sendRedirect(
                        request.getContextPath() + "/customers?success=updated"
                );

            } else if ("delete".equals(action)) {

                int customerId = Integer.parseInt(
                        request.getParameter("customerId")
                );

                customerService.deleteCustomer(customerId, shopId);

                response.sendRedirect(
                        request.getContextPath() + "/customers?success=deleted"
                );

            } else {

                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid customer action.");
            }

        } catch (IllegalArgumentException e) {

            request.setAttribute("error", e.getMessage());
            loadCustomers(request, shopId);
            request.getRequestDispatcher("/customers.jsp")
                    .forward(request, response);

        } catch (SQLException e) {

            throw new ServletException("Customer operation failed.", e);
        }
    }

    private Customer createCustomer(HttpServletRequest request, int shopId) {

        Customer customer = new Customer();

        customer.setShopId(shopId);
        customer.setCustomerName(request.getParameter("customerName"));
        customer.setMobile(request.getParameter("mobile"));
        customer.setEmail(request.getParameter("email"));
        customer.setAddress(request.getParameter("address"));

        return customer;
    }

    private void loadCustomers(HttpServletRequest request, int shopId)
            throws ServletException {

        try {
            request.setAttribute(
                    "customers",
                    customerService.getCustomers(shopId)
            );
        } catch (SQLException e) {
            throw new ServletException("Unable to reload customers.", e);
        }
    }
}
