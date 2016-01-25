package org.acm.sviet.whatru;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
    private static final String TAG="[MainActivity]";
    ProgressDialog prgDialog = null;
    String message = null;
    SeekBar wtBar=  null;
    Spinner htSpin = null;
    double htValue = 0;
    boolean htSet = false,wtSet = false;
    FloatingActionButton fab = null;
    TextSwitcher textSwitcher = null;
    private Animation fabRollFromDownAnimation;
    private Animation fabByeAnimation;

    private Handler connectHandler = new Handler();
    private Runnable connectRun;

    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;

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

        // Get the TextSwitcher view from the layout
        textSwitcher = (TextSwitcher) findViewById(R.id.wttSwitcher);

        // BEGIN_INCLUDE(setup)
        // Set the factory used to create TextViews to switch between.
        textSwitcher.setFactory(mFactory);

        /*
         * Set the in and out animations. Using the fade_in/out animations
         * provided by the framework.
         */
        Animation in = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);
        textSwitcher.setInAnimation(in);
        textSwitcher.setOutAnimation(out);
        // END_INCLUDE(setup)

        textSwitcher.setCurrentText("55.0 Kg");

        htSpin = (Spinner) findViewById(R.id.htSpinner);
        //designated height range values to fill in the HtSpinner.
        /*String strArray[] = {"Select Height","4'0\"","4'1\"","4'2\"","4'3\"","4'4\"","4'5\"","4'6\"","4'7\"","4'8\"","4'9\"","4'10\"","4'11\""
                ,"5'0\"","5'1\"","5'2\"","5'3\"","5'4\"","5'5\"","5'6\"","5'7\"","5'8\"","5'9\"","5'10\"","5'11\""
                ,"6'0\"","6'1\"","6'2\"","6'3\"","6'4\"","6'5\"","6'6\"","6'7\"","6'8\"","6'9\"","6'10\"","6'11\""
                ,"7'0\"","7'1\"","7'2\""};*/
        String strArray[] = getResources().getStringArray(R.array.ht_values);

        List<String> HtData = new ArrayList<String>(Arrays.asList(strArray));

        ArrayAdapter<String> htAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.layout_ht_spinner,R.id.layout_ht_spinner_textView,HtData);
        htSpin.setAdapter(htAdapter);
        //TODO below onItemSelectListener is provoked as the view is started, why?
        htSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                htValue = findHtBySpinnerPosition(position);
                if(position>0){
                    htSet = true;
                }
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
                textSwitcher.setText(wt+" Kg");
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

        //loading the FAB Roll from Down Animation below. used at the value checking function.
        fabRollFromDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_animator);
        fabByeAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_bye_animator);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setEnabled(false); //making the initial disabled state of FAB button.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "FAB button clicked with all inputs properly selected. moving for further HTTPRequest.");
                if (checkNetworkConnection(view)) {
                    fab.startAnimation(fabByeAnimation);

                    //fab.hide();
                    connectRun = new Runnable() {
                        @Override
                        public void run() {
                            fab.setVisibility(View.GONE);
                            connect(htValue, findWtBySeekBarProgress(wtBar.getProgress()));

                        }
                    };
                    connectHandler.postDelayed(connectRun,600);

                }
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

        Toast.makeText(getApplicationContext(),"Select Height & Weight",Toast.LENGTH_SHORT).show();


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
            shareOnFb(getResources().getString(R.string.API_home_page_url));
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
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/anuragkumarak95/GenPredict-Project/"));
            startActivity(intent);
        } else if (id == R.id.nav_api) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://whatru-svietacm.rhcloud.com"));
            startActivity(intent);
        } else if (id == R.id.nav_about) {

            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://"+getResources().getString(R.string.web_add)));
            startActivity(intent);
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

        RequestParams requestParams = new RequestParams();
        requestParams.add("ht",Double.toString(ht));
        requestParams.add("wt",Double.toString(wt));

        client.get(getResources().getString(R.string.predictor_base_url), requestParams, new AsyncHttpResponseHandler() {


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
                fab.setVisibility(View.VISIBLE);
                fab.startAnimation(fabRollFromDownAnimation);
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
        bundle.putDouble("wt", wt);
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
            if (htSet==true && wtSet==true) {
                fab.show();
                fab.startAnimation(fabRollFromDownAnimation);

                fab.setEnabled(true);

                Log.d(TAG, "FAB button functionality enabled.");
            } else {
                fab.hide();
                fab.setEnabled(false);

            }
        }
    }

    //function: Share the API home page on FB (Intent).
    private void shareOnFb(String urlToShare){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        // intent.putExtra(Intent.EXTRA_SUBJECT, "Foo bar"); // NB: has no effect!
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);

        // See if official Facebook app is found
        boolean facebookAppFound = false;
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                intent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }

        // As fallback, launch sharer.php in a browser
        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }

        startActivity(intent);
    }


    /**
     * Check whether the device is connected, and if so, whether the connection
     * is wifi or mobile (it could be something else).
     */
    private boolean checkNetworkConnection(View view) {
        // BEGIN_INCLUDE(connect)
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if(wifiConnected) {
                Log.i(TAG, getString(R.string.wifi_connection));
            } else if (mobileConnected){
                Log.i(TAG, getString(R.string.mobile_connection));
            }
            return true;
        } else {
            Log.i(TAG, getString(R.string.no_wifi_or_mobile));
            Snackbar.make(view,getString(R.string.no_wifi_or_mobile),Snackbar.LENGTH_LONG).show();
            return false;
        }
        // END_INCLUDE(connect)
    }

    // BEGIN_INCLUDE(factory)
    /**
     * The {@link android.widget.ViewSwitcher.ViewFactory} used to create {@link android.widget.TextView}s that the
     * {@link android.widget.TextSwitcher} will switch between.
     */
    private ViewSwitcher.ViewFactory mFactory = new ViewSwitcher.ViewFactory() {

        @Override
        public View makeView() {

            // Create a new TextView
            TextView wtTextView = new TextView(MainActivity.this);
            wtTextView.setGravity(Gravity.CENTER);
            wtTextView.setPadding(0,50,10,0);
            wtTextView.setTextAppearance(MainActivity.this, android.R.style.TextAppearance_Small);
            return wtTextView;
        }
    };
    // END_INCLUDE(factory)
}


