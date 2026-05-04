package hotel.config;

import java.io.*;
import java.nio.file.*;
import java.sql.*;

public class DBInitializer {

    public static void initialize() {
        try {
            Connection conn = DBConnection.getConnection();
            // Read the schema.sql file from the database/ folder
            String sql = new String(Files.readAllBytes(Paths.get("database/schema.sql")));
            // Split by ";" to get each CREATE TABLE statement
            String[] statements = sql.split(";");
            Statement stmt = conn.createStatement();
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
            System.out.println("Database initialized successfully.");
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
}