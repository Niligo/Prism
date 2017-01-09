package com.niligo.prism.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.niligo.prism.NiligoPrismApplication;
import com.niligo.prism.R;
import com.niligo.prism.adapter.NavigationDrawerAdapter;
import com.niligo.prism.callback.OnBackPressedListener;
import com.niligo.prism.fragment.AboutFragment;
import com.niligo.prism.fragment.CloudFragment;
import com.niligo.prism.fragment.DefaultFragment;
import com.niligo.prism.fragment.ProfileFragment;
import com.niligo.prism.fragment.ProfilesFragment;
import com.niligo.prism.fragment.SettingsTabFragment;
import com.niligo.prism.fragment.TroubleshootingFragment;
import com.niligo.prism.model.NavigationDrawerBean;
import com.niligo.prism.util.TypefaceSpan;
import com.niligo.prism.util.Utils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    protected OnBackPressedListener onBackPressedListener;

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolBar;
    private ListView navigation_listview;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("");
        mToolBar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(mToolBar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolBar,
                R.string.drawer_open_content_desc,
                R.string.drawer_close_content_desc
        );
        //mDrawerToggle.setDrawerIndicatorEnabled(false);
        //Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.menu, getTheme());
        //mDrawerToggle.setHomeAsUpIndicator(drawable);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigation_listview = (ListView) navigationView.findViewById(R.id.nav_drawer_listView);
        updateNavigation();
        navigation_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NavigationDrawerBean navigationDrawerBean = (NavigationDrawerBean) parent.getItemAtPosition(position);
                switch (navigationDrawerBean.getId())
                {
                    case 0:
                        fragmentTransaction(new ProfileFragment(), navigationDrawerBean.getTitle());
                        break;

                    case 1:
                        fragmentTransaction(new ProfilesFragment(), navigationDrawerBean.getTitle());
                        break;

                    case 2:
                        Intent intent = new Intent(MainActivity.this, ConfigureBulbsActivity.class);
                        if(NiligoPrismApplication.getInstance().getCurrentProfile() == null)
                        {
                            Utils.showToast(MainActivity.this, R.string.error_profile_null, Toast.LENGTH_LONG);
                            return;
                        }
                        intent.putExtra("profileBean", NiligoPrismApplication.getInstance().getCurrentProfile());
                        startActivity(intent);
                        break;

                    case 3:
                        intent = new Intent(MainActivity.this, CategorizeBulbsActivity.class);
                        if(NiligoPrismApplication.getInstance().getCurrentProfile() == null)
                        {
                            Utils.showToast(MainActivity.this, R.string.error_profile_null, Toast.LENGTH_LONG);
                            return;
                        }
                        intent.putExtra("profileBean", NiligoPrismApplication.getInstance().getCurrentProfile());
                        startActivity(intent);
                        break;

                    case 4:
                        fragmentTransaction(new CloudFragment(), navigationDrawerBean.getTitle());
                        break;

                    case 5:
                        fragmentTransaction(new TroubleshootingFragment(), navigationDrawerBean.getTitle());
                        break;

                    case 6:
                        fragmentTransaction(new SettingsTabFragment(), navigationDrawerBean.getTitle());
                        break;

                    case 7:
                        fragmentTransaction(new AboutFragment(), navigationDrawerBean.getTitle());
                        break;

                    default:
                        fragmentTransaction(new DefaultFragment(), "");
                        break;

                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateNavigation();

        if (NiligoPrismApplication.getInstance().getCurrentProfile() == null)
        {
            fragmentTransaction(new ProfilesFragment(), getString(R.string.profiles));
        }
        else
        {
            fragmentTransaction(new ProfileFragment(), NiligoPrismApplication.getInstance().getCurrentProfile().getName());
        }
    }

    public void updateNavigation() {
        final ArrayList<NavigationDrawerBean> navigationDrawerBeans = new ArrayList<>();
        if (NiligoPrismApplication.getInstance().getCurrentProfile() != null)
            navigationDrawerBeans.add(new NavigationDrawerBean(0, NiligoPrismApplication.getInstance().getCurrentProfile().getName(), R.drawable.menu_home, true));
        navigationDrawerBeans.add(new NavigationDrawerBean(1, getString(R.string.profiles), R.drawable.menu_home, false));
        navigationDrawerBeans.add(new NavigationDrawerBean(3, getString(R.string.categories_bulbs), R.drawable.menu_categories, false));
        navigationDrawerBeans.add(new NavigationDrawerBean(2, getString(R.string.configure_bulbs), R.drawable.menu_config, false));
        navigationDrawerBeans.add(new NavigationDrawerBean(4, getString(R.string.prism_cloud), R.drawable.menu_cloud, false));
        navigationDrawerBeans.add(new NavigationDrawerBean(5, getString(R.string.troubleshooting), R.drawable.menu_trouble, false));
        navigationDrawerBeans.add(new NavigationDrawerBean(6, getString(R.string.settings), R.drawable.menu_settings, false));
        navigationDrawerBeans.add(new NavigationDrawerBean(7, getString(R.string.about), R.drawable.menu_about, false));

        final NavigationDrawerAdapter navigationDrawerAdapter = new NavigationDrawerAdapter(this, navigationDrawerBeans);
        navigation_listview.setAdapter(navigationDrawerAdapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fragmentTransaction(final Fragment fragment, final String title) {

        NiligoPrismApplication.getInstance().back_count = 0;

        int delay = 0;
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            delay = 200;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (fragment != null) {

                    //Fragment current = getSupportFragmentManager().findFragmentByTag(fragment.getClass().getCanonicalName());
                    //if (current != null && current.isVisible())
                    //    return;

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    //fragmentTransaction.setCustomAnimations(R.anim.exit, R.anim.enter);
                    fragmentTransaction.replace(R.id.home_container, fragment, fragment.getClass().getCanonicalName());
                    fragmentTransaction.commit();

                    assert getSupportActionBar() != null;
                    SpannableString spannableString = new SpannableString(title);
                    spannableString.setSpan(new TypefaceSpan(),
                            0, spannableString.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    getSupportActionBar().setTitle(spannableString);
                }

            }
        }, delay);

    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }


    @Override
    protected void onDestroy() {
        onBackPressedListener = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawers();
            return;
        }

        if (onBackPressedListener != null)
            onBackPressedListener.doBack();
        else
        {
            if (NiligoPrismApplication.getInstance().back_count == 0)
            {
                NiligoPrismApplication.getInstance().back_count++;
                Utils.showToast(this, R.string.exit, Toast.LENGTH_SHORT);
            }
            else {
                NiligoPrismApplication.getInstance().back_count = 0;
                finish();
            }

        }
    }

}
