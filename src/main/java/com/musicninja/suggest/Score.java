package com.musicninja.suggest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.musicninja.controller.AuthController;
import com.musicninja.echonest.EchonestRequests;
import com.musicninja.model.ProfileEntity;
import com.musicninja.model.UserEntity;
import com.musicninja.model.TrackEntity;
import com.musicninja.persistence.ITrackDao;
import com.musicninja.spotify.SpotifyRequests;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.Track;
import com.wrapper.spotify.models.User;

public class Score {
	
	@Autowired
	public static ITrackDao trackDao;
	
	public static Map<String,Object> scoreList(Collection<PlaylistTrack> tracks) {
		
		// For each track:
		// Get info from DB
		// Add to list
		// Return List
		
		List<String> uriList = storeTracks(tracks);
		
		System.out.println("\tUri List" + uriList);
		
		Map<String,Object> listInfo =  new HashMap<String,Object>();
		for (PlaylistTrack ptrack: tracks) {
			Track track = ptrack.getTrack();
			String trackId = track.getId();
			String trackUri = track.getUri();
			Map<String,String> echoFeatures = EchonestRequests.getAudioSummary(trackUri);
			listInfo.put(trackId, echoFeatures);
		}
		
		return listInfo;
		
	}
	
	public static List<String> storeTracks(Collection<PlaylistTrack> ptracks) {
		
		// Store tracks
			// For each track:
			// If track exists in DB - Continue
			// Else - get info from Echonest, store in DB 
			// ToDo:
				// Check for artist and update as well
				// Decide when to update for hotttnesss, popularity based on last updated
		List<String> uriList = new ArrayList<String>();
		for (PlaylistTrack ptrack: ptracks) {
			
			Track track = ptrack.getTrack();
			String spotifyId = track.getId();
			String spotifyUri = track.getUri();
			
			try {
				TrackEntity mntrack = trackDao.getTrackBySpotifyId(spotifyId);
				System.out.println("\tfound track: " + mntrack.getName() + " - " + mntrack.getSpotifyId());
				uriList.add(spotifyUri);
			} catch (Exception e) {
				System.out.println("\tcould not find track in DB!");
				Map<String,String> echoFeatures = EchonestRequests.getAudioSummary(spotifyUri);
				
				TrackEntity mntrack = new TrackEntity();
				
				mntrack.setName(track.getName());
				mntrack.setSpotifyId(track.getId());
				mntrack.setInstrumentalness(Float.parseFloat(echoFeatures.get("instrumentalness")));
				mntrack.setSpeechiness(Float.parseFloat(echoFeatures.get("speechiness")));
				mntrack.setTempo(Float.parseFloat(echoFeatures.get("tempo")));
				mntrack.setLastRefresh(System.currentTimeMillis() + "");
				// save the track entity via track dao
				
				System.out.println(mntrack.getName());
				System.out.println(mntrack.getSpotifyId());
				System.out.println(mntrack.getLastRefresh());
				
				trackDao.addTrack(mntrack);
				
				
				System.out.println("\ttrack added to DB!!");
				
				uriList.add(spotifyUri);
			}

		}
		
		return uriList;
		
	}

}
