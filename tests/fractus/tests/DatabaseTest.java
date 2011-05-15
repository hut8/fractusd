package fractus.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fractus.main.Database;
import fractus.web.AccountManager;

public class DatabaseTest {
	private class AccountData {		
		private String username;
		private String password;
		private String emailAddress;
		
		
	}
	
	private List<AccountData> generateAccounts() {
		List<AccountData> accounts = new ArrayList<AccountData>();
		
		
		
		return accounts;
	}
	
	
	@Before
	public void setUp()
	throws Exception {
		
	}
	
	@After
	public void tearDown() {
		
	}
	
	@Test
	public void testCreateAccount() {
		
	}
	
	@Test
	public void testDerivePassword() {
		String password = "םك٭‡₵∑ﻙaýa϶шф";
		byte[] salt = "delicious salt".getBytes();
		byte[] passhash = AccountManager.derivePassword(password, salt);
		// Make sure we'll get the same hash a bunch of times or we're screwed.
		for (int i=0; i < 10; i++) {
			assertArrayEquals(passhash, AccountManager.derivePassword(password, salt));
		}
	}
	
	@Test
	public void testDeleteAccount() {
		
	}

	@Test
	public void testAddContact() {
		
	}

	@Test
	public void testRemoveContact() {
		fail("Not yet implemented");
	}

	@Test
	public void testVerifyContact() {
		fail("Not yet implemented");
	}

	@Test
	public void testListNonreciprocalContacts() {
		fail("Not yet implemented");
	}

	@Test
	public void testAuthenticate() {
		fail("Not yet implemented");
	}

	@Test
	public void testRegisterLocation() {
		fail("Not yet implemented");
	}

	@Test
	public void testInvalidateLocation() {
		fail("Not yet implemented");
	}

	@Test
	public void testLocateUser() {
		fail("Not yet implemented");
	}
}
