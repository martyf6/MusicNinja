package com.musicninja.persistence;

import java.util.Collection;

import com.musicninja.model.TopListEntity;

public interface ITopListDao {

	public void addTopList(TopListEntity topListEntity);
	
	public TopListEntity getTopListById(int id);
	
	public Collection<TopListEntity> getTopListsBySource(String source);
	
	public Collection<TopListEntity> getTopListsByType(String type);
	
	public void saveTopList(TopListEntity topListEntity);
	
	public void deleteTopList(TopListEntity topListEntity);

}
