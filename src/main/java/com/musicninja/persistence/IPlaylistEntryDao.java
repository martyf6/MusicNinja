package com.musicninja.persistence;

import java.util.Collection;
import java.util.Date;

import com.musicninja.model.PlaylistEntryEntity;
import com.musicninja.model.PlaylistEntity;
import com.musicninja.model.TrackEntity;

public interface IPlaylistEntryDao {

	public void addPlaylistEntry (PlaylistEntryEntity playlistEntryEntity);
	
	public PlaylistEntryEntity getPlaylistEntryById(int id);
	
	public Collection<PlaylistEntryEntity> getPlaylistEntriesByPlaylist(PlaylistEntity playlist);
	
	public Collection<PlaylistEntryEntity> getPlaylistEntriesByTrack(TrackEntity track);
	
	public PlaylistEntryEntity getPlaylistEntryByInfo(TrackEntity track, String addedById, Date addedAt);
	
	public void savePlaylistEntry(PlaylistEntryEntity playlistEntryEntity);
	
	public void deletePlaylistEntry(PlaylistEntryEntity playlistEntryEntity);

}