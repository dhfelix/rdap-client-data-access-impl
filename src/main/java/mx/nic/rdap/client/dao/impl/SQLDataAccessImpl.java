package mx.nic.rdap.client.dao.impl;

import java.util.Properties;

import mx.nic.rdap.client.dao.exception.InitializationException;
import mx.nic.rdap.client.spi.DataAccessImplementation;
import mx.nic.rdap.client.spi.UserDAO;
import mx.nic.rdap.client.spi.WalletDAO;

public class SQLDataAccessImpl implements DataAccessImplementation {

	public SQLDataAccessImpl() {
		// no code
	}

	@Override
	public UserDAO getUserDAO() {
		return new UserDAOImpl();
	}

	@Override
	public WalletDAO getWalletDAO() {
		return new WalletDAOImpl();
	}

	@Override
	public void init(Properties properties) throws InitializationException {
		// TODO Auto-generated method stub

	}

}
