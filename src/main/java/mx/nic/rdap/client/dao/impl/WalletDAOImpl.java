package mx.nic.rdap.client.dao.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.client.dao.exception.DataAccessException;
import mx.nic.rdap.client.dao.exception.IncompleteObjectException;
import mx.nic.rdap.client.dao.model.WalletDAOModel;
import mx.nic.rdap.client.dao.object.EncryptedCredential;
import mx.nic.rdap.client.spi.WalletDAO;
import mx.nic.rdap.client.sql.DatabaseSession;
import mx.nic.rdap.client.sql.QueryGroup;

public class WalletDAOImpl implements WalletDAO {

	private final static Logger logger = Logger.getLogger(WalletDAOModel.class.getName());

	private final static String QUERY_GROUP = "Wallet";

	private static QueryGroup queryGroup = null;

	private static final String GET_BY_USER_ID_AND_DOMAIN = "getByUserIdAndDomain";
	private static final String GET_ALL_BY_USER_ID = "getAllByUserId";
	private static final String STORE_RDAP_CREDENTIAL = "storeRdapCredential";
	private static final String UPDATE_RDAP_CREDENTIAL = "updateRdapCredential";
	private static final String DELETE_RDAP_CREDENTIAL = "deleteRdapCredential";

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
	public long storeCredential(EncryptedCredential encryptedCredential) throws DataAccessException {
		try {
			isValidForStore(encryptedCredential);
		} catch (IncompleteObjectException e) {
			throw new DataAccessException(e);
		}

		Long result;
		try (Connection connection = DatabaseSession.getRdapConnection()) {
			result = storeToDatabase(encryptedCredential, connection);
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}

		return result;
	}

