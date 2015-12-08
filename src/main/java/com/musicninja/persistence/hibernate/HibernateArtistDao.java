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
import com.musicninja.persistence.IArtistDao;

@Repository
public class HibernateArtistDao implements IArtistDao {
	
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
	public void addArtist(ArtistEntity artistEntity) {
		currentSession().save(artistEntity);
	}
	
	@Override
	@Transactional
	public ArtistEntity getArtistById(int id) {
		return (ArtistEntity) currentSession().get(ArtistEntity.class, id);
	}
	
	@Override
	@Transactional
	public ArtistEntity getArtistBySpotifyId(String spotifyId) {
		Criteria criteria = currentSession().createCriteria(ArtistEntity.class);
		@SuppressWarnings("unchecked")
		List<ArtistEntity> artists = criteria.add(Restrictions.eq("spotifyId", spotifyId)).list();
		if (artists== null || artists.isEmpty()) return null;
		return artists.get(0);
	}
	
	@Override
	@Transactional
	public List<ArtistEntity> getArtistsByTrack(TrackEntity track) {
		Criteria criteria = currentSession().createCriteria(ArtistEntity.class);
		@SuppressWarnings("unchecked")
		List<ArtistEntity> artists = criteria.add(Restrictions.eq("track", track.getId())).list();
		return artists;
	}
	
	
	@Override
	@Transactional
	public void saveArtist(ArtistEntity artistEntity) {
		currentSession().update(artistEntity);
	}

	@Override
	@Transactional
	public void deleteArtist(ArtistEntity artistEntity) {
		currentSession().delete(artistEntity);
	}
	
}
