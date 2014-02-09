package com.madwin.coordinaterecorder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Calendar;


public class MainActivity extends Activity {

    LocationManager locationManager;
    LocationListener locationListener;
    File file;
    Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button kill = (Button)findViewById(R.id.kill_button);
        kill.setOnClickListener(killListener);

        Button start = (Button)findViewById(R.id.start);
        start.setOnClickListener(startListener);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

		        int current_speed = mGetSpeed(location);
                float bearing = mGetBearing(location);

                TextView display_coordinates = (TextView)findViewById(R.id.coordinate_display);
                TextView display_speed = (TextView)findViewById(R.id.current_speed);
                TextView display_bearing = (TextView)findViewById(R.id.bearing);
                display_bearing.setText(Float.toString(bearing));
                display_coordinates.setText(Double.toString(location.getLatitude()) + ", "
                        + Double.toString(location.getLongitude()));
                display_speed.setText(Integer.toString(current_speed));

                calendar = Calendar.getInstance();
                writeToFile( Double.toString(location.getLatitude()) + ", "
                        + Double.toString(location.getLongitude()) + ", " + Float.toString(bearing)
                        + ", " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE)
                        + ":" + calendar.get(Calendar.SECOND));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {



            }

            @Override
            public void onProviderEnabled(String provider) {
				
				

            }

            @Override
            public void onProviderDisabled(String provider) {

				locationManager.removeUpdates(locationListener);
				
            }
        };

        TextView save_location = (TextView)findViewById(R.id.file_location);
        save_location.setText("Data File Location : " + mGetFileLocation());


    }


    private View.OnClickListener killListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            locationManager.removeUpdates(locationListener);
            finish();

        }
    };

    private View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            mCreateFile();
            writeToFile("Latitude, Longitude, Bearing, Time");


            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    Long.parseLong(preferences
                            .getString("UpdateInterval", "0")) * 1000, 0 ,locationListener);

        }
    };


    private void writeToFile(String coordinates) {
        try{

            OutputStream fileOutputStream = new BufferedOutputStream(
                    new FileOutputStream(file, true));
            fileOutputStream.write(coordinates.getBytes());
            fileOutputStream.write(System.getProperty("line.separator").getBytes());
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void mCreateFile() {

        file = new File (mGetFileLocation(), mGetFileName());

        mGetFileLocation().mkdirs();
        try{
            if (file.createNewFile()) {
             Log.d("File", "File was Created at : " + file.getPath());
            }else{
             Log.d("File", "File was not Created!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }catch (Exception e){
            Log.d("File", "EXCEPTION CAUGHT-----File was not Created!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

    }

    public String mGetFileName() {
        calendar = Calendar.getInstance();
        int seconds = calendar.get(Calendar.SECOND);
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR);
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        String date_time = String.valueOf(year) + "-" + String.valueOf(month) + "-" +
                String.valueOf(day) + "-" + String.valueOf(hour) + "-" +  String.valueOf(minute) +
                "-" +  String.valueOf(seconds);
        return ("coordinates" + date_time + ".txt");
    }

    public File mGetFileLocation() {

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        return new File(preferences.getString("SaveLocation", Environment
                .getExternalStorageDirectory().getPath() + "/coordinate_recorder"));

    }

    public int mGetSpeed(Location location) {

        return (int) (location.getSpeed() * 2.23694);
    }

    public float mGetBearing(Location location) {
        DecimalFormat df = new DecimalFormat("##.####");

        return Float.parseFloat(df.format(location.getBearing()));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = this.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                          //  | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }


    private void mShowSettings() {
        Intent Preferences = new Intent(getBaseContext(), PreferencesUI.class);
        this.startActivity(Preferences);
        finish();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                mShowSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}




   /* private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    */
