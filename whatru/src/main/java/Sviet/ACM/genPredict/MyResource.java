package Sviet.ACM.genPredict;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Sviet.ACM.genPredict.Models.AllFeedReport;
import Sviet.ACM.genPredict.Models.FeedReport;
import Sviet.ACM.genPredict.Models.GenUsers;
import Sviet.ACM.genPredict.Models.Prediction;
import Sviet.ACM.genPredict.modelServices.GenService;
import Sviet.ACM.genPredict.modelServices.Predictor;


/** Service resource class hosted at the URI path "/myresource"
 */

@RestController
public class MyResource {
	
	private String FLUSH = "1593";
	
	@Autowired private GenService genService;
 	
 	@Autowired private Predictor predictor;
	
	//service: json response of the back-end data set available to the application.
 	@RequestMapping(value="/API/feeds",method = RequestMethod.GET,produces= MediaType.APPLICATION_JSON_VALUE)
	 public AllFeedReport getAllFeeds() {
    	ArrayList<GenUsers> allUsers = (ArrayList<GenUsers>)genService.getAllFeeds();
    	if(allUsers!=null) {
    	return new AllFeedReport().addCount(allUsers.size())
    			.addStatus("STATUS OK").addGenUsers(allUsers);
    	}else {
    		return new AllFeedReport().addStatus("STATUS FAILED").addError("Data fetch returned zero elements.");
    	}
    	    }
    
    //service: predicting the gender of gathered Ht/Wt data.
    @RequestMapping(value="/API/predictor",method = RequestMethod.GET,produces= MediaType.APPLICATION_JSON_VALUE)
    public Prediction predictGender(@RequestParam("ht") double ht,@RequestParam("wt") double wt) {
    	return  predictor.predictGender(ht, wt);
    }
    
    //service: for adding new data to our data sets.
    @RequestMapping(value="/API/feed",method = RequestMethod.POST,produces= MediaType.APPLICATION_JSON_VALUE)
    public FeedReport dataUploading(@RequestParam("gender") String gender,
    		@RequestParam("ht") double ht,
    		@RequestParam("wt") double wt) {
     	
      return genService.feedUser(gender, ht, wt);
    }



	@RequestMapping(value="/API/flush/{flushCode}",method = RequestMethod.DELETE,produces= MediaType.TEXT_PLAIN_VALUE)
    public String AllFlushProtocol(@PathVariable("flushCode") String flushCode) {
    	if(flushCode.equals(FLUSH)) {return "Flush Data Set Complete. DB is clean and Healthy.";}
    	else {return "Flush Data Set Failed. Incorrect CODE input.";}
    	
    }
    
}

