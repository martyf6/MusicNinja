package com.musicninja.persistence.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.musicninja.model.ArtistEntity;
import com.musicninja.model.TrackEntity;
import com.musicninja.persistence.ITrackDao;

@Repository
public class HibernateTrackDao implements ITrackDao {
	
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
	public void addTrack(TrackEntity trackEntity) {
		currentSession().save(trackEntity);
	}
	
	@Override
	@Transactional
	public TrackEntity getTrackById(int id) {
		return (TrackEntity) currentSession().get(TrackEntity.class, id);
	}
	
	@Override
	@Transactional
	public TrackEntity getTrackBySpotifyId(String spotifyId) {
		Criteria criteria = currentSession().createCriteria(TrackEntity.class);
		@SuppressWarnings("unchecked")
		List<TrackEntity> tracks = criteria.add(Restrictions.eq("spotifyId", spotifyId)).list();
		if (tracks == null || tracks.isEmpty()) return null;
		return tracks.get(0);
	}
	
	@Override
	@Transactional
	public List<TrackEntity> getTracksByArtist(ArtistEntity artist) {
		Criteria criteria = currentSession().createCriteria(TrackEntity.class);
		@SuppressWarnings("unchecked")
		List<TrackEntity> tracks = criteria.add(Restrictions.eq("artist", artist.getId())).list();
		return tracks;
	}
	
	
	@Override
	@Transactional
	public void saveTrack(TrackEntity trackEntity) {
		currentSession().update(trackEntity);
	}

	@Override
	@Transactional
	public void deleteTrack(TrackEntity trackEntity) {
		currentSession().delete(trackEntity);
	}

}
