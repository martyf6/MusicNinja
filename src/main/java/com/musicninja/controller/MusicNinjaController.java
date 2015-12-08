package com.musicninja.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.echonest.api.v4.ArtistCatalogItem;
import com.echonest.api.v4.CatalogItem;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.GeneralCatalog;
import com.echonest.api.v4.SongCatalogItem;
import com.musicninja.echonest.EchonestRequests;
import com.musicninja.echonest.EchonestRequests.ProfileType;
import com.musicninja.model.ArtistEntity;
import com.musicninja.model.PlaylistEntity;
import com.musicninja.model.PlaylistEntryEntity;
import com.musicninja.model.ProfileEntity;
import com.musicninja.model.RedditPlaylistEntity;
import com.musicninja.model.TrackEntity;
import com.musicninja.model.UserEntity;
import com.musicninja.persistence.IArtistDao;
import com.musicninja.persistence.IPlaylistDao;
import com.musicninja.persistence.IPlaylistEntryDao;
import com.musicninja.persistence.IProfileDao;
import com.musicninja.persistence.IRedditPlaylistDao;
import com.musicninja.persistence.ITrackDao;
import com.musicninja.persistence.IUserDao;
import com.musicninja.reddit.PostPeriod;
import com.musicninja.reddit.RedditRequests;
import com.musicninja.reddit.SimpleSong;
import com.musicninja.spotify.SpotifyManage;
import com.musicninja.spotify.SpotifyRequests;
import com.musicninja.suggest.PlaylistFilter;
import com.musicninja.suggest.Preference;
import com.musicninja.suggest.Preference.Distribution;
import com.musicninja.suggest.Score;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.SimpleAlbum;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.SimplePlaylist;
import com.wrapper.spotify.models.SimpleTrack;
import com.wrapper.spotify.models.Track;
import com.wrapper.spotify.models.User;


/**
 * Controller used for handling application specific requests
 * 
 * @author marty
 *
 */
@Controller
public class MusicNinjaController {
	
	private static Logger logger = Logger.getLogger(MusicNinjaController.class);

	@Autowired
	private IUserDao userDao;
	
	@Autowired
	private IProfileDao profileDao;
	
	@Autowired
	private IRedditPlaylistDao redditPlaylistDao;
	
	@Autowired
	private ITrackDao trackDao;
	
	@Autowired
	private IArtistDao artistDao;
	
	@Autowired
	private IPlaylistDao playlistDao;
	
	@Autowired
	private IPlaylistEntryDao playlistEntryDao;
	
	@RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
	public String getHomePage(Model model, Principal principal) {
		
		String username = principal.getName();
		logger.info("Home with user '" + username + "'");
		
		model.addAttribute("username", username);
		
		UserEntity user = userDao.getUserByUsername(username);
		String spotifyUsername = user.getSpotifyUsername();
		
		if (spotifyUsername == null || spotifyUsername.isEmpty()) {
			
			return "auth-with-spotify";
			
		} else {
			
			Collection<SimplePlaylist> playlists = SpotifyRequests.getUserPlaylists(user);
			Map<String,String> playlistNameMap = new HashMap<String,String>();
			for (SimplePlaylist playlist : playlists) {
				playlistNameMap.put(playlist.getId(), playlist.getName());
			}
			model.addAttribute("playlists", playlists);
			
			Collection<ProfileEntity> profiles = profileDao.getProfilesByUser(user);
			
			// gather the playlist names for display
			Map<String,String> playlistNames = new HashMap<String,String>();
			for (ProfileEntity profile : profiles) {
				String pid = profile.getPlaylistId();
				String pOwnerid = profile.getPlaylistOwnerId();
				if (pid != null && !pid.isEmpty() &&
						pOwnerid != null && !pOwnerid.isEmpty()){
					String name = playlistNameMap.get(pid);
					if (name != null && !name.isEmpty()) {
						playlistNames.put(pid, name);
					} else {
						// we may have to look up the playlist name,
						// if it's not a playlist we have
						Playlist playlist = SpotifyRequests.getPlaylist(user, pOwnerid, pid);
						if (playlist != null) {
							String playlistName = playlist.getName();
							playlistNames.put(pid, playlistName);
						}
					}
				}
			}
			
			model.addAttribute("profiles", profiles);
			model.addAttribute("playlistNames", playlistNames);
			
			return "home";
		}
	}
	
	@RequestMapping(value = {"/me"}, method = RequestMethod.GET)
	public String getMe(Model model, Principal principal) {
		
		// get the active user
		String username = principal.getName();
		model.addAttribute("username", username);
		
		UserEntity user = userDao.getUserByUsername(username);
		try {
			User spotifyUser = SpotifyRequests.getUser(user);
			System.out.println(spotifyUser.getDisplayName());
			System.out.println(spotifyUser.getEmail());
			System.out.println(spotifyUser.getId());
			System.out.println(spotifyUser.getUri());
		} catch (Exception e) {
			System.out.println("Something went wrong!" + e.getMessage());
		}
		
		return "home";
	}
	
	@RequestMapping(value = {"/spotify_user"}, method = RequestMethod.GET)
	public String getMe(
			@RequestParam("id") String userId,
			Model model, Principal principal) {
		
		// get the active user
		String username = principal.getName();
		model.addAttribute("username", username);
		
		UserEntity user = userDao.getUserByUsername(username);
		try {
			User spotifyUser = SpotifyRequests.getUser(user, userId);
			System.out.println(spotifyUser.getDisplayName());
			System.out.println(spotifyUser.getEmail());
			System.out.println(spotifyUser.getId());
			System.out.println(spotifyUser.getUri());
		} catch (Exception e) {
			System.out.println("Something went wrong!" + e.getMessage());
		}
		
		return "home";
	}
	
