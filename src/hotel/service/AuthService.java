package hotel.service;

import hotel.dao.UserDAO;
import hotel.model.User;
import hotel.util.PasswordHasher;

import java.sql.SQLException;

public class AuthService extends BaseService {

	private final UserDAO userDAO;
	private User loggedInUser;

	public AuthService() {
		this(new UserDAO());
	}

	public AuthService(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	// public boolean login(String username, String password) {
	// 	try {
	// 		User user = userDAO.getByUsername(username);
	// 		if (user == null) {
	// 			return false;
	// 		}
	// 		boolean matched = PasswordHasher.verify(password, user.getPasswordHash());
	// 		if (matched) {
	// 			loggedInUser = user;
	// 			logAction("User logged in: " + username);
	// 			return true;
	// 		}
	// 		return false;
	// 	} catch (SQLException e) {
	// 		throw new RuntimeException("Failed to login user", e);
	// 	}
	// }
	public boolean login(String username, String password) {
    try {
        User user = userDAO.getByUsername(username);
        if (user == null) {
            System.out.println("DEBUG: user not found");
            return false;
        }
        System.out.println("DEBUG: stored hash = " + user.getPasswordHash());
        System.out.println("DEBUG: input hash  = " + PasswordHasher.hash(password));
        boolean matched = PasswordHasher.verify(password, user.getPasswordHash());
        System.out.println("DEBUG: matched = " + matched);
        if (matched) {
            loggedInUser = user;
            return true;
        }
        return false;
    } catch (SQLException e) {
        throw new RuntimeException("Failed to login user", e);
    }
}
	public void logout() {
		if (loggedInUser != null) {
			logAction("User logged out: " + loggedInUser.getUsername());
		}
		loggedInUser = null;
	}

	public boolean isLoggedIn() {
		return loggedInUser != null;
	}

	public User getCurrentUser() {
		return loggedInUser;
	}

	@Override
	protected String getServiceName() {
		return "AuthService";
	}
}
