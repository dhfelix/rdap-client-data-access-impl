package mx.nic.rdap.client.dao.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.client.dao.exception.DataAccessException;
import mx.nic.rdap.client.dao.exception.IncompleteObjectException;
import mx.nic.rdap.client.dao.object.RdapClientUser;
import mx.nic.rdap.client.spi.UserDAO;
import mx.nic.rdap.client.sql.DatabaseSession;
import mx.nic.rdap.client.sql.QueryGroup;

public class UserDAOImpl implements UserDAO {

	private static final Logger logger = Logger.getLogger(UserDAOImpl.class.getName());

	private static final String GET_USER = "getUserByUsername";
	private static final String STORE_USER = "storeUser";
	private static final String UPDATE_USER = "updateUser";
	private static final String EXIST_USER = "existUser";

	private final static String QUERY_GROUP = "user";

	private static QueryGroup queryGroup = null;

	public static void loadQueryGroup(String schema) {
		try {
			QueryGroup qG = new QueryGroup(QUERY_GROUP, schema);
			setQueryGroup(qG);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group");
		}
	}

	private static void setQueryGroup(QueryGroup qG) {
		queryGroup = qG;
	}

	private static QueryGroup getQueryGroup() {
		return queryGroup;
	}

	@Override
	public RdapClientUser getUser(String username) throws DataAccessException {
		if (Objects.isNull(username)) {
			throw new DataAccessException(new NullPointerException("username is null"));
		}

		RdapClientUser user;

		String query = getQueryGroup().getQuery(GET_USER);
		try (Connection connection = DatabaseSession.getRdapConnection();
				PreparedStatement statement = connection.prepareStatement(query);) {
			statement.setString(1, username);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			ResultSet rs = statement.executeQuery();

			if (!rs.next()) {
				return null;
			}

			user = getFromResultSet(rs);

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}

		return user;
	}

	private static RdapClientUser getFromResultSet(ResultSet rs) throws SQLException {
		RdapClientUser user = new RdapClientUser();
		user.setId(rs.getLong("usr_id"));
		user.setUsername(rs.getString("usr_username"));
		user.setHashedPassword(rs.getString("usr_hashed_password"));
		user.setHashAlgorithm(rs.getString("usr_hash_algorithm"));
		user.setIterations(rs.getInt("usr_hash_iterations"));
		user.setSalt(rs.getString("usr_hash_salt"));
		user.setPbeAlgorith(rs.getString("usr_pbe_algorithm"));
		user.setKeyAlgorithm(rs.getString("usr_key_algorithm"));
		user.setKeySize(rs.getInt("usr_key_size"));

		return user;
	}

	@Override
	public long storeUser(RdapClientUser user) throws DataAccessException {
		isValidForStore(user);
		long userId;
		try (Connection connection = DatabaseSession.getRdapConnection();) {
			userId = storeToDatabase(user, connection);
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}

		return userId;
	}

	private long storeToDatabase(RdapClientUser user, Connection connection) throws SQLException {
		Long userId;
		String query = getQueryGroup().getQuery(STORE_USER);
		try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			fillStoreStatement(user, statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			statement.executeUpdate();

			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			userId = resultSet.getLong(1);
			user.setId(userId);
		}

		return userId;
	}

	private static void fillStoreStatement(RdapClientUser user, PreparedStatement statement) throws SQLException {
		statement.setString(1, user.getUsername());
		statement.setString(2, user.getHashedPassword());
		statement.setString(3, user.getSalt());
		statement.setInt(4, user.getIterations());
		statement.setString(5, user.getHashAlgorithm());
		statement.setString(6, user.getKeyAlgorithm());
		statement.setInt(7, user.getKeySize());
		statement.setString(8, user.getPbeAlgorithm());
	}

	private static void isValidForStore(RdapClientUser user) throws DataAccessException {
		List<IncompleteObjectException> list = new ArrayList<>();
		if (user.getUsername() == null) {
			list.add(new IncompleteObjectException("username", RdapClientUser.class.getSimpleName()));
		}
		if (user.getHashedPassword() == null) {
			list.add(new IncompleteObjectException("hashedPassword", RdapClientUser.class.getSimpleName()));
		}
		if (user.getSalt() == null) {
			list.add(new IncompleteObjectException("salt", RdapClientUser.class.getSimpleName()));
		}
		if (user.getIterations() <= 0) {
			list.add(new IncompleteObjectException("iterations", RdapClientUser.class.getSimpleName()));
		}
		if (user.getHashAlgorithm() == null) {
			list.add(new IncompleteObjectException("hashAlgorithm", RdapClientUser.class.getSimpleName()));
		}
		if (user.getKeyAlgorithm() == null) {
			list.add(new IncompleteObjectException("keyAlgorithm", RdapClientUser.class.getSimpleName()));
		}
		if (user.getKeySize() <= 0) {
			list.add(new IncompleteObjectException("keySize", RdapClientUser.class.getSimpleName()));
		}
		if (user.getPbeAlgorithm() == null) {
			list.add(new IncompleteObjectException("pbeAlgorithm", RdapClientUser.class.getSimpleName()));
		}

		if (!list.isEmpty()) {
			DataAccessException e = new DataAccessException(list.get(0));
			for (int i = 1; i < list.size(); i++) {
				e.addSuppressed(list.get(i));
			}
			throw e;
		}
	}

	@Override
	public void updateUser(RdapClientUser user) throws DataAccessException {
		isValidForUpdate(user);

		try (Connection connection = DatabaseSession.getRdapConnection()) {
			updateUser(user, connection);
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}

	}

	private int updateUser(RdapClientUser user, Connection connection) throws SQLException {
		int result;
		String query = getQueryGroup().getQuery(UPDATE_USER);
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			fillUpdateStatement(user, statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			result = statement.executeUpdate();
		}

		return result;
	}

	private void fillUpdateStatement(RdapClientUser user, PreparedStatement statement) throws SQLException {
		statement.setString(1, user.getHashedPassword());
		statement.setString(2, user.getSalt());
		statement.setInt(3, user.getIterations());
		statement.setString(4, user.getHashAlgorithm());
		statement.setString(5, user.getKeyAlgorithm());
		statement.setInt(6, user.getKeySize());
		statement.setString(7, user.getPbeAlgorithm());

		statement.setLong(8, user.getId());
		statement.setString(9, user.getUsername());
	}

	private void isValidForUpdate(RdapClientUser user) throws DataAccessException {
		DataAccessException exc = null;
		try {
			isValidForStore(user);
		} catch (DataAccessException e) {
			exc = e;
		}

		if (user.getId() == null) {
			IncompleteObjectException cause = new IncompleteObjectException("id", RdapClientUser.class.getSimpleName());
			if (exc == null) {
				exc = new DataAccessException(cause);
			} else {
				exc.addSuppressed(cause);
			}
		}

		if (exc != null) {
			throw exc;
		}
	}

	@Override
	public boolean existUser(String username) throws DataAccessException {
		if (username == null) {
			throw new DataAccessException("username value is null or empty.");
		}

		boolean result;
		String query = getQueryGroup().getQuery(EXIST_USER);
		try (Connection connection = DatabaseSession.getRdapConnection();
				PreparedStatement statement = connection.prepareStatement(query);) {
			statement.setString(1, username);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			ResultSet rs = statement.executeQuery();

			result = rs.next();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
		return result;
	}

}
