package fractus.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="Location_tbl")
public class Location {
	private Integer locationId;
	private String address;
	private Integer port;
	
	public Location(String address, Integer port) {
		this.address = address;
		this.port = port;
	}
	
	public String getAddress() {
		return address;
	}
	
	public Integer getPort() {
		return port;
	}
}
