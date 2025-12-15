package com.course.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbCheck {
    public static void main(String[] args) {
        // Credentials must match docker-compose.yml
        String url = "jdbc:postgresql://db:5432/training_db";
        String user = "user";
        String pass = "password";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            if (conn.isValid(2)) {
                System.out.println("✅ POSTGRES IS ALIVE!");
                System.out.println("   Connected to: " + conn.getMetaData().getDatabaseProductName());
            }
        } catch (Exception e) {
            System.err.println("❌ DB CONNECTION FAILED.");
            e.printStackTrace();
        }
    }
}