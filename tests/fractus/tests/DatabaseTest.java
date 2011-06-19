package fractus.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fractus.domain.AccountData;
import fractus.main.Database;

public class DatabaseTest {
	private Database database;
	
	private AccountData[] accounts = 
		{ new AccountData() };
	
	
	@Before
	public void setUp() throws Exception {
		database = Database.getInstance();
		database.initialize();
	}
	
	@After
	public void tearDown() throws Exception {
		Database.getInstance().shutdown();
	}

	@Test
	public void testAddContact() {
		//this.database.addContact(sourceUsername, destinationUsername);
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
	public void testGetAccountData() {
		fail("Not yet implemented");
	}

	@Test
	public void testRegisterAccount() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeactivateAccount() {
		fail("Not yet implemented");
	}

	@Test
	public void testRegisterLocation() {
		fail("Not yet implemented");
	}

	@Test
	public void testUnregisterLocation() {
		fail("Not yet implemented");
	}

	@Test
	public void testLocateUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetContactLocationData() {
		fail("Not yet implemented");
	}

}
