package fractus.domain;

import java.io.Serializable;
import java.util.Date;

public class AccountData implements Serializable {
	private static final long serialVersionUID = 8330836100100832682L;

	private String username;
	private byte[] password;
	private byte[] salt;
	private String email;
	private Date creationDate;
	
	public AccountData() {
		this.creationDate = new Date();
	}
	
	public AccountData(final AccountData account) {
		this.username = account.username;
		this.password = account.password;
		this.salt = account.salt;
		this.email = account.email;
		this.creationDate = account.creationDate;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public byte[] getPassword() {
		return password;
	}
	public void setPassword(byte[] password) {
		this.password = password;
	}
	public byte[] getSalt() {
		return salt;
	}
	public void setSalt(byte[] salt) {
		this.salt = salt;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String toString() {
		return "[Account username=" + this.username + " email=" + this.email + "]";
	}
	
}
