package Sviet.ACM.genPredict.webService;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import Sviet.ACM.genPredict.Models.FeedReport;
import Sviet.ACM.genPredict.Models.GenUsers;
import Sviet.ACM.genPredict.Models.Prediction;
import Sviet.ACM.genPredict.modelServices.GenService;
import Sviet.ACM.genPredict.modelServices.Predictor;


/** Example resource class hosted at the URI path "/myresource"
 */
@Path("/myresource")
public class MyResource {
	AbstractApplicationContext mycontext = new ClassPathXmlApplicationContext("/../spring/appServlet/servlet-context.xml");
 	
    
    /** Method processing HTTP GET requests, producing "text/plain" MIME media
     * type.
     * @return String that will be send back as a response of type "text/plain".
     */
    @GET 
    @Produces("application/json")
    public ArrayList<GenUsers> getIt() {
    	
    	GenService genService = (GenService) mycontext.getBean("genService");
    	
     	genService.feedUser("F",6, 75);
    	
    	return (ArrayList<GenUsers>)genService.getAllFeeds();
    	
    }
    
    @GET
    @Path("/predictor/{ht}/{wt}")
    @Produces("application/json")
    public Prediction predictGender(@PathParam("ht") double ht,@PathParam("wt") double wt) {
    	Predictor predictor = (Predictor) mycontext.getBean("predictor");
    	
    	Prediction pr = predictor.predictGender(ht, wt);
    	
    	
    	return pr;
    }
    
    @GET
    @Path("/predictor")
    @Produces("application/json")
    public Prediction predictGenderDemo() {
    	Predictor predictor = (Predictor) mycontext.getBean("predictor");
    	
    	Prediction pr = predictor.predictGender(5.8, 85);
    	
    	
    	return pr;
    }
    
    
    //for adding new data to our data sets.
    @POST
    @Path("/feed/{gender}/{ht}/{wt}")
    @Produces("application/json")
    public FeedReport predictionfeeding(@PathParam("gender") String gender,
    		@PathParam("ht") double ht,
    		@PathParam("wt") double wt) {
      	GenService genService = (GenService) mycontext.getBean("genService");
     	
      GenUsers user = genService.feedUser(gender, ht, wt);
    	FeedReport report = new FeedReport();
    	report.setStatus("Data Unit Upload Successful and Healthy.");
    	return report;
    }
}

