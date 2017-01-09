package com.niligo.prism.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.niligo.prism.Constants;
import com.niligo.prism.NiligoPrismApplication;
import com.niligo.prism.R;
import com.niligo.prism.activity.LoginActivity;
import com.niligo.prism.activity.MainActivity;
import com.niligo.prism.model.IntroBean;
import com.niligo.prism.widget.NPTextView;
import com.squareup.picasso.Picasso;

/**
 * Created by mahdi on 9/15/15 AD.
 */
public class IntroFragment extends BaseFragment {

    private View mViewHome;
    private IntroBean introBean;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu1, MenuInflater inflater) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        introBean = getArguments().getParcelable("introBean");

        switch (introBean.getId())
        {
            case 0:
                page0(inflater, container);
                break;

            case 1:
                page1(inflater, container);
                break;

            case 2:
                page2(inflater, container);
                break;

            case 3:
                page3(inflater, container);
                break;

            case 4:
                page4(inflater, container);
                break;

            case 5:
                page5(inflater, container);
                break;
        }

        return mViewHome;
    }

    private void end()
    {
        SharedPreferences.Editor editor = NiligoPrismApplication.getInstance().getSharedPreferences().edit();
        editor.putBoolean(Constants.PREF_IS_INTRO_SHOWN, true).apply();

        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    private void page0(LayoutInflater inflater, ViewGroup container)
    {
        mViewHome = inflater.inflate(R.layout.fragment_intro_0, container, false);

        ImageView image = (ImageView) mViewHome.findViewById(R.id.image);
        NPTextView title = (NPTextView) mViewHome.findViewById(R.id.title);
        NPTextView desc = (NPTextView) mViewHome.findViewById(R.id.desc);
        NPTextView skip = (NPTextView) mViewHome.findViewById(R.id.skip);

        try {
            Picasso.with(getActivity()).load(R.drawable.intro_1).into(image);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        title.setText(getString(R.string.intro0_title));
        desc.setText(getString(R.string.intro0_desc));
        skip.setText(getString(R.string.skip));
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                end();
            }
        });

    }

    private void page1(LayoutInflater inflater, ViewGroup container)
    {
        mViewHome = inflater.inflate(R.layout.fragment_intro_1, container, false);
        ImageView image = (ImageView) mViewHome.findViewById(R.id.image);
        NPTextView title = (NPTextView) mViewHome.findViewById(R.id.title);
        NPTextView desc = (NPTextView) mViewHome.findViewById(R.id.desc);
        NPTextView skip = (NPTextView) mViewHome.findViewById(R.id.skip);

        try {
            Picasso.with(getActivity()).load(R.drawable.intro_2).into(image);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        title.setText(getString(R.string.intro1_title));
        desc.setText(getString(R.string.intro1_desc));
        skip.setText(getString(R.string.skip));
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                end();

            }
        });
    }

    private void page2(LayoutInflater inflater, ViewGroup container)
    {
        mViewHome = inflater.inflate(R.layout.fragment_intro_2, container, false);
        ImageView image = (ImageView) mViewHome.findViewById(R.id.image);
        NPTextView title = (NPTextView) mViewHome.findViewById(R.id.title);
        NPTextView desc = (NPTextView) mViewHome.findViewById(R.id.desc);
        NPTextView skip = (NPTextView) mViewHome.findViewById(R.id.skip);

        try {
            Picasso.with(getActivity()).load(R.drawable.intro_3).into(image);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        title.setText(getString(R.string.intro2_title));
        desc.setText(getString(R.string.intro2_desc));
        skip.setText(getString(R.string.skip));
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                end();

            }
        });
    }

    private void page3(LayoutInflater inflater, ViewGroup container)
    {
        mViewHome = inflater.inflate(R.layout.fragment_intro_3, container, false);
        ImageView image = (ImageView) mViewHome.findViewById(R.id.image);
        NPTextView title = (NPTextView) mViewHome.findViewById(R.id.title);
        NPTextView desc = (NPTextView) mViewHome.findViewById(R.id.desc);
        NPTextView skip = (NPTextView) mViewHome.findViewById(R.id.skip);

        try {
            Picasso.with(getActivity()).load(R.drawable.intro_4).into(image);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        title.setText(getString(R.string.intro3_title));
        desc.setText(getString(R.string.intro3_desc));
        skip.setText(getString(R.string.skip));
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                end();

            }
        });
    }

    private void page4(LayoutInflater inflater, ViewGroup container)
    {
        mViewHome = inflater.inflate(R.layout.fragment_intro_4, container, false);
        ImageView image = (ImageView) mViewHome.findViewById(R.id.image);
        NPTextView title = (NPTextView) mViewHome.findViewById(R.id.title);
        NPTextView desc = (NPTextView) mViewHome.findViewById(R.id.desc);
        NPTextView skip = (NPTextView) mViewHome.findViewById(R.id.skip);

        try {
            Picasso.with(getActivity()).load(R.drawable.intro_5).into(image);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        title.setText(getString(R.string.intro4_title));
        desc.setText(getString(R.string.intro4_desc));
        skip.setText(getString(R.string.skip));
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                end();

            }
        });
    }

    private void page5(LayoutInflater inflater, ViewGroup container)
    {
        mViewHome = inflater.inflate(R.layout.fragment_intro_5, container, false);
        ImageView image = (ImageView) mViewHome.findViewById(R.id.image);
        NPTextView title = (NPTextView) mViewHome.findViewById(R.id.title);
        NPTextView desc = (NPTextView) mViewHome.findViewById(R.id.desc);
        NPTextView skip = (NPTextView) mViewHome.findViewById(R.id.skip);

        try {
            Picasso.with(getActivity()).load(R.drawable.intro_6).into(image);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        title.setText(getString(R.string.intro5_title));
        desc.setText(getString(R.string.intro5_desc));
        skip.setText(getString(R.string.go));
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                end();

            }
        });
    }
}