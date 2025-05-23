package com.example.frequenciaqr.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences prefs;
    public SessionManager(Context context) {
        prefs = context.getSharedPreferences("userSession", Context.MODE_PRIVATE);
    }
    public void saveUserType(String tipo) {
        prefs.edit().putString("tipo", tipo).apply();
    }
    public String getUserType() {
        return prefs.getString("tipo", null);
    }
}
