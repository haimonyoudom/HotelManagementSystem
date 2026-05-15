package hotel.dao;

import hotel.config.DBConnection;
import hotel.model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO implements IDao<Payment> {

	@Override
	public void add(Payment payment) throws SQLException {
		String sql = "INSERT INTO payments (booking_id, amount, payment_date, method, status) VALUES (?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			stmt.setInt(1, payment.getBookingId());
			stmt.setDouble(2, payment.getAmount());
			stmt.setString(3, payment.getPaymentDate());
			stmt.setString(4, payment.getMethod());
			stmt.setString(5, payment.getStatus());
			stmt.executeUpdate();

			try (ResultSet keys = stmt.getGeneratedKeys()) {
				if (keys.next()) {
					payment.setId(keys.getInt(1));
				}
			}
		}
	}

	@Override
	public Payment getById(int id) throws SQLException {
		String sql = "SELECT * FROM payments WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() ? mapPayment(rs) : null;
			}
		}
	}

	@Override
	public List<Payment> getAll() throws SQLException {
		String sql = "SELECT * FROM payments ORDER BY id";
		List<Payment> payments = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				payments.add(mapPayment(rs));
			}
		}
		return payments;
	}

	@Override
	public void update(Payment payment) throws SQLException {
		String sql = "UPDATE payments SET booking_id = ?, amount = ?, payment_date = ?, method = ?, status = ? WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, payment.getBookingId());
			stmt.setDouble(2, payment.getAmount());
			stmt.setString(3, payment.getPaymentDate());
			stmt.setString(4, payment.getMethod());
			stmt.setString(5, payment.getStatus());
			stmt.setInt(6, payment.getId());
			stmt.executeUpdate();
		}
	}

	@Override
	public void delete(int id) throws SQLException {
		String sql = "DELETE FROM payments WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			stmt.executeUpdate();
		}
	}

	public List<Payment> getByBookingId(int bookingId) throws SQLException {
		String sql = "SELECT * FROM payments WHERE booking_id = ? ORDER BY id";
		List<Payment> payments = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, bookingId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					payments.add(mapPayment(rs));
				}
			}
		}
		return payments;
	}

	public double getTotalRevenue() throws SQLException {
		String sql = "SELECT COALESCE(SUM(amount), 0) AS total_revenue FROM payments WHERE status = 'paid'";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			return rs.next() ? rs.getDouble("total_revenue") : 0.0;
		}
	}

	private Payment mapPayment(ResultSet rs) throws SQLException {
		return new Payment(
				rs.getInt("id"),
				rs.getInt("booking_id"),
				rs.getDouble("amount"),
				rs.getString("payment_date"),
				rs.getString("method"),
				rs.getString("status")
		);
	}
}
