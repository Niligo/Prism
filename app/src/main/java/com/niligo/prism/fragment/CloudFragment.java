package com.niligo.prism.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.niligo.prism.Constants;
import com.niligo.prism.NiligoPrismApplication;
import com.niligo.prism.R;
import com.niligo.prism.ServerCoordinator;
import com.niligo.prism.activity.IntroActivity;
import com.niligo.prism.activity.LoginActivity;
import com.niligo.prism.activity.MainActivity;
import com.niligo.prism.activity.SplashScreenActivity;
import com.niligo.prism.adapter.ShareProfilesAdapter;
import com.niligo.prism.callback.BackupCallback;
import com.niligo.prism.callback.InviteCallback;
import com.niligo.prism.entity.ProfileEntity;
import com.niligo.prism.model.BackupResponseBean;
import com.niligo.prism.model.BulbBean;
import com.niligo.prism.model.ColorFavesBean;
import com.niligo.prism.model.InviteResponseBean;
import com.niligo.prism.model.ProfileBean;
import com.niligo.prism.util.Utils;

import org.fourthline.cling.Main;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahdi on 9/15/15 AD.
 */
public class CloudFragment extends BaseFragment implements InviteCallback, BackupCallback{


    private RelativeLayout backup, share, restore;
    private TextView latest, ip;
    private Dialog dialog;
    private Thread thread;
    private boolean ended;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mViewHome = inflater.inflate(R.layout.fragment_cloud, container, false);


