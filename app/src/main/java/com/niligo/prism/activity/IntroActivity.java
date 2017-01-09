package com.niligo.prism.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.niligo.prism.R;
import com.niligo.prism.adapter.IntroFragmentAdapter;
import com.niligo.prism.model.IntroBean;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

/**
 * Created by mahdi on 7/18/2016 AD.
 */
public class IntroActivity extends AppCompatActivity {


    private CirclePageIndicator circlePageIndicator;
    private ViewPager viewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        viewPager = (ViewPager) findViewById(R.id.pager);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

        ArrayList<IntroBean> introBeans = new ArrayList<>();
        for (int i = 0; i < 6; i++)
            introBeans.add(new IntroBean(i));

        final IntroFragmentAdapter adapter = new IntroFragmentAdapter(getSupportFragmentManager(),
                this,
                introBeans);
        viewPager.setAdapter(adapter);
        circlePageIndicator.setViewPager(viewPager);
        circlePageIndicator.setSnap(true);
        float density = getResources().getDisplayMetrics().density;
        circlePageIndicator.setRadius(4 * density);
        circlePageIndicator.setPageColor(getResources().getColor(android.R.color.transparent));
        circlePageIndicator.setFillColor(getResources().getColor(android.R.color.white));
        circlePageIndicator.setStrokeWidth(1);
        circlePageIndicator.setStrokeColor(getResources().getColor(R.color.color1));
        circlePageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}
