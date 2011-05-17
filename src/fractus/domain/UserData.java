package fractus.domain;

import java.util.Set;

public class UserData {
	private String username;
	private Set<Location> locations;
	
	public UserData(String username) {
		this.username =  username;
	}

	public String getUsername() {
		return username;
	}

	public Set<Location> getLocations() {
		return locations;
	}
	
	
}
