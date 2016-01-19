package Sviet.ACM.genPredict.modelServices;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		Prediction prediction = new Prediction();
		
		double male_ht_sum=0,male_wt_sum=0;
		double female_ht_sum=0,female_wt_sum=0;
		
		ArrayList<GenUsers> male = (ArrayList<GenUsers>) this.genService.getAllMale();
		ArrayList<GenUsers> female = (ArrayList<GenUsers>) this.genService.getAllFemale();
		
		int mcount = male.size(),fcount = female.size();
		
		// TODO add the algorithm for K-Mapping the given values to predict the gender on a ht-wt graph for gender values.
		// also add the assumption accuracy percentage.
		
		/*
		 * Question:
		 * -> finding mean point of a given set of 2-d points. *done*
		 * -> find distance between 2 given 2-D points.
		 */
		for(int i=0;i<mcount;i++) {
			male_ht_sum= male_ht_sum + male.get(i).getHt();
			male_wt_sum= male_wt_sum + male.get(i).getWt();
			
		}
		for(int i=0;i<fcount;i++) {
			female_ht_sum= female_ht_sum + female.get(i).getHt();
			female_wt_sum= female_wt_sum + female.get(i).getWt();
			
		}
		
		
		double male_ht_mean = male_ht_sum/mcount;
		double male_wt_mean = male_wt_sum/mcount;
		double female_ht_mean = female_ht_sum/fcount;
		double female_wt_mean = female_wt_sum/fcount;
		//code: distance between mean male point & provided point , same for female. compare distance.
		
		
		prediction.setHtChain(ht)// fill the height for further reference.
		.setWtChain(wt)			// fill the weight for further reference.
		.setGenderChain("M")		// fill the predicted gender here.
		.setAccuracyChain(0.75);	// fill the accuracy percentage here.
		
		return prediction;// return the finally assumed prediction package.
		
	}
	
}