	@RequestMapping(value = {"/playlists"}, method = RequestMethod.GET)
	public String getPlaylists(Model model, Principal principal) {
		
		// get the active user
		String username = principal.getName();
		model.addAttribute("username", username);
		
		UserEntity user = userDao.getUserByUsername(username);
		
		Collection<SimplePlaylist> playlists = SpotifyRequests.getUserPlaylists(user);
		
		model.addAttribute("playlists", playlists);
		
		return "playlists";
	}
	
	@RequestMapping(value = {"/playlist"}, method = RequestMethod.GET)
	public String getPlaylist(
			@RequestParam("pid") String playlistId,
			@RequestParam("oid") String ownerId,
			Model model, Principal principal) {
		
		// get the active user
		String username = principal.getName();
		model.addAttribute("username", username);
		
		UserEntity user = userDao.getUserByUsername(username);
		
		Playlist playlist = SpotifyRequests.getPlaylist(user, ownerId, playlistId);
		
		// determine if a profile for this playlist exists
		
		model.addAttribute("name", playlist.getName());
		model.addAttribute("pid", playlistId);
		model.addAttribute("owner", playlist.getOwner());

		Collection<PlaylistTrack> ptracks = SpotifyRequests.getPlaylistTracks(user, playlist);
		model.addAttribute("tracks", ptracks);
		
		// create list of all tracks for storing
		List<Track> tracks = new ArrayList<Track>();
		for (PlaylistTrack ptrack: ptracks) {
			Track track = ptrack.getTrack();
			tracks.add(track);
		}
		
		// store unstored tracks and return MN tracks
		List<TrackEntity> mnTrackList = storeTracks(tracks);
		model.addAttribute("mntracks", mnTrackList);
		
		// store/update playlist with proper tracks
		PlaylistEntity playlistEntity = storePlaylist(playlist, ptracks);				
		System.out.println("\tPlaylistEntity id: " + playlistEntity.getId());
		System.out.println("\tPlaylistEntity name: " + playlistEntity.getName());
		System.out.println("\tPlaylistEntity source: " + playlistEntity.getSource());
		System.out.println("\tPlaylistEntity sourceId: " + playlistEntity.getSourceId());
		
		// Get aggregate scores for the list's songs (if multiple lists, remove duplicates?)
		// Get min, 20th, median, 80th, max of all values (not genres or year)
		// Get most common and count for genre and year
		
		return "view-playlist";
	}
	
	
	@RequestMapping(value = {"/artist_summary"}, method = RequestMethod.GET)
	public @ResponseBody String getArtist(
			@RequestParam("aid") String artistId,
			Model model, Principal principal) {
		
		// get the active user
		String username = principal.getName();
		model.addAttribute("username", username);
		
		UserEntity user = userDao.getUserByUsername(username);
		
		// TODO: get artist bio, top songs, profile, news, etc.
		Map<String,Object> artistSummary = EchonestRequests.getArtistSummary("spotify:artist:" + artistId);
		Map<String,String> artistSpotifySummary = SpotifyRequests.getArtistSummary(artistId);
		
		// currently delivers:
		// familiarity (double), hotttnesss (double), genres (list of obj w/ 'name' attr), 
		// discovery (double), facebook (str id), name (str)
		
		// TODO: better integrate with echonest and spotify top tracks
		// the Echonest songs are kind of lame... let's substitute for Spotify top songs
		
		// TODO: this also doesn't include spotifys ranking of popularity
		if (artistSummary.containsKey("songs")) {
			JSONArray topTracksAr = new JSONArray();
			
			List<Track> topTracks = SpotifyRequests.getArtistTopSongs(artistId);
			for (Track t : topTracks) {
				topTracksAr.put(t.getName());
			}
			// artistSummary.put("songs", topTracksAr.toString());
		}
		
		JSONObject artistSummaryObj = new JSONObject();
		
		String summary = "";
		for(Entry<String, Object> entry : artistSummary.entrySet()) {
			summary += entry.getKey() + ": " + entry.getValue() + "<br/>";
		}
		
		for(Entry<String, String> entry : artistSpotifySummary.entrySet()) {
			summary += entry.getKey() + ": " + entry.getValue() + "<br/>";
		}
		
		artistSummaryObj.put("artistInfo", summary);
		
		createJsonResponse(true, "", artistSummaryObj);
		
		return artistSummaryObj.toString();
	}
	
