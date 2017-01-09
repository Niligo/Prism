package com.niligo.prism.fragment;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;

import com.niligo.prism.activity.MainActivity;
import com.niligo.prism.callback.OnBackPressedListener;

public abstract class BaseFragment extends Fragment {

    public BaseFragment() {
        super();
    }

    public void fragmentTransaction(Fragment fragment, String title) {
        ((MainActivity) getActivity()).fragmentTransaction(fragment, title);
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        ((MainActivity) getActivity()).setOnBackPressedListener(onBackPressedListener);
    }

}