	@Override
	public int updateCredential(EncryptedCredential encryptedCredential) throws DataAccessException {
		try {
			isValidForUpdate(encryptedCredential);
		} catch (IncompleteObjectException e) {
			throw new DataAccessException(e);
		}

		int result;
		try (Connection connection = DatabaseSession.getRdapConnection()) {
			result = updateToDatabase(encryptedCredential, connection);
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
		return result;
	}

	@Override
	public List<EncryptedCredential> getCredentialsForRdapServer(long userId, String serverId)
			throws DataAccessException {
		if (Objects.isNull(serverId)) {
			throw new DataAccessException(new IncompleteObjectException("serverId", "WalletDAO"));
		}

		List<EncryptedCredential> credentials;

		String query = getQueryGroup().getQuery(GET_BY_USER_ID_AND_DOMAIN);
		try (Connection connection = DatabaseSession.getRdapConnection();
				PreparedStatement statement = connection.prepareStatement(query);) {
			fillGetByUserIdAndDomain(userId, serverId, statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			ResultSet rs = statement.executeQuery();

			if (!rs.next()) {
				return Collections.emptyList();
			}

			credentials = new ArrayList<>();
			do {
				EncryptedCredential credential = getCredentialFromResultSet(rs);
				credentials.add(credential);
			} while (rs.next());

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}

		return credentials;
	}

	@Override
	public List<EncryptedCredential> getCredentials(long userId) throws DataAccessException {
		List<EncryptedCredential> credentials;

		String query = getQueryGroup().getQuery(GET_ALL_BY_USER_ID);
		try (Connection connection = DatabaseSession.getRdapConnection();
				PreparedStatement statement = connection.prepareStatement(query);) {
			fillGetAllByUserId(userId, statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			ResultSet rs = statement.executeQuery();

			if (!rs.next()) {
				return Collections.emptyList();
			}

			credentials = new ArrayList<>();
			do {
				EncryptedCredential credential = getCredentialFromResultSet(rs);
				credentials.add(credential);
			} while (rs.next());

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}

		return credentials;
	}

	@Override
	public void deleteCredential(long userId, long credentialId) throws DataAccessException {
		String query = getQueryGroup().getQuery(DELETE_RDAP_CREDENTIAL);
		try (Connection con = DatabaseSession.getRdapConnection();
				PreparedStatement statement = con.prepareStatement(query);) {
			fillDeleteStatement(userId, credentialId, statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	private Long storeToDatabase(EncryptedCredential rdapLogin, Connection connection) throws SQLException {
		String query = getQueryGroup().getQuery(STORE_RDAP_CREDENTIAL);
		Long loginId;
		try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			fillStoreStatement(rdapLogin, statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			statement.executeUpdate();

			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			loginId = resultSet.getLong(1);
			rdapLogin.setId(loginId);
		}

		return loginId;
	}

	private int updateToDatabase(EncryptedCredential rdapLogin, Connection connection) throws SQLException {
		int result;
		String query = getQueryGroup().getQuery(UPDATE_RDAP_CREDENTIAL);
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			fillUpdateStatement(rdapLogin, statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			result = statement.executeUpdate();
		}

		return result;
	}

	private static void isValidForStore(EncryptedCredential rdapLogin) throws IncompleteObjectException {
		if (rdapLogin.getUserId() == null) {
			throw new IncompleteObjectException("clientUserId", rdapLogin.getClass().getSimpleName());
		}

		String rdapServerDomain = rdapLogin.getRdapServerId();
		if (rdapServerDomain == null || rdapServerDomain.isEmpty()) {
			throw new IncompleteObjectException("serverDomain", rdapLogin.getClass().getSimpleName());
		}

		String username = rdapLogin.getUsername();
		if (username == null || username.isEmpty()) {
			throw new IncompleteObjectException("username", rdapLogin.getClass().getSimpleName());
		}

		String encryptedPassword = rdapLogin.getEncryptedPassword();
		if (encryptedPassword == null || encryptedPassword.isEmpty()) {
			throw new IncompleteObjectException("encryptedPassword", rdapLogin.getClass().getSimpleName());
		}
	}

	private static void isValidForUpdate(EncryptedCredential rdapLogin) throws IncompleteObjectException {
		if (rdapLogin.getId() == null) {
			throw new IncompleteObjectException("id", rdapLogin.getClass().getSimpleName());
		}
		isValidForStore(rdapLogin);
	}

	private static void fillGetByUserIdAndDomain(long userId, String domainName, PreparedStatement statement)
			throws SQLException {
		statement.setLong(1, userId);
		statement.setString(2, domainName);
	}

	private static void fillGetAllByUserId(long userId, PreparedStatement statement) throws SQLException {
		statement.setLong(1, userId);
	}

	private static void fillStoreStatement(EncryptedCredential rdapLogin, PreparedStatement statement)
			throws SQLException {
		statement.setLong(1, rdapLogin.getUserId());
		statement.setString(2, rdapLogin.getRdapServerId());
		statement.setString(3, rdapLogin.getUsername());
		statement.setString(4, rdapLogin.getEncryptedPassword());
	}

	private static void fillUpdateStatement(EncryptedCredential rdapLogin, PreparedStatement statement)
			throws SQLException {
		statement.setString(1, rdapLogin.getUsername());
		statement.setString(2, rdapLogin.getEncryptedPassword());

		statement.setLong(3, rdapLogin.getUserId());
		statement.setString(4, rdapLogin.getRdapServerId());
		statement.setLong(5, rdapLogin.getId());
	}

	private static void fillDeleteStatement(long userId, long credentialId, PreparedStatement statement)
			throws SQLException {
		statement.setLong(1, userId);
		statement.setLong(2, credentialId);
	}

	private static EncryptedCredential getCredentialFromResultSet(ResultSet rs) throws SQLException {
		EncryptedCredential credential = new EncryptedCredential();

		long id = rs.getLong("cre_id");
		credential.setId(id);
		long userId = rs.getLong("cre_user_id");
		credential.setUserId(userId);
		String serverId = rs.getString("cre_server_id");
		credential.setRdapServerId(serverId);
		String username = rs.getString("cre_username");
		credential.setUsername(username);
		String encryptedPassword = rs.getString("cre_encrypted_password");
		credential.setEncryptedPassword(encryptedPassword);

		return credential;
	}

}
