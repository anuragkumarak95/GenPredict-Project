package org.acm.sviet.whatru;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
/*TODO list:
*  -> finding way to implicitly intent a browser for drawer components.
*  -> Place a Weight current Value indicator and fill it. *done*
*  -> find a way to indicate that fab is not enabled while ht and wt values are not established by the user.
* */
    private final String TAG="[MainActivity]";
    ProgressDialog prgDialog = null;
    String message = null;
    SeekBar wtBar=  null;
    Spinner htSpin = null;
    double htValue = 0;
    TextView textViewWTIndicator = null;
    boolean htSet = false,wtSet = false;
    FloatingActionButton fab = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("well let me think a bit...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Assigning needful assets.
        textViewWTIndicator = (TextView) findViewById(R.id.textViewWTIndicator);

        htSpin = (Spinner) findViewById(R.id.htSpinner);
        //designated height range values to fill in the HtSpinner.
        String strArray[] = {"Select Height","4'0\"","4'1\"","4'2\"","4'3\"","4'4\"","4'5\"","4'6\"","4'7\"","4'8\"","4'9\"","4'10\"","4'11\""
                ,"5'0\"","5'1\"","5'2\"","5'3\"","5'4\"","5'5\"","5'6\"","5'7\"","5'8\"","5'9\"","5'10\"","5'11\""
                ,"6'0\"","6'1\"","6'2\"","6'3\"","6'4\"","6'5\"","6'6\"","6'7\"","6'8\"","6'9\"","6'10\"","6'11\""
                ,"7'0\"","7'1\"","7'2\""};

        List<String> HtData = new ArrayList<String>(Arrays.asList(strArray));

        ArrayAdapter<String> htAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.layout_ht_spinner,R.id.layout_ht_spinner_textView,HtData);
        htSpin.setAdapter(htAdapter);
        htSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                htValue = findHtBySpinnerPosition(position);
                htSet = true;
                checkValuesSelectedState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        wtBar = (SeekBar) findViewById(R.id.wtSeek);
        wtBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double wt = findWtBySeekBarProgress(progress);
                textViewWTIndicator.setText(wt+" Kg");
                wtSet = true;
                checkValuesSelectedState();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //do nothing.
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //do nothing.
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setEnabled(false); //making the initial disabled state of FAB button.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "FAB button clicked with all inputs properly selected. moving for further HTTPRequest.");
                //connect(htValue, findWtBySeekBarProgress(wtBar.getProgress()));
/*                Snackbar.make(view,"Height value Obtained : "+htValue+" || Weight Value Obtained : "+findWtBySeekBarProgress(wtBar.getProgress()),Snackbar.LENGTH_INDEFINITE)
                        .setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Do nothing.
                            }
                        }).show();*/
                passOn("M", htValue, findWtBySeekBarProgress(wtBar.getProgress()), 0.75);
                            }
        });
        checkValuesSelectedState();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(),"Settings Menu Item Selected.",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cluster) {
            Toast.makeText(getApplicationContext(),"Cluster Drawer Item Selected.",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_gitfork) {
            Toast.makeText(getApplicationContext(),"GitFork Drawer Item Selected.",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_api) {
            Toast.makeText(getApplicationContext(),"API Link Drawer Item Selected.",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_about) {
            Toast.makeText(getApplicationContext(),"Developer site Drawer Item Selected.",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Intent.CATEGORY_APP_BROWSER);
            Uri uri =  Uri.parse("http://www.google.com").buildUpon().build();
            intent.setData(uri);

            if(intent.resolveActivity(getPackageManager())!=null){
                startActivity(intent);
            }else{
                Log.e(TAG,"implicit intent resolving failure.");//TODO the intent is not resolving. find out how to start an implicit browser intent.
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //function : connect to genPredict-webapp WebService and forward the ht/wt values to gather the predicted gender response bundle and redirect bundle to resultActivity.
    private void connect(double ht,double wt){
        prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(getResources().getString(R.string.predictor_base_url) + ht + "/" + wt, null, new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.v(TAG, "Successfull Response Gathering.");
                prgDialog.hide();
                try {
                    String response = new String(responseBody, "UTF-8");
                    JSONObject obj = new JSONObject(response);


                    message = "gender - " + obj.getString("gender") + " : ht - " + obj.getDouble("ht") + " : wt - " + obj.getDouble("wt");
                    Log.d(TAG, message);//data set log

                    passOn(obj.getString("gender"), obj.getDouble("ht"), obj.getDouble("wt"), obj.getDouble("accuracy"));

                } catch (UnsupportedEncodingException e) {
                    message = "default";
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

    //function: create bundle with appropriate set of data and advance to ResutActivity.
    private void passOn(String gender,Double ht,Double wt,Double accuracy){
        //final bundle filling and activity redirection below.
        Bundle bundle = new Bundle();
        bundle.putString("hi","hi there fellows...!!");
        bundle.putString("gender",gender);
        bundle.putDouble("ht",ht);
        bundle.putDouble("wt",wt);
        bundle.putDouble("accuracy", accuracy);
        Intent intent = new Intent(MainActivity.this,ResultActivity.class);
        intent.putExtras(bundle);
        //bye bye
        startActivity(intent);
        finish();
    }
    //function: get the designated height value according to position of selected spinner value.
    private double findHtBySpinnerPosition(int position){
        double baseFoot=4,baseInches= 0;

        position--;
        baseInches = 0.01*(position%12);
        baseFoot = baseFoot+position/12;

        return baseFoot+baseInches;
    }

    //function: get the designated weight value according to progressof WtSpinner.
    private double findWtBySeekBarProgress(int progress){
        return 30+progress;
    }

    private void checkValuesSelectedState(){
        if(!fab.isEnabled()) {
            if (htSet == true && wtSet==true) {
                fab.setEnabled(true);

                Log.d(TAG, "FAB button functionality enabled.");
            } else {
                fab.setEnabled(false);


            }
        }
    }

}
