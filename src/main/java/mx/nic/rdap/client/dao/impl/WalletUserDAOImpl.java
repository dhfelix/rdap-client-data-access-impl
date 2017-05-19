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
import mx.nic.rdap.client.dao.object.WalletUser;
import mx.nic.rdap.client.spi.WalletUserDAO;
import mx.nic.rdap.client.sql.DatabaseSession;
import mx.nic.rdap.client.sql.QueryGroup;

public class WalletUserDAOImpl implements WalletUserDAO {

	private static final Logger logger = Logger.getLogger(WalletUserDAOImpl.class.getName());

	private static final String GET_BY = "getByUsername";
	private static final String STORE = "store";
	private static final String UPDATE = "update";
	private static final String EXIST = "exist";
	private static final String DELETE = "delete";

	private final static String QUERY_GROUP = "walletUser";

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

	public WalletUserDAOImpl() {
		// no code
	}

	@Override
	public WalletUser getByUsername(String username) throws DataAccessException {
		if (Objects.isNull(username)) {
			throw new DataAccessException(new NullPointerException("username is null"));
		}

		WalletUser user;

		String query = getQueryGroup().getQuery(GET_BY);
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

	private static WalletUser getFromResultSet(ResultSet rs) throws SQLException {
		WalletUser user = new WalletUser();

		user.setId(rs.getLong("wusr_id"));
		user.setUsername(rs.getString("wusr_username"));

		user.setHashedPassword(rs.getString("wusr_hashed_password"));
		user.setHashAlgorithm(rs.getString("wusr_hash_algorithm"));
		user.setSalt(rs.getString("wusr_salt"));
		user.setIterations(rs.getInt("wusr_iterations"));
		user.setPbeAlgorithm(rs.getString("wusr_pbe_algorithm"));

		user.setEncryptedWalletKey(rs.getString("wusr_encrypted_wallet_key"));
		user.setKeyAlgorithm(rs.getString("wusr_key_algorithm"));
		user.setKeySize(rs.getInt("wusr_key_size"));
		user.setCipherAlgorithm(rs.getString("wusr_cipher_algorithm"));

		return user;
	}

	@Override
	public long store(WalletUser walletUser) throws DataAccessException {
		isValidForStore(walletUser);
		long userId;
		try (Connection connection = DatabaseSession.getRdapConnection();) {
			userId = storeToDatabase(walletUser, connection);
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}

		return userId;
	}

	private long storeToDatabase(WalletUser walletUser, Connection connection) throws SQLException {
		Long userId;
		String query = getQueryGroup().getQuery(STORE);
		try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			fillStoreStatement(walletUser, statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			statement.executeUpdate();

			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			userId = resultSet.getLong(1);
			walletUser.setId(userId);
		}

		return userId;
	}

	private static void fillStoreStatement(WalletUser user, PreparedStatement statement) throws SQLException {
		statement.setString(1, user.getUsername());
		statement.setString(2, user.getHashedPassword());
		statement.setString(3, user.getHashAlgorithm());

		statement.setString(4, user.getSalt());
		statement.setInt(5, user.getIterations());

		statement.setString(6, user.getPbeAlgorithm());

		statement.setString(7, user.getEncryptedWalletKey());
		statement.setString(8, user.getKeyAlgorithm());
		statement.setInt(9, user.getKeySize());
		statement.setString(10, user.getCipherAlgorithm());

	}

	private static void isValidForStore(WalletUser user) throws DataAccessException {
		List<IncompleteObjectException> list = new ArrayList<>();
		if (user.getUsername() == null) {
			list.add(new IncompleteObjectException("username", WalletUser.class.getSimpleName()));
		}
		if (user.getHashedPassword() == null) {
			list.add(new IncompleteObjectException("hashedPassword", WalletUser.class.getSimpleName()));
		}
		if (user.getHashAlgorithm() == null) {
			list.add(new IncompleteObjectException("hashAlgorithm", WalletUser.class.getSimpleName()));
		}
		if (user.getSalt() == null) {
			list.add(new IncompleteObjectException("salt", WalletUser.class.getSimpleName()));
		}
		if (user.getIterations() <= 0) {
			list.add(new IncompleteObjectException("iterations", WalletUser.class.getSimpleName()));
		}
		if (user.getPbeAlgorithm() == null) {
			list.add(new IncompleteObjectException("pbeAlgorithm", WalletUser.class.getSimpleName()));
		}
		if (user.getEncryptedWalletKey() == null) {
			list.add(new IncompleteObjectException("encryptedWalletKey", WalletUser.class.getSimpleName()));
		}
		if (user.getKeyAlgorithm() == null) {
			list.add(new IncompleteObjectException("keyAlgorithm", WalletUser.class.getSimpleName()));
		}
		if (user.getKeySize() <= 0) {
			list.add(new IncompleteObjectException("keySize", WalletUser.class.getSimpleName()));
		}
		if (user.getCipherAlgorithm() == null) {
			list.add(new IncompleteObjectException("cipherAlgorithm", WalletUser.class.getSimpleName()));
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
	public void update(WalletUser walletUser) throws DataAccessException {
		isValidForUpdate(walletUser);

		try (Connection connection = DatabaseSession.getRdapConnection()) {
			updateUser(walletUser, connection);
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}

	}

	private int updateUser(WalletUser user, Connection connection) throws SQLException {
		int result;
		String query = getQueryGroup().getQuery(UPDATE);
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			fillUpdateStatement(user, statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			result = statement.executeUpdate();
		}

		return result;
	}

	private void fillUpdateStatement(WalletUser user, PreparedStatement statement) throws SQLException {
		statement.setString(1, user.getHashedPassword());
		statement.setString(2, user.getHashAlgorithm());
		statement.setString(3, user.getSalt());
		statement.setInt(4, user.getIterations());
		statement.setString(5, user.getPbeAlgorithm());
		statement.setString(6, user.getEncryptedWalletKey());
		statement.setString(7, user.getKeyAlgorithm());
		statement.setInt(8, user.getKeySize());
		statement.setString(9, user.getCipherAlgorithm());

		statement.setLong(10, user.getId());
		statement.setString(11, user.getUsername());
	}

	private void isValidForUpdate(WalletUser user) throws DataAccessException {
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
	public boolean existByUsername(String username) throws DataAccessException {
		if (username == null) {
			throw new DataAccessException("username value is null or empty.");
		}

		boolean result;
		String query = getQueryGroup().getQuery(EXIST);
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

	@Override
	public void delete(WalletUser walletUser) throws DataAccessException {
		isValidForDelete(walletUser);

		String query = getQueryGroup().getQuery(DELETE);
		try (Connection connection = DatabaseSession.getRdapConnection();
				PreparedStatement statement = connection.prepareStatement(query);) {
			statement.setLong(1, walletUser.getId());
			statement.setString(2, walletUser.getUsername());
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			statement.executeUpdate();

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	public void isValidForDelete(WalletUser walletUser) throws DataAccessException {
		List<NullPointerException> list = new ArrayList<>();
		if (walletUser.getUsername() == null) {
			list.add(new NullPointerException("username is null"));
		}

		if (walletUser.getId() == null) {
			list.add(new NullPointerException("id is null"));
		}

		if (list.size() > 0) {
			throw new DataAccessException("Invalid object to delete");
		}
	}

}
