package com.musicninja.persistence.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.musicninja.model.RedditPlaylistEntity;
import com.musicninja.model.UserEntity;
import com.musicninja.persistence.IRedditPlaylistDao;

@Repository
public class HibernateRedditPlaylistDao implements IRedditPlaylistDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session currentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	@Transactional
	public void addRedditPlaylist(RedditPlaylistEntity playlistEntity) {
		currentSession().save(playlistEntity);
	}

	@Override
	@Transactional
	public RedditPlaylistEntity getRedditPlaylistById(int id) {
		return (RedditPlaylistEntity) currentSession().get(RedditPlaylistEntity.class, id);
	}
	
	@Override
	@Transactional
	public RedditPlaylistEntity getRedditPlaylistByPlaylistId(String playlistId) {
		Criteria criteria = currentSession().createCriteria(RedditPlaylistEntity.class);
		@SuppressWarnings("unchecked")
		List<RedditPlaylistEntity> playlists = criteria.add(Restrictions.eq("playlistId", playlistId)).list();
		if (playlists == null || playlists.isEmpty()) return null;
		return playlists.get(0);
	}

	@Override
	@Transactional
	public List<RedditPlaylistEntity> getRedditPlaylistsByUser(UserEntity owner) {
		Criteria criteria = currentSession().createCriteria(RedditPlaylistEntity.class);
		@SuppressWarnings("unchecked")
		List<RedditPlaylistEntity> playlists = criteria.add(Restrictions.eq("owner", owner.getId())).list();
		return playlists;
	}

	@Override
	@Transactional
	public void saveRedditPlaylist(RedditPlaylistEntity playlistEntity) {
		currentSession().update(playlistEntity);
	}

	@Override
	@Transactional
	public void deleteRedditPlaylist(RedditPlaylistEntity playlistEntity) {
		currentSession().delete(playlistEntity);
	}
}
