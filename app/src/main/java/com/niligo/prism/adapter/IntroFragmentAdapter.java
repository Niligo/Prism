package com.niligo.prism.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.niligo.prism.fragment.IntroFragment;
import com.niligo.prism.model.IntroBean;

import java.util.ArrayList;

/**
 * Created by mahdi on 8/27/15 AD.
 */
public class IntroFragmentAdapter extends FragmentStatePagerAdapter  {

    private Context mContext;
    private ArrayList<IntroBean> introBeans;

    public IntroFragmentAdapter(FragmentManager fm, Context mContext, ArrayList<IntroBean> introBeans) {
        super(fm);
        this.mContext = mContext;
        this.introBeans = introBeans;
    }

    public IntroFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("introBean", introBeans.get(position));
        IntroFragment introFragment = new IntroFragment();
        introFragment.setArguments(bundle);
        return introFragment;
    }

    @Override
    public int getCount() {
        return introBeans.size();
    }

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }
}
