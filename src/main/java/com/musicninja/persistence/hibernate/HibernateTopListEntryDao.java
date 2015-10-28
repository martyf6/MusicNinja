package com.musicninja.persistence.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.musicninja.model.MusicObjectEntity;
import com.musicninja.model.TopListEntity;
import com.musicninja.model.TopListEntryEntity;
import com.musicninja.persistence.ITopListEntryDao;

@Repository
public class HibernateTopListEntryDao implements ITopListEntryDao {

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
	public void addTopListEntry(TopListEntryEntity topListEntryEntity) {
		currentSession().save(topListEntryEntity);
	}

	@Override
	@Transactional
	public TopListEntryEntity getTopListEntryById(int id) {
		return (TopListEntryEntity) currentSession().get(TopListEntryEntity.class, id);
	}
	
	@Override
	@Transactional
	public List<TopListEntryEntity> getTopListEntriesByList(TopListEntity topList) {
		Criteria criteria = currentSession().createCriteria(TopListEntity.class);
		@SuppressWarnings("unchecked")
		List<TopListEntryEntity> topListEntries = criteria.add(Restrictions.eq("topList", topList)).list();
		return topListEntries;
	}
	
	@Override
	@Transactional
	public List<TopListEntryEntity> getTopListEntriesByMusicObject(MusicObjectEntity musicObject) {
		Criteria criteria = currentSession().createCriteria(TopListEntryEntity.class);
		@SuppressWarnings("unchecked")
		List<TopListEntryEntity> topListEntries = criteria.add(Restrictions.eq("musicObject", musicObject)).list();
		return topListEntries;
	}

	@Override
	@Transactional
	public void saveTopListEntry(TopListEntryEntity topListEntry) {
		currentSession().update(topListEntry);
	}

	@Override
	@Transactional
	public void deleteTopListEntry(TopListEntryEntity topListEntry) {
		currentSession().delete(topListEntry);
	}

}
