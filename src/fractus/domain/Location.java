package fractus.domain;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Location {
	private Inet4Address address;
	private Short port;
	
	public Location(byte[] address, Short port)
	throws UnknownHostException, InvalidLocationException {
		if (address.length != 4) {
			throw new UnknownHostException("Must be an IPv4 Address");
		}
		if (port > 65535 || port < 1) {
			throw new InvalidLocationException();
		}
		this.address = (Inet4Address) InetAddress.getByAddress(address);
	}
	
	public Location(Inet4Address address, Short port)
	throws InvalidLocationException {
		if (port > 65535 || port < 1) {
			throw new InvalidLocationException();
		}
		this.address = address;
		this.port = port;
	}
	
	public Inet4Address getAddress() {
		return address;
	}
	
	public Short getPort() {
		return port;
	}
	
	public class InvalidLocationException extends Exception {
		private static final long serialVersionUID = -7401268211634555748L;
	}
}
