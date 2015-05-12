package com.musicninja.suggest;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.musicninja.model.ProfileEntity;
import com.musicninja.model.UserEntity;
import com.musicninja.spotify.SpotifyRequests;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

public class PlaylistFilter {
	
	// filter owner
	private UserEntity user;
	
	// Echonest profile
	private ProfileEntity profile;
	
	// Spotify playlist
	private Playlist playlist;
	
	// Playlist preferences
	private Preference preference;
	
	private Map<String,Set<String>> artistTracks;
	private Map<String,Set<String>> artistPopularTracks;
	
	public PlaylistFilter(UserEntity user,
			ProfileEntity profile, 
			Playlist playlist, 
			Preference preference){
		this.profile = profile;
		this.playlist = playlist;
		this.preference = preference;
		
		// other tasks to prep the filter...
		
		// create an artist histogram
		artistTracks = new HashMap<String,Set<String>>();
		artistPopularTracks = new HashMap<String,Set<String>>();
		
		System.out.println("Creating artist track histogram");
		Collection<PlaylistTrack> playlistTracks = SpotifyRequests.getPlaylistTracks(user, playlist);
		for (PlaylistTrack track : playlistTracks) {
			for (SimpleArtist artist : track.getTrack().getArtists()) {
				
				System.out.println(track.getTrack().getId());

				if (artistTracks.containsKey(artist.getId())) {
					// Testing use of name instead of track id
					//artistTracks.get(artist.getId()).add(track.getTrack().getId());
					artistTracks.get(artist.getId()).add(track.getTrack().getName());
				} else {
					// Testing use of name instead of track id
					//artistTracks.put(artist.getId(), new HashSet<String> (Arrays.asList(track.getTrack().getId())));
					artistTracks.put(artist.getId(), new HashSet<String> (Arrays.asList(track.getTrack().getName())));
				}
			}
		}
		
		System.out.println("Determining familiar artist most popular tracks for exclusion");
		for (Entry<String, Set<String>> tracks : artistTracks.entrySet()) {
			int numTracks = tracks.getValue().size(); 
			int stopAfter = 0;
			if (numTracks > 1 && numTracks < 3) stopAfter = 5;
			else if (numTracks >= 3) stopAfter = 10;
			if (stopAfter == 0) continue;
			
			System.out.println("Artist: " + tracks.getKey() + " is familiar to user... avoiding top " + stopAfter + " tracks.");
			Set<String> tracksToAdd = new HashSet<String>();
			List<Track> mostPopularTracks = SpotifyRequests.getArtistTopSongs(tracks.getKey());
			for(int i=0; i < stopAfter; i++) {
				Track t = mostPopularTracks.get(i);
				System.out.println("\t\tTrack: " + t.getName() + " - " + t.getId());
				// Testing use of name instead of track id
				//tracksToAdd.add(t.getId());
				tracksToAdd.add(t.getName());
			}
			artistPopularTracks.put(tracks.getKey(), tracksToAdd);	
		}
		
		// use the histogram to populate discouraged songs list
	}
	
	/**
	 * Determine whether or not to include a track in a playlist recommendation.
	 * @param song
	 * @return true if the song should be included, false if it should be excluded
	 */
	public FilterResponse filter(Song song) {
		System.out.println("Determining to filter song: " + song.getArtistName() + " - " + song.getTitle());
		try {
			
			JSONArray artists = (JSONArray) song.getObject("artist_foreign_ids");
			for (int i=0; i<artists.size(); i++) {
				JSONObject artist = (JSONObject) artists.get(i);
				String artistId = artist.get("foreign_id").toString();
				if (artistId.contains(":artist:")) artistId = artistId.substring(artistId.lastIndexOf(':') + 1);
				
				com.echonest.api.v4.Track track = song.getTrack("spotify");
				String trackId = track.getForeignID();
				if (trackId.contains(":track:")) trackId = trackId.substring(trackId.lastIndexOf(':') + 1);
				
				System.out.println("\tArtist: " + artistId + " - Track: " + trackId);
				
				// Testing use of name instead of track id
				//if (artistTracks.containsKey(artistId) && artistTracks.get(artistId).contains(trackId)) {
				if (artistTracks.containsKey(artistId) && artistTracks.get(artistId).contains(track.getTitle())) {
					String reason = "User already has song in playlist.";
					System.out.println("\t" + reason);
					return new FilterResponse(false, reason);
				} else if (artistPopularTracks.containsKey(artistId)) {
					System.out.println("\tUser is familiar with artist.");
					// Testing use of name instead of track id
					//if (artistPopularTracks.get(artistId).contains(trackId)){
					if (artistPopularTracks.get(artistId).contains(track.getTitle())){
						String reason = "Song is too popular for this user.";
						System.out.println("\t" + reason);
						return new FilterResponse(false, reason);
					}
				}
			}			
		} catch (EchoNestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// default is to include the song
		return new FilterResponse(true, "");
	}

	public UserEntity getUser() {
		return user;
	}

	public ProfileEntity getProfile() {
		return profile;
	}

	public Playlist getPlaylist() {
		return playlist;
	}

	public Preference getPreference() {
		return preference;
	}
	
	public class FilterResponse {
		private boolean isFiltered;
		private String reason;
		
		public FilterResponse(boolean isFiltered, String reason) {
			this.isFiltered = isFiltered;
			this.reason = reason;
		}

		public boolean isFiltered() {
			return isFiltered;
		}

		public void setFiltered(boolean isFiltered) {
			this.isFiltered = isFiltered;
		}

		public String getReason() {
			return reason;
		}

		public void setReason(String reason) {
			this.reason = reason;
		}
	}
}
