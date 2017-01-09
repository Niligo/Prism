package com.niligo.prism.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.niligo.prism.Constants;
import com.niligo.prism.NiligoPrismApplication;
import com.niligo.prism.R;
import com.niligo.prism.model.ProfileBean;
import com.niligo.prism.util.Utils;
import com.squareup.picasso.Picasso;

public class SplashScreenActivity extends AppCompatActivity {


    private ImageView bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        bg = (ImageView) findViewById(R.id.bg);

        try
        {
            Picasso.with(this).load(R.drawable.splashscreen).into(bg);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sharedPreferences = NiligoPrismApplication.getInstance().getSharedPreferences();
                        if (sharedPreferences.getBoolean(Constants.PREF_IS_INTRO_SHOWN, false))
                        {
                            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        }
                        else
                        {
                            startActivity(new Intent(SplashScreenActivity.this, IntroActivity.class));
                        }
                        finish();
                    }
                });
            }
        }).start();

    }
}
