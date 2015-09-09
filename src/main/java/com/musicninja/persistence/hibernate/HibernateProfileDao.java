package com.musicninja.persistence.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.musicninja.model.ProfileEntity;
import com.musicninja.model.UserEntity;
import com.musicninja.persistence.IProfileDao;

@Repository
public class HibernateProfileDao implements IProfileDao {

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
	public void addProfile(ProfileEntity profileEntity) {
		currentSession().save(profileEntity);
	}

	@Override
	@Transactional
	public ProfileEntity getProfileById(int id) {
		return (ProfileEntity) currentSession().get(ProfileEntity.class, id);
	}
	
	@Override
	@Transactional
	public ProfileEntity getProfileByEchoId(String echoId) {
		Criteria criteria = currentSession().createCriteria(ProfileEntity.class);
		@SuppressWarnings("unchecked")
		List<ProfileEntity> profiles = criteria.add(Restrictions.eq("echoId", echoId)).list();
		if (profiles == null || profiles.isEmpty()) return null;
		return profiles.get(0);
	}

	@Override
	@Transactional
	public List<ProfileEntity> getProfilesByUser(UserEntity owner) {
		Criteria criteria = currentSession().createCriteria(ProfileEntity.class);
		@SuppressWarnings("unchecked")
		List<ProfileEntity> profiles = criteria.add(Restrictions.eq("owner", owner.getId())).list();
		return profiles;
	}

	@Override
	@Transactional
	public void saveProfile(ProfileEntity profile) {
		currentSession().update(profile);
	}

	@Override
	@Transactional
	public void deleteProfile(ProfileEntity profile) {
		currentSession().delete(profile);
	}
}
