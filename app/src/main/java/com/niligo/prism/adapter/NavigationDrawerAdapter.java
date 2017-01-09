package com.niligo.prism.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.niligo.prism.NiligoPrismApplication;
import com.niligo.prism.R;
import com.niligo.prism.model.NavigationDrawerBean;

import java.util.ArrayList;


public class NavigationDrawerAdapter extends BaseAdapter {
    private Activity mContext;
    private ArrayList<NavigationDrawerBean> navigationDrawerBeans;
    private ViewHolder holder;


    public NavigationDrawerAdapter(Activity mContext, ArrayList<NavigationDrawerBean> navigationDrawerBeans) {
        this.mContext = mContext;
        this.navigationDrawerBeans = navigationDrawerBeans;
    }

    @Override
    public int getCount() {
        return navigationDrawerBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return navigationDrawerBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.row_nav_drawer, parent, false);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.nav_drawer_list_row_title);
            holder.image = (ImageView) convertView.findViewById(R.id.nav_drawer_list_row_icon);
            holder.background = (RelativeLayout) convertView.findViewById(R.id.nav_drawer_list_row_content);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NavigationDrawerBean navigationDrawerBean = navigationDrawerBeans.get(position);
        holder.title.setText(navigationDrawerBean.getTitle());
        holder.image.setImageDrawable(mContext.getResources().getDrawable(navigationDrawerBean.getIcon()));

        return convertView;
    }

    public void update(ArrayList<NavigationDrawerBean> navigationDrawerBeans) {
        this.navigationDrawerBeans = navigationDrawerBeans;
    }


    private static class ViewHolder {
        private TextView title;
        private ImageView image;
        private RelativeLayout background;
    }
}