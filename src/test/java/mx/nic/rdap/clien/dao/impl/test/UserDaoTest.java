package mx.nic.rdap.clien.dao.impl.test;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import mx.nic.rdap.client.dao.exception.DataAccessException;
import mx.nic.rdap.client.dao.impl.UserDAOImpl;
import mx.nic.rdap.client.dao.object.RdapClientUser;

public class UserDaoTest extends DatabaseTest {

	@Test
	public void testDAO() throws DataAccessException {
		UserDAOImpl dao = new UserDAOImpl();
		String username = "dummy_user_" + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);

		RdapClientUser user = createUser(username, "");
		// RdapClientUser user =
		// createUser(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE) +
		// "");
		long userId = dao.storeUser(user);
		user.setId(userId);

		dao = new UserDAOImpl();
		RdapClientUser user2 = dao.getUser(username);

		Assert.assertEquals(user, user2);
		Assert.assertTrue(dao.existUser(username));
		Assert.assertFalse(dao.existUser("random_dwakadk"));

		user2.setHashedPassword("another pass");
		dao.updateUser(user2);

		RdapClientUser user3 = dao.getUser(username);

		Assert.assertNotEquals(user, user3);
		Assert.assertEquals(user2, user3);

	}

	public static RdapClientUser createUser(String username, String randomString) {
		RdapClientUser user = new RdapClientUser();

		user.setUsername(username + randomString);
		user.setHashAlgorithm("hash_alg");
		user.setHashedPassword("hash_password_" + randomString);
		user.setIterations(10);
		user.setKeyAlgorithm("key_alg");
		user.setKeySize(256);
		user.setPbeAlgorith("pbe algo");
		user.setSalt("random salt_" + randomString);

		return user;
	}
}
