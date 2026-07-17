package com.buddyapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREFS_NAME = "buddy_session";
    private static final String KEY_USER_ID = "user_id";
    private static final int NO_USER = -1;

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setLoggedIn(int userId) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply();
    }

    public boolean isLoggedIn() {
        return prefs.getInt(KEY_USER_ID, NO_USER) != NO_USER;
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, NO_USER);
    }

    public void logout() {
        prefs.edit().remove(KEY_USER_ID).apply();
    }
}
