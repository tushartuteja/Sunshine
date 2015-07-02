package com.example.tushartuteja.sunshine.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by tushartuteja on 01/07/15.
 */
public class Utility {
    public static String getPreferredLocation(Context context){
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
    String location = pref.getString(context.getString(R.string.pref_location_key), context.getString(R.string.pref_location_default));
    return location;
}
}
