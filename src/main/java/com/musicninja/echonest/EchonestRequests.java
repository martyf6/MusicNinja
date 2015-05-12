package com.musicninja.echonest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.GeneralCatalog;
import com.echonest.api.v4.Playlist;
import com.echonest.api.v4.PlaylistParams;
import com.echonest.api.v4.PlaylistParams.PlaylistType;
import com.echonest.api.v4.Song;
import com.musicninja.model.ProfileEntity;
import com.musicninja.suggest.PlaylistFilter;
import com.musicninja.suggest.PlaylistFilter.FilterResponse;


public class EchonestRequests implements IEchonestRequests {
	
	private static final String API_KEY = "";
	
	private static EchoNestAPI EN = new EchoNestAPI(API_KEY);
	
	private static CloseableHttpClient httpClient = HttpClientBuilder.create().build();
	
	public enum ProfileType {
		
		ARTIST, SONG, GENERAL;
		
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	public static void getGagaHottness() throws IOException {
		
		try {
			URIBuilder builder = new URIBuilder("http://developer.echonest.com/api/v4/artist/hotttnesss")
				.addParameter("api_key", API_KEY)
				.addParameter("name", "lady+gaga")
				.addParameter("format", "json");
            HttpGet request = new HttpGet(builder.build());
            request.addHeader("content-type", "application/json");
            HttpResponse result = httpClient.execute(request);
            
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            try {
            	
                JSONObject obj = new JSONObject(json);
                
                System.out.println(obj.get("response"));

            } catch (JSONException e) {
                // TODO: handle exception
            }

        } catch (URISyntaxException e) {
        	// TODO: handle exception
        }
    }
	
	public static String printTrackInfo(String artist, String title) throws IOException {
		
		System.out.println("Getting echonest track info for " + artist + " - " + title);
		
		try {
			
			// first find the artist
			
			URIBuilder builder = new URIBuilder("http://developer.echonest.com/api/v4/song/search")
				.addParameter("api_key", API_KEY)
				.addParameter("artist", artist)
				.addParameter("title", title)
				.addParameter("format", "json");
            HttpGet request = new HttpGet(builder.build());
            request.addHeader("content-type", "application/json");
            HttpResponse result = httpClient.execute(request);
            
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            try {
            	
                JSONObject resp = (JSONObject) new JSONObject(json).get("response");
                JSONObject status = (JSONObject) resp.get("status");
                System.out.println("\t" + status.get("message"));
                JSONArray songs = (JSONArray) resp.get("songs");
                JSONObject song = (JSONObject) songs.get(0);
                System.out.print("\t" + song.get("artist_name"));
                System.out.print("(" + song.get("artist_id") + ") - ");
                System.out.print(song.get("title"));
                System.out.println("(" + song.get("id") + ")");
                return song.getString("id");

            } catch (JSONException e) {
                // TODO: handle exception
            	System.err.println("Failure. " + artist + " - " + title + ".\n" + e.getMessage());
            }

        } catch (URISyntaxException e) {
        	// TODO: handle exception
        }
		return null;
	}
	
	public static Map<String,String> getProfileInfo(String profileId) {
		//http://developer.echonest.com/api/v4/tasteprofile/profile?api_key=FILDTEOIK2HBORODV&format=json&id=CAJTFEO131216286ED
		return null;
	}
	
	public static String createProfile(String user, String profileName) {
		return createProfile(user, profileName, ProfileType.GENERAL);
	}
	
