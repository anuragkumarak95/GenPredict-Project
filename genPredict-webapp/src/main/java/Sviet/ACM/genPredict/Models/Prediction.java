package Sviet.ACM.genPredict.Models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Prediction {

	private String gender;
	private double accuracy;
	private double ht;
	private double wt;

	public Prediction() {
		// blank for json or xml automated tracing.
	}
	
	//getters & setters...
	
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

	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	
	//Chained methods..
	public Prediction setHtChain(double ht) {
		this.ht = ht;
		return this;
	}
	
	public Prediction setWtChain(double wt) {
		this.wt = wt;
		return this;
	}
	
	public Prediction setGenderChain(String gender) {
		this.gender = gender;
		return this;
	}
	
	public Prediction setAccuracyChain(double accuracy) {
		this.accuracy = accuracy;
		return this;
	}
	
}
