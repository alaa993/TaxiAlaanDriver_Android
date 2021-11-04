package com.taxialaan.drivers.Fragment;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.taxialaan.drivers.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.taxialaan.drivers.Utilities.Utils.getString;

public class BaseMap {


    public String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    public String getDate(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String dateName = new SimpleDateFormat("dd").format(cal.getTime());
        return dateName;
    }

    public String getYear(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
        return yearName;
    }

    public String getTime(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String timeName = new SimpleDateFormat("hh:mm a").format(cal.getTime());
        return timeName;
    }


    public String getDirectionsUrl(LatLng sourceLatLng, LatLng destLatLng) {

        if (sourceLatLng == null || destLatLng == null) {
            return "";
        }

        // Origin of routelng;
        String str_origin = "origin=" + sourceLatLng.latitude + "," + sourceLatLng.longitude;
        String str_dest = "destination=" + destLatLng.latitude + "," + destLatLng.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        // Waypoints
        String key = getString(R.string.google_map_api);// G.getInstance().getString(R.string.google_map_api);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=" + key;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.e("url", url);
        return url;

    }

}
