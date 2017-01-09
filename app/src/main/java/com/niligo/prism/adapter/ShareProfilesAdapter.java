package com.niligo.prism.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.niligo.prism.R;
import com.niligo.prism.model.LocalWifiBean;
import com.niligo.prism.model.ProfileBean;

import java.util.ArrayList;


public class ShareProfilesAdapter extends BaseAdapter {
    private Activity mContext;
    private ArrayList<ProfileBean> profileBeens;
    private ViewHolder holder;
    private ListView listView;


    public ShareProfilesAdapter(Activity mContext, ArrayList<ProfileBean> profileBeens, ListView listView) {
        this.mContext = mContext;
        this.profileBeens = profileBeens;
        this.listView = listView;
    }

    @Override
    public int getCount() {
        return profileBeens.size();
    }

    @Override
    public Object getItem(int position) {
        return profileBeens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.row_local_wifi, parent, false);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.nav_drawer_list_row_title);
            holder.image = (ImageView) convertView.findViewById(R.id.nav_drawer_list_row_icon);
            holder.background = (RelativeLayout) convertView.findViewById(R.id.nav_drawer_list_row_content);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProfileBean profileBean = profileBeens.get(position);
        holder.title.setText(profileBean.getName());

        if (profileBean.isSelected())
            holder.image.setImageResource(R.drawable.bulb_selected);
        else
            holder.image.setImageResource(R.drawable.bulb_unselected);

        return convertView;
    }

    public void update(ArrayList<ProfileBean> profiles) {
        this.profileBeens = profiles;
        notifyDataSetChanged();
    }

    public ArrayList<ProfileBean> getProfileBeens() {
        return profileBeens;
    }

    private static class ViewHolder {
        private TextView title;
        private ImageView image;
        private RelativeLayout background;
    }
}