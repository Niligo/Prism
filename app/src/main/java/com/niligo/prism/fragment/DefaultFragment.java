package com.niligo.prism.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.niligo.prism.R;
import com.niligo.prism.activity.MainActivity;


public class DefaultFragment extends BaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_default, container, false);

        ((MainActivity) getActivity()).setOnBackPressedListener(null);
        return view;
    }
}

