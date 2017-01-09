package com.niligo.prism.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.niligo.prism.NiligoPrismApplication;
import com.niligo.prism.R;
import com.niligo.prism.callback.ProfileButtonClickCallbacks;
import com.niligo.prism.model.ProfileBean;

/**
 * Created by mahdi on 5/8/16 AD.
 */
public class ProfileButton extends RelativeLayout {

    private ImageView bg, plus;
    private NPTextView name;
    private Context context;
    private boolean isFull;
    private ProfileButtonClickCallbacks profileButtonClickCallbacks;
    private int id;

    public ProfileButton(Context context) {
        super(context);
        this.context = context;
    }

    public ProfileButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public ProfileButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View root = LayoutInflater.from(context).inflate(R.layout.button_profile, this, true);
        bg = (ImageView) root.findViewById(R.id.bg);
        plus = (ImageView) root.findViewById(R.id.plus);
        name = (NPTextView) root.findViewById(R.id.name);
        bg.setVisibility(GONE);
        plus.setVisibility(GONE);
        name.setVisibility(GONE);
        isFull = false;
    }

    public void setProfileButtonClickCallbacks(ProfileButtonClickCallbacks profileButtonClickCallbacks, int id) {
        this.profileButtonClickCallbacks = profileButtonClickCallbacks;
        this.id = id;
    }

    public void setEmpty()
    {
        plus.setVisibility(VISIBLE);
        bg.setImageResource(R.drawable.profile_hexa_bw);
        bg.setVisibility(VISIBLE);
        name.setVisibility(GONE);
        isFull = false;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                profileButtonClickCallbacks.profileClick(isFull, id);
            }
        });
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                profileButtonClickCallbacks.profileLongClick(isFull, id);
                return true;
            }
        });
    }

    public void setFull(ProfileBean profileBean)
    {
        plus.setVisibility(GONE);
        if (NiligoPrismApplication.getInstance().getCurrentProfile() != null
                && NiligoPrismApplication.getInstance().getCurrentProfile().getId() == profileBean.getId())
            bg.setImageResource(R.drawable.profile_hexa_colored_selected);
        else
            bg.setImageResource(R.drawable.profile_hexa_colored);
        bg.setVisibility(VISIBLE);
        name.setVisibility(VISIBLE);
        name.setText(profileBean.getName());
        isFull = true;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                profileButtonClickCallbacks.profileClick(isFull, id);
            }
        });
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                profileButtonClickCallbacks.profileLongClick(isFull, id);
                return true;
            }
        });
    }
}
