package com.mcuadrada.proyectointegrador.Patterns;

import android.content.Context;
import android.content.SharedPreferences;

import com.mcuadrada.proyectointegrador.Classes.User;

public class SingletonPreferences {

    private static SingletonPreferences mInstance;
    private static Context mContext;
    private static final String LOGIN_PREFERENCES = "login_pref";
    private static final String[] USER_DATA = {
            "username", "email", "session_token", "last_time"
    };

    public static SingletonPreferences getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SingletonPreferences(context);
        }
        return mInstance;
    }

    private SingletonPreferences(Context context) {
        mContext = context;
    }

    private static SharedPreferences getSharedPreferences(String prefs, Context context) {
        return context.getSharedPreferences(prefs,
                Context.MODE_PRIVATE);
    }

    public User getLoginData() {
        User user = new User();
        SharedPreferences prefs = getSharedPreferences(LOGIN_PREFERENCES, mContext);
        user.setUsername(prefs.getString(USER_DATA[0], ""));
        user.setEmail(prefs.getString(USER_DATA[1], ""));
        user.setSession_token(prefs.getString(USER_DATA[2], ""));
        user.setLast_time(prefs.getString(USER_DATA[3], ""));
        return user;
    }

    public void setLoginData(User user) {
        SharedPreferences.Editor editor = getSharedPreferences(LOGIN_PREFERENCES, mContext).edit();
        editor.putString(USER_DATA[0], user.getUsername());
        editor.putString(USER_DATA[1], user.getEmail());
        editor.putString(USER_DATA[2], user.getSession_token());
        editor.putString(USER_DATA[3], user.getLast_time());
        editor.apply();
    }

    public void clearLoginData() {
        SharedPreferences.Editor editor = getSharedPreferences(LOGIN_PREFERENCES, mContext).edit();
        editor.clear();
        editor.apply();
    }
}
