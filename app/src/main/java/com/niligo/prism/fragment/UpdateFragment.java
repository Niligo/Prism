package com.niligo.prism.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.niligo.prism.Constants;
import com.niligo.prism.NiligoPrismApplication;
import com.niligo.prism.R;
import com.niligo.prism.ServerCoordinator;
import com.niligo.prism.activity.MainActivity;
import com.niligo.prism.entity.ProfileEntity;
import com.niligo.prism.model.BulbBean;
import com.niligo.prism.model.GroupBulbBean;
import com.niligo.prism.model.ProfileBean;
import com.niligo.prism.util.Utils;

import java.util.List;


public class UpdateFragment extends BaseFragment {


    private RelativeLayout update_rel;
    private RelativeLayout reset_rel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_update, container, false);

        reset_rel = (RelativeLayout) view.findViewById(R.id.reset_rel);
        update_rel = (RelativeLayout) view.findViewById(R.id.update_rel);
        update_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final ProfileBean profileBean = NiligoPrismApplication.getInstance().getCurrentProfile();
                if (profileBean == null)
                {
                    Utils.showToast(getActivity(), R.string.error_profile_null, Toast.LENGTH_LONG);
                    return;
                }

                Utils.showWaiting(getActivity());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (GroupBulbBean groupBulbBean : profileBean.getGroupBulbBeens())
                        {
                            for (BulbBean bulbBean : groupBulbBean.getBulbBeens())
                            {
                                ServerCoordinator.getInstance()
                                        .setBulbCredentials(
                                                bulbBean.getUser(),
                                                bulbBean.getPass(),
                                                bulbBean.getWS())
                                        .update();
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (getActivity() == null)
                            return;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utils.hideWaiting();
                            }
                        });
                    }
                }).start();
            }
        });
        reset_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProfileBean profileBean = NiligoPrismApplication.getInstance().getCurrentProfile();
                if (profileBean == null)
                {
                    Utils.showToast(getActivity(), R.string.error_profile_null, Toast.LENGTH_LONG);
                    return;
                }

                Utils.showWaiting(getActivity());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (GroupBulbBean groupBulbBean : profileBean.getGroupBulbBeens())
                        {
                            for (BulbBean bulbBean : groupBulbBean.getBulbBeens())
                            {
                                ServerCoordinator.getInstance()
                                        .setBulbCredentials(
                                                bulbBean.getUser(),
                                                bulbBean.getPass(),
                                                bulbBean.getWS())
                                        .reset();
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (getActivity() == null)
                            return;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utils.hideWaiting();
                            }
                        });
                    }
                }).start();

            }
        });
        ((MainActivity) getActivity()).setOnBackPressedListener(null);
        return view;
    }
}

