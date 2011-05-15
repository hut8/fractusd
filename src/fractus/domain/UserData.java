package fractus.domain;

import java.util.Set;

public class UserData {
	private String userName;
	private Set<UserData> contacts;
	private Set<Location> locations;
	
	public UserData() {
		
	}
	
	public Set<UserData> getContacts() {
		return contacts;
	}
}
