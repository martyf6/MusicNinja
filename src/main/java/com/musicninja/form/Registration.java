package com.musicninja.form;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.musicninja.model.UserEntity;

public class Registration {
	
	@NotEmpty 
	@Size(min=2, max=30)
	private String username;
	
	@NotEmpty 
	@Size(min=6, max=30) 
	private String password;
	
	@NotEmpty
	private String confirmPassword;

	@NotEmpty
	@Email
	private String email;
	
	private String firstname;
	
	private String lastname;
	
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
	
	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
	
	/**
	 * Creates a new {@link UserEntity} with the registration data
	 * 
	 * @return
	 */
	public UserEntity createUser() {
		UserEntity newUser = new UserEntity();
		newUser.setUsername(getUsername());
		String hashedPw = sha1(getPassword());
		newUser.setPassword(hashedPw);
		newUser.setEmail(getEmail());
		return newUser;
	}
	
	private static String sha1(String toHash) {
		String hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[] bytes = toHash.getBytes("ASCII");
			digest.update(bytes, 0, bytes.length);
			bytes = digest.digest();
			StringBuilder sb = new StringBuilder();
			for (byte b : bytes) {
				sb.append(String.format("%02X", b));
			}
			hash = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return hash.toLowerCase(Locale.ENGLISH);
	}

}
