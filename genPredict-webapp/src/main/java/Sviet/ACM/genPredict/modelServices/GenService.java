package Sviet.ACM.genPredict.modelServices;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Sviet.ACM.genPredict.Models.GenUsers;

@Service
public class GenService {

	@Autowired
	private SessionFactory sessionFactory;
	
	private String tag = "[GenService] : ";
	
	@Transactional
	public GenUsers feedUser(String gender,double ht,double wt) {
		
		GenUsers user = new GenUsers();
		user.setGenderChain(gender).setHtChain(ht).setWtChain(wt);
	
		this.sessionFactory.getCurrentSession().save(user);
		System.out.println(tag+"user feeded to database");
		return user;
		
	}
	
	@Transactional
	public List<GenUsers> getAllFeeds(){
		System.out.println(tag+"all feeds fetched");
		return this.sessionFactory.getCurrentSession().createCriteria(GenUsers.class).list();
	}
	
	/*
	 * Services to make:
	 * -> all male entries. *done*
	 * -> all female entries. *done*
	 * -> get male and female entries count and sum count individually. *done*
	 * -> use all this to predict further using another service, PREDICTOR.class
	 *  */
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<GenUsers> getAllMale(){
		System.out.println(tag+"all Male feeds fetched");
		return  this.sessionFactory.getCurrentSession().createCriteria(GenUsers.class)
				.add( Restrictions.eq("gender", "M")).list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<GenUsers> getAllFemale(){
		System.out.println(tag+"all Female feeds fetched");
		return  this.sessionFactory.getCurrentSession().createCriteria(GenUsers.class)
				.add( Restrictions.eq("gender", "F")).list();
	}
	
}
