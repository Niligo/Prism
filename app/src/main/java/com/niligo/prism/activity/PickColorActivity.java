package com.niligo.prism.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.niligo.prism.Constants;
import com.niligo.prism.R;
import com.niligo.prism.widget.Preview;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PickColorActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private RelativeLayout pick;
    private ImageView photo, thumb;
    private String fileName;
    private float dX, dY;
    private int color = Color.argb(255, 255, 255, 255);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_pick);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("");
        mToolBar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(mToolBar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        thumb = (ImageView) findViewById(R.id.thumb);
        photo = (ImageView) findViewById(R.id.photo);
		pick = (RelativeLayout) findViewById(R.id.take);

        fileName = getIntent().getStringExtra("fileName");
        try {
            Picasso.with(this)
                    .load(new File(Constants.PHOTO_PATH + "/" + fileName))
                    .into(photo,  new Callback() {
                        @Override
                        public void onSuccess() {
                            final Bitmap bitmap = ((BitmapDrawable) photo.getDrawable()).getBitmap();
                            photo.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent event) {

                                    switch (event.getAction()) {

                                        case MotionEvent.ACTION_MOVE:
                                            float eventX = event.getX();
                                            float eventY = event.getY();
                                            float[] eventXY = new float[] {eventX, eventY};

                                            Matrix invertMatrix = new Matrix();
                                            ((ImageView)view).getImageMatrix().invert(invertMatrix);

                                            invertMatrix.mapPoints(eventXY);
                                            int x = (int) eventXY[0];
                                            int y = (int) eventXY[1];

                                            if(x < 0){
                                                x = 0;
                                            }else if(x > bitmap.getWidth()-1){
                                                x = bitmap.getWidth()-1;
                                            }

                                            if(y < 0){
                                                y = 0;
                                            }else if(y > bitmap.getHeight()-1){
                                                y = bitmap.getHeight()-1;
                                            }

                                            ViewCompat.animate(thumb)
                                                    .x(event.getRawX())
                                                    .y(event.getRawY())
                                                    .setDuration(0)
                                                    .start();

                                            color = bitmap.getPixel(x,y);
                                            pick.setBackgroundColor(color);
                                            return false;

                                    }
                                    return true;
                                }
                            });
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                end();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        end();
    }

    private void end()
    {
        Intent dataIntent = new Intent();
        dataIntent.putExtra("color", color);
        setResult(RESULT_OK, dataIntent);
        finish();
    }
}


