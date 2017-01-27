package com.bignerdranch.android.splash;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.bignerdranch.android.splash.R.id.map;

/**
 * Created by Lorena on 28/12/2016.
 */

public class Maps_Activity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private KmlLayer layer;
    protected Location mLastLocation;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    private LocationManager locationManager;
    private LocationListener locationListener;
    Marker lastOpenned = null;
    static Map<String, Integer> alphabet = new HashMap<>();
    static Map<String, Integer> alphabet_scores = new HashMap<>();
    static int score = 49;
    static int totalScore = 456;

    final private int REQUEST_LOCATION = 123;

    GoogleApiClient mGoogleApiClient;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    Dialog dialog;

    private void showDialog() {
        // custom dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);

        alphabet.put("E", alphabet.get("E") + 1);

        // set the custom dialog components - text, image and button
        //ImageButton close = (ImageButton) dialog.findViewById(R.id.btnClose);

        ImageView backPreConfirmation = (ImageView) dialog.findViewById(R.id.dailyRewardImage);
        backPreConfirmation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }
        });
//
//        // Close Button
//        close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                //TODO Close button action
//            }
//        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            return;
        }
        getLocation();//if already has permission


        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

//        SharedPreferences sharedPref2 = getSharedPreferences("Map", Context.MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = sharedPref2.getString(getResources().getString(R.string.alphabet_shared_preference),"");
//
//
//        alphabet = gson.fromJson(json,alphabet.getClass());
        try {
            FileInputStream fileInputStream = new FileInputStream(this.getBaseContext().getFilesDir() + "/hashmap.ser");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            alphabet = (HashMap) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
            return;
        }

        //System.out.println("Deserialized HashMap..");
        // Display content using Iterator
        Set set = alphabet.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();
            Log.d("Logging: ", "key: " + mentry.getKey() + " & Value: ");
            Log.d("Logging: ", "" + mentry.getValue());
        }

//        if(alphabet==null){
//            alphabet = new HashMap<>();
//            populateHashMap(alphabet);
//        }

