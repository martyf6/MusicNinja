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
import com.musicninja.persistence.IMusicObjectDao;

@Repository
public class HibernateMusicObjectDao implements IMusicObjectDao {

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
	public void addMusicObject(MusicObjectEntity musicObjectEntity) {
		currentSession().save(musicObjectEntity);
	}
	
	/**
	 * entityManager.createNativeQuery("LOAD DATA INFILE :fileName INTO TABLE test").setParameter("fileName", "C:\\samples\\test\\abcd.csv").executeUpdate();
	*/
	public void addTopListToDB() {
		currentSession().createSQLQuery("LOAD DATA INFILE :filename INTO TABLE testtable (text,price)").setString("filename", "/path/to/MyFile.csv").executeUpdate();;
	}

	@Override
	@Transactional
	public MusicObjectEntity getMusicObjectById(int id) {
		return (MusicObjectEntity) currentSession().get(MusicObjectEntity.class, id);
	}
	
	@Override
	@Transactional
	public List<MusicObjectEntity> getMusicObjectsByType(String type) {
		Criteria criteria = currentSession().createCriteria(MusicObjectEntity.class);
		@SuppressWarnings("unchecked")
		List<MusicObjectEntity> musicObjects = criteria.add(Restrictions.eq("type", type)).list();
		return musicObjects;
	}

	@Override
	@Transactional
	public void saveMusicObject(MusicObjectEntity musicObject) {
		currentSession().update(musicObject);
	}

	@Override
	@Transactional
	public void deleteMusicObject(MusicObjectEntity musicObject) {
		currentSession().delete(musicObject);
	}

}
