package br.com.chrezende.messengersocketclient.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferencesHelper(Context mContext, String prefKey) {
        sharedPreferences = mContext.getSharedPreferences(prefKey, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setParam(String prefText, String param) {
        editor.putString(prefText, param);
        editor.apply();
    }

    public String getStringParam(String prefString) {
        String response = sharedPreferences.getString(prefString, "");
        return response;
    }

    public void remove(String key) {
        editor.remove(key);
        editor.apply();
    }
}
