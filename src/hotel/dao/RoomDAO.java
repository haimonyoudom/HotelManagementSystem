package hotel.dao;

import hotel.config.DBConnection;
import hotel.model.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO implements IDao<Room> {

	@Override
	public void add(Room room) throws SQLException {
		String sql = "INSERT INTO rooms (room_number, type, price_per_night, is_available, status) VALUES (?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, room.getRoomNumber());
			stmt.setString(2, room.getType());
			stmt.setDouble(3, room.getPricePerNight());
			stmt.setBoolean(4, room.isAvailable());
			stmt.setString(5, room.getStatus() != null ? room.getStatus() : "available");
			stmt.executeUpdate();

			try (ResultSet keys = stmt.getGeneratedKeys()) {
				if (keys.next()) {
					room.setId(keys.getInt(1));
				}
			}
		}
	}

	@Override
	public Room getById(int id) throws SQLException {
		String sql = "SELECT * FROM rooms WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() ? mapRoom(rs) : null;
			}
		}
	}

	@Override
	public List<Room> getAll() throws SQLException {
		String sql = "SELECT * FROM rooms ORDER BY id";
		List<Room> rooms = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				rooms.add(mapRoom(rs));
			}
		}
		return rooms;
	}

	@Override
	public void update(Room room) throws SQLException {
		String sql = "UPDATE rooms SET room_number = ?, type = ?, price_per_night = ?, is_available = ?, status = ? WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, room.getRoomNumber());
			stmt.setString(2, room.getType());
			stmt.setDouble(3, room.getPricePerNight());
			stmt.setBoolean(4, room.isAvailable());
			stmt.setString(5, room.getStatus() != null ? room.getStatus() : "available");
			stmt.setInt(6, room.getId());
			stmt.executeUpdate();
		}
	}

	@Override
	public void delete(int id) throws SQLException {
		String sql = "DELETE FROM rooms WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			stmt.executeUpdate();
		}
	}

	public List<Room> getAvailableRooms() throws SQLException {
		String sql = "SELECT * FROM rooms WHERE is_available = 1 ORDER BY id";
		List<Room> rooms = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				rooms.add(mapRoom(rs));
			}
		}
		return rooms;
	}

	public List<Room> getRoomsByType(String type) throws SQLException {
		String sql = "SELECT * FROM rooms WHERE type = ? ORDER BY id";
		List<Room> rooms = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, type);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					rooms.add(mapRoom(rs));
				}
			}
		}
		return rooms;
	}

	private Room mapRoom(ResultSet rs) throws SQLException {
		Room room = new Room(
				rs.getInt("id"),
				rs.getString("room_number"),
				rs.getString("type"),
				rs.getDouble("price_per_night"),
				rs.getBoolean("is_available"));
		// Read status column; fall back to deriving from is_available if null
		try {
			String status = rs.getString("status");
			room.setStatus(status != null ? status : (room.isAvailable() ? "available" : "booked"));
		} catch (SQLException e) {
			// status column doesn't exist yet — fall back gracefully
			room.setStatus(room.isAvailable() ? "available" : "booked");
		}
		return room;
	}
}