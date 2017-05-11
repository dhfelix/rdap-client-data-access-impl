package mx.nic.rdap.client.dao.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.client.dao.exception.DataAccessException;
import mx.nic.rdap.client.dao.exception.IncompleteObjectException;
import mx.nic.rdap.client.dao.object.EncryptedWalletKey;
import mx.nic.rdap.client.spi.WalletKeyDAO;
import mx.nic.rdap.client.sql.DatabaseSession;
import mx.nic.rdap.client.sql.QueryGroup;

public class WalletKeyDAOImpl implements WalletKeyDAO {
	private static final Logger logger = Logger.getLogger(WalletKeyDAOImpl.class.getName());

	private static final String GET_WALLET_KEY = "getWalletKeyByUserId";
	private static final String STORE_WALLET_KEY = "storeWalletKey";
	private static final String UPDATE_WALLET_KEY = "updateWalletKey";

	private final static String QUERY_GROUP = "walletKey";

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
	public EncryptedWalletKey getWalletKey(long userId) throws DataAccessException {
		if (userId <= 0) {
			throw new DataAccessException("Invalid value userId: " + userId);
		}

		EncryptedWalletKey key;

		String query = getQueryGroup().getQuery(GET_WALLET_KEY);
		try (Connection connection = DatabaseSession.getRdapConnection();
				PreparedStatement statement = connection.prepareStatement(query);) {
			statement.setLong(1, userId);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			ResultSet rs = statement.executeQuery();

			if (!rs.next()) {
				return null;
			}

			key = getWalletKeyFromResultSet(rs);

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}

		return key;
	}

	private EncryptedWalletKey getWalletKeyFromResultSet(ResultSet rs) throws SQLException {
		long keyId = rs.getLong("key_id");
		long userId = rs.getLong("usr_id");
		String encryptedWalletKey = rs.getString("key_wallet_key");
		String walletKeyAlgorithm = rs.getString("key_wallet_key_algorithm");

		EncryptedWalletKey key = new EncryptedWalletKey();
		key.setId(keyId);
		key.setUserId(userId);
		key.setEncryptedWalletKey(encryptedWalletKey);
		key.setWalletKeyAlgorithm(walletKeyAlgorithm);
		return key;
	}

	@Override
	public long storeWalletKey(EncryptedWalletKey encryptedWalletKey) throws DataAccessException {
		isValidForStore(encryptedWalletKey);
		long keyId;
		try (Connection connection = DatabaseSession.getRdapConnection();) {
			keyId = storeToDatabase(encryptedWalletKey, connection);
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}

		return keyId;
	}

	private void isValidForStore(EncryptedWalletKey key) throws DataAccessException {
		List<IncompleteObjectException> list = new ArrayList<>();
		if (key.getUserId() == null) {
			list.add(new IncompleteObjectException("user", EncryptedWalletKey.class.getSimpleName()));
		}
		if (key.getEncryptedWalletKey() == null) {
			list.add(new IncompleteObjectException("encryptedWalletKey", EncryptedWalletKey.class.getSimpleName()));
		}
		if (key.getWalletKeyAlgorithm() == null) {
			list.add(new IncompleteObjectException("walletKeyAlgorithm", EncryptedWalletKey.class.getSimpleName()));
		}

		if (!list.isEmpty()) {
			DataAccessException e = new DataAccessException(list.get(0));
			for (int i = 1; i < list.size(); i++) {
				e.addSuppressed(list.get(i));
			}
			throw e;
		}
	}

	private long storeToDatabase(EncryptedWalletKey key, Connection connection) throws SQLException {
		Long keyId;
		String query = getQueryGroup().getQuery(STORE_WALLET_KEY);
		try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			fillStoreStatement(key, statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			statement.executeUpdate();

			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			keyId = resultSet.getLong(1);
			key.setId(keyId);
		}

		return keyId;

	}

	private void fillStoreStatement(EncryptedWalletKey key, PreparedStatement statement) throws SQLException {
		statement.setLong(1, key.getUserId());
		statement.setString(2, key.getEncryptedWalletKey());
		statement.setString(3, key.getWalletKeyAlgorithm());

	}

	@Override
	public void updateWalletKey(EncryptedWalletKey encryptedWalletKey) throws DataAccessException {
		isValidForUpdate(encryptedWalletKey);

		try (Connection connection = DatabaseSession.getRdapConnection()) {
			updateWalletKey(encryptedWalletKey, connection);
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}

	}

	private void isValidForUpdate(EncryptedWalletKey key) throws DataAccessException {
		DataAccessException exc = null;
		try {
			isValidForStore(key);
		} catch (DataAccessException e) {
			exc = e;
		}

		if (key.getId() == null) {
			IncompleteObjectException cause = new IncompleteObjectException("id",
					EncryptedWalletKey.class.getSimpleName());
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

	private void updateWalletKey(EncryptedWalletKey key, Connection connection) throws SQLException {
		String query = getQueryGroup().getQuery(UPDATE_WALLET_KEY);
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			fillUpdateStatement(key, statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			statement.executeUpdate();
		}

	}

	private void fillUpdateStatement(EncryptedWalletKey key, PreparedStatement statement) throws SQLException {
		statement.setString(1, key.getEncryptedWalletKey());
		statement.setString(2, key.getWalletKeyAlgorithm());

		statement.setLong(3, key.getId());
		statement.setLong(4, key.getUserId());
	}

}
