package com.niligo.prism.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.niligo.prism.Constants;
import com.niligo.prism.R;
import com.niligo.prism.activity.MainActivity;
import com.niligo.prism.adapter.TabsViewPagerAdapter;
import com.niligo.prism.util.Utils;


public class SettingsTabFragment extends BaseFragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DevelopersFragment developersFragment;
    private SupportFragment supportFragment;
    private UpdateFragment updateFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager();

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Utils.changeTabsFont(tabLayout);



        ((MainActivity) getActivity()).setOnBackPressedListener(null);
        return view;
    }

    private void setupViewPager() {
        TabsViewPagerAdapter adapter = new TabsViewPagerAdapter(getChildFragmentManager());
        updateFragment = new UpdateFragment();
        developersFragment = new DevelopersFragment();
        supportFragment = new SupportFragment();
        adapter.addFragment(updateFragment, getString(R.string.settings_tab1));
        adapter.addFragment(supportFragment, getString(R.string.settings_tab2));
        adapter.addFragment(developersFragment, getString(R.string.settings_tab3));
        viewPager.setAdapter(adapter);
    }
}

