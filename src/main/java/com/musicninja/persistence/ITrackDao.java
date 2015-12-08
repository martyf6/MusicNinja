package com.musicninja.persistence;

import java.util.List;

import com.musicninja.model.ArtistEntity;
import com.musicninja.model.TrackEntity;

public interface ITrackDao {
	
	public void addTrack(TrackEntity trackEntity);
	
	public TrackEntity getTrackById(int id);
	
	public TrackEntity getTrackBySpotifyId(String spotifyId);
	
	public List<TrackEntity> getTracksByArtist(ArtistEntity artist);
	
	public void saveTrack(TrackEntity trackEntity);
	
	public void deleteTrack(TrackEntity trackEntity);

}