	@RequestMapping(value = {"/artist"}, method = RequestMethod.GET)
	public String getArtistPage(
			@RequestParam("aid") String artistId,
			Model model, Principal principal) {
		
		// get the active user
		String username = principal.getName();
		model.addAttribute("username", username);
		
		UserEntity user = userDao.getUserByUsername(username);
		
		// TODO: get artist bio, top songs, profile, news, etc.
		Map<String,Object> artistEchoSummary = EchonestRequests.getArtistSummary("spotify:artist:" + artistId);
		Map<String,String> artistSpotifySummary = SpotifyRequests.getArtistSummary(artistId);
	
		// currently delivers:
		// familiarity (double), hotttnesss (double), songs (list of strs), genres (list of obj w/ 'name' attr), 
		// discovery (double), facebook (str id), name (str)
		// track_ids (string), tracks (list map), lastfm_bio (string), image_url (string), followers (string), popularity (string)

		// TODO: this also doesn't include spotify's ranking of popularity
		if (artistEchoSummary.containsKey("songs")) {
			
			List<Track> topTracks = SpotifyRequests.getArtistTopSongs(artistId);
			// mnTrackList has all of the tracks with available Echo info
			List<TrackEntity> mnTrackList = storeTracks(topTracks);
			
			// "tracks" is used for spotify player, should be using "mnTrackList"
			model.addAttribute("tracks", topTracks);
		}
		
		// Move below info to ArtistScore(input=artistEchoSummary,artistSpotifySummary) function?
		
		// TODO: get top albums from spotify: By popularity and release date. Need to create SpotifyRequests.getArtistsAlbums
			// Not sure API can filter on 'market', or else we will get duplicates.
			// Need to do another search on each album to get popularity and release info	
		// TODO: add features to artistInfo programmatically
		// TODO: return albums, top tracks, genres, and images as objects
		// info: familiarity, hotttnesss, discovery, facebook, name, lastfm_bio, followers, popularity
		// genres: objects with 'name' attr (currently from echo, could be from spotify)
		// songs: objects with 'name' and 'id' attr
		// images: objects with 'size' and 'url' attr
		
		String discovery = artistEchoSummary.get("discovery").toString();
		String familiarity = artistEchoSummary.get("familiarity").toString();
		String hotttnesss = artistEchoSummary.get("hotttnesss").toString();
		//String twitter = artistEchoSummary.get("id:twitter").toString();
		//String facebook = artistEchoSummary.get("id:facebook").toString();
		String name = artistEchoSummary.get("name").toString();
		Object genres = artistEchoSummary.get("genres");
		String lastfm_bio = artistEchoSummary.get("lastfm_bio").toString();
		String followers = artistSpotifySummary.get("followers").toString();
		String popularity = artistSpotifySummary.get("popularity").toString();
		String image_url = artistSpotifySummary.get("image_url").toString();
		
		Map<String,Object> artistInfo = new HashMap<String,Object>();
		artistInfo.put("discovery", discovery);
		artistInfo.put("familiarity", familiarity);
		artistInfo.put("hotttnesss", hotttnesss);
		//artistInfo.put("twitter", twitter);
		//artistInfo.put("facebook", facebook);
		artistInfo.put("name", name);
		artistInfo.put("genres", genres);
		artistInfo.put("lastfm_bio", lastfm_bio);
        artistInfo.put("followers", followers);
        artistInfo.put("popularity", popularity);
        artistInfo.put("image_url", image_url);
        
		model.addAttribute("aid", artistId);
		model.addAttribute("info", artistInfo);
		
		return "view-artist";
	}
	
	@RequestMapping(value = {"/album"}, method = RequestMethod.GET)
	public String getAlbumPage(
			@RequestParam("aid") String albumId,
			Model model, Principal principal) {
		
		// get the active user
		String username = principal.getName();
		model.addAttribute("username", username);
		
		UserEntity user = userDao.getUserByUsername(username);
		
		Map<String,Object> albumSpotifySummary = SpotifyRequests.getAlbumSummary(albumId);
		
		// currently delivers:
		// familiarity (double), hotttnesss (double), songs (list of strs), genres (list of obj w/ 'name' attr), 
		// discovery (double), facebook (str id), name (str)
		// track_ids (string), tracks (list map), lastfm_bio (string), image_url (string), followers (string), popularity (string)

		if (albumSpotifySummary.containsKey("tracks")) {
			
			Object albumTracks = albumSpotifySummary.get("tracks");
			List<Track> tracks = (List<Track>)albumTracks;
			
			// TODO:
				// Above tracks should be SimpleTracks
				// Get each as a Track
				// Store all tracks with storeTracks
			
			JSONArray albumTracksAr = new JSONArray();
			for (Track t : tracks) {
				albumTracksAr.put(t.getId());
			}
			
			// mnTrackList has all of the tracks with available Echo info
			List<TrackEntity> mnTrackList = storeTracks(tracks);
			
			model.addAttribute("tracks", tracks);
			// trackSummaries not currently being used in view
			// model.addAttribute("trackSummaries", trackSummaries);
		}
		
		
		// Move below info to AlbumScore(input=albumSpotifySummary) function?
		
		// TODO: get top albums from spotify: By popularity and release date. Need to create SpotifyRequests.getArtistsAlbums
					// Not sure API can filter on 'market', or else we will get duplicates.
					// Need to do another search on each album to get popularity and release info	
		// TODO: go through features programmatically
		// TODO: return albums, top tracks, genres, and images as objects
		// info: familiarity, hotttnesss, discovery, facebook, name, lastfm_bio, followers, popularity
		// genres: objects with 'name' attr
		// songs: objects with 'name' and 'id' attr
		// images: objects with 'size' and 'url' attr
		
		String name = albumSpotifySummary.get("name").toString();
		String popularity = albumSpotifySummary.get("popularity").toString();
		String image_url = albumSpotifySummary.get("image_url").toString();
		Object genres = albumSpotifySummary.get("genres");
		
		Map<String,Object> albumInfo = new HashMap<String,Object>();
		albumInfo.put("name", name);
		albumInfo.put("popularity", popularity);
		albumInfo.put("image_url", image_url);
		albumInfo.put("genres", genres);
        
		model.addAttribute("aid", albumId);
		model.addAttribute("info", albumInfo);
		
		return "view-album";
	}
	
	@RequestMapping(value = {"/track_summary"}, method = RequestMethod.GET)
	public @ResponseBody String getTrackSummary(
			@RequestParam("tid") String trackId,
			Model model, Principal principal) {
		
		// get the active user
		//String username = principal.getName();
		//UserEntity user = userDao.getUserByUsername(username);
		
		Map<String,String> audioSummary = EchonestRequests.getAudioSummary("spotify:track:" + trackId);
		JSONObject audioSummaryObj = new JSONObject();
		
		String summary = "";
		for(Entry<String, String> entry : audioSummary.entrySet()) {
			//audioSummaryObj.put(entry.getKey(), entry.getValue());
			summary += entry.getKey() + ": " + entry.getValue() + "<br/>";
		}
		
		audioSummaryObj.put("trackInfo", summary);
		
		createJsonResponse(true, "", audioSummaryObj);
		
		return audioSummaryObj.toString();
	}
	
