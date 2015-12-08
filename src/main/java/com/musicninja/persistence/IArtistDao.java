package com.musicninja.persistence;

import java.util.List;

import com.musicninja.model.ArtistEntity;
import com.musicninja.model.TrackEntity;

public interface IArtistDao {
	
	public void addArtist(ArtistEntity artistEntity);
	
	public ArtistEntity getArtistById(int id);
	
	public ArtistEntity getArtistBySpotifyId(String spotifyId);
	
	public List<ArtistEntity> getArtistsByTrack(TrackEntity track);
	
	public void saveArtist(ArtistEntity artistEntity);
	
	public void deleteArtist(ArtistEntity artistEntity);

}
