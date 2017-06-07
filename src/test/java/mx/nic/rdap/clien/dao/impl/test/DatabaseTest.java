package mx.nic.rdap.clien.dao.impl.test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import mx.nic.rdap.client.dao.desktop.DatabaseCreator;
import mx.nic.rdap.client.dao.exception.InitializationException;
import mx.nic.rdap.client.dao.impl.CredentialDAOImpl;
import mx.nic.rdap.client.dao.impl.WalletUserDAOImpl;
import mx.nic.rdap.client.sql.DatabaseSession;

/**
 *
 */
public class DatabaseTest {

	/**
	 * Connection for this tests
	 */
	public static Connection connection = null;
	private static String databaseConfigurationFile = "test_config/database.properties";

	@Before
	public void before() throws SQLException {
		connection = DatabaseSession.getRdapConnection();
	}

	@After
	public void after() throws SQLException {
		connection.close();
	}

	@BeforeClass
	public static void init() throws IOException, InitializationException, SQLException {
		Properties p = new Properties();

		try (InputStream resourceAsStream = DatabaseTest.class.getClassLoader()
				.getResourceAsStream(databaseConfigurationFile);) {
			p.load(resourceAsStream);
		}

		DatabaseSession.initRdapConnection(p);
		DatabaseCreator.createDatabaseTables();
		initAllStoreModels(p.getProperty("schema"));
	}

	private static void initAllStoreModels(String schema) {
		CredentialDAOImpl.loadQueryGroup(schema);
		WalletUserDAOImpl.loadQueryGroup(schema);
	}

	@AfterClass
	public static void end() throws SQLException {
		DatabaseSession.closeRdapDataSource();
	}

}
