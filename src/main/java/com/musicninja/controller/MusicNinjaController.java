package com.musicninja.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.musicninja.model.ProfileEntity;
import com.musicninja.model.RedditPlaylistEntity;
import com.musicninja.model.UserEntity;
import com.musicninja.persistence.IProfileDao;
import com.musicninja.persistence.IRedditPlaylistDao;
import com.musicninja.persistence.IUserDao;
import com.musicninja.reddit.PostPeriod;
import com.musicninja.reddit.RedditRequests;
import com.musicninja.reddit.SimpleSong;
import com.musicninja.spotify.SpotifyManage;
import com.musicninja.spotify.SpotifyRequests;
import com.musicninja.suggest.PlaylistFilter;
import com.musicninja.suggest.Preference;
import com.musicninja.suggest.Preference.Distribution;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.SimplePlaylist;
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
		
		Collection<PlaylistTrack> tracks = SpotifyRequests.getPlaylistTracks(user, playlist);
		
		model.addAttribute("tracks", tracks);
		
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
		Map<String,String> artistSummary = EchonestRequests.getArtistSummary("spotify:artist:" + artistId);
		
		// currently delivers:
		// familiarity (double), hotttnesss (double), songs (list of strs), genres (list of obj w/ 'name' attr), 
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
			artistSummary.put("songs", topTracksAr.toString());
		}
		
		JSONObject artistSummaryObj = new JSONObject();
		
		String summary = "";
		for(Entry<String, String> entry : artistSummary.entrySet()) {
			//audioSummaryObj.put(entry.getKey(), entry.getValue());
			summary += entry.getKey() + ": " + entry.getValue() + "<br/>";
		}
		
		artistSummaryObj.put("artistInfo", summary);
		
		createJsonResponse(true, "", artistSummaryObj);
		
		return artistSummaryObj.toString();
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
		
		try {
			
			FileInputStream backupIn = new FileInputStream(backup);
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
	
	@RequestMapping(value = "/backup_spotify", method = RequestMethod.GET)
	public String getBackupSpotify(Model model, Principal principal) {
		
		String username = principal.getName();
		
		UserEntity user = userDao.getUserByUsername(username);	
		model.addAttribute("owner", user);
		
		//SpotifyManage.backupPlaylists(user, false);
		return "backup-spotify";
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
}
