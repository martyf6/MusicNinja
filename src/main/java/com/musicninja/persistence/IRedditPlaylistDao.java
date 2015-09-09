package com.musicninja.persistence;

import java.util.List;

import com.musicninja.model.RedditPlaylistEntity;
import com.musicninja.model.UserEntity;

public interface IRedditPlaylistDao {

	public void addRedditPlaylist(RedditPlaylistEntity playlistEntity);
	
	public RedditPlaylistEntity getRedditPlaylistById(int id);
	
	public RedditPlaylistEntity getRedditPlaylistByPlaylistId(String playlistId);
	
	public List<RedditPlaylistEntity> getRedditPlaylistsByUser(UserEntity user);
	
	public void saveRedditPlaylist(RedditPlaylistEntity playlistEntity);
	
	public void deleteRedditPlaylist(RedditPlaylistEntity playlistEntity);

}
