package com.musicninja.reddit;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.musicninja.model.RedditPlaylistEntity;
import com.musicninja.model.UserEntity;
import com.musicninja.spotify.SpotifyRequests;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.Track;

public class RedditRequests {
	
	private static CloseableHttpClient httpClient = HttpClientBuilder.create().build();
	
	private static final String MUSIC_CRITERIA = "flair:\"music streaming\" OR flair:\"new release\" OR flair:\"music download\" OR flair:\"stream\" OR flair:\"download\" OR flair:\"underground\"";
	private static final PostPeriod DEFAULT_PERIOD = PostPeriod.DAY;
	private static final int DEFAULT_LIMIT = 50;
	
	private static final String DEFAULT_SUB = "Music";
	
	public static List<SimpleSong> getSubMusic(String sub) {
		return getSubMusic(sub, DEFAULT_PERIOD, DEFAULT_LIMIT);
	}
	
	public static List<SimpleSong> getSubMusic(String sub, PostPeriod period, int limit) {
		
		List<SimpleSong> songs = new ArrayList<SimpleSong>();
		
		if (limit < 1) {
			System.err.println("Argument 'limit' for number of songs to retrieve cannot be less than 1.");
			return songs;
		}
		
		String limitStr = Integer.toString(limit);
		
		try {
			URIBuilder builder = new URIBuilder("http://xl.reddit.com/r/" + sub + "/search.json")
				.addParameter("limit", limitStr)
				.addParameter("restrict_sr","on")
				.addParameter("sort", "top")
				.addParameter("t", period.getRequestVal());
			
			// Music sub can better filter using flair tags on posts
			if (sub.equals("Music")) {
				builder.addParameter("q", MUSIC_CRITERIA);
			}
			
			System.out.println(builder.toString());
			
            HttpGet request = new HttpGet(builder.build());
            request.addHeader("User-Agent", "java:com.musicninja.reddit:v0.0.1 (by /u/MusicNinjaTool)");
            request.addHeader("content-type", "application/json");
            HttpResponse result = httpClient.execute(request);
            
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            try {
            	
                JSONObject obj = new JSONObject(json);
                
                System.out.println(obj.get("kind"));
                JSONObject data = obj.getJSONObject("data");
                // data.children is list of listings
                JSONArray listings = data.getJSONArray("children");
                System.out.println("Listings: " + listings.length());
                
                for (int i=0; i<listings.length(); i++) {
                	
                	// listing.data.title
                	JSONObject listing = listings.getJSONObject(i);
                	JSONObject listingObj = listing.getJSONObject("data");
                	//System.out.println(listingObj.get("title"));
                	Object domain = listingObj.get("domain");
                	
                	// check domain
                	if (domain != null && domain instanceof String &&
                			(domain.equals("soundcloud.com")
                					|| domain.equals("m.soundcloud.com")
                					|| domain.equals("youtube.com") 
                					|| domain.equals("youtu.be")
                					|| domain.equals("m.youtube.com"))) {
                		
                		SimpleSong song = SimpleSong.parseSong(listingObj.get("title").toString());
                		if (song != null)
                			songs.add(song);
                		
                		if (song == null) {
                			System.out.println("\tNo song could be parsed from post title: " + listingObj.get("title").toString());
                		}
                	}
                }
            } catch (JSONException e) {
            	// TODO: handle exception
            	e.printStackTrace();
            }
        } catch (URISyntaxException e) {
        	// TODO: handle exception
        	e.printStackTrace();
        } catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return songs;
    }
	
	public static List<SimpleSong> updateSpotifyPlaylist(UserEntity user, RedditPlaylistEntity playlist) {
		String subReddit = playlist.getSubReddit();
		subReddit = (subReddit == null || subReddit.isEmpty()) ? DEFAULT_SUB : subReddit;
		PostPeriod period = PostPeriod.fromString(playlist.getPeriod());
		
		return updateSpotifyPlaylist(user, subReddit, period, playlist.getLimit(), playlist.getPlaylistId());
	}
	
	public static List<SimpleSong> updateSpotifyPlaylist(UserEntity user, String subReddit, PostPeriod period, int limit, String playlistId) {
		
		
		
		List<String> songsToAdd = new ArrayList<String>();
		
		System.out.println("Getting songs from reddit sub: " + subReddit + "\n");
		List<SimpleSong> redditTopSongs = RedditRequests.getSubMusic(subReddit, period, limit);
		List<SimpleSong> redditSongsNotFound = new ArrayList<SimpleSong>();
		
		for (SimpleSong song : redditTopSongs) {
			System.out.println(song.toString() + "\n");
			
			Track track = SpotifyRequests.getSongs(user, song.getArtist(), song.getTitle());
			
			if (track != null) {
				System.out.println("\tfound track: " + track.getArtists().get(0).getName() + " - " + track.getName());
				songsToAdd.add(track.getUri());
			} else {
				System.out.println("\tcould not find track on Spotify!");
				redditSongsNotFound.add(song);
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// avoid adding duplicate tracks
		Collection<PlaylistTrack> playlistTracks = SpotifyRequests.getPlaylistTracks(user, user.getSpotifyUsername(), playlistId);
		for (PlaylistTrack pTrack : playlistTracks) {
			Track track = pTrack.getTrack();
			String trackUri = track.getUri();
			if (songsToAdd.contains(trackUri)) {
				System.out.println("Avoiding duplicate track: " + track.getName());
				songsToAdd.remove(trackUri);
			}
		}
		
		if (!songsToAdd.isEmpty()) {
			System.out.println("Adding " + songsToAdd.size() + " songs to reddit playlist.");
			boolean success = SpotifyRequests.addTracksToPlaylist(user, playlistId, songsToAdd);
			System.out.println("Successfully added tracks? " + success);
		}
		
		System.out.println("Could not find " + redditSongsNotFound.size() + " songs:");
		for (SimpleSong song : redditSongsNotFound) {
			System.out.println("\t" + song);
		}
		
		return redditSongsNotFound;
	}
}
