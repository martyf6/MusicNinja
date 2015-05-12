package com.musicninja.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Table(name = "USER")
public class UserEntity {
 
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;
    
	@Column(name = "USERNAME", unique = true, nullable = false)
	private String username;

	@Column(name = "PASSWORD", nullable = false)
	private String password;
 
    @Column(name = "FIRSTNAME")
    private String firstname;
 
    @Column(name = "LASTNAME")
    private String lastname;
 
    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;
    
    @Column(name = "SPOTIFY_USERNAME")
    private String spotifyUsername;
    
    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;
    
    public int getId() {
    	return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getSpotifyUsername() {
        return spotifyUsername;
    }

    public void setSpotifyUsername(String spotifyUsername) {
        this.spotifyUsername = spotifyUsername;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    @Override
    public String toString(){
    	return "User {id: " + id + 
    			", username: " + username + 
    			", password: " + password + 
    			", firstname: " + firstname + 
    			", lastname: " + lastname + 
    			", spotify-username: " + spotifyUsername + 
    			", email: " + email + "}";
    }
}
