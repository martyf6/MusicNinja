package com.musicninja.spotify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.musicninja.model.UserEntity;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.SimplePlaylist;
import com.wrapper.spotify.models.Track;

/**
 * This class provides utilities for managing a Spotify account.
 * In particular, the primary functionality provided in this class
 * is the ability to create and restore a backup file with a user's
 * Spotify playlists.
 * <br/>
 * Warning: restoring a backup file has not been tested yet.
 * <br/>
 * TODO:
 * <ul> 
 * <li>Test restoring</li>
 * <li>Make backup location configurable</li>
 * <li>Add better exception/error handling</li>
 * </ul>
 * 
 * @author marty
 *
 */
public class SpotifyManage {
	
	// We should move this to be a configurable parameter
	private static final String BACKUP_DIR = "/tmp/";
	
	public static File backupPlaylists(UserEntity user, boolean includeFollowedPlaylists){
		
		System.out.println("Backing up playlists for user: " + user.getSpotifyUsername());
		if (!includeFollowedPlaylists)
			System.out.println("Not backing up followed playlists.");
		
		// map playlist id to file name
		List<File> playlistFiles = new ArrayList<File>();
		
		// get all user's playlists
		Collection<SimplePlaylist> playlists = SpotifyRequests.getUserPlaylists(user);

		for (SimplePlaylist playlist : playlists) {
			
			if (includeFollowedPlaylists || 
					playlist.getOwner().getId().equals(user.getSpotifyUsername())) {
				
				try {
					
					if (playlist.getName().trim().equals("extras")) continue;
					
					// make file for playlist
					// (Uri is 'spotify:user:<id>:playlist:<playlist_id>')
					String playlistFilename = playlist.getId();

					System.out.println("\tBacking up playlist: '" + playlist.getName() + "' to file: " + playlistFilename);
					
					File playlistFile = new File(BACKUP_DIR + playlistFilename + ".bk");
					PrintWriter writer = new PrintWriter(playlistFile,"UTF-8");

					// add line to file: owner id, playlist name
					PlaylistBackupHeader header = new PlaylistBackupHeader(playlist);
					writer.println(header.outputCsvLine());

					Collection<PlaylistTrack> tracks = SpotifyRequests.getPlaylistTracks(user, playlist.getOwner().getId(), playlist.getId());
					
					// track index
					int i = 1;
					
					for (PlaylistTrack pTrack : tracks) {
						Track track = pTrack.getTrack();
						// add line to file with: index, id, title, artist, album
						PlaylistBackupLine trackLine = new PlaylistBackupLine(i, track);
						writer.println(trackLine.outputCsvLine());
						i++;
					}
					System.out.println("\tPlaylist file written");
					writer.close();
					playlistFiles.add(playlistFile);
					
				} catch (FileNotFoundException e) {
					System.err.println("Error writing to file - file not found.");
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					System.err.println("Error writing to file.");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// wait a bit before making next request/backup
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("Not backing up followed playlist: " + playlist.getName());
			}
		}
		
		// now zip up all playlist backups into one zipped file for downloading
		if (!playlistFiles.isEmpty()) {
			return zipFiles(playlistFiles, BACKUP_DIR + user.getSpotifyUsername() + ".zip");
		}
		
		// either no files are being backed up or an error has occurred
		return null;
	}
	
	public static void restoreCombinedBackupFile(UserEntity user, File combinedBackup) {
		
		// verify the combined file is a zip
		String ext = combinedBackup.getName().substring(
				combinedBackup.getName().lastIndexOf('.'));
		
		if (!ext.equals("zip")) 
			return;
		
		try {
			
			// read the zipped backup
			ZipFile backupZip = new ZipFile(combinedBackup);
			Enumeration<? extends ZipEntry> entries = backupZip.entries();

		    while(entries.hasMoreElements()){
		    	
		    	// for each backup file, restore the entries as a playlist
		        ZipEntry entry = entries.nextElement();
		        InputStream backup = backupZip.getInputStream(entry);
		        Playlist playlist = restoreSingleBackupFile(user, backup);
		        System.out.println("Created playlist: " + playlist.getName());
		        backup.close();
		    }
		    backupZip.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Playlist restoreSingleBackupFile(UserEntity user, InputStream backup) {
		
		// verify the file is the right type (UTF-8)
		BufferedReader backupReader = new BufferedReader(
				new InputStreamReader(backup, StandardCharsets.UTF_8));
		
		try {
			
			// start reading the file line-by-line
			String line = backupReader.readLine();
			if (line == null) return null;
			
			PlaylistBackupHeader header = PlaylistBackupHeader.parseCsvLine(line);
			if(header == null) {
				System.out.println("Error parsing playlist backup header: " + line);
				return null;
			}
			
			// create spotify playlist with name
			System.out.println("Creating playlist with name: " + header.name);
			Playlist playlist = SpotifyRequests.createPlaylist(user, header.name);
			
			// create a 'buffer' list of tracks to be added
			int bufferSize = 30;
			List<String> tracksToAdd = new ArrayList<String>();
			// read each track from the playlist backup
			while ((line = backupReader.readLine()) != null) {
				
				PlaylistBackupLine track = PlaylistBackupLine.parseCsvLine(line);
				if (track == null) {
					System.out.println("Error parsing playlist backup line: " + line);
					continue;
				}
				System.out.println("\tRestoring track: " + track.artist + " - " + track.name);
				// add track to buffer list
				tracksToAdd.add(track.id);
				// if buffer list is full, push items into playlist and clear the buffer
				if (tracksToAdd.size() >= bufferSize) {
					SpotifyRequests.addTracksToPlaylist(user, playlist.getId(), tracksToAdd);
					tracksToAdd.clear();
				}
			}
			
			// if buffer list still has items, push them into the playlist
			if (!tracksToAdd.isEmpty()) {
				SpotifyRequests.addTracksToPlaylist(user, playlist.getId(), tracksToAdd);
				tracksToAdd.clear();
			}
			
			// return the new playlist
			return playlist;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static File zipFiles(List<File> files, String zipFilename) {
		File zippedFile = new File(zipFilename);
		byte[] buffer = new byte[1024];

		try {

			FileOutputStream fos = new FileOutputStream(zippedFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			System.out.println("Outputting to Zip: " + zipFilename);

			for (File file : files) {

				System.out.println("\tFile Added : " + file.getName());
				ZipEntry ze = new ZipEntry(file.getName());
				zos.putNextEntry(ze);

				FileInputStream in = new FileInputStream(file);

				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
			}

			zos.closeEntry();
			zos.close();

			System.out.println("Finished zipping files");
			return zippedFile;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static String artistsToString(List<SimpleArtist> artists) {
		String out = "";
		Iterator<SimpleArtist> it = artists.iterator();
		while (it.hasNext()) {
			SimpleArtist artist = it.next();
			out += artist.getName();
			if (it.hasNext()) out += ", ";
		}
		return out;
	}
	
	public static void main(String[] args) {
		File backupDir = new File(BACKUP_DIR);
		zipFiles(Arrays.asList(backupDir.listFiles()), BACKUP_DIR + "backup.zip");
	}
}

/*
 * TODO: these backup helper classes should probably have an abstracted interface
 * and be moved to a separate package within spotify
 */

class PlaylistBackupHeader {
	String id, uri, name;
	
	PlaylistBackupHeader(SimplePlaylist playlist){
		this(playlist.getId(), playlist.getUri(), playlist.getName());
	}
	
	PlaylistBackupHeader(String id, String uri, String name) {
		this.id = id;
		this.uri = uri;
		this.name = name;
	}
	
	String outputCsvLine() {
		return id+","+uri+","+name;
	}
	
	static PlaylistBackupHeader parseCsvLine(String line) {
		List<String> items = Arrays.asList(line.split("\\s*,\\s*", 3));
		if(items.size() != 3) return null;
		return new PlaylistBackupHeader(
			items.get(0),
			items.get(1),
			items.get(2));
	}
}

class PlaylistBackupLine {
	int i;
	String id, name, artist, album;
	static final String TRACK_DEL = "::";
	
	PlaylistBackupLine(int i, Track track) {
		this(i, track.getId(), track.getName(), 
				SpotifyManage.artistsToString(track.getArtists()),
				track.getAlbum().getName());
	}
	
	PlaylistBackupLine(int i, String id, String name, String artist, String album) {
		this.i = i;
		this.id = id;
		this.name = name;
		this.artist = artist;
		this.album = album;
	}
	
	String outputCsvLine() {
		return i+","+id+","+name+TRACK_DEL+artist+TRACK_DEL+album;
	}
	
	static PlaylistBackupLine parseCsvLine(String line) {
		List<String> items = Arrays.asList(line.split("\\s*,\\s*", 3));
		if(items.size() != 3) return null;
		List<String> trackItems = Arrays.asList(items.get(2).split("\\s*" + TRACK_DEL + "\\s*", 3));
		if(trackItems.size() != 3) return null;
		return new PlaylistBackupLine(
				Integer.parseInt(items.get(0)),
				items.get(1),
				trackItems.get(0),
				trackItems.get(1),
				trackItems.get(2));
	}
}
