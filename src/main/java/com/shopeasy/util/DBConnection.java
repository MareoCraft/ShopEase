
package com.shopeasy.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521/FREE";
    private static final String USERNAME = "C##ADVANCEJAVA";
    private static final String PASSWORD = "JAVA";

    public static Connection getConnection() throws SQLException
    {
        try
        {
            Class.forName("oracle.jdbc.OracleDriver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        catch (ClassNotFoundException e)
        {
            throw new SQLException("Oracle JDBC Driver not found!", e);
        }
    }
}
