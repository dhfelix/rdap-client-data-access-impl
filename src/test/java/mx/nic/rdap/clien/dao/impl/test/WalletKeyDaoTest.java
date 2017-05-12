package mx.nic.rdap.clien.dao.impl.test;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import mx.nic.rdap.client.dao.exception.DataAccessException;
import mx.nic.rdap.client.dao.impl.UserDAOImpl;
import mx.nic.rdap.client.dao.impl.WalletKeyDAOImpl;
import mx.nic.rdap.client.dao.object.EncryptedWalletKey;
import mx.nic.rdap.client.dao.object.RdapClientUser;
import mx.nic.rdap.client.spi.UserDAO;
import mx.nic.rdap.client.spi.WalletKeyDAO;

public class WalletKeyDaoTest extends DatabaseTest {

	@Test
	public void testDAO() throws DataAccessException {
		long randomId = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
		String username = "wallet_user_" + randomId;

		RdapClientUser createUser = UserDaoTest.createUser(username, "");

		UserDAO userDao = new UserDAOImpl();
		userDao.storeUser(createUser);

		WalletKeyDAO dao = new WalletKeyDAOImpl();
		EncryptedWalletKey walletKey = createWalletKey(createUser.getId());

		dao.storeWalletKey(walletKey);

		EncryptedWalletKey walletKey2 = dao.getWalletKey(createUser.getId());

		Assert.assertEquals(walletKey, walletKey2);

		walletKey2.setWalletKeyAlgorithm("AES-128");
		dao.updateWalletKey(walletKey2);

		EncryptedWalletKey walletKey3 = dao.getWalletKey(createUser.getId());
		Assert.assertEquals(walletKey2, walletKey3);
		Assert.assertNotEquals(walletKey, walletKey3);

	}

	public EncryptedWalletKey createWalletKey(long userId) {
		EncryptedWalletKey encKey = new EncryptedWalletKey();

		encKey.setUserId(userId);
		encKey.setEncryptedWalletKey("soime randome key jiawijw" + userId);
		encKey.setWalletKeyAlgorithm("AES-256");
		return encKey;

	}
}
