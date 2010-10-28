package fractus.main;


public class UserLocation {
	private String address;
	private Integer port;
	
	public UserLocation(String address, Integer port) {
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
