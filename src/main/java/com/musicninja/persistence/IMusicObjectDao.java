package com.musicninja.persistence;

import java.util.Collection;

import com.musicninja.model.MusicObjectEntity;

public interface IMusicObjectDao {

	public void addMusicObject(MusicObjectEntity musicObjectEntity);
	
	public MusicObjectEntity getMusicObjectById(int id);
	
	public Collection<MusicObjectEntity> getMusicObjectsByType(String type);
	
	public void saveMusicObject(MusicObjectEntity musicObjectEntity);
	
	public void deleteMusicObject(MusicObjectEntity musicObjectEntity);

}
