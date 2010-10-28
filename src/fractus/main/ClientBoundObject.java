package fractus.main;

import java.net.InetAddress;


public interface ClientBoundObject {
	InetAddress getAddress();
	Integer getPort();
	Integer getUserId();
	
	
}
