package com.musicninja.spotify;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.musicninja.model.UserEntity;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.authentication.RefreshAccessTokenRequest;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.wrapper.spotify.models.RefreshAccessTokenCredentials;

public class TokenManager {
	
	private static Map<String,UserToken> userTokens = new HashMap<String,UserToken>();
	
	public static String getUserToken(UserEntity user) {

		UserToken token = userTokens.get(user.getSpotifyUsername());
		
		// check if the access token is available
		if (token != null) {
			
			// check if the token has expired
			long time = System.currentTimeMillis();
			if (time < token.getExpiration()) {
				return token.getAccessToken();
			}
		}
		
		SpotifyRequests.API.setRefreshToken(user.getRefreshToken());
		final RefreshAccessTokenRequest request = SpotifyRequests.API.refreshAccessToken().build();
	    
		try {
			RefreshAccessTokenCredentials refreshedToken = request.get();
			saveUserToken(user.getSpotifyUsername(), 
					refreshedToken.getAccessToken(),
					user.getRefreshToken(),
					refreshedToken.getExpiresIn());
			
			return refreshedToken.getAccessToken();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WebApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void saveUserToken(String spotifyUser, final AuthorizationCodeCredentials credentials) {
		saveUserToken(spotifyUser, 
				credentials.getAccessToken(),
				credentials.getRefreshToken(),
				credentials.getExpiresIn());
	}
	
	public static void saveUserToken(String spotifyUser, 
			String accessToken,
			String refreshToken,
			long expiresIn) {
		long expiration = System.currentTimeMillis() + (expiresIn * 1000);
		UserToken token = new UserToken(accessToken, refreshToken, expiration);
		userTokens.put(spotifyUser, token);
	}
	
	private static class UserToken {
		String accessToken;
		String refreshToken;
		long expiration;
		
		
		public UserToken(String accessToken, String refreshToken,
				long expiration) {
			this.accessToken = accessToken;
			this.refreshToken = refreshToken;
			this.expiration = expiration;
		}

		public String getAccessToken() {
			return accessToken;
		}
		
		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}
		
		public String getRefreshToken() {
			return refreshToken;
		}
		
		public void setRefreshToken(String refreshToken) {
			this.refreshToken = refreshToken;
		}
		
		public long getExpiration() {
			return expiration;
		}
		
		public void setExpiration(long expiration) {
			this.expiration = expiration;
		}
	}
	
}