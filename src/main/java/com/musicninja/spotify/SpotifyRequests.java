package com.musicninja.spotify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.musicninja.model.UserEntity;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.AddTrackToPlaylistRequest;
import com.wrapper.spotify.methods.CurrentUserRequest;
import com.wrapper.spotify.methods.PlaylistCreationRequest;
import com.wrapper.spotify.methods.PlaylistRequest;
import com.wrapper.spotify.methods.PlaylistTracksRequest;
import com.wrapper.spotify.methods.TopTracksRequest;
import com.wrapper.spotify.methods.TrackSearchRequest;
import com.wrapper.spotify.methods.UserPlaylistsRequest;
import com.wrapper.spotify.methods.UserRequest;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.SimplePlaylist;
import com.wrapper.spotify.models.SnapshotResult;
import com.wrapper.spotify.models.Track;
import com.wrapper.spotify.models.User;

public class SpotifyRequests {
	
	private static String CLIENT_ID = "";
	private static String CLIENT_SECRET_ID = "";
	private static String REDIRECT_URI = "http://localhost:8080/MusicNinja/spotifyauthdone";
	
	public static final Api API = Api.builder()
			  .clientId(CLIENT_ID)
			  .clientSecret(CLIENT_SECRET_ID)
			  .redirectURI(REDIRECT_URI)
			  .build();
	
	public static String getAuthURL() {

		/* Set the necessary scopes that the application will need from the user */
		final List<String> scopes = Arrays.asList(
				"playlist-read-private",
				"playlist-modify-private",
				"playlist-modify-public",
				"user-read-private",
				"user-library-read",
				"user-library-modify",
				"user-follow-read"
		);

		/* Set a state. This is used to prevent cross site request forgeries. */
		final String state = "randomStateString";

		String authorizeURL = API.createAuthorizeURL(scopes, state);

		/* Continue by sending the user to the authorizeURL, which will look something like
		   https://accounts.spotify.com:443/authorize?client_id=5fe01282e44241328a84e7c5cc169165&response_type=code&redirect_uri=https://example.com/callback&scope=user-read-private%20user-read-email&state=some-state-of-my-choice
		 */
		return authorizeURL;
	}
	
	public static User getUser(UserEntity user) throws IOException, WebApiException {
		String accessToken = TokenManager.getUserToken(user);
		API.setAccessToken(accessToken);
		final CurrentUserRequest request = API.getMe().build();
		return request.get();
	}
	
	public static User getUser(UserEntity user, String userId) throws IOException, WebApiException {
		String accessToken = TokenManager.getUserToken(user);
		API.setAccessToken(accessToken);
		final UserRequest request = API.getUser(userId).build();
		return request.get();
	}
	
	// get user's playlists (user arg)
	public static Collection<SimplePlaylist> getUserPlaylists(UserEntity user) {
		
		String accessToken = TokenManager.getUserToken(user);
		API.setAccessToken(accessToken);	
		
		int resolved = 0;
		int total = 0;
		int nextPageSize = 50;
		
		Collection<SimplePlaylist> playlists = new ArrayList<SimplePlaylist>();
		
		do {
		
			final UserPlaylistsRequest request = API.getPlaylistsForUser(user.getSpotifyUsername())
					.limit(nextPageSize)
					.offset(resolved)
					.build();
	
			try {
				
				final Page<SimplePlaylist> playlistsPage = request.get();
				total = playlistsPage.getTotal();
				System.out.println("User has: " + playlistsPage.getTotal() + " playlists.");
	
				for (SimplePlaylist playlist : playlistsPage.getItems()) {
					playlists.add(playlist);
					System.out.println(playlist.getName() + " : " + playlist.getId());
					resolved++;
				}
				
				nextPageSize = (total - resolved) > 50 ? 50 : (total - resolved);
			} catch (Exception e) {
				System.out.println("Something went wrong!" + e.getMessage());
			}
		} while (resolved < total);
		
		return playlists;
	}
	
	/**
	 * Get a user's playlist
	 * @param user
	 * @param playlistOwner - spotify user id of the playlist owner
	 * @param playlistId
	 * @return
	 */
	public static Playlist getPlaylist(UserEntity user, String playlistOwner, String playlistId) {
		
		String accessToken = TokenManager.getUserToken(user);
		API.setAccessToken(accessToken);	
		
		System.out.println("Requesting playlist: " + playlistId + " by owner: " + playlistOwner);
		
		final PlaylistRequest request = API.getPlaylist(playlistOwner, playlistId).build();

		try {
		   final Playlist playlist = request.get();

		   System.out.println("\tRetrieved playlist " + playlist.getName());
		   System.out.println("\t" + playlist.getDescription());
		   System.out.println("\tIt contains " + playlist.getTracks().getTotal() + " tracks");
		   
		   return playlist;

		} catch (Exception e) {
		   System.out.println("Something went wrong!" + e.getMessage());
		   e.printStackTrace();
		}
		
		return null;
	}
	
	/** 
	 * Get playlist tracks.
	 * TODO: use playlist pages to get tracks (since we have the playlist object), 
	 * rather than calling the other function.
	 * 
	 * @param user
	 * @param playlist
	 * @return
	 */
	public static Collection<PlaylistTrack> getPlaylistTracks(UserEntity user, Playlist playlist) {
		return getPlaylistTracks(user, playlist.getOwner().getId(), playlist.getId());
	}
	
