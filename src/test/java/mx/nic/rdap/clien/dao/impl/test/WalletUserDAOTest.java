package mx.nic.rdap.clien.dao.impl.test;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import mx.nic.rdap.client.dao.exception.DataAccessException;
import mx.nic.rdap.client.dao.impl.WalletUserDAOImpl;
import mx.nic.rdap.client.dao.object.WalletUser;
import mx.nic.rdap.client.spi.WalletUserDAO;

public class WalletUserDAOTest extends DatabaseTest {

	@Test
	public void testDAO() throws DataAccessException {
		WalletUserDAO dao = new WalletUserDAOImpl();
		String username = "dummy_user_" + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);

		WalletUser user = createUser(username, "");
		// RdapClientUser user =
		// createUser(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE) +
		// "");
		long userId = dao.store(user);
		user.setId(userId);

		dao = new WalletUserDAOImpl();
		WalletUser user2 = dao.getByUsername(username);

		Assert.assertEquals(user, user2);
		Assert.assertTrue(dao.existByUsername(username));
		Assert.assertFalse(dao.existByUsername("random_dwakadk"));

		user2.setHashedPassword("another pass");
		dao.update(user2);

		WalletUser user3 = dao.getByUsername(username);

		Assert.assertNotEquals(user, user3);
		Assert.assertEquals(user2, user3);

		dao.delete(user);
	}

	public static WalletUser createUser(String username, String randomString) {
		WalletUser user = new WalletUser();

		user.setUsername(username + randomString);
		user.setHashedPassword("hash_password_" + randomString);
		user.setHashAlgorithm("hash_alg");

		user.setSalt("random salt_" + randomString);
		user.setIterations(10);

		user.setPbeAlgorithm("pbe algo");
		user.setKeyAlgorithm("key_alg");
		user.setKeySize(256);

		user.setEncryptedWalletKey("encrypted wallet");
		user.setCipherAlgorithm("cipher_Algo");

		return user;
	}
}
