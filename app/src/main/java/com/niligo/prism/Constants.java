package com.niligo.prism;

import android.os.Environment;

/**
 * Created by mahdi on 7/19/2016 AD.
 */
public class Constants {

    public static final String DEFAULT_IP = "192.168.100.1";
    public static final String WS = "http://napi.niligo.com/api/";
    public static final String TROUBLESHOOTING_URL = "http://www.niligo.com/prism/prism-support/";
    public static final String SUPPORT_URL = "http://www.niligo.com/prism/prism-support/";
    public static final String DEVELOPERS_URL = "http://www.niligo.com/developer/";
    public static final String PHOTO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/prism";
    public static final String PRISM_SSID = "Niligo-Prism";
    public static final String PRISM_UUID = "uuid:8e80263c-8994-4f1f-a31c-e35";
    public static final int DIALOG_WIDTH = 300;
    public static final String DEFAULT_USERNAME = "admin";
    public static final String DEFAULT_PASSWORD = "admin";
    public static final int CONNECT_TIME_OUT = 10;
    public static final int TIME_OUT = 30;
    public static final String PREF_TOKEN = "PREF_TOKEN";
    public static final String PREF_IS_INTRO_SHOWN = "PREF_IS_INTRO_SHOWN";
    public static final String PREF_IS_LOGGED_IN = "PREF_IS_LOGGED_IN";
    public static final String PREF_EMAIL = "PREF_EMAIL";
    public static final String PREF_PASSWORD = "PREF_PASSWORD";
    public static final String PREF_LATEST_BACKUP = "PREF_LATEST_BACKUP";
    public static final String PREF_FAV_COLORS = "PREF_FAV_COLORS";
    public static final int FADE = 100;
    public static final int DELAY = 200;
    public static final int SHARE_PORT = 1234;

    public static final String UNAUTH = "UNAUTH";
}
