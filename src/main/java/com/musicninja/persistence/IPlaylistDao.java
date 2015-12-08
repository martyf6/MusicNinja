package com.musicninja.persistence;

import java.util.Collection;

import com.musicninja.model.PlaylistEntity;

public interface IPlaylistDao {

	public void addPlaylist(PlaylistEntity topListEntity);
	
	public PlaylistEntity getPlaylistById(int id);
	
	public Collection<PlaylistEntity> getPlaylistsBySource(String source);
	
	public PlaylistEntity getPlaylistBySourceId(String sourceId);
	
	public void savePlaylist(PlaylistEntity playlistEntity);
	
	public void deletePlaylist(PlaylistEntity playlistEntity);

}
