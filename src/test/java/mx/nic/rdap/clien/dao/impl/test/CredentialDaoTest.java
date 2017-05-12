package mx.nic.rdap.clien.dao.impl.test;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import mx.nic.rdap.client.dao.exception.DataAccessException;
import mx.nic.rdap.client.dao.impl.CredentialDAOImpl;
import mx.nic.rdap.client.dao.impl.UserDAOImpl;
import mx.nic.rdap.client.dao.object.EncryptedCredential;
import mx.nic.rdap.client.dao.object.RdapClientUser;
import mx.nic.rdap.client.spi.CredentialDAO;
import mx.nic.rdap.client.spi.UserDAO;

public class CredentialDaoTest extends DatabaseTest {
	@Test
	public void testDAO() throws DataAccessException {
		long randomId = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
		String username = "wallet_user_" + randomId;

		RdapClientUser createUser = UserDaoTest.createUser(username, "");

		UserDAO userDao = new UserDAOImpl();
		userDao.storeUser(createUser);

		CredentialDAO dao = new CredentialDAOImpl();
		EncryptedCredential credential = getCredential(createUser.getId(), "MX");
		dao.storeCredential(credential);

		List<EncryptedCredential> credentials = dao.getCredentials(createUser.getId());

		Assert.assertTrue(credentials.contains(credential));

		credential.setRdapServerId("COM");
		dao.updateCredential(credential);

		List<EncryptedCredential> credentials2 = dao.getCredentials(createUser.getId());
		Assert.assertFalse(credentials.contains(credential));
		Assert.assertTrue(credentials2.contains(credential));

		for (EncryptedCredential cred : credentials2) {
			dao.deleteCredential(createUser.getId(), cred.getId());
		}

		List<EncryptedCredential> emptyCredentials = dao.getCredentials(createUser.getId());
		Assert.assertTrue(emptyCredentials.isEmpty());

	}

	public EncryptedCredential getCredential(long userID, String serverId) {
		EncryptedCredential cred = new EncryptedCredential();

		cred.setUserId(userID);
		cred.setUsername("random user name for server" + serverId + userID);
		cred.setRdapServerId(serverId);
		cred.setEncryptedPassword("random password " + userID);

		return cred;
	}

}
