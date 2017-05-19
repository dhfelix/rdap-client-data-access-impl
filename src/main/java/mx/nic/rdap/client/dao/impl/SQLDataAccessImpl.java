package mx.nic.rdap.client.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import mx.nic.rdap.client.dao.exception.InitializationException;
import mx.nic.rdap.client.spi.CredentialDAO;
import mx.nic.rdap.client.spi.DataAccessImplementation;
import mx.nic.rdap.client.spi.WalletUserDAO;
import mx.nic.rdap.client.sql.DatabaseSession;

public class SQLDataAccessImpl implements DataAccessImplementation {

	// Keys for the configuration file
	private final static String SCHEMA_KEY = "schema";

	public SQLDataAccessImpl() {
		// no code
	}

	@Override
	public CredentialDAO getCredentialDAO() {
		return new CredentialDAOImpl();
	}

	@Override
	public WalletUserDAO getWalletUserDAO() {
		return new WalletUserDAOImpl();
	}

	@Override
	public void init(Properties properties) throws InitializationException {
		DatabaseSession.initRdapConnection(properties);

		String schema = properties.getProperty(SCHEMA_KEY);
		initSchema(schema);
	}

	private static void initSchema(String schema) throws InitializationException {
		if (schema == null) {
			try (InputStream resourceAsStream = SQLDataAccessImpl.class.getClassLoader()
					.getResourceAsStream("META-INF/da_impl_config.properties");) {
				Properties p = new Properties();
				p.load(resourceAsStream);
				schema = p.getProperty(SCHEMA_KEY);
			} catch (IOException e) {
				throw new InitializationException("Error while reading default properties", e);
			}
		}

		Objects.requireNonNull(schema);

		WalletUserDAOImpl.loadQueryGroup(schema);
		CredentialDAOImpl.loadQueryGroup(schema);
	}

}
