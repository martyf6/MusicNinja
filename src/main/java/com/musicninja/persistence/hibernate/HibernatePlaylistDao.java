package com.musicninja.persistence.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.musicninja.model.PlaylistEntity;
import com.musicninja.persistence.IPlaylistDao;

@Repository
public class HibernatePlaylistDao implements IPlaylistDao {

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
	public void addPlaylist(PlaylistEntity playlistEntity) {
		currentSession().save(playlistEntity);
	}

	@Override
	@Transactional
	public PlaylistEntity getPlaylistById(int id) {
		return (PlaylistEntity) currentSession().get(PlaylistEntity.class, id);
	}

	@Override
	@Transactional
	public List<PlaylistEntity> getPlaylistsBySource(String source) {
		Criteria criteria = currentSession().createCriteria(PlaylistEntity.class);
		@SuppressWarnings("unchecked")
		List<PlaylistEntity> playlists = criteria.add(Restrictions.eq("source", source)).list();
		return playlists;
	}
	
	@Override
	@Transactional
	public PlaylistEntity getPlaylistBySourceId(String sourceId) {
		Criteria criteria = currentSession().createCriteria(PlaylistEntity.class);
		@SuppressWarnings("unchecked")
		List<PlaylistEntity> playlists = criteria.add(Restrictions.eq("sourceId", sourceId)).list();
		if (playlists == null || playlists.isEmpty()) return null;
		return playlists.get(0);
	}

	@Override
	@Transactional
	public void savePlaylist(PlaylistEntity playlist) {
		currentSession().update(playlist);
	}

	@Override
	@Transactional
	public void deletePlaylist(PlaylistEntity playlist) {
		currentSession().delete(playlist);
	}

}
