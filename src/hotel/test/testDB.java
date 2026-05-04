package hotel.test;

import hotel.config.DBConnection;
import hotel.config.DBInitializer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class testDB {
	public static void main(String[] args) throws Exception {
		testDBConnection();
		testDBInitializer();
		System.out.println("All DB tests passed.");
	}

	private static void testDBConnection() throws Exception {
		Connection first = DBConnection.getConnection();
		assertTrue(first != null, "DBConnection.getConnection() returned null");
		assertTrue(!first.isClosed(), "DBConnection returned a closed connection");

		Connection second = DBConnection.getConnection();
		assertTrue(first == second, "DBConnection did not return the same instance");

		DBConnection.closeConnection();
		assertTrue(first.isClosed(), "DBConnection.closeConnection() did not close the connection");
	}

	private static void testDBInitializer() throws Exception {
		DBInitializer.initialize();

		Connection conn = DBConnection.getConnection();
		assertTrue(tableExists(conn, "users"), "Table 'users' was not created");
		assertTrue(tableExists(conn, "rooms"), "Table 'rooms' was not created");
		assertTrue(tableExists(conn, "customers"), "Table 'customers' was not created");
		assertTrue(tableExists(conn, "staff"), "Table 'staff' was not created");
		assertTrue(tableExists(conn, "bookings"), "Table 'bookings' was not created");
		assertTrue(tableExists(conn, "payments"), "Table 'payments' was not created");

		DBConnection.closeConnection();
	}

	private static boolean tableExists(Connection conn, String tableName) throws Exception {
		String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, tableName);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		}
	}

	private static void assertTrue(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}
}
