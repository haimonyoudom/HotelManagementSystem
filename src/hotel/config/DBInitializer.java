package hotel.config;

import java.nio.file.*;
import java.sql.*;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBInitializer {

    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile(
            "(?i)^CREATE\\s+TABLE\\s+(?:IF\\s+NOT\\s+EXISTS\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)"
    );

    public static void initialize() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = new String(Files.readAllBytes(Paths.get("database/schema.sql")));
            String[] statements = sql.split(";");

            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    if (isCreateTable(trimmed)) {
                        String tableName = extractTableName(trimmed);
                        if (tableName != null && tableExists(tableName)) {
                            continue;
                        }
                    }
                    stmt.execute(trimmed);
                }
            }
            System.out.println("Database initialized successfully.");
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }

    public static boolean tableExists(String tableName) {
        String sql = "SELECT 1 FROM sqlite_master WHERE type='table' AND lower(name)=lower(?) LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tableName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private static boolean isCreateTable(String statement) {
        return statement.toUpperCase(Locale.ROOT).startsWith("CREATE TABLE");
    }

    private static String extractTableName(String createTableSql) {
        Matcher matcher = CREATE_TABLE_PATTERN.matcher(createTableSql.trim());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
}