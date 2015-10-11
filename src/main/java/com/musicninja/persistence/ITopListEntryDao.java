package com.musicninja.persistence;

import java.util.Collection;

import com.musicninja.model.TopListEntryEntity;
import com.musicninja.model.TopListEntity;
import com.musicninja.model.MusicObjectEntity;

public interface ITopListEntryDao {

	public void addTopListEntry (TopListEntryEntity topListEntryEntity);
	
	public TopListEntryEntity getTopListEntryById(int id);
	
	public Collection<TopListEntryEntity> getTopListEntriesByList(TopListEntity topList);
	
	public Collection<TopListEntryEntity> getTopListEntriesByMusicObject(MusicObjectEntity musicObject);
	
	public void saveTopListEntry(TopListEntryEntity topListEntryEntity);
	
	public void deleteTopListEntry(TopListEntryEntity topListEntryEntity);

}