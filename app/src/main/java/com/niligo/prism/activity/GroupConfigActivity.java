package com.niligo.prism.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.niligo.prism.Constants;
import com.niligo.prism.NiligoPrismApplication;
import com.niligo.prism.R;
import com.niligo.prism.ServerCoordinator;
import com.niligo.prism.adapter.GroupConfigHorizontalAdapter;
import com.niligo.prism.callback.GetColorCallback;
import com.niligo.prism.model.BulbBean;
import com.niligo.prism.model.ColorBean;
import com.niligo.prism.model.ColorFavesBean;
import com.niligo.prism.model.GroupBulbBean;
import com.niligo.prism.model.ProfileBean;
import com.niligo.prism.util.TypefaceSpan;
import com.niligo.prism.util.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it.sephiroth.android.library.widget.AbsHListView;
import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;

public class GroupConfigActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST = 1000;
    private ColorPicker picker;
    private SVBar svBar;
    private ImageView schedule, camera, fave, fave2, power;
    private HListView hListView;
    private Toolbar mToolBar;
    private GroupBulbBean group;
    private BulbBean current_bulb;
    private boolean isOn;
    private int last_color = 0;
    private Thread thread;
    private boolean ended;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_config);

        group = getIntent().getParcelableExtra("group");
        if (group == null)
            group = new GroupBulbBean();

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("");
        mToolBar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(mToolBar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(group.getName());

        picker = (ColorPicker) findViewById(R.id.picker);
        svBar = (SVBar) findViewById(R.id.svbar);
        picker.addSVBar(svBar);
        schedule = (ImageView) findViewById(R.id.schedule);
        camera = (ImageView) findViewById(R.id.camera);
        fave = (ImageView) findViewById(R.id.fave);
        fave2 = (ImageView) findViewById(R.id.fave2);
        power = (ImageView) findViewById(R.id.power);
        hListView = (HListView) findViewById(R.id.hlistview);
        hListView.setDivider(new ColorDrawable(getResources().getColor(R.color.color1)));

        final Drawable wrappedDrawable = DrawableCompat.wrap(getResources().getDrawable(R.drawable.favorite2));
        //DrawableCompat.setTint(wrappedDrawable, picker.getColor());
        //fave2.setImageDrawable(wrappedDrawable);

        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(final int color) {

                DrawableCompat.setTint(wrappedDrawable, color);
                fave2.setImageDrawable(wrappedDrawable);

            }
        });

        ArrayList<BulbBean> adapter_bulbbeans = new ArrayList<>();
        adapter_bulbbeans.add(new BulbBean(-1));
        adapter_bulbbeans.addAll(group.getBulbBeens());
        final GroupConfigHorizontalAdapter groupConfigHorizontalAdapter = new GroupConfigHorizontalAdapter(this, adapter_bulbbeans, hListView);
        hListView.setAdapter(groupConfigHorizontalAdapter);
        hListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        hListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hListView.setItemChecked(position, true);
                if (position == 0)
                {
                    setTitle(group.getName());
                    current_bulb = null;
                    isOn = false;
                    for (final BulbBean bulbBean : group.getBulbBeens())
                    {
                        ColorBean colorBean = ServerCoordinator.getInstance()
                                .setBulbCredentials(
                                        bulbBean.getUser(),
                                        bulbBean.getPass(),
                                        bulbBean.getWS()
                                ).getColor();

                        if (colorBean != null && !isOn)
                            isOn = colorBean.getPower() == 1;

                    }
                    power.setImageResource(isOn ? R.drawable.power_on : R.drawable.power_off);

                }
                else
                {
                    current_bulb = (BulbBean) parent.getItemAtPosition(position);
                    setTitle(current_bulb.getName());
                    ColorBean colorBean = ServerCoordinator.getInstance()
                            .setBulbCredentials(
                                    current_bulb.getUser(),
                                    current_bulb.getPass(),
                                    current_bulb.getWS()
                            ).getColor();

                    if (colorBean != null)
                    {
                        picker.setColor(Color.parseColor(colorBean.getColor()));
                        isOn = colorBean.getPower() == 1;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                power.setImageResource(isOn ? R.drawable.power_on : R.drawable.power_off);
                            }
                        });
                    }

                }
            }
        });
        hListView.performItemClick(
                hListView.getAdapter().getView(0, null, null),
                0,
                hListView.getAdapter().getItemId(0));


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Utils.showWaiting(GroupConfigActivity.this);
                if (Build.VERSION.SDK_INT >= 23)
                {
                    if (ContextCompat.checkSelfPermission(GroupConfigActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            ||
                            ContextCompat.checkSelfPermission(GroupConfigActivity.this,
                                    Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(GroupConfigActivity.this,
                                new String[]{
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.CAMERA
                                },
                                MY_PERMISSIONS_REQUEST);
                    }
                }
                else
                {
                    startActivityForResult(new Intent(GroupConfigActivity.this, CameraActivity.class), 1000);
                }

            }
        });

        power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOn = !isOn;
                GroupConfigActivity.this.power.setImageResource(isOn ? R.drawable.power_on : R.drawable.power_off);
                if (current_bulb == null)
                {
                    for (final BulbBean bulbBean : group.getBulbBeens())
                    {
                        ServerCoordinator.getInstance()
                                .setBulbCredentials(
                                        bulbBean.getUser(),
                                        bulbBean.getPass(),
                                        bulbBean.getWS()
                                ).setPower(isOn);
                    }
                }
                else
                {
                    ServerCoordinator.getInstance()
                            .setBulbCredentials(
                                    current_bulb.getUser(),
                                    current_bulb.getPass(),
                                    current_bulb.getWS()
                            ).setPower(isOn);

                }
            }
        });

        fave2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.putFaveColor(picker.getColor());
                Utils.showToast(GroupConfigActivity.this, R.string.success_fav_color, Toast.LENGTH_SHORT);
            }
        });

        fave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog view = new Dialog(GroupConfigActivity.this, R.style.Theme_Dialog);
                view.setContentView(R.layout.dialog_color_fav);
                view.setCancelable(true);
                if(!isFinishing())
                    view.show();
                view.getWindow().setLayout(Utils.dp2px(GroupConfigActivity.this), ViewGroup.LayoutParams.WRAP_CONTENT);
                view.getWindow().setGravity(Gravity.CENTER);
                final ImageView fave1 = (ImageView) view.findViewById(R.id.fave1);
                final ImageView fave2 = (ImageView) view.findViewById(R.id.fave2);
                final ImageView fave3 = (ImageView) view.findViewById(R.id.fave3);
                final ImageView fave4 = (ImageView) view.findViewById(R.id.fave4);
                final ImageView fave5 = (ImageView) view.findViewById(R.id.fave5);

                ColorFavesBean colorFavesBean = Utils.getColorsFromSharedPref();
                if (colorFavesBean == null)
                    colorFavesBean = new ColorFavesBean();
                final String color1 = colorFavesBean.getFave1() != null ? colorFavesBean.getFave1().getColor() : "";
                final String color2 = colorFavesBean.getFave2() != null ? colorFavesBean.getFave2().getColor() : "";
                final String color3 = colorFavesBean.getFave3() != null ? colorFavesBean.getFave3().getColor() : "";
                final String color4 = colorFavesBean.getFave4() != null ? colorFavesBean.getFave4().getColor() : "";
                final String color5 = colorFavesBean.getFave5() != null ? colorFavesBean.getFave5().getColor() : "";


                final Drawable wrappedDrawable1 = DrawableCompat.wrap(getResources().getDrawable(R.drawable.favorite_color1));
                if (!color1.equals(""))
                    DrawableCompat.setTint(wrappedDrawable1, Integer.parseInt(color1));
                else
                    DrawableCompat.setTint(wrappedDrawable1, getResources().getColor(android.R.color.black));
                fave1.setImageDrawable(wrappedDrawable1);

                final Drawable wrappedDrawable2 = DrawableCompat.wrap(getResources().getDrawable(R.drawable.favorite_color2));
                if (!color2.equals(""))
                    DrawableCompat.setTint(wrappedDrawable2, Integer.parseInt(color2));
                else
                    DrawableCompat.setTint(wrappedDrawable2, getResources().getColor(android.R.color.black));
                fave2.setImageDrawable(wrappedDrawable2);

                final Drawable wrappedDrawable3 = DrawableCompat.wrap(getResources().getDrawable(R.drawable.favorite_color3));
                if (!color3.equals(""))
                    DrawableCompat.setTint(wrappedDrawable3, Integer.parseInt(color3));
                else
                    DrawableCompat.setTint(wrappedDrawable3, getResources().getColor(android.R.color.black));
                fave3.setImageDrawable(wrappedDrawable3);

                final Drawable wrappedDrawable4 = DrawableCompat.wrap(getResources().getDrawable(R.drawable.favorite_color4));
                if (!color4.equals(""))
                    DrawableCompat.setTint(wrappedDrawable4, Integer.parseInt(color4));
                else
                    DrawableCompat.setTint(wrappedDrawable4, getResources().getColor(android.R.color.black));
                fave4.setImageDrawable(wrappedDrawable4);

                final Drawable wrappedDrawable5 = DrawableCompat.wrap(getResources().getDrawable(R.drawable.favorite_color5));
                if (!color5.equals(""))
                    DrawableCompat.setTint(wrappedDrawable5, Integer.parseInt(color5));
                else
                    DrawableCompat.setTint(wrappedDrawable5, getResources().getColor(android.R.color.black));
                fave5.setImageDrawable(wrappedDrawable5);

                RelativeLayout done = (RelativeLayout) view.findViewById(R.id.done);
                fave1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!color1.equals(""))
                            picker.setColor(Integer.parseInt(color1));
                        view.dismiss();
                    }
                });
                fave2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!color2.equals(""))
                            picker.setColor(Integer.parseInt(color2));
                        view.dismiss();
                    }
                });
                fave3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!color3.equals(""))
                            picker.setColor(Integer.parseInt(color3));
                        view.dismiss();
                    }
                });
                fave4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!color4.equals(""))
                            picker.setColor(Integer.parseInt(color4));
                        view.dismiss();
                    }
                });
                fave5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!color5.equals(""))
                            picker.setColor(Integer.parseInt(color5));
                        view.dismiss();
                    }
                });
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        view.dismiss();
                    }
                });
            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog view = new Dialog(GroupConfigActivity.this, R.style.Theme_Dialog);
                view.setContentView(R.layout.dialog_schedule);
                view.setCancelable(true);
                if(!isFinishing())
                    view.show();
                view.getWindow().setLayout(Utils.dp2px(GroupConfigActivity.this), ViewGroup.LayoutParams.WRAP_CONTENT);
                view.getWindow().setGravity(Gravity.CENTER);
                final Spinner spinner= (Spinner) view.findViewById(R.id.spinner);
                ArrayList<String> strings = new ArrayList<String>();
                strings.add("ON");
                strings.add("OFF");
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(GroupConfigActivity.this, R.layout.row_group_type, strings);
                spinner.setAdapter(arrayAdapter);
                final EditText hour_et = (EditText) view.findViewById(R.id.edittext2);
                final EditText minute_et = (EditText) view.findViewById(R.id.edittext1);
                final RelativeLayout config = (RelativeLayout) view.findViewById(R.id.config);
                config.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int hour = -1;
                        int minute = -1;
                        try{
                            hour = Integer.parseInt(hour_et.getText().toString().trim());
                            minute = Integer.parseInt(minute_et.getText().toString().trim());
                        }
                        catch (Exception e)
                        {
                            Utils.showToast(GroupConfigActivity.this, R.string.error_invalid_time, Toast.LENGTH_LONG);
                            return;
                        }
                        if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
                        {
                            Utils.showToast(GroupConfigActivity.this, R.string.error_invalid_time, Toast.LENGTH_LONG);
                            return;
                        }
                        Calendar calendar = Calendar.getInstance();
                        int current_hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int current_minute = calendar.get(Calendar.MINUTE);
                        final int diff;
                        if (hour > current_hour || (hour == current_hour && minute > current_minute))
                        {
                            int diff_hour = hour - current_hour;
                            int diff_minute = minute - current_minute;
                            diff = diff_hour * 60 * 60 + diff_minute * 60;
                        }
                        else
                        {
                            Calendar tommorow = Calendar.getInstance();
                            tommorow.set(Calendar.DAY_OF_MONTH, tommorow.get(Calendar.DAY_OF_MONTH) + 1);
                            tommorow.set(Calendar.HOUR_OF_DAY, hour);
                            tommorow.set(Calendar.MINUTE, minute);
                            diff = (int) ((tommorow.getTime().getTime() - new Date().getTime()) / 1000);
                        }
                        boolean on = false;
                        if ("ON".equals(spinner.getSelectedItem()))
                        {
                            on = true;
                        }
                        final boolean power = on;
                        final String scolor = String.format("%06X", 0xFFFFFF & picker.getColor());
                        if (current_bulb == null)
                        {
                            for (final BulbBean bulbBean : group.getBulbBeens())
                            {
                                ServerCoordinator.getInstance()
                                        .setBulbCredentials(
                                                bulbBean.getUser(),
                                                bulbBean.getPass(),
                                                bulbBean.getWS()
                                        ).schedule(diff, power, scolor);
                            }
                        }
                        else
                        {
                            ServerCoordinator.getInstance()
                                    .setBulbCredentials(
                                            current_bulb.getUser(),
                                            current_bulb.getPass(),
                                            current_bulb.getWS()
                                    ).schedule(diff, power, scolor);
                        }
                        Utils.showToast(GroupConfigActivity.this, R.string.succes_schedule, Toast.LENGTH_SHORT);
                        view.dismiss();
                    }
                });


            }
        });

        createThread();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.hideWaiting();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startActivityForResult(new Intent(GroupConfigActivity.this, CameraActivity.class), MY_PERMISSIONS_REQUEST);
                }
                else
                {
                    Utils.showToast(this, R.string.permission_needed, Toast.LENGTH_LONG);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
            case RESULT_OK:
            {
                if (requestCode == MY_PERMISSIONS_REQUEST)
                {
                    if (data.getExtras() != null)
                    {
                        picker.setColor(data.getExtras().getInt("color"));
                    }
                }
            }
        }
    }

    private void createThread()
    {
        ended = false;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!ended)
                {
                    try {
                        Thread.sleep(Constants.DELAY);
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }

                    if (last_color == 0)
                    {
                        if (group.getBulbBeens().size() == 0)
                            continue;
                        final ColorBean colorBean = ServerCoordinator.getInstance()
                                .setBulbCredentials(
                                        group.getBulbBeens().get(0).getUser(),
                                        group.getBulbBeens().get(0).getPass(),
                                        group.getBulbBeens().get(0).getWS()
                                ).getColor();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (colorBean != null)
                                {
                                    String c = colorBean.getColor();
                                    if (c != null)
                                    {
                                        int color = Color.parseColor(c);
                                        picker.setColor2(color);
                                        last_color = color;
                                    }
                                }
                            }
                        });

                    }
                    else
                    {
                        final int color = picker.getColor();
                        if (color != last_color)
                        {
                            final String scolor = String.format("%06X", 0xFFFFFF & color);
                            last_color = color;
                            if (current_bulb == null)
                            {
                                if (!isOn)
                                    continue;
                                for (final BulbBean bulbBean : group.getBulbBeens())
                                {
                                    ServerCoordinator.getInstance()
                                            .setBulbCredentials(
                                                    bulbBean.getUser(),
                                                    bulbBean.getPass(),
                                                    bulbBean.getWS()
                                            ).state(scolor, Constants.FADE);
                                    try {
                                        Thread.sleep(Constants.DELAY);
                                    } catch (InterruptedException e) {
                                        //e.printStackTrace();
                                    }
                                }

                            }
                            else
                            {
                                if (!isOn)
                                    continue;
                                ServerCoordinator.getInstance()
                                        .setBulbCredentials(
                                                current_bulb.getUser(),
                                                current_bulb.getPass(),
                                                current_bulb.getWS()
                                        ).state(scolor, Constants.FADE);

                            }
                        }
                    }

                }
            }
        });
    }

    private void setTitle(String title)
    {
        SpannableString spannableString = new SpannableString(title);
        spannableString.setSpan(new TypefaceSpan(),
                0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(spannableString);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (thread != null && !thread.isAlive())
        {
            try {
                thread.start();
            }
            catch (Exception e)
            {
                //e.printStackTrace();
                thread = null;
                createThread();
                if (thread != null && !thread.isAlive())
                {
                    try {
                        thread.start();
                    }
                    catch (Exception e2)
                    {
                        //e2.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (thread != null && thread.isAlive() && !thread.isInterrupted())
        {
            try {
                ended = true;
                thread.interrupt();
            }
            catch (Exception e)
            {
                //e.printStackTrace();
            }
        }
    }

}
