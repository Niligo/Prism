package com.niligo.prism.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.niligo.prism.R;
import com.niligo.prism.model.BulbBean;

import java.util.ArrayList;


public class BulbConfigAdapter extends BaseAdapter {
    private Activity mContext;
    private ArrayList<BulbBean> bulbBeens;
    private ViewHolder holder;


    public BulbConfigAdapter(Activity mContext, ArrayList<BulbBean> bulbBeens) {
        this.mContext = mContext;
        this.bulbBeens = bulbBeens;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.row_bulb_config, parent, false);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.nav_drawer_list_row_title);
            holder.image = (ImageView) convertView.findViewById(R.id.nav_drawer_list_row_icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BulbBean bulbBeen = bulbBeens.get(position);
        holder.title.setText(bulbBeen.getSsid());
        if (bulbBeen.isSelected())
            holder.image.setImageResource(R.drawable.bulb_selected);
        else
            holder.image.setImageResource(R.drawable.bulb_unselected);

        return convertView;
    }

    public void update(ArrayList<BulbBean> bulbBeens) {
        this.bulbBeens = bulbBeens;
    }


    private static class ViewHolder {
        private TextView title;
        private ImageView image;
    }
}