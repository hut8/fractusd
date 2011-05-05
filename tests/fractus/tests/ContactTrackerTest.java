package fractus.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fractus.main.ContactTracker;
import fractus.main.ContactTracker.ContactRequestException;

public class ContactTrackerTest {
	private ContactTracker contactTracker;
	private List<String> users;
	
	public ContactTrackerTest() {
		users = new ArrayList<String>();
		RandomString rs = new RandomString(32);
		for (int i=0; i < 100; i++) {
			users.add(rs.nextString());
		}
	}
	
	@Before
	public void setUp() throws Exception {
		contactTracker = ContactTracker.getInstance();
		contactTracker.initialize();
	}
	
	public void testAddContacts() {
		
	}
	
	public void testAddRedundant() {
		
	}
	
	public void testRemoveContact()
	throws ContactRequestException, IOException {
		// Give the first person 50 contacts
		for (int i=1; i < 51; i++) {
			contactTracker.addContact(users.get(0), users.get(i));
		}
		// Remove the first, then the second twice.  Fails on last.
		contactTracker.removeContact(users.get(0), users.get(1));
		contactTracker.removeContact(users.get(0), users.get(2));
		
	}
	
	@Test(expected = ContactTracker.ContactRequestException.class)
	public void testRemoveContactRedundant()
	throws ContactRequestException, IOException {
		// Give the first person 50 contacts
		for (int i=1; i < 51; i++) {
			contactTracker.addContact(users.get(0), users.get(i));
		}
		// Remove the first, then the second twice.  Fails on last.
		contactTracker.removeContact(users.get(0), users.get(1));
		contactTracker.removeContact(users.get(0), users.get(2));
		contactTracker.removeContact(users.get(0), users.get(2));
	}
	
	@Test
	public void testGetContacts()
	throws ContactRequestException, IOException {
		for (int i=0; i < users.size(); i++) {
			assertTrue(contactTracker.getContacts(users.get(i)).isEmpty());
		}
		
		// Give the first person the rest of the contacts
		for (int i=1; i < users.size(); i++) {
			contactTracker.addContact(users.get(0), users.get(i));
		}
		
		Set<String> expected = new HashSet<String>(users.subList(1, users.size()-1));
		Set<String> received = contactTracker.getContacts(users.get(0));
		assertEquals("Got correct contacts", expected, received);
		
		for (int i=1; i < users.size(); i++) {
			assertTrue(contactTracker.getContacts(users.get(i)).isEmpty());
		}
	}
	
	@After
	public void tearDown()
	throws IOException {
		for (String s : users) {
			contactTracker.deleteUser(s);
		}
	}
}
