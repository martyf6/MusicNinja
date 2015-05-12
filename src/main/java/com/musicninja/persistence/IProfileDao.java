package com.musicninja.persistence;

import java.util.Collection;

import com.musicninja.model.ProfileEntity;
import com.musicninja.model.UserEntity;

public interface IProfileDao {

	public void addProfile(ProfileEntity profileEntity);
	
	public ProfileEntity getProfileById(int id);
	
	public ProfileEntity getProfileByEchoId(String echoId);
	
	public Collection<ProfileEntity> getProfilesByUser(UserEntity user);
	
	public void saveProfile(ProfileEntity profileEntity);
	
	public void deleteProfile(ProfileEntity profileEntity);

}
