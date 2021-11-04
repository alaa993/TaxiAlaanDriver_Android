package com.taxialaan.drivers.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedHelper {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static void putKey(Context context, String Key, String Value) {
        sharedPreferences = context.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(Key, Value);
        editor.apply();

    }

    public static String getKey(Context contextGetKey, String Key) {
        sharedPreferences = contextGetKey.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        return sharedPreferences.getString(Key, "");

    }


    public static void clearSharedPreferences(Context context)
    {
        sharedPreferences = context.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

    }



    public static void putKeyDeviceToken(Context context, String Key, String Value) {
        sharedPreferences = context.getSharedPreferences("device", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(Key, Value);
        editor.apply();

    }

    public static String getKeyDeviceToken(Context contextGetKey, String Key) {
        sharedPreferences = contextGetKey.getSharedPreferences("device", Context.MODE_PRIVATE);
        return sharedPreferences.getString(Key, "");

    }


}
