package com.madwin.coordinaterecorder;

import android.content.Context;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;


public class MainActivity extends Activity {


    Calendar c = Calendar.getInstance();
    int seconds = c.get(Calendar.SECOND);
    int minute = c.get(Calendar.MINUTE);
    int hour = c.get(Calendar.HOUR);
    int day = c.get(Calendar.DATE);
    int month = c.get(Calendar.MONTH);
    int year = c.get(Calendar.YEAR);
    String LOG_TAG = "coordinate_recorder";
    String date_time = String.valueOf(year) + "-" + String.valueOf(month) + "-" +
            String.valueOf(day) + "-" + String.valueOf(hour) + "-" +  String.valueOf(minute) +
            "-" +  String.valueOf(seconds);
    private static final String TAG = "Coordinates";
    String file_location = "/sdcard/coordinate_recorder/";
    String filename = "coordinates" + date_time + ".txt";
    File file = new File (file_location, filename);

    double latitude_current = 0;
    double latitude_previous = 0;
    double longitude_current = 0;
    double longitude_previous = 0;
    int second_current;
    int second_previous;
    int minute_previous;
    int minute_current;
    Boolean first_entry = true;
    double speed;
    double d2r = (180 / Math.PI);

    double current_time, previous_time;

    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERRR", "Could not create file",e);
        }

        Button kill = (Button)findViewById(R.id.kill_button);
        kill.setOnClickListener(killListener);




        /*LocationManager*/ locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (first_entry){
                    latitude_current = location.getLatitude();
                    longitude_current = location.getLatitude();
                    c = Calendar.getInstance();
                    second_current = c.get(Calendar.SECOND);
                    minute_current = c.get(Calendar.MINUTE);


                }


                if (!first_entry){

                    latitude_previous = latitude_current;
                    longitude_previous = longitude_current;

                    second_previous = second_current;
                    minute_previous = minute_current;

                    c = Calendar.getInstance();
                    latitude_current = location.getLatitude();
                    longitude_current = location.getLatitude();


                    second_current = c.get(Calendar.SECOND);
                    minute_current = c.get(Calendar.MINUTE);
                }

                    first_entry = false;

                Float speed = location.getSpeed();
                Double current_speed = speed * 2.23694;

               // mCalculateSpeed();

                String coordinates = Double.toString(location.getLatitude()) + ", "
                        + Double.toString(location.getLongitude());

                Log.d(TAG, "coordinates = " + coordinates);
                TextView display_coordinates = (TextView)findViewById(R.id.coordinate_display);
                TextView display_speed = (TextView)findViewById(R.id.current_speed);
                display_coordinates.setText(coordinates);
                display_speed.setText(Double.toString(current_speed));
                writeToFile(coordinates);
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

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        //locationManager.removeGpsStatusListener((GpsStatus.Listener) locationListener);

    }

    private void mCalculateSpeed() {

       // double distance = Math.sqrt((Math.pow(((latitude_current - latitude_previous) * d2r),2)
        //        + (Math.pow(((longitude_current - longitude_previous) * d2r),2))));



        double dlong = (longitude_current - longitude_previous) * d2r;
        double dlat = (latitude_current - latitude_previous) * d2r;
        double a =
                Math.pow(Math.sin(dlat / 2.0), 2)
                        + Math.cos(latitude_previous*d2r)
                        * Math.cos(latitude_current*d2r)
                        * Math.pow(Math.sin(dlong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = 3963 * c; /// * 5280 for feet,,,,3956 for mi

        current_time = (((double) minute_current / 60) + ((double) second_current / 3600));
        previous_time = (((double) minute_previous / 60) + ((double) second_previous / 3600));

        double time = current_time - previous_time;

        speed = distance / time;

        Log.d(LOG_TAG, "Distance = " + distance);


    }

    //private View.OnClickListener startRecording;

    private View.OnClickListener killListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            locationManager.removeUpdates(locationListener);
            finish();

        }
    };

    private void writeToFile(String coordinates) {

        Log.d(TAG, "coordinates(in writeToFile) = " + coordinates);

        try{

            OutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(file, true));
            fileOutputStream.write(coordinates.getBytes());
            fileOutputStream.write(System.getProperty("line.separator").getBytes());
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }




    
}
