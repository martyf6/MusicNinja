package com.musicninja.echonest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class EchonestTests {
	
	public static final String PROP_FILE = "profile.properties";
	
	public static final String[][] TEST_SONGS = new String[][] {
		
		// light rock n' roll
		{"Bruce Springsteen", "The River", "SOKEGGN146164D412E"},
		{"Creedence Clearwater Revival", "Someday Never Comes", "SOEMNZJ131712D1363"},
		{"Eric Clapton", "Promises", "SONWXJJ144B6F7996D"},
		{"Billy Joel", "Vienna", "SOMZDVG12A8C131FFE"},
		{"Van Morrison", "Into The Mystic", "SOCCZTW12B0B808F99"},
		{"Jim Croce", "I Got A Name", "SOBVDTP13A5BCA3C40"},
		{"Derek and The Dominos", "Bell Bottom Blues", "SOKLPPY13167711129"},
		{"Jackson Browne", "These Days", "SOBFKSM12B0B8091BD"},
		{"Bruce Springsteen", "Stolen Car", "SOVFSZQ12AB0182980"},
		
		// a lil' hiphop
		{"A Tribe Called Quest", "Jazz", "SOLVVBM144BD7ABFA9"},
		{"Large Professor", "'Bout That Time", "SOMLIOF12B3A178EF9"},
		{"Mobb Deep", "Hell on Earth", "SOJMTUS14560CF4583"},
		{"Jay Electronica", "Exhibit C", "SONUJSS144C28067F7"},
		{"Mos Def", "Got", "SOHKZJY1315CD47982"},
		{"Grand Puba", "I Like It", "SOSMPLG14A45772C78"},
		{"AZ", "Rather Unique", "SOFXQDO137176803F5"}
	};
	
	public static final String TEST_ARTIST = "6zFYqv1mOsgBRQbae3JJ9e";
	public static final String [][] TEST_ARTIST_DATA = new String[][] {
		{"name", "Billy Joel"},
		{"songs","Piano Man"},
		{"genres","rock"},
		{"twitter","twitter:artist:billyjoel"},
		{"facebook","facebook:artist:57084011597"}
	};
	
	public static final String TEST_AUDIO = "6zsZvVaKK3VR0s7STZUH5u";
	public static final String [][] TEST_AUDIO_DATA = new String[][] {
		{"artist_name", "Van Morrison"},
		{"title","And It Stoned Me"},
		{"tempo","75.889"},
		{"duration","270.2"},
		{"acousticness","0.329569"},
		{"key","7"},
		{"energy","0.485533"},
		{"time_signature","4"}
	};
	
	public static void main(String[] args) throws IOException {
		
		Properties props = new Properties();
		InputStream resourceStream = EchonestTests.class.getResourceAsStream("/profile.properties");
	    props.load(resourceStream);
	    
	    if (props.isEmpty()) {
			System.out.println("Failed to load properties.");
			return;
		} 
		
		String echoNestApi = props.getProperty("echonest.apiKey");
		System.out.println("Using API key: " + echoNestApi);
		
		if (echoNestApi == null || echoNestApi.isEmpty() || echoNestApi.contains(" ")) {
			System.out.println("Failed to load Echonest API key from properties file.");
			return;
		}
		
		EchonestRequests.setApi(echoNestApi);
		
		testSongIds(TEST_SONGS);
		
		// Get artist summary for 'Billy Joel'
		testArtistSummary(TEST_ARTIST, TEST_ARTIST_DATA);
		
		// Get audio summary for 'Van Morrison - And It Stoned Me'
		testAudioSummary(TEST_AUDIO, TEST_AUDIO_DATA);
	}
	
	public static void testSongIds(String[][] testSongs) {
		System.out.println("Testing Echonest song IDs.");
		
		for (String[] testSong : testSongs) {
			try {
				String songId = EchonestRequests.printTrackInfo(testSong[0], testSong[1]);
				if (songId.equals(testSong[2])) {
					System.out.println("PASS.");
				} else {
					System.out.println("FAILED. Expecting '" + testSong[2] + "', got '" + songId + "'.");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void testArtistSummary(String artistId, String[][] artistTestData) {
		System.out.println("Testing Echonest artist summary for artist '" + artistId + "'.");
		
		Map<String,Object> artistSummary = EchonestRequests.getArtistSummary("spotify:artist:" + artistId);
		
		for (String[] data : artistTestData) {
			
			if (artistSummary.containsKey(data[0])) {
				String summaryData = artistSummary.get(data[0]).toString();
				if (data[0].equals("songs") || data[0].equals("genres")) {
					if (summaryData.contains(data[1])) {
						System.out.println("PASS.");
					} else {
						System.out.println("FAILED. Expecting to contain '" + data[1] + "', got '" + summaryData + "'.");
					}
				} else if (summaryData.equals(data[1])) {
					System.out.println("PASS.");
				} else {
					System.out.println("FAILED. Expecting '" + data[1] + "', got '" + summaryData + "'.");
				}
			}
		}
	}
	
	public static void testAudioSummary(String trackId, String[][] audioTestData) {
		System.out.println("Testing Echonest audio summary for song '" + trackId + "'.");
		
		Map<String,String> audioSummary = EchonestRequests.getAudioSummary("spotify:track:" + trackId);

		for (String[] data : audioTestData) {
			if (audioSummary.containsKey(data[0])) {
				String summaryData = audioSummary.get(data[0]);
				if (summaryData.equals(data[1])) {
					System.out.println("PASS.");
				} else {
					System.out.println("FAILED. Expecting '" + data[1] + "', got '" + summaryData + "'.");
				}
			}
		}
	}
}
