package mx.nic.rdap.client.dao.model;

public class WalletDAOModel {

	// private final static Logger logger =
	// Logger.getLogger(WalletDAOModel.class.getName());
	//
	// private final static String QUERY_GROUP = "Domain";
	//
	// private static QueryGroup queryGroup = null;
	//
	// private static final String GET_RDAP_CREDENTIAL = "getByUserIdAndDomain";
	// private static final String STORE_RDAP_CREDENTIAL =
	// "storeRdapCredential";
	// private static final String UPDATE_RDAP_CREDENTIAL =
	// "updateRdapCredential";
	// private static final String DELETE_RDAP_CREDENTIAL =
	// "deleteRdapCredential";
	//
	// public static void loadQueryGroup(String schema) {
	// try {
	// QueryGroup qG = new QueryGroup(QUERY_GROUP, schema);
	// setQueryGroup(qG);
	// } catch (IOException e) {
	// throw new RuntimeException("Error loading query group");
	// }
	// }
	//
	// private static void setQueryGroup(QueryGroup qG) {
	// queryGroup = qG;
	// }
	//
	// private static QueryGroup getQueryGroup() {
	// return queryGroup;
	// }
	//
	// private WalletDAOModel() {
	// // No code;
	// }
	//
	// public UserEncryptedCredential getLogin(long userId, String serverDomain)
	// throws DataAccessException {
	//
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public long storeLogin(UserEncryptedCredential userServerCredential)
	// throws DataAccessException {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// public void updateLogin(UserEncryptedCredential userServerCredential)
	// throws DataAccessException {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// public static Long storeToDatabase(UserEncryptedCredential rdapLogin,
	// Connection connection) throws SQLException {
	// String query = getQueryGroup().getQuery(STORE_RDAP_CREDENTIAL);
	// Long loginId;
	// isValidForStore(rdapLogin);
	// try (PreparedStatement statement = connection.prepareStatement(query,
	// Statement.RETURN_GENERATED_KEYS)) {
	// fillStoreStatement(rdapLogin, statement);
	// logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
	// statement.executeUpdate();
	//
	// ResultSet resultSet = statement.getGeneratedKeys();
	// resultSet.next();
	// loginId = resultSet.getLong(1);
	// rdapLogin.setId(loginId);
	// }
	//
	// return loginId;
	// }
	//
	// private static void isValidForStore(UserEncryptedCredential rdapLogin)
	// throws IncompleteObjectException {
	// if (rdapLogin.getClientUserId() == null) {
	// throw new IncompleteObjectException("clientUserId",
	// rdapLogin.getClass().getSimpleName());
	// }
	//
	// String rdapServerDomain = rdapLogin.getRdapServerDomain();
	// if (rdapServerDomain == null || rdapServerDomain.isEmpty()) {
	// throw new IncompleteObjectException("serverDomain",
	// rdapLogin.getClass().getSimpleName());
	// }
	//
	// String username = rdapLogin.getUsername();
	// if (username == null || username.isEmpty()) {
	// throw new IncompleteObjectException("username",
	// rdapLogin.getClass().getSimpleName());
	// }
	//
	// String encryptedPassword = rdapLogin.getEncryptedPassword();
	// if (encryptedPassword == null || encryptedPassword.isEmpty()) {
	// throw new IncompleteObjectException("encryptedPassword",
	// rdapLogin.getClass().getSimpleName());
	// }
	// }
	//
	// private static void isValidForUpdate(UserEncryptedCredential rdapLogin)
	// throws IncompleteObjectException {
	// if (rdapLogin.getId() == null) {
	// throw new IncompleteObjectException("id",
	// rdapLogin.getClass().getSimpleName());
	// }
	// isValidForStore(rdapLogin);
	// }
	//
	// private static void fillGetByUserIdAndDomain(long userId, String
	// domainName, PreparedStatement statement)
	// throws SQLException {
	// statement.setLong(1, userId);
	// statement.setString(2, domainName);
	// }
	//
	// private static void fillStoreStatement(UserEncryptedCredential rdapLogin,
	// PreparedStatement statement) throws SQLException {
	// statement.setLong(1, rdapLogin.getClientUserId());
	// statement.setString(2, rdapLogin.getRdapServerDomain());
	// statement.setString(3, rdapLogin.getUsername());
	// statement.setString(4, rdapLogin.getEncryptedPassword());
	// }
	//
	// private static void fillUpdateStatement(UserEncryptedCredential
	// rdapLogin, PreparedStatement statement) throws SQLException {
	// statement.setString(1, rdapLogin.getUsername());
	// statement.setString(2, rdapLogin.getEncryptedPassword());
	//
	// statement.setLong(3, rdapLogin.getClientUserId());
	// statement.setString(4, rdapLogin.getRdapServerDomain());
	// statement.setLong(5, rdapLogin.getId());
	// }
	//
	// private static void fillDeleteStatement(UserEncryptedCredential
	// rdapLogin, PreparedStatement statement) throws SQLException {
	// statement.setLong(1, rdapLogin.getClientUserId());
	// }

}
