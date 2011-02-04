package fractus.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "User_tbl")
public class User {
	private Long id;
	private Set<User> contacts;
	private Set<Location> locations;
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	@Column(name="UserId",nullable=false)
	public Long getId() {
	    return id;
	}
	
	
	public Set<User> getContacts() {
		return contacts;
	}
}
