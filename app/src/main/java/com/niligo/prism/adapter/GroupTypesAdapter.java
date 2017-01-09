package com.niligo.prism.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.niligo.prism.R;
import com.niligo.prism.widget.NPTextView;

import java.util.List;

/**
 * Created by mahdi on 8/22/16.
 */
public class GroupTypesAdapter extends ArrayAdapter<String> {

    Context mContext;
    int layoutResourceId;
    List<String> data = null;

    public GroupTypesAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.layoutResourceId = resource;
        this.data = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        final String card = data.get(position);
        NPTextView textViewItem = (NPTextView) convertView.findViewById(android.R.id.text1);
        textViewItem.setText(card);

        return convertView;

    }
}
