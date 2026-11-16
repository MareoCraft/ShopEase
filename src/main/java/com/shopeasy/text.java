
package com.shopeasy;

import com.shopeasy.util.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class text {

    public static void main(String[] args) {

        try (Connection con = DBConnection.getConnection()) {

            if (con != null) {
                System.out.println("Database Connected Successfully!");
                System.out.println("Connection: " + con);
            }

        } catch (SQLException e) {
            System.out.println("Database Connection Failed!");
            e.printStackTrace();
        }
    }
}
