package fractus.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactTracker {
	private static ContactTracker instance = new ContactTracker();
	public static ContactTracker getInstance() {
		return instance;
	}
	
	public static final String CONTACT_DATA_DIR = "Data" + File.pathSeparator + "Contacts";
	
	File dataDirectory;
	
	private ContactTracker() {	}
	
	public synchronized void initialize()
	throws IllegalAccessException {
		dataDirectory = new File(CONTACT_DATA_DIR);
		dataDirectory.mkdir();
		if (!dataDirectory.isDirectory()) {
			throw new IllegalAccessException("Could not use " + CONTACT_DATA_DIR);
		}		
	}
	
	private synchronized File getUserFile(String username)
	throws IOException {
		File file = new File(CONTACT_DATA_DIR, username);
		file.createNewFile();
		return file;
	}
	
	public synchronized Set<String> getContacts(String username)
	throws IOException {
		File file = getUserFile(username);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		Set<String> contacts = new HashSet<String>();
		while ((line = reader.readLine()) != null) {
			contacts.add(line);
		}
		reader.close();
		return contacts;
	}
	
	private synchronized void setContacts(String username, Set<String> contacts)
	throws IOException {
		List<String> contactList = new ArrayList<String>(contacts);
		Collections.sort(contactList);
		File file = getUserFile(username);
		PrintWriter writer = new PrintWriter(file); 
		for (String contact : contactList) {
			writer.println(contact);
		}
		writer.flush();
		writer.close();
	}
	
	public synchronized boolean verifyContacts(String username1, String username2)
	throws IOException {
		Set<String> contacts1 = getContacts(username1);
		Set<String> contacts2 = getContacts(username2);
		return contacts1.contains(username2) && contacts2.contains(username1);
	}
	
	public synchronized void addContact(String sourceUsername, String destinationUsername)
	throws ContactRequestException, IOException {
		Set<String> sourceContacts = getContacts(sourceUsername);
		if (sourceContacts.contains(destinationUsername)) {
			throw new ContactRequestException();
		}
		sourceContacts.add(destinationUsername);
		setContacts(sourceUsername, sourceContacts);
	}
	
	public synchronized void removeContact(String sourceUsername, String destinationUsername)
	throws IOException, ContactRequestException {
		Set<String> sourceContacts = getContacts(sourceUsername);
		if (!sourceContacts.remove(destinationUsername)) {
			throw new ContactRequestException();
		}
		setContacts(sourceUsername, sourceContacts);
	}
	
	public synchronized void deleteUser(String username)
	throws IOException {
		File file = getUserFile(username);
		file.delete();
	}
	
	public static class ContactRequestException extends Exception {
		private static final long serialVersionUID = 1363806554357020771L;
	}
}
