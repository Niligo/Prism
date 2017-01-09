package com.niligo.prism;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.niligo.prism.entity.DatabaseHelper;
import com.niligo.prism.entity.ProfileEntity;
import com.niligo.prism.model.ProfileBean;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by mahdi on 7/18/2016 AD.
 */
public class NiligoPrismApplication extends Application {


    private static NiligoPrismApplication instance;
    private String font_name = "myriad.otf";
    private Typeface typeface;
    private Handler threadHandler;
    private SharedPreferences sharedPreferences;
    private ConnectivityManager connectivityManager;
    public int back_count = 0;
    private WifiManager wifiManager;
    private ProfileBean current_profile = null;
    private DatabaseHelper helper;
    private ActivityManager activityManager;


    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            try {
                StringWriter errors = new StringWriter();
                throwable.printStackTrace(new PrintWriter(errors));
                String error = errors.toString();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            uncaughtExceptionHandler.uncaughtException(thread, throwable);
        }
    };


    public static NiligoPrismApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        typeface = Typeface.createFromAsset(getAssets(), font_name);
        threadHandler = new Handler();

        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);

        helper = new DatabaseHelper(getApplicationContext());

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        reloadCurrentProfile();
    }

    public void reloadCurrentProfile()
    {
        current_profile = null;

        String ssid = wifiManager.getConnectionInfo().getSSID().replace("\"", "");
        List<ProfileEntity> profileEntities = getDatabaseHelper().getProfileEntities();

        for (ProfileEntity profileEntity : profileEntities)
        {
            ProfileBean profileBean = new GsonBuilder().create().fromJson(profileEntity.getProfile_json(), ProfileBean.class);
            if (profileBean != null)
            {
                if (ssid.equals(profileBean.getSsid()))
                {
                    current_profile = profileBean;
                    break;
                }
            }
        }
    }

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    public DatabaseHelper getDatabaseHelper() {
        return helper;
    }

    public ProfileBean getCurrentProfile() {

        return current_profile;
    }

    public void setCurrentProfile(ProfileBean current_profile) {

        this.current_profile = current_profile;
    }

    public void addProfile(ProfileBean profile) {

        if (profile == null)
            return;

        String element = new GsonBuilder().create().toJson(profile);
        getDatabaseHelper().addProfile(new ProfileEntity(element));

        current_profile = profile;

    }

    public void updateProfile(ProfileBean profile) {

        if (profile == null)
            return;

        List<ProfileEntity> profileEntities = getDatabaseHelper().getProfileEntities();
        boolean found = false;
        String element = new GsonBuilder().create().toJson(profile);
        for (ProfileEntity profileEntity : profileEntities)
        {
            ProfileBean profileBean = new GsonBuilder().create().fromJson(profileEntity.getProfile_json(), ProfileBean.class);
            if (profileBean.getId() == profile.getId())
            {
                profileEntity.setProfile_json(element);
                getDatabaseHelper().updateProfile(profileEntity);
                found = true;
                break;
            }
        }
        if (!found)
        {
            getDatabaseHelper().addProfile(new ProfileEntity(element));
        }

        current_profile = profile;

    }

    public WifiManager getWifiManager() {
        return wifiManager;
    }

    public String getVersionName() {

        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "0.0";
    }

    public int getVersionCode() {

        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 240;
    }

    public ConnectivityManager getConnectivityManager()
    {
        return connectivityManager;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public Typeface getTypeface() {
        return typeface;
    }
}
