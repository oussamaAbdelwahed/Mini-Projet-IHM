package spring.blog.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
// Needed for JPA (Java Persistence API)
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

@Entity
@Table(name="users")
public class User {	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="user_name",nullable = false, length = 30, unique = true)
	//@NotEmpty(message = "Please provide your User Name")
	@NotEmpty(message = "Veuillez saisir votre nom d'utilisateur")
	private String userName;
	
	@Column(name="password_hash",length = 60)
	@Length(min = 5, message = "Veuillez saisir un mot de passe de 5 caractéres au minimum")
	@NotEmpty(message = "Veuillez saisir un mot de passe")
	private String passwordHash;
	
	@Column(name="full_name",length = 100)
	@NotEmpty(message = "Veuillez saisir votre prenom")
	//means first name
	private String fullName;
	
	
	@Column(name="last_name",columnDefinition = "varchar(255) default 'PRENOMMMM'")
	@NotEmpty(message = "Veuillez saisir votre nom")
	public String lastName;
	
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@OneToMany(mappedBy = "author")
	private Set<Post> posts = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPasswordHash() {
		return passwordHash;
	}
	
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public Set<Post> getPosts() {
		return posts;
	}
	
	public void setPosts(Set<Post> posts) {
		this.posts = posts;
	}
	
	public User(Long id, String userName, String fullName) {
		this.id = id;
		this.userName = userName;
		this.fullName = fullName;
	}
	
	public User(Long id, String userName, String fullName,String lastName) {
		this(id,userName,fullName);
		this.lastName= lastName;
	}
		

	public User() {}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", passwordHash=" + passwordHash + ", fullName=" + fullName + "]";
	}
	

}
