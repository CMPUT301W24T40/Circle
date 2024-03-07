package com.example.circleapp.Firebase;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
    private static final String PREF_PROFILE_CREATED = "profile_created";
    private static final String PREF_CURRENT_USER_ID = "current_user_id";
    private static final String PREFS_NAME = "MyPrefs";

    // Method to check if profile is created
    public static boolean isProfileCreated(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_PROFILE_CREATED, false);
    }

    // Method to set profile creation flag
    public static void setProfileCreated(Context context, boolean profileCreated) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_PROFILE_CREATED, profileCreated);
        editor.apply();
    }

    // Method to store current user ID
    public static void setCurrentUserID(Context context, String userID) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_CURRENT_USER_ID, userID);
        editor.apply();
    }

    // Method to retrieve current user ID
    public static String getCurrentUserID(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_CURRENT_USER_ID, null);
    }
}