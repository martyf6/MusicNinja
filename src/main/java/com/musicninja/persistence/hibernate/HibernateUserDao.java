package com.musicninja.persistence.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.musicninja.model.UserEntity;
import com.musicninja.persistence.IUserDao;

@Repository
public class HibernateUserDao implements IUserDao {

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
	public void addUser(UserEntity userEntity) {
		currentSession().save(userEntity);
	}

	@Override
	@Transactional
	public UserEntity getUserById(int id) {
		return (UserEntity) currentSession().get(UserEntity.class, id);
	}

	@Override
	@Transactional
	public UserEntity getUserByUsername(String username) {
		Criteria criteria = currentSession().createCriteria(UserEntity.class);
		@SuppressWarnings("unchecked")
		List<UserEntity> list = criteria.add(Restrictions.eq("username", username)).list();
		if (list.isEmpty()) return null;
		return list.get(0);
	}

	@Override
	@Transactional
	public void saveUser(UserEntity userEntity) {
		currentSession().update(userEntity);
	}

	@Override
	@Transactional
	public void deleteUser(UserEntity userEntity) {
		currentSession().delete(userEntity);
	}
}
