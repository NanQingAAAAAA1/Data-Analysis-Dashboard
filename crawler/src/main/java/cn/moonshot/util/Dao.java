package cn.moonshot.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Dao {
    private static final String drivername = "com.mysql.cj.jdbc.Driver";
    private static final String url = "jdbc:mysql://localhost:3306/crawler_ai_new";
    private static final String username = "root";
    private static final String password = "123456";

    static {
        try {
            Class.forName(drivername);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到JDBC驱动", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
