package mx.nic.rdap.client.dao.impl;

import mx.nic.rdap.client.credential.RdapClientUser;
import mx.nic.rdap.client.credential.UserEncryptedWalletKey;
import mx.nic.rdap.client.dao.exception.DataAccessException;
import mx.nic.rdap.client.spi.UserDAO;

public class UserDAOImpl implements UserDAO {

	@Override
	public RdapClientUser getUserCredential(String username) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long storeUserCredential(RdapClientUser userCredential) throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void updateUserCredential(RdapClientUser userCredential) throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public UserEncryptedWalletKey getUserWalletKey(long userId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeUserWalletKey(UserEncryptedWalletKey userWalletKey) throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateUserWalletKey(UserEncryptedWalletKey userWalletKey) throws DataAccessException {
		// TODO Auto-generated method stub

	}

}
