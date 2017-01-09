package com.niligo.prism.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.niligo.prism.Constants;
import com.niligo.prism.NiligoPrismApplication;
import com.niligo.prism.R;
import com.niligo.prism.ServerCoordinator;
import com.niligo.prism.adapter.BulbConfigAdapter;
import com.niligo.prism.adapter.LocalWifiAdapter;
import com.niligo.prism.model.BulbBean;
import com.niligo.prism.model.LocalWifiBean;
import com.niligo.prism.model.ProfileBean;
import com.niligo.prism.util.TypefaceSpan;
import com.niligo.prism.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by mahdi on 7/18/2016 AD.
 */
public class ConfigureBulbsActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST = 1000;
    private RelativeLayout root_layout;
    private ArrayList<String> niligo_connections = new ArrayList<>();
    private ArrayList<String> local_connections = new ArrayList<>();
    private WifiReceiver receiverWifi;
    private WifiManager wifiManager;
    private boolean stop = false;
    private Toolbar mToolBar;
    private ListView listView;
    private RelativeLayout config;
    private MenuItem menuItem;
    //1 = niligo prism
    //2 = local wifi
    private int scan_mode;
    private ArrayList<BulbBean> bulbBeens;
    private ProfileBean profileBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_config);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("");
        mToolBar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(mToolBar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SpannableString spannableString = new SpannableString(getString(R.string.configure_bulbs));
        spannableString.setSpan(new TypefaceSpan(),
                0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(spannableString);

        root_layout = (RelativeLayout) findViewById(R.id.root);

        profileBean = getIntent().getParcelableExtra("profileBean");
        showTurnOn();

    }

    private void showTurnOn()
    {
        final View view = View.inflate(this, R.layout.activity_turnon, null);

        if (Build.VERSION.SDK_INT >= 23)
        {
            if (ContextCompat.checkSelfPermission(ConfigureBulbsActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    ||
                    ContextCompat.checkSelfPermission(ConfigureBulbsActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(ConfigureBulbsActivity.this,
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        MY_PERMISSIONS_REQUEST);
            }
            else
            {
                permissionGranted();
            }
        }
        else
        {
            permissionGranted();
        }

        root_layout.removeAllViews();
        root_layout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    permissionGranted();
                }
                else
                {
                    Utils.showToast(this, R.string.permission_needed, Toast.LENGTH_LONG);
                }
            }
        }
    }
    private void permissionGranted()
    {
        wifiManager = NiligoPrismApplication.getInstance().getWifiManager();
        receiverWifi = new WifiReceiver();
        if(!wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(true);
        }

        scan_mode = 1;
        doInback();
    }

    private void showList()
    {
        final View view = View.inflate(this, R.layout.activity_bulbs_list, null);
        menuItem.setVisible(true);

        listView = (ListView) view.findViewById(R.id.listView);
        config = (RelativeLayout) view.findViewById(R.id.config);

        bulbBeens = new ArrayList<>();
        for (int i = 0; i < niligo_connections.size(); i++)
        {
            String ssid = niligo_connections.get(i);
            bulbBeens.add(new BulbBean(i, ssid, true));
        }
        final BulbConfigAdapter bulbConfigAdapter = new BulbConfigAdapter(this, bulbBeens);
        listView.setAdapter(bulbConfigAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bulbBeens.get(i).setSelected(!bulbBeens.get(i).isSelected());
                bulbConfigAdapter.notifyDataSetChanged();
            }
        });

        config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.showWaiting(ConfigureBulbsActivity.this);
                scan_mode = 2;
                stop = false;
                doInback();
            }
        });

        root_layout.removeAllViews();
        root_layout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void showLocalWifiDialog()
    {
        Utils.hideWaiting();

        final Dialog view = new Dialog(this, R.style.Theme_Dialog);
        view.setContentView(R.layout.dialog_local_wifi);
        view.setCancelable(true);
        if(!isFinishing())
            view.show();
        view.getWindow().setLayout(Utils.dp2px(this), ViewGroup.LayoutParams.WRAP_CONTENT);
        view.getWindow().setGravity(Gravity.CENTER);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        final ArrayList<LocalWifiBean> localWifiBeens = new ArrayList<>();
        for (int i = 0; i < local_connections.size(); i++)
        {
            String ssid = local_connections.get(i);
            localWifiBeens.add(new LocalWifiBean(i, ssid, false));
        }
        final LocalWifiAdapter localWifiAdapter = new LocalWifiAdapter(this, localWifiBeens);
        listView.setAdapter(localWifiAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view1, int i, long l) {

                view.dismiss();
                LocalWifiBean localWifiBean = (LocalWifiBean) adapterView.getItemAtPosition(i);
                showLocalWifiPassDialog(localWifiBean);
            }
        });

    }

    private void showLocalWifiPassDialog(final LocalWifiBean localWifiBean)
    {

        final Dialog view = new Dialog(this, R.style.Theme_Dialog);
        view.setContentView(R.layout.dialog_local_wifi_pass);
        view.setCancelable(true);
        if(!isFinishing())
            view.show();
        view.getWindow().setLayout(Utils.dp2px(this), ViewGroup.LayoutParams.WRAP_CONTENT);
        view.getWindow().setGravity(Gravity.CENTER);
        final EditText pass_et = (EditText) view.findViewById(R.id.edittext2);
        RelativeLayout config = (RelativeLayout) view.findViewById(R.id.config);
        config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                String pass = pass_et.getText().toString();

                if (pass.length() == 0)
                    return;

                //testWifiPass(localWifiBean, pass);

                for (BulbBean bulbBean : bulbBeens)
                {
                    if (!bulbBean.isSelected())
                        continue;
                    bulbBean.setUser(Constants.DEFAULT_USERNAME);
                    bulbBean.setPass(Constants.DEFAULT_PASSWORD);
                    bulbBean.setIp(Constants.DEFAULT_IP);
                    bulbBean.setNetwork_password(pass);
                    bulbBean.setSta_ssid(localWifiBean.getSsid());
                }
                profileBean.setSsid(localWifiBean.getSsid());
                showWaiting();

                view.dismiss();
            }
        });

    }

    private void testWifiPass(final LocalWifiBean localWifiBean, final String pass)
    {

        Utils.showWaiting(ConfigureBulbsActivity.this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = "\"" + localWifiBean.getSsid() + "\"";
                conf.preSharedKey = String.format("\"%s\"", pass);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiManager.addNetwork(conf);
                boolean state = Utils.enableNetwork(wifiManager, conf.SSID);
                Log.e("tag", "state = " + state);
                if (state)
                {
                    for (BulbBean bulbBean : bulbBeens)
                    {
                        if (!bulbBean.isSelected())
                            continue;
                        bulbBean.setUser(Constants.DEFAULT_USERNAME);
                        bulbBean.setPass(Constants.DEFAULT_PASSWORD);
                        bulbBean.setIp(Constants.DEFAULT_IP);
                        bulbBean.setNetwork_password(pass);
                        bulbBean.setSta_ssid(localWifiBean.getSsid());
                    }
                    profileBean.setSsid(localWifiBean.getSsid());

                    ConfigureBulbsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Utils.hideWaiting();
                            showWaiting();

                        }
                    });
                }
                else
                {
                    ConfigureBulbsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Utils.hideWaiting();
                            Utils.showToast(ConfigureBulbsActivity.this, R.string.error_incorrect_pass, Toast.LENGTH_LONG);
                            showLocalWifiPassDialog(localWifiBean);
                        }
                    });
                }

            }
        }).start();


    }

    private void showWaiting()
    {
        menuItem.setVisible(false);
        final View view = View.inflate(this, R.layout.activity_waiting, null);
        GifImageView gif = (GifImageView) view.findViewById(R.id.gif);
        try {
            GifDrawable gifFromAssets = new GifDrawable(getResources().getAssets(), "loading.gif");
            gifFromAssets.setLoopCount(0);
            gif.setImageDrawable(gifFromAssets);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (BulbBean bulbBean : bulbBeens)
                {
                    if (!bulbBean.isSelected())
                        continue;

                    WifiConfiguration conf = new WifiConfiguration();
                    conf.SSID = "\"" + bulbBean.getSsid() + "\"";
                    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    wifiManager.addNetwork(conf);
                    Utils.enableNetwork(wifiManager, conf.SSID);

                    int count = 10;
                    while (count > 0)
                    {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (Utils.isConnected())
                        {
                            break;
                        }
                        else
                        {
                            count--;
                        }
                    }

                    Log.e("tag", "setNetworkSTA");

                    ServerCoordinator.getInstance()
                            .setBulbCredentials(
                                    bulbBean.getUser(),
                                    bulbBean.getPass(),
                                    bulbBean.getWS())
                            .setNetworkSTA(
                                    bulbBean.getSta_ssid(),
                                    bulbBean.getNetwork_password());
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = "\"" + profileBean.getSsid() + "\"";
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiManager.addNetwork(conf);
                Utils.enableNetwork(wifiManager, conf.SSID);

                int count = 10;
                while (count > 0)
                {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (Utils.isConnected())
                    {
                        break;
                    }
                    else
                    {
                        count--;
                    }
                }


                ConfigureBulbsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        NiligoPrismApplication.getInstance().updateProfile(profileBean);
                        Intent intent = new Intent(ConfigureBulbsActivity.this, CategorizeBulbsActivity.class);
                        intent.putExtra("profileBean", profileBean);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).start();


        root_layout.removeAllViews();
        root_layout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.config, menu);
        menuItem = menu.findItem(R.id.refresh);
        menuItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.refresh:
                stop = false;
                doInback();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        stop = false;
        try {
            registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        stop = true;
        try {
            unregisterReceiver(receiverWifi);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void doInback()
    {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run()
            {
                Log.e("tag", "doInback");
                wifiManager.startScan();
                if (!stop)
                    doInback();
            }
        }, 1000);

    }

    public class WifiReceiver extends BroadcastReceiver {

        private List<ScanResult> wifiList;
        @Override
        public void onReceive(Context c, Intent intent)
        {
            Log.e("tag", "onReceive1");
            if (stop)
                return;
            Log.e("tag", "onReceive2");

            niligo_connections = new ArrayList<>();
            local_connections = new ArrayList<>();
            wifiList = wifiManager.getScanResults();
            Log.e("tag", "onReceive size = " + wifiList.size());

            switch (scan_mode)
            {
                case 1:
                    niligo();
                    break;

                case 2:
                    local();
                    break;
            }


        }

        private void niligo()
        {

            for(int i = 0; i < wifiList.size(); i++)
            {
                String ssid = wifiList.get(i).SSID;
                if (ssid.startsWith(Constants.PRISM_SSID))
                    niligo_connections.add(ssid);
            }

            if (niligo_connections.size() > 0)
            {
                showList();
                stop = true;
            }
        }

        private void local()
        {
            for(int i = 0; i < wifiList.size(); i++)
            {
                String ssid = wifiList.get(i).SSID;
                if (!ssid.startsWith(Constants.PRISM_SSID))
                    local_connections.add(ssid);
            }

            if (local_connections.size() > 0)
            {
                showLocalWifiDialog();
                stop = true;
            }
        }
    }
}
