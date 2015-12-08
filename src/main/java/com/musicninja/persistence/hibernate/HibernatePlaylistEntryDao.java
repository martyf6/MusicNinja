package com.musicninja.persistence.hibernate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.musicninja.model.TrackEntity;
import com.musicninja.model.PlaylistEntity;
import com.musicninja.model.PlaylistEntryEntity;
import com.musicninja.persistence.IPlaylistEntryDao;

@Repository
public class HibernatePlaylistEntryDao implements IPlaylistEntryDao {

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
	public void addPlaylistEntry(PlaylistEntryEntity playlistEntryEntity) {
		currentSession().save(playlistEntryEntity);
	}

	@Override
	@Transactional
	public PlaylistEntryEntity getPlaylistEntryById(int id) {
		return (PlaylistEntryEntity) currentSession().get(PlaylistEntryEntity.class, id);
	}
	
	@Override
	@Transactional
	public List<PlaylistEntryEntity> getPlaylistEntriesByPlaylist(PlaylistEntity playlist) {
		Criteria criteria = currentSession().createCriteria(PlaylistEntryEntity.class);
		@SuppressWarnings("unchecked")
		List<PlaylistEntryEntity> playlistEntries = criteria.add(Restrictions.eq("playlist", playlist)).list();
		return playlistEntries;
	}
	
	@Override
	@Transactional
	public List<PlaylistEntryEntity> getPlaylistEntriesByTrack(TrackEntity track) {
		Criteria criteria = currentSession().createCriteria(PlaylistEntryEntity.class);
		@SuppressWarnings("unchecked")
		List<PlaylistEntryEntity> playlistEntries = criteria.add(Restrictions.eq("track", track)).list();
		return playlistEntries;
	}
	
	@Override
	@Transactional
	public PlaylistEntryEntity getPlaylistEntryByInfo(TrackEntity track, String addedById, Date addedAt) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("track", track);
		map.put("addedAtId", addedById);
		map.put("addedBy", addedAt);
		Criteria criteria = currentSession().createCriteria(PlaylistEntryEntity.class);
		@SuppressWarnings("unchecked")
		List<PlaylistEntryEntity> playlistEntries = criteria.add(Restrictions.allEq(map)).list();
		return playlistEntries.get(0);
	}

	@Override
	@Transactional
	public void savePlaylistEntry(PlaylistEntryEntity playlistEntry) {
		currentSession().update(playlistEntry);
	}

	@Override
	@Transactional
	public void deletePlaylistEntry(PlaylistEntryEntity playlistEntry) {
		currentSession().delete(playlistEntry);
	}

}