	@RequestMapping(value = {"/profiles"}, method = RequestMethod.GET)
	public String getProfiles(Model model, Principal principal) {
		
		// get the active user
		String username = principal.getName();
		model.addAttribute("username", username);
		
		UserEntity user = userDao.getUserByUsername(username);
		
		Collection<ProfileEntity> profiles = profileDao.getProfilesByUser(user);
		
		// get the profile names
		Map<String,String> profileNames = new HashMap<String,String>();
		for (ProfileEntity profile : profiles) {
			// make echonest request to get profile name...
		}
		
		// gather the playlist names for display
		Map<String,String> playlistNames = new HashMap<String,String>();
		for (ProfileEntity profile : profiles) {
			String pid = profile.getPlaylistId();
			String pOwnerid = profile.getPlaylistOwnerId();
			if (pid != null && !pid.isEmpty() &&
					pOwnerid != null && !pOwnerid.isEmpty()){
				Playlist playlist = SpotifyRequests.getPlaylist(user, pOwnerid, pid);
				if (playlist != null) {
					String playlistName = playlist.getName();
					playlistNames.put(pid, playlistName);
				}
			}
		}
		
		model.addAttribute("profiles", profiles);
		model.addAttribute("playlistNames", playlistNames);
		
		return "profiles";
	}
	
	@RequestMapping(value = {"/listen"}, method = RequestMethod.GET)
	public String getListen(
			@RequestParam("prid") String profileId,
			Model model, Principal principal) {
		
		// get the active user
		String username = principal.getName();
		model.addAttribute("username", username);
		
		UserEntity user = userDao.getUserByUsername(username);
		
		ProfileEntity profile = profileDao.getProfileByEchoId(profileId);
		GeneralCatalog echoCatalog = EchonestRequests.getGeneralProfile(profile);
		
		Playlist playlist = SpotifyRequests.getPlaylist(user, 
				profile.getPlaylistOwnerId(),
				profile.getPlaylistId());
		
		model.addAttribute("profile", profile);
		model.addAttribute("playlist", playlist);
		model.addAttribute("owner", user);
		model.addAttribute("name", echoCatalog.getName());

		return "listen";
	}
	
	@RequestMapping(value = {"/getMusic"}, method = RequestMethod.GET)
	public @ResponseBody String getMusic(
			@RequestParam(value="prid", required=true) String profileId,
			@RequestParam(value="variety", required=false) Float variety,
			@RequestParam(value="distribution", required=false) String distributionStr,
			@RequestParam(value="adventurousness", required=false) Float adventurousness,
			@RequestParam(value="lesserKnown", required=false, defaultValue="false") boolean lesserKnown,
			@RequestParam(value="size", required=false, defaultValue="20") Integer size,
			Model model, Principal principal) {
	
		boolean isFocused = (distributionStr != null) && distributionStr.equals("focused");
		
		// get the active user
		String username = principal.getName();
		model.addAttribute("username", username);
		
		UserEntity user = userDao.getUserByUsername(username);
		
		ProfileEntity profile = profileDao.getProfileByEchoId(profileId);

		Preference preference = new Preference();
		preference.setVariety(variety);
		preference.setAdventurousness(adventurousness);
		if (isFocused) preference.setDistribution(Distribution.FOCUSED);
		else preference.setDistribution(Distribution.WANDERING);
		preference.setLesserKnown(lesserKnown);
		
		Playlist playlist = SpotifyRequests.getPlaylist(user, 
				profile.getPlaylistOwnerId(),
				profile.getPlaylistId());
		
		PlaylistFilter filter = new PlaylistFilter(user, profile, playlist, preference);
		List<String> songs = EchonestRequests.getNewSpotifyPlaylist(filter, size);
		
		JSONObject resp = new JSONObject();
		resp.put("songs", new JSONArray(songs));
		
		createJsonResponse(true,"", resp);
		return resp.toString();
	}
	
