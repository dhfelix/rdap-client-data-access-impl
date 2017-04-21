package mx.nic.rdap.client.dao.impl;

import java.util.Properties;

import mx.nic.rdap.client.dao.exception.DataAccessException;
import mx.nic.rdap.client.dao.exception.InitializationException;
import mx.nic.rdap.client.spi.DataAccessImplementation;
import mx.nic.rdap.client.spi.UserDAO;
import mx.nic.rdap.client.spi.WalletDAO;

public class SQLDataAccessImpl implements DataAccessImplementation {

	public SQLDataAccessImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public UserDAO getUserDAO() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WalletDAO getWalletDAO() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(Properties arg0) throws InitializationException {
		// TODO Auto-generated method stub

	}

}