	/**
	 * Creates an Echonest music profile.  The profile name is generated from the user's name
	 * and the profile name (to minimize conflicts across all users).
	 * 
	 * @param user - the user the profile is being created for
	 * @param profileName - name of the profile to be created
	 * @param type - profile type (artist, song, general). If left null, 'general' is used as a default.
	 * @return
	 */
	public static String createProfile(String user, String profileName, ProfileType type) {
		
		if (user == null || profileName == null ||
				user.isEmpty() || profileName.isEmpty()) {
			throw new NullPointerException("A valid user and profile name must be supplied.");
		}
		
		String uniqueProfileName = user + ":" + profileName;
		type = (type == null) ? ProfileType.GENERAL : type;
		
		System.out.println("Creating profile: " + uniqueProfileName + ", of type: " + type.toString());

		try {
			HttpPost request = new HttpPost("http://developer.echonest.com/api/v4/catalog/create");

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("api_key", API_KEY));
			urlParameters.add(new BasicNameValuePair("type", type.toString()));
			urlParameters.add(new BasicNameValuePair("name", uniqueProfileName));
			urlParameters.add(new BasicNameValuePair("format", "json"));
		 
			request.setEntity(new UrlEncodedFormEntity(urlParameters));
		 
			HttpResponse result = httpClient.execute(request);
            
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            try {
            	
                JSONObject resp = (JSONObject) new JSONObject(json).get("response");
                JSONObject status = (JSONObject) resp.get("status");
                // TODO: how do we handle a failure status? do we want to throw an exception? return null?
                System.out.println("\tmessage: " + status.get("message"));
                System.out.println("\tid: " + resp.get("id"));
                return resp.getString("id");

            } catch (JSONException e) {
                // TODO: handle exception
            	System.err.println("Failure");
            }

        } catch (Exception e) {
        	// TODO: handle exception
        	System.err.println("Failure");
        }
		return null;
	}
	
	public static String getProfileItemId(String id) {
		String itemId = id;
		int rosettaId = itemId.lastIndexOf(':');
		if (rosettaId != -1) itemId = itemId.substring(rosettaId);
		return "item-" + itemId;
	}
	
	/**
	 * Add a track to an Echonest taste profile
	 * 
	 * @param profileId
	 * @param trackIds - a list of track ids (either a rosetta ID or ENID)
	 */
	public static boolean addTrackToProfile(String profileId, Collection<String> trackIds) {
		
		System.out.println("Adding " + trackIds.size() + " track(s) to profile: " + profileId);

		JSONArray itemBlock = new JSONArray();
		for (String trackId : trackIds) {
			
			boolean isRosettaTrack = trackId.contains(":track:");
			String idType = isRosettaTrack ? "track_id" : "song_id";
			
			JSONObject itemUpdate = new JSONObject();
			itemUpdate.put("action", "update");
			JSONObject item = new JSONObject();
			item.put("item_id", getProfileItemId(trackId));
			item.put(idType, trackId);
			item.put("favorite", true);
			itemUpdate.put("item", item);
			itemBlock.put(itemUpdate);
		}
		System.out.println("\t" + itemBlock.toString());
		
		try {
			HttpPost request = new HttpPost("http://developer.echonest.com/api/v4/catalog/update");

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("api_key", API_KEY));
			urlParameters.add(new BasicNameValuePair("id", profileId));
			urlParameters.add(new BasicNameValuePair("format", "json"));
			urlParameters.add(new BasicNameValuePair("data", itemBlock.toString()));
		 
			request.setEntity(new UrlEncodedFormEntity(urlParameters));
		 
			HttpResponse result = httpClient.execute(request);
            
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            try {
            	
                JSONObject resp = (JSONObject) new JSONObject(json).get("response");
                JSONObject status = (JSONObject) resp.get("status");
                System.out.println("\tmessage: " + status.get("message"));
                System.out.println("\tticket: " + resp.get("ticket"));
                
                return status.get("message").equals("Success");

            } catch (JSONException e) {
                // TODO: handle exception
            	System.err.println(e.getMessage());
            }

        } catch (Exception e) {
        	System.err.println(e.getMessage());
        }
		
