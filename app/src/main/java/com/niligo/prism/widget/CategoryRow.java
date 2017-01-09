package com.niligo.prism.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.niligo.prism.Constants;
import com.niligo.prism.R;
import com.niligo.prism.ServerCoordinator;
import com.niligo.prism.callback.GetColorCallback;
import com.niligo.prism.callback.GetPowerCallback;
import com.niligo.prism.callback.ProfileButtonClickCallbacks;
import com.niligo.prism.model.BulbBean;
import com.niligo.prism.model.ColorBean;
import com.niligo.prism.model.GroupBulbBean;
import com.niligo.prism.util.Utils;
import com.squareup.picasso.Picasso;

/**
 * Created by mahdi on 5/8/16 AD.
 */
public class CategoryRow extends RelativeLayout {

    private ImageView background, overlay, section, icon;
    private NPTextView title;
    private SwitchCompat switch_;
    private Context context;
    private GroupBulbBean groupBulbBean;
    private Activity activity;
    private ColorBean color;
    private boolean isOn = false;

    public CategoryRow(Context context) {
        super(context);
        this.context = context;
    }

    public CategoryRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CategoryRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View root = LayoutInflater.from(context).inflate(R.layout.row_category, this, true);
        background = (ImageView) root.findViewById(R.id.background);
        overlay = (ImageView) root.findViewById(R.id.overlay);
        section = (ImageView) root.findViewById(R.id.section);
        icon = (ImageView) root.findViewById(R.id.icon);
        title = (NPTextView) root.findViewById(R.id.title);
        switch_ = (SwitchCompat) root.findViewById(R.id.switch_);

        switch_.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {

                for (BulbBean bulbBean : groupBulbBean.getBulbBeens())
                {
                    ServerCoordinator.getInstance()
                            .setBulbCredentials(
                                    bulbBean.getUser(),
                                    bulbBean.getPass(),
                                    bulbBean.getWS()
                            ).setPower(b);
                }

                new Thread(runnable).start();

            }
        });
    }

    public void setGroupBulbBean(Activity activity, final GroupBulbBean groupBulbBean, int height) {
        this.groupBulbBean = groupBulbBean;
        this.activity = activity;
        title.setText(groupBulbBean.getName());
        icon.setImageResource(Utils.getGroupIcon(groupBulbBean.getType()));
        int width = Utils.getScreenSize().width;

        Picasso.with(context)
                .load(Utils.getGroupBackground(groupBulbBean.getType()))
                .centerCrop()
                .resize(width, height)
                .into(background);

        new Thread(runnable).start();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            color = null;
            isOn = false;
            color = CategoryRow.this.groupBulbBean.getColor();
            isOn();
            init();
        }
    };

    private void init()
    {
        CategoryRow.this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (color == null)
                {
                    section.setBackgroundColor(getResources().getColor(android.R.color.black));
                    final ColorDrawable offDrawable  = new ColorDrawable(context.getResources().getColor(R.color.color10));
                    offDrawable.setAlpha(140);
                    overlay.setImageDrawable(offDrawable);
                    //switch_.setChecked(false);
                }
                else
                {
                    section.setBackgroundColor(Color.parseColor(color.getColor()));
                    if (!isOn)
                    {
                        final ColorDrawable offDrawable  = new ColorDrawable(context.getResources().getColor(R.color.color10));
                        offDrawable.setAlpha(140);
                        overlay.setImageDrawable(offDrawable);
                        //switch_.setChecked(false);
                    }
                    else
                    {
                        final ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(color.getColor()));
                        colorDrawable.setAlpha(140);
                        overlay.setImageDrawable(colorDrawable);
                        //switch_.setChecked(true);
                    }
                }
            }
        });
    }

    public void setPower(final boolean power)
    {
        switch_.setChecked(power);
    }

    public ColorBean getColor()
    {
        return color;
    }

    public boolean isOn()
    {
        isOn = false;
        for (BulbBean bulbBean : CategoryRow.this.groupBulbBean.getBulbBeens())
        {
            if (bulbBean.isPower())
            {
                isOn = true;
                break;
            }
        }
        return isOn;
    }

}
