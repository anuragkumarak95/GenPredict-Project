package Sviet.ACM.genPredict.Models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Prediction {

	private String gender;
	private double accuracy;
	private double ht;
	private double wt;
	
	private String status;
	private String error;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
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
	public Prediction addHt(double ht) {
		this.ht = ht;
		return this;
	}
	
	public Prediction addWt(double wt) {
		this.wt = wt;
		return this;
	}
	
	public Prediction addGender(String gender) {
		this.gender = gender;
		return this;
	}
	
	public Prediction addAccuracy(double accuracy) {
		this.accuracy = accuracy;
		return this;
	}
	
	public Prediction addStatus(String status) {
		this.status = status;
		return this;
	}
	
	public Prediction addError(String error) {
		this.error = error;
		return this;
	}
	
}
