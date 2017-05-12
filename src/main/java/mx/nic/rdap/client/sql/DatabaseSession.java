package mx.nic.rdap.client.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import mx.nic.rdap.client.dao.exception.InitializationException;

/**
 * Manage the Datasource used in the database connection
 */
public class DatabaseSession {

	private static final Logger logger = Logger.getLogger(DatabaseSession.class.getName());

	private static DataSource rdapDataSource;

	private static void testDatabase(BasicDataSource ds) throws SQLException {
		// http://stackoverflow.com/questions/3668506
		final String TEST_QUERY = "select 1";
		try (Connection connection = ds.getConnection(); Statement statement = connection.createStatement();) {
			ResultSet resultSet = statement.executeQuery(TEST_QUERY);

			if (!resultSet.next()) {
				throw new SQLException("'" + TEST_QUERY + "' returned no rows.");
			}
			int result = resultSet.getInt(1);
			if (result != 1) {
				throw new SQLException("'" + TEST_QUERY + "' returned " + result);
			}
		}
	}

	public static Connection getRdapConnection() throws SQLException {
		return rdapDataSource.getConnection();
	}

	public static void initRdapConnection(Properties config) throws InitializationException {
		/*
		 * This code is rather awkward, I admit.
		 * 
		 * The problem is, at compile time, the application does not know the
		 * data access implementation, and the data access implementation does
		 * not know the application.
		 * 
		 * So we don't know for sure where the data source can be found, and the
		 * application cannot tell us. There is no interface for the application
		 * to provide us with a data source, because the data source is OUR
		 * problem. From the app's perspective, it might not even exist.
		 * 
		 * So what we're going to do is probe the candidate locations and stick
		 * with what works.
		 */

		rdapDataSource = findRdapDataSource(config);
		if (rdapDataSource != null) {
			logger.info("Data source found.");
			return;
		}

		logger.info("I could not find the RDAP data source in the context. "
				+ "This won't be a problem if I can find it in the configuration. Attempting that now... ");
		rdapDataSource = loadDataSourceFromProperties(config);
	}

	private static DataSource findRdapDataSource(Properties config) {
		Context context;
		try {
			context = new InitialContext();
		} catch (NamingException e) {
			logger.log(Level.INFO, "I could not instance an initial context. "
					+ "I will not be able to find the data source by JDNI name.", e);
			return null;
		}

		String jdniName = config.getProperty("db_resource_name");
		if (jdniName != null) {
			return findRdapDataSource(context, jdniName);
		}

		// Try the default string.
		// In some server containers (such as Wildfly), the default string is
		// "java:/comp/env/jdbc/rdap".
		// In other server containers (such as Payara), the string is
		// "java:comp/env/jdbc/rdap".
		// In other server containers (such as Tomcat), it doesn't matter.
		DataSource result = findRdapDataSource(context, "java:/comp/env/jdbc/rdap");
		if (result != null) {
			return result;
		}

		return findRdapDataSource(context, "java:comp/env/jdbc/rdap");
	}

	private static DataSource findRdapDataSource(Context context, String jdniName) {
		logger.info("Attempting to find data source '" + jdniName + "'...");
		try {
			return (DataSource) context.lookup(jdniName);
		} catch (NamingException e) {
			logger.info("Data source not found. Attempting something else...");
			return null;
		}
	}

	private static DataSource loadDataSourceFromProperties(Properties config) throws InitializationException {
		String driverClassName = config.getProperty("driverClassName");
		String url = config.getProperty("url");
		if (driverClassName == null || url == null) {
			throw new InitializationException("I can't find a data source in the configuration.");
		}

		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(config.getProperty("userName"));
		dataSource.setPassword(config.getProperty("password"));
		dataSource.setDefaultAutoCommit(Boolean.parseBoolean(config.getProperty("autoCommit", "false")));
		dataSource.setMaxTotal(Integer.parseInt(config.getProperty("maxTotal", "8")));
		
		dataSource.setInitialSize(Integer.parseInt(config.getProperty("initialSize", "0")));
		dataSource.setMaxIdle(Integer.parseInt(config.getProperty("maxIdle", "8")));
		dataSource.setMinIdle(Integer.parseInt(config.getProperty("minIdle", "0")));
		dataSource.setMaxWaitMillis(Integer.parseInt(config.getProperty("maxWaitMillis", "-1")));
		dataSource.setMinEvictableIdleTimeMillis(
				Integer.parseInt(config.getProperty("minEvictableIdleTimeMillis", "1800000")));

		dataSource.setMaxConnLifetimeMillis(Integer.parseInt(config.getProperty("maxConnLifetimeMillis", "-1")));

		try {
			testDatabase(dataSource);
		} catch (SQLException e) {
			throw new InitializationException("The database connection test yielded failure.", e);
		}

		return dataSource;
	}

	public static void closeRdapDataSource() throws SQLException {
		if (rdapDataSource instanceof BasicDataSource) {
			((BasicDataSource) rdapDataSource).close();
		}
	}

}
