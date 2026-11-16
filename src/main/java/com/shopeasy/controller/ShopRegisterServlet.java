
package com.shopeasy.controller;

import com.shopeasy.model.Shop;
import com.shopeasy.model.User;
import com.shopeasy.service.ShopService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class ShopRegisterServlet extends HttpServlet {

    private ShopService shopService;

    @Override
    public void init() {
        shopService = new ShopService();
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {

            // 1. Receive shop details
            String shopName = request.getParameter("shopName");
            String shopCategory = request.getParameter("shopCategory");
            String ownerName = request.getParameter("ownerName");
            String mobile = request.getParameter("mobile");
            String email = request.getParameter("email");
            String address = request.getParameter("address");
            String city = request.getParameter("city");
            String state = request.getParameter("state");
            String pincode = request.getParameter("pincode");

            // 2. Receive owner account details
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String confirmPassword =
                    request.getParameter("confirmPassword");

            // 3. Check password confirmation
            if (password == null ||
                    !password.equals(confirmPassword)) {

                throw new IllegalArgumentException(
                        "Passwords do not match!"
                );
            }

            // 4. Create Shop object
            Shop shop = new Shop();

            shop.setShopName(shopName);
            shop.setShopCategory(shopCategory);
            shop.setOwnerName(ownerName);
            shop.setMobile(mobile);
            shop.setEmail(email);
            shop.setAddress(address);
            shop.setCity(city);
            shop.setState(state);
            shop.setPincode(pincode);

            // 5. Create User object
            User user = new User();

            user.setFullName(ownerName);
            user.setUsername(username);
            user.setPassword(password);

            // 6. Call business logic
            int shopId = shopService.registerShop(shop, user);

            // 7. Registration successful
            response.sendRedirect(
                    request.getContextPath()
                            + "/login.jsp?registered=success"
            );

        } catch (IllegalArgumentException e) {

            request.setAttribute("errorMessage", e.getMessage());

            request.getRequestDispatcher("/register.jsp")
                    .forward(request, response);

        } catch (Exception e) {

            getServletContext().log(
                    "Shop registration failed", e
            );

            request.setAttribute(
                    "errorMessage",
                    "Registration failed. Please try again."
            );

            request.getRequestDispatcher("/register.jsp")
                    .forward(request, response);
        }
    }
}
