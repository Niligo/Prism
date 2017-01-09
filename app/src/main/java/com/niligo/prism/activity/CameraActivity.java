package com.niligo.prism.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.niligo.prism.Constants;
import com.niligo.prism.R;
import com.niligo.prism.util.Utils;
import com.niligo.prism.widget.Preview;

public class CameraActivity extends AppCompatActivity {
	private Preview preview;
    private RelativeLayout buttonClick;
    private Camera camera;
    private Toolbar mToolBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_camera);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("");
        mToolBar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(mToolBar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		preview = new Preview(this, (SurfaceView)findViewById(R.id.surfaceView));
		preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		((RelativeLayout) findViewById(R.id.layout)).addView(preview);
		preview.setKeepScreenOn(true);

        buttonClick = (RelativeLayout) findViewById(R.id.take);
        buttonClick.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
                try {
                    camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Utils.showToast(CameraActivity.this, R.string.error_camera_take_picture_failed, Toast.LENGTH_LONG);
                }
			}
		});
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onResume() {
		super.onResume();
		int numCams = Camera.getNumberOfCameras();
		if(numCams > 0){
			try{
				camera = Camera.open(0);
                camera.setDisplayOrientation(90);
				camera.startPreview();
				preview.setCamera(camera);
			} catch (RuntimeException ex){
				ex.printStackTrace();
			}
		}
	}

	@Override
	protected void onPause() {
		if(camera != null) {
			camera.stopPreview();
			preview.setCamera(null);
			camera.release();
			camera = null;
		}
		super.onPause();
	}

	private void resetCam() {
		camera.startPreview();
		preview.setCamera(camera);
	}

	private void refreshGallery(File file) {
		Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(Uri.fromFile(file));
		sendBroadcast(mediaScanIntent);
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {

		}
	};

    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			new SaveImageTask().execute(data);
			resetCam();

		}
	};

	private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        private String fileName = "";
		@Override
		protected Void doInBackground(byte[]... data) {
			FileOutputStream outStream = null;

			try {
				File dir = new File(Constants.PHOTO_PATH);
				dir.mkdirs();				

				fileName = String.format(Locale.US, "%d.jpg", System.currentTimeMillis());
				File outFile = new File(dir, fileName);

				outStream = new FileOutputStream(outFile);
				outStream.write(data[0]);
				outStream.flush();
				outStream.close();

				refreshGallery(outFile);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(CameraActivity.this, PickColorActivity.class);
            intent.putExtra("fileName", fileName);
            startActivityForResult(intent, 1000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
            case RESULT_OK:
            {
                if (requestCode == 1000)
                {
                    if (data.getExtras() != null)
                    {
                        Intent dataIntent = new Intent();
                        dataIntent.putExtra("color", data.getExtras().getInt("color"));
                        setResult(RESULT_OK, dataIntent);
                        finish();
                    }
                }
            }
        }
    }
}


