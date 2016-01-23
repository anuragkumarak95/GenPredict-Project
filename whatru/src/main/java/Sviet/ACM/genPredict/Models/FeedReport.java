package Sviet.ACM.genPredict.Models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FeedReport {
	private String error;
	private String status;
	
	public FeedReport() {
		
	}
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	//Chained Methods..
	public FeedReport addError(String error) {
		this.error = error;
		return this;
	}
	
	public FeedReport addStatus(String status) {
		this.status = status;
		return this;
	}
	
}