	@RequestMapping(value = {"/profile"}, method = RequestMethod.GET)
	public String getProfile(
			@RequestParam("prid") String profileId,
			Model model, Principal principal) {
		
		// get the active user
		String username = principal.getName();
		model.addAttribute("username", username);
		
		UserEntity user = userDao.getUserByUsername(username);
		
		ProfileEntity profile = profileDao.getProfileByEchoId(profileId);
		GeneralCatalog echoCatalog = EchonestRequests.getGeneralProfile(profile);
		System.out.println("Dumping catalog key values for catalog '" + echoCatalog.getName() + "':");
		System.out.println("Total items:" + echoCatalog.getTotal());
		List<ArtistCatalogItem> artists = new ArrayList<ArtistCatalogItem>();
		List<SongCatalogItem> songs = new ArrayList<SongCatalogItem>();
		try {
			for (CatalogItem item : echoCatalog.read()) {
				if (item instanceof ArtistCatalogItem) {
					ArtistCatalogItem artist = (ArtistCatalogItem) item;
					artists.add(artist);
				}
				else if (item instanceof SongCatalogItem) {
					SongCatalogItem song = (SongCatalogItem) item;
					songs.add(song);
				}
			}
		} catch (EchoNestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		model.addAttribute("profile", profile);
		model.addAttribute("echoCatalog", echoCatalog);
		model.addAttribute("artists", artists);
		model.addAttribute("songs", songs);

		return "profile";
	}
	
	@RequestMapping(value = {"/create_profile"}, method = RequestMethod.GET)
	public String getCreateProfile(
			@RequestParam("pid") String playlistId,
			@RequestParam("oid") String ownerId,
			Model model, Principal principal) {
		
		// get the active user
		String username = principal.getName();
		model.addAttribute("username", username);
		
		UserEntity user = userDao.getUserByUsername(username);
		
		Playlist playlist = SpotifyRequests.getPlaylist(user, ownerId, playlistId);
		
		model.addAttribute("name", playlist.getName());
		model.addAttribute("pid", playlistId);
		model.addAttribute("owner", playlist.getOwner());
		
		Collection<PlaylistTrack> tracks = SpotifyRequests.getPlaylistTracks(user, playlist);
		
		model.addAttribute("tracks", tracks);
		
		return "create-profile";
	}
	
	@RequestMapping(value = {"/create_profile"}, method = RequestMethod.POST)
	public @ResponseBody String createProfile(
			@RequestParam("tids") String trackIdsStr,
			@RequestParam("name") String profileName,
			@RequestParam("pid") String playlistId,
			// list of tracks or artists to add to the profile
			Model model, Principal principal) {
		
		// get the active user
		String username = principal.getName();
		UserEntity user = userDao.getUserByUsername(username);
		
		List<String> trackIds = new ArrayList<String>();     
		JSONArray trackIdsObj = new JSONArray(trackIdsStr);
		if (trackIds != null) { 
			for (int i=0;i<trackIdsObj.length();i++){ 
				trackIds.add("spotify:track:" + trackIdsObj.get(i).toString());
			} 
		} 
		
		ProfileType profileType = ProfileType.GENERAL;
		
		// call echonest request to create profile
		String profileId = EchonestRequests.createProfile(username, profileName, profileType);
		// add the tracks
		boolean success = EchonestRequests.addTrackToProfile(profileId, trackIds);
		
		if (success) {
			
			// use id returned from the request to create profile entity
			ProfileEntity profile = new ProfileEntity();
			profile.setOwner(user.getId());
			profile.setProfileId(profileId);
			profile.setPlaylistId(playlistId);
			profile.setPlaylistOwnerId(user.getSpotifyUsername());
			// TODO: this seems wrong... should toString be uppercase or lowercase?!
			profile.setType(profileType.toString().toUpperCase());
			// save the profile entity via profile dao
			profileDao.addProfile(profile);
			
			return createJsonResponse(true,"").toString();
			
		} else {
			return createJsonResponse(false,"Failed to add tracks to new profile.").toString();
		}
	}
	
	@RequestMapping(value = {"/save_playlist"}, method = RequestMethod.POST)
	public @ResponseBody String savePlaylist(
			@RequestParam("tids") String trackIdsStr,
			@RequestParam("name") String playlistName,
			// list of tracks or artists to add to the profile
			Model model, Principal principal) {
		
		// get the active user
		String username = principal.getName();
		UserEntity user = userDao.getUserByUsername(username);
		
		List<String> trackIds = new ArrayList<String>();     
		JSONArray trackIdsObj = new JSONArray(trackIdsStr);
		if (trackIds != null) { 
			for (int i=0;i<trackIdsObj.length();i++){ 
				trackIds.add(trackIdsObj.get(i).toString());
			} 
		} 
		
		boolean success = SpotifyRequests.createPlaylistWithTracks(user, playlistName, trackIds);
		
		return createJsonResponse(success,"").toString();
	}
	
	@RequestMapping(value = {"/reddit"}, method = RequestMethod.GET)
	public String getReddit(
			Model model, Principal principal) {
		
		// get the active user
		String username = principal.getName();
		
		UserEntity user = userDao.getUserByUsername(username);
		model.addAttribute("owner", user);
		
		List<RedditPlaylistEntity> redditPlaylists = redditPlaylistDao.getRedditPlaylistsByUser(user);
		
		if (redditPlaylists != null && !redditPlaylists.isEmpty()) {
			
			model.addAttribute("redditPlaylists", redditPlaylists);
			
			Map<Integer,String> redditPlaylistNames = new HashMap<Integer,String>();
			for (RedditPlaylistEntity redditPlaylist : redditPlaylists) {
				
				Playlist playlist = SpotifyRequests.getPlaylist(user, user.getSpotifyUsername(), redditPlaylist.getPlaylistId());
				if (playlist != null) {
					redditPlaylistNames.put(redditPlaylist.getId(), playlist.getName());
				}
			}
			
			model.addAttribute("redditPlaylistNames", redditPlaylistNames);

		} else {
			
			redditPlaylists = new ArrayList<RedditPlaylistEntity>();
			model.addAttribute("redditPlaylists", redditPlaylists);
		}	
		
		return "reddit";
	}
	
	@RequestMapping(value = "/create_reddit_playlist", method = RequestMethod.GET)
	public String postCreateRedditPlaylist(Model model, Principal principal) {
		// get the active user
		String username = principal.getName();
		
		UserEntity user = userDao.getUserByUsername(username);
		model.addAttribute("owner", user);
		
		return "create-reddit-playlist";
	}
	
	@RequestMapping(value = "/create_reddit_playlist", method = RequestMethod.POST)
	public String postCreateRedditPlaylist(
			@RequestParam("playlistName") String playlistName,
			@RequestParam("subreddit") String subreddit,
			@RequestParam("period") String period,
			@RequestParam("limit") Integer limit,
			Model model, Principal principal) {
		
		String username = principal.getName();
		
		UserEntity user = userDao.getUserByUsername(username);
		model.addAttribute("owner", user);
		
		// first identify simple issues with the params
		// TODO: should probably handle these on the client side in the future
		if (limit == null || limit <= 0) {
			model.addAttribute("playlistNameError", "Limit must be a number greater than 0.");
			return "create-reddit-playlist";
		}
		
		if (subreddit == null || subreddit.isEmpty()) {
			model.addAttribute("subredditError", "A music subreddit must be entered.");
			return "create-reddit-playlist";
		}
		
		if (period == null 
				|| period.isEmpty() 
				|| PostPeriod.fromString(period) == null) {
			model.addAttribute("periodError", "A valid post period must be entered.");
			return "create-reddit-playlist";
		}
		
		PostPeriod periodObj = PostPeriod.fromString(period);
		
		// try creating the spotify playlist
		Playlist playlist = SpotifyRequests.createPlaylist(user, playlistName);
		
		if (playlist == null) {
			model.addAttribute("playlistNameError","Could not create playlist with name '" + playlistName +"'.");
			return "create-reddit-playlist";
		}
		
		RedditPlaylistEntity redditPlaylist = new RedditPlaylistEntity();
		redditPlaylist.setOwner(user.getId());
		redditPlaylist.setPlaylistId(playlist.getId());
		redditPlaylist.setSubReddit(subreddit);
		redditPlaylist.setPeriod(periodObj.getRequestVal());
		redditPlaylist.setLimit(limit);
		redditPlaylist.setLastRefresh(System.currentTimeMillis() + "");
				
		redditPlaylistDao.addRedditPlaylist(redditPlaylist);
		
		List<SimpleSong> songsNotFound = RedditRequests.updateSpotifyPlaylist(user, redditPlaylist);
		
		model.addAttribute("playlist", redditPlaylist);
		model.addAttribute("success", true);
		model.addAttribute("songsNotFound", songsNotFound);
		
		return "create-reddit-playlist";
	}
	
	@RequestMapping(value = {"/update_reddit_playlist"}, method = RequestMethod.GET)
	public String getReddit(
			@RequestParam("rpid") Integer redditPlaylistId,
			Model model, Principal principal) {
		
		// get the active user
		String username = principal.getName();
		
		UserEntity user = userDao.getUserByUsername(username);
		model.addAttribute("owner", user);
		
		if (redditPlaylistId == null) {
			return "redirect:/reddit";
		}
		
		RedditPlaylistEntity redditPlaylist = redditPlaylistDao.getRedditPlaylistById(redditPlaylistId);
		
		if (redditPlaylist != null) {
			
			if (redditPlaylist.getOwner() != user.getId()) {
				// playlist isn't owned by the current user
				return "redirect:/reddit";
			}

			model.addAttribute("playlist", redditPlaylist);
			Playlist playlist = SpotifyRequests.getPlaylist(user, user.getSpotifyUsername(), redditPlaylist.getPlaylistId());
			if (playlist != null) {
				model.addAttribute("playlistName", playlist.getName());
			}
			
			return "update-reddit-playlist";

		} else {
			
			return "redirect:/reddit";
		}
	}
	
	@RequestMapping(value = "/update_reddit_playlist", method = RequestMethod.POST)
	public String postUpdatePlaylist(
			@RequestParam("pid") Integer playlistId,
			@RequestParam("subreddit") String subreddit,
			@RequestParam("period") String period,
			@RequestParam("limit") Integer limit,
			Model model, Principal principal) {
		
		String username = principal.getName();
			
		UserEntity user = userDao.getUserByUsername(username);	
		model.addAttribute("owner", user);
		
		if (playlistId == null) {
			return "redirect:/reddit";
		}
		
		RedditPlaylistEntity redditPlaylist = redditPlaylistDao.getRedditPlaylistById(playlistId);
		
		if (redditPlaylist != null) {
			
			if (redditPlaylist.getOwner() != user.getId()) {
				// playlist isn't owned by the current user
				return "redirect:/reddit";
			}
			
			model.addAttribute("playlist", redditPlaylist);
			// TODO: this should eventually be capable of modification by the user
			Playlist playlist = SpotifyRequests.getPlaylist(user, user.getSpotifyUsername(), redditPlaylist.getPlaylistId());
			if (playlist != null) {
				model.addAttribute("playlistName", playlist.getName());
			}
			
			// first identify simple issues with the params
			// TODO: should probably handle these on the client side in the future
			if (limit == null || limit <= 0) {
				model.addAttribute("playlistNameError", "Limit must be a number greater than 0.");
				return "update-reddit-playlist";
			}
			
			if (subreddit == null || subreddit.isEmpty()) {
				model.addAttribute("subredditError", "A music subreddit must be entered.");
				return "update-reddit-playlist";
			}
			
			if (period == null 
					|| period.isEmpty() 
					|| PostPeriod.fromString(period) == null) {
				model.addAttribute("periodError", "A valid post period must be entered.");
				return "update-reddit-playlist";
			}
			
			PostPeriod periodObj = PostPeriod.fromString(period);
			
			boolean updated =  false;
			if (!redditPlaylist.getSubReddit().equals(subreddit)) {
				updated = true;
				redditPlaylist.setSubReddit(subreddit);
			}
			if (!redditPlaylist.getPeriod().equals(periodObj.getRequestVal())) {
				updated = true;
				redditPlaylist.setPeriod(periodObj.getRequestVal());
			}
			if (!redditPlaylist.getLimit().equals(limit)) {
				updated = true;
				redditPlaylist.setLimit(limit);
			}
			
			if (updated) {
				redditPlaylist.setLastRefresh(System.currentTimeMillis() + "");
				redditPlaylistDao.saveRedditPlaylist(redditPlaylist);
			}
			
			List<SimpleSong> songsNotFound = RedditRequests.updateSpotifyPlaylist(user, redditPlaylist);
			
			model.addAttribute("success", true);
			model.addAttribute("songsNotFound", songsNotFound);
			
			return "update-reddit-playlist";
			
		} else {
			
			// TODO: need to also have errors...
			//model.addAttribute("error", "No playlist was found to update.");
			
			return "redirect:/reddit";
		}
	}
	
	@RequestMapping(value = "/backup_spotify", method = RequestMethod.POST)
	public void postBackupSpotify(Model model, Principal principal,
			@RequestParam(value="includeFollowed", required=false, defaultValue="false") boolean includeFollowed,
			HttpServletResponse resp) {
		
		String username = principal.getName();
		
		UserEntity user = userDao.getUserByUsername(username);	
		model.addAttribute("owner", user);
		
		File backup = SpotifyManage.backupPlaylists(user, includeFollowed);
		
		resp.setContentType("application/zip");
		resp.setHeader("Content-Disposition",
				"attachment;filename=SpotifyBackup.zip");
		// While this is not required, it should probably be set
		//resp.setContentLength((int) backup.length());
		
		fileDownloadResponse(backup, resp);
	}
	
	@RequestMapping(value = "/backup_spotify", method = RequestMethod.GET)
	public String getBackupSpotify(Model model, Principal principal) {
		
		String username = principal.getName();
		
		UserEntity user = userDao.getUserByUsername(username);	
		model.addAttribute("owner", user);
		
		return "backup-spotify";
	}
	
	/**
	 * Attach a file to a response for a user to download
	 * @param file
	 * @param resp
	 */
	// TODO: need better exception handling
	private static void fileDownloadResponse(File file, HttpServletResponse resp) {
		
		try {
			
			FileInputStream backupIn = new FileInputStream(file);
			ServletOutputStream out = resp.getOutputStream();

			// copy content to output stream
			byte[] buffer = new byte[4096];
			
			int len;
			while((len = backupIn.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}

			backupIn.close();
			out.flush();
			out.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Creates a JSONObject response with success and message attributes
	 * @param success response success boolean
	 * @param message response message string
	 * @return JSONObject containing response attributes
	 */
	private static JSONObject createJsonResponse(boolean success, String message) {
		JSONObject response = new JSONObject();
		createJsonResponse(success, message, response);
		return response;
	}
	
	/**
	 * Modifies the response param to include success and message attributes
	 * @param success response success boolean
	 * @param message response message string
	 * @param response JSONObject modified to include response attributes
	 */
	private static void createJsonResponse(boolean success, String message, JSONObject response) {
		response.put("success", success);
		response.put("message", message);
	}
	
	
	private List<TrackEntity> storeTracks(List<Track> tracks) {
		// TODO:
		// Decide when to update for hotttnesss, popularity based on last refresh
		List<TrackEntity> mnTrackList = new ArrayList<TrackEntity>();
		// List of Spotify tracks not in MN, to be stored 
		List<Track> tracksToStore = new ArrayList<Track>();
		
		for (Track track: tracks) {
			String spotifyId = track.getId();
			try {
				TrackEntity mnTrack = trackDao.getTrackBySpotifyId(spotifyId);
				if (mnTrack.getEchoId() != null) {
					System.out.println("\tFound track: " + mnTrack.getName() + " - " + mnTrack.getSpotifyId());
				} else {
					System.out.println("\tFound track: " + mnTrack.getName() + " - " + mnTrack.getSpotifyId() + " - No Audio Data");
				}
				mnTrackList.add(mnTrack);
			} catch (Exception e) {
				System.out.println("\tcould not find track in DB!");
				tracksToStore.add(track);
			}

		}
		Integer echoCounter = 0;
		for (Track track: tracksToStore) {
			String spotifyUri = track.getUri(); 
			
			if (echoCounter > 19 ) {
				try {
				    Thread.sleep(60000);				//1000 milliseconds is one second.
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
				echoCounter = 0;
			}
			
			// get Echonest features and store track data (Echo, spotify, artist, album)
			Map<String,String> echoFeatures = EchonestRequests.getAudioSummary(spotifyUri);
			echoCounter++;
			
			// If null when searching with SpotifyURI, try to search with artist name and track title
			if (echoFeatures == null) {
				String artistName = track.getArtists().get(0).getName();
				String trackTitle = track.getName();
				// May need two calls, search and audio summary
				if (echoCounter > 18 ) {
					try {
					    Thread.sleep(60000);				//1000 milliseconds is one second.
					} catch(InterruptedException ex) {
					    Thread.currentThread().interrupt();
					}
					echoCounter = 0;
				}
				echoFeatures = EchonestRequests.getAudioSummarySearch(artistName, trackTitle);
				if (echoFeatures != null) {
					echoCounter++;
					echoCounter++;
				} else {
					echoCounter++;
				}
			}
			
			System.out.println("\tEchoCounter " + echoCounter);
			
			TrackEntity mnTrack = new TrackEntity();
			// set all track info
			mnTrack.setName(track.getName());
			mnTrack.setSpotifyId(track.getId());
			mnTrack.setLastRefresh(System.currentTimeMillis() + "");
			if (echoFeatures != null) {
				System.out.println("\tTrack EchoId " + echoFeatures.get("id"));
				mnTrack.setEchoId(echoFeatures.get("id"));
				mnTrack.setInstrumentalness(Float.parseFloat(echoFeatures.get("instrumentalness")));
				try {
					mnTrack.setSpeechiness(Float.parseFloat(echoFeatures.get("speechiness")));
				} catch (Exception j) {
					System.out.println("\tNo speechiness for track.");
				}
				
				mnTrack.setTempo(Float.parseFloat(echoFeatures.get("tempo")));
				
				// Artists
				List<SimpleArtist> trackArtists = track.getArtists();
				Set<ArtistEntity> mnArtists =  new HashSet<ArtistEntity>();
				
				for (SimpleArtist artist: trackArtists) {
					Map<String,Object> artistMap = storeArtist(artist, echoCounter);
					ArtistEntity mnArtist = (ArtistEntity) artistMap.get("artist");
					echoCounter = (Integer) artistMap.get("echoCounter");
					mnTrack.getArtists().add(mnArtist);
					System.out.println("\ttrack Artists " + mnArtists.toString());
				}
		        
			}
	        
	        // Album
			// get Album => check if stored => storeAlbum
			// SimpleAlbum trackAlbum = track.getAlbum();
			// Store Album - return Album object
				// Check if album stored in MN
				// If yes:
					// return album object
				// If no:
					// Store album
					// return album object
			// Set track album to returned album object
			System.out.println("\ttrack before storing " + mnTrack.getArtists().toString());
			
			trackDao.addTrack(mnTrack);
	
			System.out.println("\ttrack added to DB!!");
			
			mnTrackList.add(mnTrack);
		}
		return mnTrackList;
		
	}
	private Map<String,Object> storeArtist(SimpleArtist artist, Integer echoCounter) {
		Map<String,Object> artistMap = new HashMap<String,Object>();
		String spotifyId = artist.getId();
		String spotifyUri = artist.getUri();
		try {
			ArtistEntity mnArtist = artistDao.getArtistBySpotifyId(spotifyId);
			if (mnArtist.getEchoId() != null) {
				System.out.println("\tFound artist: " + mnArtist.getName() + " - " + mnArtist.getSpotifyId());
			} else {
				System.out.println("\tFound artist: " + mnArtist.getName() + " - " + mnArtist.getSpotifyId() + " - No Audio Data");
			}
			artistMap.put("artist", mnArtist);
			artistMap.put("echoCounter", echoCounter);
			return artistMap;
		} catch (Exception e) {
			System.out.println("\tcould not find Artist in DB!");
			
			if (echoCounter > 19 ) {
				try {
				    Thread.sleep(60000);                 //1000 milliseconds is one second.
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
				echoCounter = 0;
			}
			
			Map<String,Object> echoFeatures = EchonestRequests.getArtistSummary(spotifyUri);
			echoCounter++;
			
			ArtistEntity mnArtist = new ArtistEntity();
			
			// set all track info starting with Spotify info
			mnArtist.setName(artist.getName());
			mnArtist.setSpotifyId(spotifyId);
			mnArtist.setLastRefresh(System.currentTimeMillis() + "");
			// No getPopularity function for Simple Artist
			// mnartist.setPopularity(artist.getPopularity());
			
			// echoFeatures stores Objects rather than Strings, which is forcing toString() on everything
			// need to add discoveryRank, familiarityRank and other features to Echo request
			if (echoFeatures != null) {
				System.out.println("\tEchoId" + echoFeatures.get("id"));
				mnArtist.setEchoId(echoFeatures.get("id").toString());
				try {
					mnArtist.setLastfmBio(echoFeatures.get("lastfm_bio").toString());
				} catch (Exception j) {
					System.out.println("\tNo lastfm bio.");
				}
				mnArtist.setDiscovery(Float.parseFloat(echoFeatures.get("discovery").toString()));
				mnArtist.setFamiliarity(Float.parseFloat(echoFeatures.get("familiarity").toString()));
				mnArtist.setHotttnesss(Float.parseFloat(echoFeatures.get("hotttnesss").toString()));
			}
			artistDao.addArtist(mnArtist);
			
			System.out.println("\tArtist added to DB!!");
			
			artistMap.put("artist", mnArtist);
			artistMap.put("echoCounter", echoCounter);
			return artistMap;
		}
		
	}
	
	private PlaylistEntity storePlaylist(Playlist playlist, Collection<PlaylistTrack> ptracks) {
		// TODO: Do not delete track entry, change to inactive
		// Check if playlist exists
		PlaylistEntity mnPlaylist = null;
		String playlistSpotifyId = playlist.getId();
		try {
			// If yes, continue
			mnPlaylist = playlistDao.getPlaylistBySourceId(playlistSpotifyId);
		} finally {
			if (mnPlaylist == null){
				// If not, create playlist
				mnPlaylist = new PlaylistEntity();
				mnPlaylist.setName(playlist.getName());
				mnPlaylist.setSource("spotify");
				mnPlaylist.setSourceId(playlistSpotifyId);
				playlistDao.addPlaylist(mnPlaylist);
			}
			
		}
		
		Collection<PlaylistEntryEntity> playlistEntries = new ArrayList<PlaylistEntryEntity>();
		for (PlaylistTrack ptrack: ptracks) {
			Track track = ptrack.getTrack();
			String trackSpotifyId = track.getId();
			TrackEntity mnTrack = trackDao.getTrackBySpotifyId(trackSpotifyId);
			String addedById = ptrack.getAddedBy().getId();
			Date addedAt = ptrack.getAddedAt();
			
			// Check if playlistEntry exists, add to list
			
			try {
				// If yes, add entry id to list and continue
				PlaylistEntryEntity mnPlaylistEntry = playlistEntryDao.getPlaylistEntryByInfo(mnTrack, addedById, addedAt);
				playlistEntries.add(mnPlaylistEntry);
			} catch (Exception e) {
				// If no, create entry, add entry_id to list
				PlaylistEntryEntity mnPlaylistEntry = new PlaylistEntryEntity();
				mnPlaylistEntry.setPlaylist(mnPlaylist);
				mnPlaylistEntry.setTrack(mnTrack);
				mnPlaylistEntry.setAddedAt(addedAt);
				mnPlaylistEntry.setAddedById(addedById);
				System.out.println("\tAbout to add playlistEntry");
				playlistEntryDao.addPlaylistEntry(mnPlaylistEntry);
				
				playlistEntries.add(mnPlaylistEntry);
			}
			
			
		}

		// For all entries with playlist_id == input_playlist_id
		Collection<PlaylistEntryEntity> allPlaylistEntries = playlistEntryDao.getPlaylistEntriesByPlaylist(mnPlaylist);
		// Remove 
		allPlaylistEntries.removeAll(playlistEntries);
			// If not in list, delete list_entry
		System.out.println("\tSongs no longer in list" + allPlaylistEntries.toString());
		
		return mnPlaylist;
		
	}
	
}
