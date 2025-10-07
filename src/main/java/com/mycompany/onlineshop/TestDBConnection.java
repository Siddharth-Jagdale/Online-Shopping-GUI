package com.mycompany.onlineshop;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestDBConnection {
    public static void main(String[] args) {
        try {
            DBHelper dbHelper = new DBHelper();
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/online_shopping", 
                    "root", 
                    "Akanksha@sql"
            );
            if(conn != null && !conn.isClosed()) {
                System.out.println("✅ Database connected successfully!");
            } else {
                System.out.println("❌ Failed to connect to database.");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
