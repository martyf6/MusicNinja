package com.musicninja.persistence.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.musicninja.model.TopListEntity;
import com.musicninja.persistence.ITopListDao;

@Repository
public class HibernateTopListDao implements ITopListDao {

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
	public void addTopList(TopListEntity topListEntity) {
		currentSession().save(topListEntity);
	}

	@Override
	@Transactional
	public TopListEntity getTopListById(int id) {
		return (TopListEntity) currentSession().get(TopListEntity.class, id);
	}

	@Override
	@Transactional
	public List<TopListEntity> getTopListsBySource(String source) {
		Criteria criteria = currentSession().createCriteria(TopListEntity.class);
		@SuppressWarnings("unchecked")
		List<TopListEntity> topLists = criteria.add(Restrictions.eq("source", source)).list();
		return topLists;
	}
	
	@Override
	@Transactional
	public List<TopListEntity> getTopListsByType(String type) {
		Criteria criteria = currentSession().createCriteria(TopListEntity.class);
		@SuppressWarnings("unchecked")
		List<TopListEntity> topLists = criteria.add(Restrictions.eq("type", type)).list();
		return topLists;
	}

	@Override
	@Transactional
	public void saveTopList(TopListEntity topList) {
		currentSession().update(topList);
	}

	@Override
	@Transactional
	public void deleteTopList(TopListEntity topList) {
		currentSession().delete(topList);
	}

}
