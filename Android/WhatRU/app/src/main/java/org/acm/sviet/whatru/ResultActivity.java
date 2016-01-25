package org.acm.sviet.whatru;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

public class ResultActivity extends AppCompatActivity {
    /*TODO list:
    * -> Replace Gender TextView with an imageView and appropriately assign gender images to it.
    * */

    private ImageView imageViewgender;
    private TextView textViewtrue;
    private TextView textViewfalse;
    private String curr_gender;
    private InputStream imageInput;
    private final String TAG = "[ResultActivity]";

    private ProgressDialog prgDialog=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("I'll remember this forever...");
        prgDialog.setCancelable(false);

        imageViewgender = (ImageView) findViewById(R.id.imageViewgender);
        textViewtrue =(TextView) findViewById(R.id.textViewtrue);
        textViewfalse = (TextView) findViewById(R.id.textViewfalse);




        final Bundle bundle = getIntent().getExtras();
        curr_gender = bundle.getString("gender");

        View view = findViewById(R.id.resultActlinearLayout);


        try {
            TFbackgroundFill(curr_gender); // used for dynamically coloring the T/F buttons according to current gender value provided.
        } catch (IOException e) {
            e.printStackTrace();
        }

        Snackbar.make(view,"This App is on it's Learning phase and can predict wrong value.",Snackbar.LENGTH_INDEFINITE)
                .setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),"Was the prediction true or false?",Toast.LENGTH_LONG).show();
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.male_color)).show();

        /*Toast.makeText(getApplicationContext(),curr_gender
                +" || ht : "+bundle.getDouble("ht")+" || wt : "+bundle.getDouble("wt")
                +" || accuracy : "+bundle.getDouble("accuracy"),Toast.LENGTH_LONG).show();
*/

        //click events below..
        textViewtrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                Add code for uploading the gathered data for data set for true prediction. true condition.
                 */
                Log.v(TAG, "The Prediction result is declared true by user, uploading for further data set expansion.");
                uploadBundle(bundle, true);
            }
        });

        textViewfalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                Add code for uploading the gathered data with reversed gender value for data set for false prediction. false condition.
                 */
                Log.v(TAG,"The Prediction result is declared false by user, uploading for further data set expansion.");
                uploadBundle(bundle, false);
            }
        });
    }


    //function : true false scenario shift color cording to the gender.
    private void TFbackgroundFill(String curr_gender) throws IOException {
        if(curr_gender.equals("M")){
            Log.v(TAG,"Male gender value detected.");
            imageInput = getAssets().open("images/male_symbol.png");
            textViewtrue.setBackgroundColor(getResources().getColor(R.color.male_color));
            textViewfalse.setBackgroundColor(getResources().getColor(R.color.female_color));
            // code the true scenario where male gender is predicted
        }else if(curr_gender.equals("F")){
            Log.v(TAG,"Female gender value detected.");
            imageInput = getAssets().open("images/female_symbol.png");
            textViewtrue.setBackgroundColor(getResources().getColor(R.color.female_color));
            textViewfalse.setBackgroundColor(getResources().getColor(R.color.male_color));
            // code the true scenario where female gender is predicted
        }
        Bitmap imageBitmap = BitmapFactory.decodeStream(imageInput);
        imageViewgender.setImageBitmap(imageBitmap);
    }

    //function : upload the gathers value for both true and false prediction scenarios for expanding the data set.
    private void uploadBundle(Bundle bundle,boolean bool){
        String gender = bundle.getString("gender");
        Double ht = bundle.getDouble("ht");
        Double wt = bundle.getDouble("wt");

        if(bool==true){
            //the data set as it is.
        }
        else{
            //reversed gender value.
            gender = sexChange(gender);
        }

        //code for uploading the data set.
        feedData(gender,ht,wt);
    }

    //function : reverse the input gender value
    private String sexChange(String gender){
        if (gender.equals("M") ) {
            gender = "F";

        } else if (gender.equals("F")) {
            gender = "M";

        }
        return  gender;
            }


    //function: Using AsyncHttpClient POST Request, here the gathered bundle data is uploaded to the API data set.
    private void feedData(String gender,double ht,double wt){
        prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams requestParams = new RequestParams();
        requestParams.add("gender",gender);
        requestParams.add("ht",Double.toString(ht));
        requestParams.add("wt",Double.toString(wt));


        client.post(getResources().getString(R.string.feedData_base_url), requestParams, new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.v(TAG, "Successfull Response Gathering.");
                prgDialog.hide();
                try {
                    String response = new String(responseBody, "UTF-8");
                    JSONObject obj = new JSONObject(response);
                    String err = obj.getString("error");
                    String status = obj.getString("status");
                    if (status.equals("DATA UPLOAD HEALTHY OK")) {
                        Log.d(TAG,"Upload Success Report : "+status);
                        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Server Error : "+err,Toast.LENGTH_LONG).show();
                    }

                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.v(TAG, "Unsuccessful Response Gathering.");

                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occurred! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}
