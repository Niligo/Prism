package com.niligo.prism.adapter;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.niligo.prism.R;
import com.niligo.prism.callback.SwitchChangeCallback;
import com.niligo.prism.model.BulbBean;
import com.niligo.prism.util.Utils;
import com.niligo.prism.widget.NPTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.sephiroth.android.library.widget.HListView;


public class GroupConfigHorizontalAdapter extends BaseAdapter {
    private Activity mContext;
    private ArrayList<BulbBean> bulbBeens;
    private ViewHolder holder;
    private HListView hListView;


    public GroupConfigHorizontalAdapter(Activity mContext, ArrayList<BulbBean> bulbBeens, HListView hListView) {
        this.mContext = mContext;
        this.bulbBeens = bulbBeens;
        this.hListView = hListView;
    }

    @Override
    public int getCount() {
        return bulbBeens.size();
    }

    @Override
    public Object getItem(int position) {
        return bulbBeens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.cell_group_config, parent, false);

            holder = new ViewHolder();
            holder.background = (ImageView) convertView.findViewById(R.id.background);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final BulbBean bulbBean = bulbBeens.get(position);
        if (bulbBean.getId() == -1)
            holder.background.setImageResource(R.drawable.bulb_group);
        else
            holder.background.setImageResource(R.drawable.bulb_single);

        if (position == hListView.getCheckedItemPosition())
            holder.background.setBackgroundColor(mContext.getResources().getColor(R.color.color12));
        else
            holder.background.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));

        return convertView;
    }

    private static class ViewHolder {
        private ImageView background;
    }
}