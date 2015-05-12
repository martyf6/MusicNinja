package com.musicninja.persistence;

import com.musicninja.model.UserEntity;

public interface IUserDao {

	public void addUser(UserEntity userEntity);
	
	public UserEntity getUserById(int id);
	
	public UserEntity getUserByUsername(String username);
	
	public void saveUser(UserEntity userEntity);
	
	public void deleteUser(UserEntity userEntity);

}