//        Intent intent = new Intent(Maps_Activity.this, DailyReward.class);
//        Maps_Activity.this.startActivity(intent);

        Calendar c = Calendar.getInstance();
        int thisDay = c.get(Calendar.DAY_OF_YEAR); //You can chose something else to compare too, such as DATE..
        long todayMillis = c.getTimeInMillis(); //We might need this a bit later.

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        long last = prefs.getLong("date", 0); //If we don't have a saved value, use 0.
        c.setTimeInMillis(last);
        int lastDay = c.get(Calendar.DAY_OF_YEAR);

        if (last == 0 || lastDay != thisDay) {
            //New day, we offer the daily reward
            showDialog();
            SharedPreferences.Editor edit = prefs.edit();
            edit.putLong("date", todayMillis);
            edit.commit();
        }

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.d("Debug Tag : ", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.d("Debug Tag : ", "Can't find style. Error: ", e);
        }

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setScrollGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setZoomGesturesEnabled(false);
        uiSettings.setMyLocationButtonEnabled(true);
        //mMap.setMinZoomPreference(20.0f);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 20));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(20)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(60)                   // Sets the tilt of the camera to 60 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        //Prevent re-centering of the map when user clicks on markers
        // Since we are consuming the event this is necessary to
        // manage closing openned markers before openning new ones

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                // Check if there is an open info window
                if (lastOpenned != null) {
                    // Close the info window
                    lastOpenned.hideInfoWindow();

                    // Is the marker the same marker that was already open
                    if (lastOpenned.equals(marker)) {
                        // Nullify the lastOpenned object
                        lastOpenned = null;
                        // Return so that the info window isn't openned again
                        return true;
                    }
                }

                // Open the info window for the marker
                marker.showInfoWindow();
                // Re-assign the last openned such that we can close it later
                lastOpenned = marker;

                // Event was handled by our code do not launch default behaviour.
                return true;
            }
        });
        // End of re-centering tweaks

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                uiSettings.setMyLocationButtonEnabled(true);
                uiSettings.setCompassEnabled(false);

            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            uiSettings.setMyLocationButtonEnabled(true);
            uiSettings.setCompassEnabled(false);
        }


        ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            String urlink = fetchUrl();
            new DownloadURLTask().execute(urlink);
        } else {
            Log.d("No connection", "No connection available");
        }

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: LINK FAB TO NEW TABBED ACTIVITY
                Intent intent = new Intent(Maps_Activity.this, Tabbed_Activity.class);
                Maps_Activity.this.startActivity(intent);
            }
        });

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public String fetchUrl() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String file = "";

        switch (day) {
            case Calendar.SUNDAY:
                file = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/sunday.kml";
                break;
            case Calendar.MONDAY:
                file = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/monday.kml";
                break;
            case Calendar.TUESDAY:
                file = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/tuesday.kml";
                break;
            case Calendar.WEDNESDAY:
                file = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/wednesday.kml";
                break;
            case Calendar.THURSDAY:
                file = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/thursday.kml";
                break;
            case Calendar.FRIDAY:
                file = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/friday.kml";
                break;
            case Calendar.SATURDAY:
                file = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/saturday.kml";
                break;
            default:
                break;
        }
        return file;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Accepted
                    getLocation();
                } else {
                    // Denied
                    Toast.makeText(Maps_Activity.this, "LOCATION Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void handlePermissionsAndGetLocation() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            return;
        }
        getLocation();//if already has permission
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void getLocation() {
        int LOCATION_REFRESH_TIME = 5000;
        int LOCATION_REFRESH_DISTANCE = 5;

        if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Log.d("WEAVER_", "Has permission");
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);
        } else {
            Log.d("WEAVER_", "Does not have permission");
        }

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("WEAVER_", "Location Change");
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 20);
            mMap.animateCamera(cameraUpdate);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(this);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class DownloadURLTask extends AsyncTask<String, Void, List<KMLParser.Placemark>> {
        private static final String DEBUG_TAG = "Downloading URL Task";


        @Override
        protected List<KMLParser.Placemark> doInBackground(String... params) {
            try {
                return loadXmlFromNetwork(params[0]);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private List<KMLParser.Placemark> loadXmlFromNetwork(String url) throws
                XmlPullParserException, IOException {
            InputStream is = null;
            KMLParser kmlParser = new KMLParser();

            List<KMLParser.Placemark> placemarks = null;

            try {
                is = downloadUrl(url);
                placemarks = kmlParser.parse(is);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (is != null) {
                    is.close();
                }
            }
            return placemarks;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(List<KMLParser.Placemark> result) {
            Log.d(DEBUG_TAG, "inside onPostExecute");

            ArrayList<Location> locs = new ArrayList<>();
            final ArrayList<Marker> markers = new ArrayList<>();


            for (final KMLParser.Placemark mark : result) {
                String name = mark.name;
                String description = mark.description;
                PointF p = mark.point;

                final Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(p.x, p.y))
                        .title(description));
                markers.add(marker);

                populateHashMap(alphabet);
                populateHashMapScores(alphabet_scores);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker arg0) {
                        alphabet.put(arg0.getTitle(), alphabet.get(arg0.getTitle())+1);
                        arg0.remove();
                        Log.d(DEBUG_TAG, "marker has been removed from the map and added to alphabet string");
                        Log.d(DEBUG_TAG, arg0.getTitle() + " was added to the alphabet array");
                    }
                });
            }
        }
    }

    public void populateHashMap (Map alphabet){
        alphabet.put("A", 0);
        alphabet.put("B", 0);
        alphabet.put("C", 0);
        alphabet.put("D", 0);
        alphabet.put("E", 0);
        alphabet.put("F", 0);
        alphabet.put("G", 0);
        alphabet.put("H", 0);
        alphabet.put("I", 0);
        alphabet.put("J", 0);
        alphabet.put("K", 0);
        alphabet.put("L", 0);
        alphabet.put("M", 0);
        alphabet.put("N", 0);
        alphabet.put("O", 0);
        alphabet.put("P", 0);
        alphabet.put("Q", 0);
        alphabet.put("R", 0);
        alphabet.put("S", 0);
        alphabet.put("T", 0);
        alphabet.put("U", 0);
        alphabet.put("V", 0);
        alphabet.put("W", 0);
        alphabet.put("X", 0);
        alphabet.put("Y", 0);
        alphabet.put("Z", 0);
    }

    public void populateHashMapScores (Map alphabet){
        alphabet.put("A", 3);
        alphabet.put("B", 20);
        alphabet.put("C", 13);
        alphabet.put("D", 10);
        alphabet.put("E", 1);
        alphabet.put("F", 15);
        alphabet.put("G", 18);
        alphabet.put("H", 9);
        alphabet.put("I", 5);
        alphabet.put("J", 25);
        alphabet.put("K", 22);
        alphabet.put("L", 11);
        alphabet.put("M", 14);
        alphabet.put("N", 6);
        alphabet.put("O", 4);
        alphabet.put("P", 19);
        alphabet.put("Q", 24);
        alphabet.put("R", 8);
        alphabet.put("S", 7);
        alphabet.put("T", 2);
        alphabet.put("U", 12);
        alphabet.put("V", 21);
        alphabet.put("W", 17);
        alphabet.put("X", 23);
        alphabet.put("Y", 16);
        alphabet.put("Z", 26);
    }

    private InputStream downloadUrl(String myurl) throws IOException {

            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            return conn.getInputStream();
    }

    // Reads an InputStream and converts it to a String .
//    public String readIt (InputStream stream) throws IOException, UnsupportedEncodingException {
//        StringBuffer data = new StringBuffer("");
//        try {
//            InputStreamReader is = new InputStreamReader ( stream ) ;
//            BufferedReader buffreader = new BufferedReader( is ) ;
//
//            String readString = buffreader.readLine ( ) ;
//            while ( readString != null ) {
//                data.append(readString + "\n");
//                readString = buffreader.readLine ( ) ;
//            }
//
//            is.close () ;
//        } catch ( IOException ioe ) {
//            ioe.printStackTrace () ;
//        }
//        return data.toString() ;
//    }
}