        backup = (RelativeLayout) mViewHome.findViewById(R.id.backup_rel);
        share = (RelativeLayout) mViewHome.findViewById(R.id.share_rel);
        restore = (RelativeLayout) mViewHome.findViewById(R.id.restore_rel);
        latest = (TextView) mViewHome.findViewById(R.id.latest);
        ip = (TextView) mViewHome.findViewById(R.id.ip);
        String lat = NiligoPrismApplication.getInstance().getSharedPreferences().getString(Constants.PREF_LATEST_BACKUP, "");
        String text = getString(R.string.backup_latest) + " ";
        if (!lat.equals(""))
        {
            text += lat;
        }
        else
        {
            text += getString(R.string.never);
        }
        latest.setText(text);
        ip.setText(getString(R.string.current_ip) + " " + Utils.getIPAddress());

        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showWaiting(getActivity());
                String value = "";
                try {
                    JSONArray jsonArray = new JSONArray();
                    List<ProfileEntity> profileEntities = NiligoPrismApplication.getInstance().getDatabaseHelper().getProfileEntities();
                    for (ProfileEntity profileEntity : profileEntities)
                    {
                        jsonArray.put(profileEntity.getProfile_json());
                    }
                    value = jsonArray.toString();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                ServerCoordinator.getInstance().backup(value,
                        CloudFragment.this);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dialog != null)
                {
                    dialog.dismiss();
                    dialog = null;
                }
                dialog = new Dialog(getActivity(), R.style.Theme_Dialog);
                dialog.setContentView(R.layout.dialog_share_profile);
                dialog.setCancelable(true);
                dialog.show();
                final ListView listView = (ListView) dialog.findViewById(R.id.listview);
                final ArrayList<ProfileBean> profiles = new ArrayList<>();
                List<ProfileEntity> profileEntities = NiligoPrismApplication.getInstance().getDatabaseHelper().getProfileEntities();
                for (ProfileEntity profileEntity : profileEntities)
                {
                    ProfileBean profileBean = new GsonBuilder().create().fromJson(profileEntity.getProfile_json(), ProfileBean.class);
                    if (profileBean != null)
                    {
                        profiles.add(profileBean);
                    }
                }
                final ShareProfilesAdapter shareProfilesAdapter = new ShareProfilesAdapter(getActivity(), profiles, listView);
                listView.setAdapter(shareProfilesAdapter);
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        for (int i = 0; i < profiles.size(); i++)
                        {
                            if (i == position)
                                profiles.get(i).setSelected(true);
                            else
                                profiles.get(i).setSelected(false);
                        }
                        shareProfilesAdapter.update(profiles);
                    }
                });

                final EditText email_et = (EditText) dialog.findViewById(R.id.edittext2);
                RelativeLayout config = (RelativeLayout) dialog.findViewById(R.id.config);
                config.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ip = email_et.getText().toString().trim();
                        if (ip.length() == 0)
                        {
                            Utils.setError(getString(R.string.error_empty), email_et);
                            return;
                        }

                        ProfileBean profileBean = null;
                        for (ProfileBean profileBean1 : shareProfilesAdapter.getProfileBeens())
                            if (profileBean1.isSelected())
                                profileBean = profileBean1;
                        if (profileBean == null)
                        {
                            Utils.showToast(getActivity(), R.string.select_profile, Toast.LENGTH_LONG);
                            return;
                        }
                        startSending(profileBean, ip);
                        //ServerCoordinator.getInstance().invite(email, CloudFragment.this);

                    }
                });
                dialog.getWindow().setLayout(Utils.dp2px(getActivity()), ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setGravity(Gravity.CENTER);
            }
        });

        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        //todo
        /*SharedPreferences sharedPreferences = NiligoPrismApplication.getInstance().getSharedPreferences();
        if (!sharedPreferences.getBoolean(Constants.PREF_IS_LOGGED_IN, false))
        {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }*/


        createThread();

        ((MainActivity) getActivity()).setOnBackPressedListener(null);
        return mViewHome;
    }

    private void createThread()
    {
        ended = false;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket ss = new ServerSocket(Constants.SHARE_PORT);
                    while(!ended){
                        Socket s = ss.accept();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        String message = reader.readLine();
                        Log.e("tag","Message Received: " + message);
                        if (message != null && message.length() > 0)
                        {
                            ProfileBean profileBean = new GsonBuilder().create().fromJson(message, ProfileBean.class);
                            if (profileBean != null)
                            {
                                NiligoPrismApplication.getInstance().addProfile(profileBean);
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                getActivity().finish();
                            }
                        }
                        reader.close();
                        s.close();
                    }
                    ss.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (thread != null && !thread.isAlive())
        {
            try {
                thread.start();
            }
            catch (Exception e)
            {
                //e.printStackTrace();
                thread = null;
                createThread();
                if (thread != null && !thread.isAlive())
                {
                    try {
                        thread.start();
                    }
                    catch (Exception e2)
                    {
                        //e2.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (thread != null && thread.isAlive() && !thread.isInterrupted())
        {
            try {
                ended = true;
                thread.interrupt();
            }
            catch (Exception e)
            {
                //e.printStackTrace();
            }
        }
    }

    private void startSending(final ProfileBean profileBean, final String ip)
    {
        Utils.showWaiting(getActivity());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket(ip, Constants.SHARE_PORT);
                    OutputStream out = s.getOutputStream();
                    PrintWriter output = new PrintWriter(out, true);
                    String element = new GsonBuilder().create().toJson(profileBean);
                    Log.e("Tag", "element = " + element);
                    output.println(element);
                    s.close();
                    Log.e("Tag", "close");
                } catch (IOException e) {
                    e.printStackTrace();
                    Utils.showToast(getActivity(), R.string.error_share_profile, Toast.LENGTH_LONG);
                }
                if (getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.hideWaiting();
                            if (dialog != null)
                            {
                                dialog.dismiss();
                                dialog = null;
                            }
                        }
                    });
            }
        }).start();

    }

    @Override
    public void inviteSuccess(final InviteResponseBean inviteResponseBean) {
        if (getActivity() == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.hideWaiting();
                if (inviteResponseBean.getError_code() == 0)
                {
                    if (dialog != null)
                    {
                        dialog.dismiss();
                        dialog = null;
                    }
                    Utils.showToast(getActivity(), R.string.success_invite, Toast.LENGTH_LONG);
                }
                else
                {
                    if (inviteResponseBean.getError_message() != null && !inviteResponseBean.getError_message().equals("null"))
                    {
                        Utils.showToast(getActivity(), inviteResponseBean.getError_message(), Toast.LENGTH_LONG);
                    }
                    else
                    {
                        Utils.handleError(inviteResponseBean.getError_code(), getActivity());
                    }
                }

            }
        });
    }

    @Override
    public void inviteFailure(final int error_code) {
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.hideWaiting();
                switch (error_code)
                {
                    case 401:
                        Utils.showToast(getActivity(), R.string.error_unuth, Toast.LENGTH_LONG);
                        break;

                    case 500:
                        Utils.showToast(getActivity(), R.string.error_connection, Toast.LENGTH_LONG);
                        break;
                }
            }
        });
    }

    @Override
    public void backupSuccess(final BackupResponseBean backupResponseBean) {
        if (getActivity() == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.hideWaiting();
                if (backupResponseBean.getError_code() == 0)
                {
                    if (dialog != null)
                    {
                        dialog.dismiss();
                        dialog = null;
                    }

                    String dateTime = Utils.getCurrentDateTime();
                    SharedPreferences.Editor editor = NiligoPrismApplication.getInstance().getSharedPreferences().edit();
                    editor.putString(Constants.PREF_LATEST_BACKUP, dateTime).apply();
                    String text = getString(R.string.backup_latest) + " " + dateTime;
                    latest.setText(text);
                    Log.e("tag", "latest = " + latest);
                    Utils.showToast(getActivity(), R.string.success_backup, Toast.LENGTH_LONG);
                }
                else
                {
                    if (backupResponseBean.getError_message() != null && !backupResponseBean.getError_message().equals("null"))
                    {
                        Utils.showToast(getActivity(), backupResponseBean.getError_message(), Toast.LENGTH_LONG);
                    }
                    else
                    {
                        Utils.handleError(backupResponseBean.getError_code(), getActivity());
                    }
                }

            }
        });
    }

    @Override
    public void backupFailure(final int error_code) {
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.hideWaiting();
                switch (error_code)
                {
                    case 401:
                        Utils.showToast(getActivity(), R.string.error_unuth, Toast.LENGTH_LONG);
                        break;

                    case 500:
                        Utils.showToast(getActivity(), R.string.error_connection, Toast.LENGTH_LONG);
                        break;
                }
            }
        });
    }
}