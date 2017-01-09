package com.niligo.prism.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.niligo.prism.NiligoPrismApplication;

/**
 * Created by mahdi on 7/18/2016 AD.
 */
public class NPTextView extends TextView {


    public NPTextView(Context context) {
        super(context);
        init();
    }

    public NPTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NPTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NPTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        if (!isInEditMode())
            setTypeface(NiligoPrismApplication.getInstance().getTypeface());
    }

}
