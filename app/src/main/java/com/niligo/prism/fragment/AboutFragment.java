package com.niligo.prism.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.niligo.prism.NiligoPrismApplication;
import com.niligo.prism.R;
import com.niligo.prism.activity.MainActivity;

/**
 * Created by mahdi on 9/15/15 AD.
 */
public class AboutFragment extends BaseFragment {


    private TextView email, site, version;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mViewHome = inflater.inflate(R.layout.fragment_about, container, false);


        email = (TextView) mViewHome.findViewById(R.id.email);
        site = (TextView) mViewHome.findViewById(R.id.site);
        version = (TextView) mViewHome.findViewById(R.id.version);

        version.setText("Version: " + NiligoPrismApplication.getInstance().getVersionName());

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email.getText().toString(), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
            }
        });

        site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + site.getText().toString()));
                startActivity(browserIntent);
            }
        });

        ((MainActivity) getActivity()).setOnBackPressedListener(null);
        return mViewHome;
    }


}