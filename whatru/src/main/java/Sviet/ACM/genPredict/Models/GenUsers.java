package Sviet.ACM.genPredict.Models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;



@Entity @XmlRootElement
public class GenUsers {
	@Id @GeneratedValue
	private int identity;
	
	 
	private String gender;
	
	
	private double ht;
	
	
	private double wt;
	
    public GenUsers() {
		// TODO Auto-generated constructor stub
	}	//Empty Constructor stub is necessary when you are using automated text conversions like text to xml or text to json.
	
	
	//getters & setters....
	public int getIdentity() {
		return identity;
	}
	public void setIdentity(int identity) {
		this.identity = identity;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public double getHt() {
		return ht;
	}
	public void setHt(double ht) {
		this.ht = ht;
	}
	public double getWt() {
		return wt;
	}
	public void setWt(double wt) {
		this.wt = wt;
	}
	
	// chained methods
	public GenUsers setWtChain(double wt) {
		this.wt = wt;
		return this;
	}
	
	public GenUsers setHtChain(double ht) {
		this.ht = ht;
		return this;
	}
	
	public GenUsers setGenderChain(String gender) {
		this.gender = gender;
		return this;
	}
	
	

}