		return false;
	}

	/**
	 * Delete an item from a profile
	 * 
	 * @param profileId
	 * @param itemId
	 * @return
	 */
	public static boolean deleteItemFromProfile(String profileId, String itemId) {
		
		System.out.println("Deleting " + itemId + " from profile: " + profileId);

		JSONArray itemBlock = new JSONArray();
		JSONObject itemUpdate = new JSONObject();
		itemUpdate.put("action", "delete");
		JSONObject item = new JSONObject();
		item.put("item_id", itemId);
		itemUpdate.put("item", item);
		itemBlock.put(itemUpdate);
		
		System.out.println("\t" + itemBlock.toString());
		
		try {
			HttpPost request = new HttpPost("http://developer.echonest.com/api/v4/catalog/update");

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("api_key", API_KEY));
			urlParameters.add(new BasicNameValuePair("id", profileId));
			urlParameters.add(new BasicNameValuePair("format", "json"));
			urlParameters.add(new BasicNameValuePair("data", itemBlock.toString()));
		 
			request.setEntity(new UrlEncodedFormEntity(urlParameters));
		 
			HttpResponse result = httpClient.execute(request);
            
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            try {
            	
                JSONObject resp = (JSONObject) new JSONObject(json).get("response");
                JSONObject status = (JSONObject) resp.get("status");
                System.out.println("\tmessage: " + status.get("message"));
                System.out.println("\tticket: " + resp.get("ticket"));
                
                return status.get("message").equals("Success");

            } catch (JSONException e) {
                // TODO: handle exception
            	System.err.println(e.getMessage());
            }

        } catch (Exception e) {
        	System.err.println(e.getMessage());
        }
		
		return false;
	}
	
	public static Map<String,String> getArtistSummary(String artistId) {
		
		System.out.println("Getting artist summary for artist " + artistId);
		
		String[] buckets = {"discovery", "familiarity", "hotttnesss"};
		String[] foreignBuckets = {"id:twitter", "id:facebook"};

		try {
			
			// first find the artist
			
			URIBuilder builder = new URIBuilder("http://developer.echonest.com/api/v4/artist/profile")
				.addParameter("api_key", API_KEY)
				.addParameter("id", artistId)
				.addParameter("bucket", "songs")
				.addParameter("bucket", "genre")
				.addParameter("format", "json");
			
			for (String bucket : buckets)
				builder.addParameter("bucket", bucket);
			
			for (String bucket : foreignBuckets)
				builder.addParameter("bucket", bucket);
			
            HttpGet request = new HttpGet(builder.build());
            request.addHeader("content-type", "application/json");
            HttpResponse result = httpClient.execute(request);
            
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            try {
            	
                JSONObject resp = (JSONObject) new JSONObject(json).get("response");
                JSONObject status = (JSONObject) resp.get("status");
                System.out.println("\t" + status.get("message"));

                JSONObject artist = resp.getJSONObject("artist");
                String name = artist.get("name").toString();
                System.out.println("\t" + name);
                
                JSONArray songs = (JSONArray) artist.get("songs");
                System.out.println("\t" + songs.toString());
                
                JSONArray genres = (JSONArray) artist.get("genres");
                System.out.println("\t" + genres.toString());
                
                Map<String,String> artistSummary = new HashMap<String,String>();
                artistSummary.put("name", name);
                artistSummary.put("songs", songs.toString());
                artistSummary.put("genres", genres.toString());
                
                for (String bucket : buckets) {
                	String bucketVal = artist.get(bucket).toString();
                	System.out.println("\t" + bucket + " - " + bucketVal);
                	artistSummary.put(bucket, bucketVal);
                }
                
                JSONArray foreignBucketVals = artist.getJSONArray("foreign_ids");
                
                for (int i=0; i<foreignBucketVals.length(); i++) {
                	JSONObject foreignBucketVal = foreignBucketVals.getJSONObject(i);
                	String catalog = foreignBucketVal.getString("catalog");
                	String foreignId = foreignBucketVal.getString("foreign_id");
                	System.out.println("\t" + catalog + " - " + foreignId);
                	artistSummary.put(catalog, foreignId);
                }
                
                return artistSummary;

            } catch (JSONException e) {
                // TODO: handle exception
            	System.err.println("Failure. " + artistId + ".\n" + e.getMessage());
            }

        } catch (URISyntaxException | IOException e) {
        	// TODO: handle exception
        	System.err.println("Failure. " + artistId + ".\n" + e.getMessage());
        }
		
		return null;
	}
	
	public static Map<String,String> getAudioSummary(String trackId) {
		
		System.out.println("Getting audio summary for track " + trackId);
		boolean isRosettaTrack = trackId.contains(":track:");
		String idType = isRosettaTrack ? "track_id" : "id";
		
		// custom attribute buckets
		String[] buckets = {"song_currency", "song_hotttnesss", "song_discovery"};
		try {
			
			// first find the artist
			
			URIBuilder builder = new URIBuilder("http://developer.echonest.com/api/v4/song/profile")
				.addParameter("api_key", API_KEY)
				.addParameter(idType, trackId)
				.addParameter("bucket", "audio_summary")
				.addParameter("format", "json");
			
			for (String bucket : buckets)
				builder.addParameter("bucket", bucket);
			
            HttpGet request = new HttpGet(builder.build());
            request.addHeader("content-type", "application/json");
            HttpResponse result = httpClient.execute(request);
            
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            try {
            	
                JSONObject resp = (JSONObject) new JSONObject(json).get("response");
                JSONObject status = (JSONObject) resp.get("status");
                System.out.println("\t" + status.get("message"));

                JSONObject song = (JSONObject) ((JSONArray) resp.get("songs")).get(0);
                
                System.out.println("\t" + song.get("artist_name") + " - " + song.get("title") + ":");
                
                Map<String,String> audioSummary = new HashMap<String,String>();
                audioSummary.put("artist_name", song.get("artist_name").toString());
                audioSummary.put("title", song.get("title").toString());
                
                for (String bucket : buckets) {
                	String bucketVal = song.get(bucket).toString();
                	System.out.println("\t" + bucket + " - " + bucketVal);
                	audioSummary.put(bucket, bucketVal);
                }
                
                JSONObject summary = (JSONObject) song.get("audio_summary");
                
                for (Object key : summary.keySet()) {
                	String keyStr = key.toString();
                	if (keyStr.equals("analysis_url")) continue;
                	String keyVal = summary.get(keyStr).toString();
	                System.out.println("\t\t" + keyStr + ": " + keyVal);
	                audioSummary.put(keyStr, keyVal);
                }
                
                return audioSummary;

            } catch (JSONException e) {
                // TODO: handle exception
            	System.err.println("Failure. " + trackId + ".\n" + e.getMessage());
            }

        } catch (URISyntaxException | IOException e) {
        	// TODO: handle exception
        	System.err.println("Failure. " + trackId + ".\n" + e.getMessage());
        }
		return null;
	}
	
	/**
	 * Creates a default playlist of 20 songs
	 * 
	 * @param profile - profile used to generate a customized playlist
	 * @param preference - preferences used for generating the custom playlist
	 * @return - a list of spotify track ids
	 */
	public static List<String> getNewSpotifyPlaylist(PlaylistFilter filter) {
		return getNewSpotifyPlaylist(filter, 20);
	}
	
	/**
	 * Creates a playlist of songs
	 * 
	 * @param profile - profile used to generate a customized playlist
	 * @param preference - preferences used for generating the custom playlist
	 * @param size - the size of the playlist
	 * @return - a list of spotify track ids
	 */
	public static List<String> getNewSpotifyPlaylist(PlaylistFilter filter, Integer size) {
		if (size == null) size = 20;
		
		List<String> songs = new ArrayList<String>();
		
		// TODO: how should we handle inappropriate playlist sizes?
		if (size <= 0) return songs;
		
		// API endpoint:
		// http://developer.echonest.com/api/v4/playlist/static?api_key=<key>

		PlaylistParams playlistParams = new PlaylistParams();
		playlistParams.addSeedCatalog(filter.getProfile().getProfileId());
		playlistParams.setVariety(filter.getPreference().getVariety());
		playlistParams.setAdventurousness(filter.getPreference().getAdventurousness());
		playlistParams.setDistribution(filter.getPreference().isFocused());
		playlistParams.addIDSpace("id:spotify");
		playlistParams.includeTracks();
		// We'll ask for 100 songs, then filter amongst the results
		// to better tailor to the preferences supplied
		playlistParams.setResults(100);
		playlistParams.setLimit(true);
		playlistParams.setType(PlaylistType.CATALOG_RADIO);
		
		try {
			Playlist playlist = EN.createStaticPlaylist(playlistParams);
			int numSongs = 0;
			for (Song song : playlist.getSongs()){
				String foreignId = song.getTrack("spotify").getForeignID();
				System.out.println(foreignId);
				
				// determine if the song should be filtered via recommendations
				FilterResponse filtered = filter.filter(song);
				
				if (filtered.isFiltered()) {
					songs.add(foreignId);
					numSongs++;
					if (numSongs >= size) break;
				}
			}
		} catch (EchoNestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return songs;
	}
	
	public static GeneralCatalog getGeneralProfile(ProfileEntity profile) {
		try {
			for(GeneralCatalog catalog : EN.listGeneralCatalogs()){
				if (catalog.getID().equals(profile.getProfileId())) {
					return catalog;
				}
			}
		} catch (EchoNestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
