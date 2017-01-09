package com.niligo.prism.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.niligo.prism.NiligoPrismApplication;
import com.niligo.prism.R;
import com.niligo.prism.model.LocalWifiBean;
import com.niligo.prism.model.NavigationDrawerBean;

import java.util.ArrayList;


public class LocalWifiAdapter extends BaseAdapter {
    private Activity mContext;
    private ArrayList<LocalWifiBean> localWifiBeens;
    private ViewHolder holder;


    public LocalWifiAdapter(Activity mContext, ArrayList<LocalWifiBean> localWifiBeens) {
        this.mContext = mContext;
        this.localWifiBeens = localWifiBeens;
    }

    @Override
    public int getCount() {
        return localWifiBeens.size();
    }

    @Override
    public Object getItem(int position) {
        return localWifiBeens.get(position);
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

        LocalWifiBean localWifiBean = localWifiBeens.get(position);
        holder.title.setText(localWifiBean.getSsid());
        if (localWifiBean.isSelected())
            holder.image.setImageResource(R.drawable.bulb_selected);
        else
            holder.image.setImageResource(R.drawable.bulb_unselected);

        return convertView;
    }

    public void update(ArrayList<LocalWifiBean> localWifiBeens) {
        this.localWifiBeens = localWifiBeens;
    }


    private static class ViewHolder {
        private TextView title;
        private ImageView image;
        private RelativeLayout background;
    }
}