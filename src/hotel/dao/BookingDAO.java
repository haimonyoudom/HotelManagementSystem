package hotel.dao;

import hotel.config.DBConnection;
import hotel.model.Booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO implements IDao<Booking> {

	@Override
	public void add(Booking booking) throws SQLException {
		String sql = "INSERT INTO bookings (customer_id, room_id, check_in_date, check_out_date, total_price, status) VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			stmt.setInt(1, booking.getCustomerId());
			stmt.setInt(2, booking.getRoomId());
			stmt.setString(3, booking.getCheckInDate());
			stmt.setString(4, booking.getCheckOutDate());
			stmt.setDouble(5, booking.getTotalPrice());
			stmt.setString(6, booking.getStatus());
			stmt.executeUpdate();

			try (ResultSet keys = stmt.getGeneratedKeys()) {
				if (keys.next()) {
					booking.setId(keys.getInt(1));
				}
			}
		}
	}

	@Override
	public Booking getById(int id) throws SQLException {
		String sql = "SELECT * FROM bookings WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() ? mapBooking(rs) : null;
			}
		}
	}

	@Override
	public List<Booking> getAll() throws SQLException {
		String sql = "SELECT * FROM bookings ORDER BY id";
		List<Booking> bookings = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				bookings.add(mapBooking(rs));
			}
		}
		return bookings;
	}

	@Override
	public void update(Booking booking) throws SQLException {
		String sql = "UPDATE bookings SET customer_id = ?, room_id = ?, check_in_date = ?, check_out_date = ?, total_price = ?, status = ? WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, booking.getCustomerId());
			stmt.setInt(2, booking.getRoomId());
			stmt.setString(3, booking.getCheckInDate());
			stmt.setString(4, booking.getCheckOutDate());
			stmt.setDouble(5, booking.getTotalPrice());
			stmt.setString(6, booking.getStatus());
			stmt.setInt(7, booking.getId());
			stmt.executeUpdate();
		}
	}

	@Override
	public void delete(int id) throws SQLException {
		String sql = "DELETE FROM bookings WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			stmt.executeUpdate();
		}
	}

	public List<Booking> getByCustomerId(int customerId) throws SQLException {
		String sql = "SELECT * FROM bookings WHERE customer_id = ? ORDER BY id";
		List<Booking> bookings = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, customerId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					bookings.add(mapBooking(rs));
				}
			}
		}
		return bookings;
	}

	public List<Booking> getByStatus(String status) throws SQLException {
		String sql = "SELECT * FROM bookings WHERE status = ? ORDER BY id";
		List<Booking> bookings = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, status);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					bookings.add(mapBooking(rs));
				}
			}
		}
		return bookings;
	}

	public List<Booking> getByRoomId(int roomId) throws SQLException {
		String sql = "SELECT * FROM bookings WHERE room_id = ? ORDER BY id";
		List<Booking> bookings = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, roomId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					bookings.add(mapBooking(rs));
				}
			}
		}
		return bookings;
	}

	private Booking mapBooking(ResultSet rs) throws SQLException {
		return new Booking(
				rs.getInt("id"),
				rs.getInt("customer_id"),
				rs.getInt("room_id"),
				rs.getString("check_in_date"),
				rs.getString("check_out_date"),
				rs.getDouble("total_price"),
				rs.getString("status")
		);
	}
}
