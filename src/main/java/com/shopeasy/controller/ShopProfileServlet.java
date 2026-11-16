
package com.shopeasy.controller;

import com.shopeasy.dao.ShopDAO;
import com.shopeasy.model.Shop;
import com.shopeasy.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;

@WebServlet("/profile")
public class ShopProfileServlet extends HttpServlet {

    private ShopDAO shopDAO;

    @Override
    public void init() {
        shopDAO = new ShopDAO();
    }

    // Display shop profile
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        int shopId = (Integer) session.getAttribute("shopId");

        try (Connection con = DBConnection.getConnection()) {

            Shop shop = shopDAO.getShopById(con, shopId);

            if (shop == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Shop not found"
                );
                return;
            }

            request.setAttribute("shop", shop);

            request.getRequestDispatcher("/profile.jsp")
                    .forward(request, response);

        } catch (Exception e) {

            getServletContext().log("Unable to load shop profile", e);

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load shop profile"
            );
        }
    }

    // Update shop profile
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        int shopId = (Integer) session.getAttribute("shopId");

        String shopName = request.getParameter("shopName");
        String shopCategory = request.getParameter("shopCategory");
        String ownerName = request.getParameter("ownerName");
        String mobile = request.getParameter("mobile");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String pincode = request.getParameter("pincode");

        if (shopName == null || shopName.isBlank()
                || ownerName == null || ownerName.isBlank()
                || mobile == null || mobile.isBlank()) {

            request.setAttribute(
                    "errorMessage",
                    "Shop name, owner name and mobile are required."
            );

            doGet(request, response);
            return;
        }

        Shop shop = new Shop();

        shop.setShopId(shopId);
        shop.setShopName(shopName.trim());
        shop.setShopCategory(shopCategory);
        shop.setOwnerName(ownerName.trim());
        shop.setMobile(mobile.trim());
        shop.setEmail(email);
        shop.setAddress(address);
        shop.setCity(city);
        shop.setState(state);
        shop.setPincode(pincode);

        try (Connection con = DBConnection.getConnection()) {

            boolean updated = shopDAO.updateShop(con, shop);

            if (updated) {

                // Update the displayed owner name in session
                session.setAttribute("fullName", ownerName.trim());

                response.sendRedirect(
                        request.getContextPath()
                                + "/profile?updated=success"
                );

            } else {

                request.setAttribute(
                        "errorMessage",
                        "Shop profile could not be updated."
                );

                doGet(request, response);
            }

        } catch (Exception e) {

            getServletContext().log("Shop profile update failed", e);

            request.setAttribute(
                    "errorMessage",
                    "Unable to update shop profile."
            );

            doGet(request, response);
        }
    }
}
