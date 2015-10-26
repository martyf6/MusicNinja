package com.musicninja.spotify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.musicninja.model.UserEntity;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.SimplePlaylist;
import com.wrapper.spotify.models.Track;

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
					
					// make file for playlist
					// (Uri is 'spotify:user:<id>:playlist:<playlist_id>')
					String playlistFilename = playlist.getId();

					System.out.println("\tBacking up playlist: '" + playlist.getName() + "' to file: " + playlistFilename);
					
					File playlistFile = new File(BACKUP_DIR + playlistFilename + ".txt");
					PrintWriter writer = new PrintWriter(playlistFile,"UTF-8");

					// add line to file: owner id, playlist name
					writer.println(playlist.getUri() + "," + playlist.getId() + "," + playlist.getName());

					Collection<PlaylistTrack> tracks = SpotifyRequests.getPlaylistTracks(user, playlist.getOwner().getId(), playlist.getId());
					
					// track index
					int i = 1;
					
					for (PlaylistTrack pTrack : tracks) {
						Track track = pTrack.getTrack();
						
						// add line to file with: index, id, title, artist, album
						writer.println(i
								+ "," + track.getId()
								+ "," + track.getName()
								+ "," + artistsToString(track.getArtists())
								+ "," + track.getAlbum().getName());
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
		
		// either no files are being backed up or an error has occured
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
