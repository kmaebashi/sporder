package com.kmaebashi.sporder;


import java.sql.Connection;
import java.sql.DriverManager;

public class SpOrderTestUtil {
    private SpOrderTestUtil() {}

    public static Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/sporderdb?currentSchema=sporderschema",
                "sporderuser", "sporderpass#0425");
        return conn;
    }
}
