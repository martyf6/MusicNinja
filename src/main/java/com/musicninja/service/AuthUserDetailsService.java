package com.musicninja.service;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.musicninja.model.UserEntity;
import com.musicninja.persistence.IUserDao;

/**
 * Service used to load user-specific data during authentication.
 * 
 * @author marty
 *
 */
public class AuthUserDetailsService implements UserDetailsService {
	
	private static Logger logger = Logger.getLogger(AuthUserDetailsService.class);
	
	@Autowired
	private IUserDao userDao;
	
	@Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException, DataAccessException {
		
		// make sure that null/empty users can't login
		if (username == null || username.isEmpty()) throw new UsernameNotFoundException("No username supplied.");
		
		// check that the user exists
		UserEntity user = userDao.getUserByUsername(username);
		if (user == null) throw new UsernameNotFoundException("No user found.");
		
		// authenticate the existing user
		logger.info("Authenticating user '" + username + "'.");
		
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		SimpleGrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER");
		authorities.add(userAuthority);
		
		return new User(user.getUsername(), user.getPassword(), true, true, true, true, authorities);
    }

}
