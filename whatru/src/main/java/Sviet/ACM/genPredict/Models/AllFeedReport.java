package Sviet.ACM.genPredict.Models;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

//Response JSON object 
@XmlRootElement
public class AllFeedReport{
	private String status;
	private String error;
	private int count;
	private ArrayList<GenUsers> genUsers;
	
	public AllFeedReport() {
		//for json and xml conversion.
	}
	
	//getters n setters...
	
	public String getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public ArrayList<GenUsers> getGenUsers() {
		return genUsers;
	}

	public void setGenUsers(ArrayList<GenUsers> genUsers) {
		this.genUsers = genUsers;
	}
	
	//Chained Methods..
	
	public AllFeedReport addStatus(String status) {
		this.status = status;
		return this;
	}
	
	public AllFeedReport addCount(int count) {
		this.count = count;
		return this;
	}
	
	public AllFeedReport addGenUsers(ArrayList<GenUsers> genUsers) {
		this.genUsers = genUsers;
		return this;
	}
	
	public AllFeedReport addError(String error) {
		this.error = error;
		return this;
	}

	
	
}
