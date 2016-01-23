package Sviet.ACM.genPredict.modelServices;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Sviet.ACM.genPredict.Models.FeedReport;
import Sviet.ACM.genPredict.Models.GenUsers;
import Sviet.ACM.genPredict.Models.Prediction;

@Service
public class Predictor {

	/*
	 * Purpose : for predicting the gender of a user using the inputs including users height and weight,
	 * 			 by applying machine learning algorithms on the available data set of user informations.
	 * 
	 * Author  : Anurag Kumar , anuragkumarak95@gmail.com
	 * Date    : 14-01-2016 
	 * 
	 * -A-K-
	 * 
	 */
	
	@Autowired
	private GenService genService;
	
	@Transactional
	public Prediction predictGender(double ht,double wt) {

		if(ht<4.0||ht>7.2||wt<30||wt>130) {
			return new Prediction().addStatus("STATUS FAILED").addError("Height range is : 4.0 - 7.2 & Weight range is 30 - 130");
		}else {
		ArrayList<GenUsers> male = (ArrayList<GenUsers>) this.genService.getAllMale();
		ArrayList<GenUsers> female = (ArrayList<GenUsers>) this.genService.getAllFemale();
		
		
		Point male_mean = findMeanByArrayList(male);
		Point female_mean = findMeanByArrayList(female);
		Point current = new Point(ht,wt);
		
		
		
		return new Prediction()	//return the finally assumed prediction package.
		.addHt(ht)				//fill the height for further reference.
		.addWt(wt)				//fill the weight for further reference.
		.addGender(
				(dist(male_mean, current)<dist(female_mean, current))?"M":"F"
			)					//fill the predicted gender here.
		.addAccuracy(0.75)   	//fill the accuracy percentage here.
		.addStatus("STATUS OK");//fill OK complete status
		}
		
		
	}
	
    //function: finding distance between two 2-D points.
	private double dist(Point a,Point b)//to find distance between the new entry to predict and the male or female cluster mean positions.
    { double d;
      d=Math.sqrt( Math.pow((a.ht-b.ht),2) + Math.pow((a.wt-b.wt),2));
      return d;
    }
	
	//function: find Mean 2-D Point of Ht/Wt values by consuming an Array List of GenUsers.
	private Point findMeanByArrayList(ArrayList<GenUsers> ulist) {
		
        int count = ulist.size();
        double ht_sum=0,wt_sum=0;
		
		//code: find the mean male point and mean female point
		for(int i=0;i<count;i++) {
			ht_sum= ht_sum + ulist.get(i).getHt();
			wt_sum= wt_sum + ulist.get(i).getWt();
			
		}
		
		double ht_mean = ht_sum/count;
		double wt_mean = wt_sum/count;
		return new Point(ht_mean,wt_mean);
	}
	
	//class: for ease of use as understanding the mean values of ht/wt objects as a point on a 2-D graph.
	private class Point{
		double ht,wt;
		public Point(double ht,double wt) {
			this.ht = ht;
			this.wt = wt;
		}
		
	}
	
}

