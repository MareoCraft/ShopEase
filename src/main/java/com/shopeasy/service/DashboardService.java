
package com.shopeasy.service;

import com.shopeasy.dao.DashboardDAO;
import com.shopeasy.util.DBConnection;

import java.sql.Connection;

public class DashboardService {

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    public com.shopeasy.model.Dashboard getDashboard(int shopId) throws Exception {

        try (Connection con = DBConnection.getConnection()) {

            return dashboardDAO.getDashboardData(con, shopId);

        }
    }
}
