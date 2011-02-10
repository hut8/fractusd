package fractus.domain;

import java.util.Set;

public class User {
	private Long id;
	private Set<User> contacts;
	private Set<Location> locations;
	

	public Long getId() {
	    return id;
	}
	
	
	public Set<User> getContacts() {
		return contacts;
	}
}