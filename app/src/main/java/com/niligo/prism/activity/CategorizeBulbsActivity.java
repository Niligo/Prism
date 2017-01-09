package com.niligo.prism.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.net.InetAddresses;
import com.niligo.prism.Constants;
import com.niligo.prism.NiligoPrismApplication;
import com.niligo.prism.R;
import com.niligo.prism.ServerCoordinator;
import com.niligo.prism.adapter.GroupTypesAdapter;
import com.niligo.prism.adapter.ItemAdapter;
import com.niligo.prism.model.BulbBean;
import com.niligo.prism.model.GroupBulbBean;
import com.niligo.prism.model.GroupType;
import com.niligo.prism.model.ProfileBean;
import com.niligo.prism.service.BrowserUpnpService;
import com.niligo.prism.util.TypefaceSpan;
import com.niligo.prism.util.Utils;
import com.niligo.prism.widget.NPTextView;
import com.woxthebox.draglistview.DragListView;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.android.FixedAndroidLogHandler;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by mahdi on 7/18/2016 AD.
 */
public class CategorizeBulbsActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST = 1000;

    private Toolbar mToolBar;
    private DragListView listView;
    private FloatingActionButton fab;
    private ProfileBean profileBean;
    private WifiManager wifiManager;

    private ItemAdapter listAdapter;
    private BrowseRegistryListener registryListener = new BrowseRegistryListener();
    private AndroidUpnpService upnpService;
    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;
            upnpService.getRegistry().addListener(registryListener);
            for (Device device : upnpService.getRegistry().getDevices()) {
                registryListener.deviceAdded(device);
            }
            upnpService.getControlPoint().search();
        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorize_bulbs);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("");
        mToolBar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(mToolBar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SpannableString spannableString = new SpannableString(getString(R.string.categories_bulbs));
        spannableString.setSpan(new TypefaceSpan(),
                0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(spannableString);

        listView = (DragListView) findViewById(R.id.listview);
        listView.setCanNotDragAboveTopItem(true);
        listView.setDragListCallback(new DragListView.DragListCallbackAdapter() {
            @Override
            public boolean canDragItemAtPosition(int dragPosition) {
                return dragPosition != 0;
            }

            @Override
            public boolean canDropItemAtPosition(int dropPosition) {
                return dropPosition != 0;

            }
        });
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setCanDragHorizontally(false);
        listAdapter = new ItemAdapter(listView,
                new ArrayList<DeviceDisplay>(),
                R.layout.row_bulb_categorize_cat,
                R.layout.row_bulb_categorize_bulb,
                R.id.drag,
                false,
                this);
        listView.setAdapter(listAdapter, false);

        profileBean = getIntent().getParcelableExtra("profileBean");

        if (profileBean != null && profileBean.getGroupBulbBeens() != null && profileBean.getGroupBulbBeens().size() > 0)
        {
            for (GroupBulbBean groupBulbBean : profileBean.getGroupBulbBeens())
            {
                listAdapter.add(new DeviceDisplay(new Category(groupBulbBean.getName(), groupBulbBean.getType())));
            }
        }
        else
        {
            listAdapter.add(new DeviceDisplay(new Category("Uncategorized", GroupType.NONE)));
        }

        fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                final Dialog view = new Dialog(CategorizeBulbsActivity.this, R.style.Theme_Dialog);
                view.setContentView(R.layout.dialog_add_category);
                view.setCancelable(true);
                if(!isFinishing())
                    view.show();
                view.getWindow().setLayout(Utils.dp2px(CategorizeBulbsActivity.this), ViewGroup.LayoutParams.WRAP_CONTENT);
                view.getWindow().setGravity(Gravity.CENTER);
                final EditText name_et = (EditText) view.findViewById(R.id.edittext2);
                final Spinner type_spinner = (Spinner) view.findViewById(R.id.type_spinner);
                ArrayList<String> types = new ArrayList<String>();
                for (GroupType groupType : GroupType.values())
                    if (groupType != GroupType.NONE)
                        types.add(groupType.name());
                GroupTypesAdapter groupTypesAdapter = new GroupTypesAdapter(CategorizeBulbsActivity.this,
                        R.layout.row_group_type,
                        types);
                type_spinner.setAdapter(groupTypesAdapter);
                type_spinner.setSelection(0);
                RelativeLayout config = (RelativeLayout) view.findViewById(R.id.config);
                config.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {

                        String name = name_et.getText().toString().trim();
                        if (name.length() == 0)
                            return;
                        GroupType groupType = GroupType.valueOf((String) type_spinner.getSelectedItem());
                        listAdapter.add(new DeviceDisplay(new Category(name, groupType)));
                        view.dismiss();
                    }
                });
            }
        });

        fillBulbs();
        //askPermission();

    }

    public void askPermission()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (ContextCompat.checkSelfPermission(CategorizeBulbsActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    ||
                    ContextCompat.checkSelfPermission(CategorizeBulbsActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(CategorizeBulbsActivity.this,
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
        if(!wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(true);
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
            if (wifiManager.getConnectionInfo().getSSID().equals(conf.SSID))
            {
                break;
            }
            else
            {
                count--;
            }
        }

        fillBulbs();
    }

    public void fillBulbs() {

        bindService(new Intent(this, AndroidUpnpServiceImpl.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (upnpService != null) {
            upnpService.getRegistry().removeListener(registryListener);
        }
        unbindService(serviceConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.categorize, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.done:
                Utils.showWaiting(CategorizeBulbsActivity.this);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<GroupBulbBean> groupBulbBeens = new ArrayList<>();
                        GroupBulbBean groupBulbBean;
                        boolean end = false;
                        for (int i = 0; i < listAdapter.getItemList().size();)
                        {
                            if (end)
                                break;
                            DeviceDisplay deviceDisplay = listAdapter.getItemList().get(i);
                            groupBulbBean = new GroupBulbBean(i,
                                    deviceDisplay.getCategory().name,
                                    deviceDisplay.getCategory().type,
                                    profileBean.getId()
                            );
                            while (true)
                            {
                                i++;
                                if (i >= listAdapter.getItemList().size())
                                {
                                    end = true;
                                    break;
                                }
                                DeviceDisplay deviceDisplay2 = listAdapter.getItemList().get(i);
                                if (deviceDisplay2.getCategory() == null)
                                    groupBulbBean.addBulbBeen(new BulbBean(deviceDisplay2.toString(), deviceDisplay2.getIP()));
                                else
                                    break;
                            }
                            groupBulbBeens.add(groupBulbBean);
                        }



                        ArrayList<GroupBulbBean> corrected_groupBulbBeens = new ArrayList<>();
                        for (GroupBulbBean groupBulbBean1 : groupBulbBeens)
                        {
                            //if (groupBulbBean1.getBulbBeens().size() == 0)
                            //    continue;
                            Log.e("tag", groupBulbBean1.getName() + " " + groupBulbBean1.getBulbBeens().size());
                            corrected_groupBulbBeens.add(groupBulbBean1);
                        }

                        for (int j = 0; j < corrected_groupBulbBeens.size(); j++)
                        {
                            for (int k = 0; k < corrected_groupBulbBeens.get(j).getBulbBeens().size(); k++)
                            {
                                /*Log.e("tag", "setUsernamePassword");
                                String new_username = Utils.generateString();
                                ServerCoordinator.getInstance()
                                        .setBulbCredentials(
                                                Constants.DEFAULT_USERNAME,
                                                Constants.DEFAULT_PASSWORD,
                                                corrected_groupBulbBeens.get(j).getBulbBeens().get(k).getWS())
                                        .setUsernamePassword(
                                                new_username,
                                                Constants.DEFAULT_PASSWORD);*/

                                //todo check
                                //corrected_groupBulbBeens.get(j).getBulbBeens().get(k).setUser(new_username);
                                corrected_groupBulbBeens.get(j).getBulbBeens().get(k).setUser(Constants.DEFAULT_USERNAME);
                                corrected_groupBulbBeens.get(j).getBulbBeens().get(k).setPass(Constants.DEFAULT_PASSWORD);

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        final ArrayList<GroupBulbBean> corrected_groupBulbBeens2 = corrected_groupBulbBeens;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utils.hideWaiting();
                                profileBean.setGroupBulbBeens(corrected_groupBulbBeens2);
                                NiligoPrismApplication.getInstance().updateProfile(profileBean);
                                finish();
                            }
                        });

                    }
                }).start();


                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    protected class BrowseRegistryListener extends DefaultRegistryListener {

        /* Discovery performance optimization for very slow Android devices! */
        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {

            deviceRemoved(device);
        }
        /* End of optimization, you can remove the whole block if your Android handset is fast (>= 600 Mhz) */

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            deviceRemoved(device);
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            deviceAdded(device);
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            deviceRemoved(device);
        }

        public void deviceAdded(final Device device) {
            runOnUiThread(new Runnable() {
                public void run() {
                    DeviceDisplay d = new DeviceDisplay(device);

                    String uuid = d.getDevice().getIdentity().getUdn().toString();
                    Log.e("Tag", "uuid = " + uuid);

                    if (!uuid.startsWith(Constants.PRISM_UUID))
                        return;

                    String identity = d.getDevice().getIdentity().toString();
                    String host = identity.substring(identity.indexOf("Descriptor: ") + 12, identity.length());
                    host = host.substring(7, host.indexOf("/device.xml"));
                    Log.e("Tag", "host = " + host);
                    d.setIP(host);
                    int position = listAdapter.getPosition(d);
                    if (position >= 0)
                    {
                        // Device already in the list, re-set new value at same position
                        listAdapter.remove(d);
                        listAdapter.insert(d, position);
                    }
                    else
                    {
                        if (profileBean != null && profileBean.getGroupBulbBeens() != null && profileBean.getGroupBulbBeens().size() > 0)
                        {
                            boolean set = false;
                            int i = 0;
                            for (GroupBulbBean groupBulbBean : profileBean.getGroupBulbBeens())
                            {
                                i++;
                                boolean found = false;
                                ArrayList<BulbBean> bulbBeans = groupBulbBean.getBulbBeens();
                                for (BulbBean bulbBean : bulbBeans)
                                {
                                    if (bulbBean.getIp().equals(d.getIP()))
                                    {
                                        listAdapter.insert(d, i);
                                        set = true;
                                        found = true;
                                        break;
                                    }
                                }
                                i++;
                                if (found)
                                    break;
                            }
                            if (!set)
                                listAdapter.add(d);
                        }
                        else
                        {
                            listAdapter.add(d);
                        }
                    }
                }
            });
        }

        public void deviceRemoved(final Device device) {
            runOnUiThread(new Runnable() {
                public void run() {
                    DeviceDisplay deviceDisplay = new DeviceDisplay(device);
                    int position = listAdapter.getPosition(deviceDisplay);
                    if (position >= 0)
                        listAdapter.remove(deviceDisplay);
                }
            });
        }
    }

    public static class Category{
        private String name;
        private GroupType type;

        public Category(String name, GroupType type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public GroupType getType() {
            return type;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(GroupType type) {
            this.type = type;
        }
    }

    public class DeviceDisplay {

        private Category category;
        private Device device;
        private String ip;

        public String getIP() {
            return ip;
        }

        public void setIP(String ip) {
            this.ip = ip;
        }

        public DeviceDisplay(Device device) {
            this.device = device;
        }

        public DeviceDisplay(Category category) {
            this.category = category;
        }

        public Category getCategory() {
            return category;
        }

        public Device getDevice() {
            return device;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DeviceDisplay that = (DeviceDisplay) o;
            if(device != null)
            {
                return device.equals(that.device);
            }
            return category.equals(that.category);
        }

        @Override
        public int hashCode() {
            return device != null ? device.hashCode() : category.hashCode();
        }

        @Override
        public String toString() {
            try {
                return device != null ? device.getDetails().getFriendlyName() : category.name;
            }
            catch (Exception e)
            {
                return device.getIdentity().getUdn().getIdentifierString();
            }
        }
    }
}