	/**
	 * Get a playlist's tracks
	 * @param user
	 * @param ownerId - spotify user id of the playlist owner
	 * @param playlistId
	 * @return
	 */
	public static Collection<PlaylistTrack> getPlaylistTracks(UserEntity user,
			String ownerId,
			String playlistId) {

		String accessToken = TokenManager.getUserToken(user);
		API.setAccessToken(accessToken);

		int resolved = 0;
		int total = 0;
		int nextPageSize = 50;
		
		Collection<PlaylistTrack> tracks = new ArrayList<PlaylistTrack>();
		
		do {
			
			final PlaylistTracksRequest request = API.getPlaylistTracks(
					ownerId, playlistId)
					.limit(nextPageSize)
					.offset(resolved)
					.build();
			
			try {
				
				final Page<PlaylistTrack> trackPage = request.get();
				total = trackPage.getTotal();
				System.out.println("User '" + ownerId + "' has: " + trackPage.getTotal() + " tracks in playlist: " + playlistId);
	
				final List<PlaylistTrack> playlistTracks = trackPage.getItems();
	
				for (PlaylistTrack playlistTrack : playlistTracks) {
					tracks.add(playlistTrack);
					System.out.println(playlistTrack.getTrack().getName() + ", id: " + playlistTrack.getTrack().getId() + ", uri: " + playlistTrack.getTrack().getUri());
					resolved++;
				}
	
				nextPageSize = (total - resolved) > 50 ? 50 : (total - resolved);
			} catch (Exception e) {
				System.out.println("Something went wrong!" + e.getMessage());
			}
		} while (resolved < total);
		
		return tracks;
	}
	
	/**
	 * Use a spotify artist's id (not URL) to get a list of top tracks
	 * @param artistId
	 * @return
	 */
	public static List<Track> getArtistTopSongs(String artistId) {
		
		final TopTracksRequest request = API.getTopTracksForArtist(artistId, "US")
				.build();
		
		try {
			final List<Track> tracks = request.get();
			
			for (SimpleArtist artist : tracks.get(0).getArtists()) {
				if (artist.getId().equals(artistId))
					System.out.println("Getting most popular tracks for: " + artist.getName());
			}
			
			if (tracks.size() != 10) 
				System.out.println("10 TRACKS NOT RETURNED!");
			
			for (Track track : tracks) {
				System.out.println("\t" + track.getName() + " with popularity: " + track.getPopularity());
			}
			
			return tracks;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WebApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	public static Track getSongs(UserEntity user, String artist, String title) {
		
		String accessToken = TokenManager.getUserToken(user);
		API.setAccessToken(accessToken);
		final TrackSearchRequest request = API.searchTracks(artist + " " + title)
				.build();
		
		try {
			final Page<Track> tracks = request.get();
			
			if (tracks != null) {
				List<Track> tracksList = tracks.getItems();
				if (tracksList != null && !tracksList.isEmpty()) {
					return tracksList.get(0);
				}
			}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WebApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	public static Playlist createPlaylist(UserEntity user, String title) {
		String accessToken = TokenManager.getUserToken(user);
		API.setAccessToken(accessToken);
		return createPlaylist(user.getSpotifyUsername(),title);
	}
	
	private static Playlist createPlaylist(String spotifyUsername, String title) {
		
		final PlaylistCreationRequest request = API.createPlaylist(spotifyUsername, title)
				.publicAccess(false)
				.build();
			
		try {
			Playlist playlist = request.get();
			
			if (playlist != null) {
				
				System.out.println("Created playlist '" + title + "' for user: " + spotifyUsername);
				return playlist;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WebApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static boolean createPlaylistWithTracks(UserEntity user, String title, List<String> tracksToAdd) {
		
		final PlaylistCreationRequest request = API.createPlaylist(user.getSpotifyUsername(), title)
			.build();
		
		try {
			Playlist playlist = request.get();
			
			if (playlist != null) {
				
				System.out.println("Created playlist '" + title + "' for user: " + user.getSpotifyUsername());
				
				List<String> trackUrisToAdd = new ArrayList<String>();
				for (String track : tracksToAdd) {
					trackUrisToAdd.add("spotify:track:" + track);
				}
				
				return addTracksToPlaylist(user, playlist.getId(), trackUrisToAdd);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WebApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}
	
	public static boolean addTracksToPlaylist(UserEntity user, String playlistId, List<String> tracksToAdd) {
		
		String accessToken = TokenManager.getUserToken(user);
		API.setAccessToken(accessToken);
		return addTracksToPlaylist(user.getSpotifyUsername(), playlistId, tracksToAdd);
	}
	
	public static boolean addTracksToPlaylist(String username, String playlistId, List<String> tracksToAdd) {
		
		final AddTrackToPlaylistRequest request = API.addTracksToPlaylist(username, playlistId, tracksToAdd)
				.position(0)
				.build();
		
		try {
			SnapshotResult result = request.get();
			
			System.out.println(result.getSnapshotId());
			
			return true;
		} catch (IOException | WebApiException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.err.println(e.getMessage());
			System.err.println(e.getCause());
		}
		return false;
	}
}
