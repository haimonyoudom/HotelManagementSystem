package hotel.dao;

import hotel.config.DBConnection;
import hotel.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IDao<User> {

	@Override
	public void add(User user) throws SQLException {
		String sql = "INSERT INTO users (username, password, role, created_at) VALUES (?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPasswordHash());
			stmt.setString(3, user.getRole());
			stmt.setString(4, user.getCreatedAt());
			stmt.executeUpdate();

			try (ResultSet keys = stmt.getGeneratedKeys()) {
				if (keys.next()) {
					user.setId(keys.getInt(1));
				}
			}
		}
	}

	@Override
	public User getById(int id) throws SQLException {
		String sql = "SELECT * FROM users WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() ? mapUser(rs) : null;
			}
		}
	}

	@Override
	public List<User> getAll() throws SQLException {
		String sql = "SELECT * FROM users ORDER BY id";
		List<User> users = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				users.add(mapUser(rs));
			}
		}
		return users;
	}

	@Override
	public void update(User user) throws SQLException {
		String sql = "UPDATE users SET username = ?, password = ?, role = ?, created_at = ? WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPasswordHash());
			stmt.setString(3, user.getRole());
			stmt.setString(4, user.getCreatedAt());
			stmt.setInt(5, user.getId());
			stmt.executeUpdate();
		}
	}

	@Override
	public void delete(int id) throws SQLException {
		String sql = "DELETE FROM users WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			stmt.executeUpdate();
		}
	}

	public User getByUsername(String username) throws SQLException {
		String sql = "SELECT * FROM users WHERE username = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() ? mapUser(rs) : null;
			}
		}
	}

	private User mapUser(ResultSet rs) throws SQLException {
		return new User(
				rs.getInt("id"),
				rs.getString("username"),
				rs.getString("password"),
				rs.getString("role"),
				rs.getString("created_at")
		);
	}
}
