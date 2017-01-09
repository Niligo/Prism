/**
 * Copyright 2014 Magnus Woxblom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.niligo.prism.adapter;

import android.app.Dialog;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.niligo.prism.Constants;
import com.niligo.prism.R;
import com.niligo.prism.ServerCoordinator;
import com.niligo.prism.activity.CategorizeBulbsActivity;
import com.niligo.prism.model.GroupType;
import com.niligo.prism.util.Utils;
import com.woxthebox.draglistview.DragItemAdapter;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;

public class ItemAdapter extends DragItemAdapter<CategorizeBulbsActivity.DeviceDisplay, ItemAdapter.ViewHolder> {

    private int mCatLayoutId;
    private int mBulbLayoutId;
    private int mGrabHandleId;
    private DragListView dragListView;
    private CategorizeBulbsActivity categorizeBulbsActivity;

    public ItemAdapter(DragListView dragListView,
                       ArrayList<CategorizeBulbsActivity.DeviceDisplay> list,
                       int catlayoutId,
                       int bulbLayoutId,
                       int grabHandleId,
                       boolean dragOnLongPress,
                       CategorizeBulbsActivity categorizeBulbsActivity) {
        super(dragOnLongPress);
        this.dragListView = dragListView;
        mCatLayoutId = catlayoutId;
        mBulbLayoutId = bulbLayoutId;
        mGrabHandleId = grabHandleId;
        this.categorizeBulbsActivity = categorizeBulbsActivity;
        setHasStableIds(true);
        setItemList(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType)
        {
            case 0:
                View view = LayoutInflater.from(parent.getContext()).inflate(mCatLayoutId, parent, false);
                return new ViewHolder(view, true);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(mBulbLayoutId, parent, false);
                return new ViewHolder(view, false);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        CategorizeBulbsActivity.DeviceDisplay text = mItemList.get(position);
        holder.mText.setText(text.toString());
        holder.itemView.setTag(text);
    }

    @Override
    public int getItemViewType(int position) {

        if (mItemList.get(position).getCategory() != null)
            return 0;

        return 1;
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).hashCode();
    }

    public void clear() {
        mItemList.clear();
        notifyDataSetChanged();
    }

    public void add(CategorizeBulbsActivity.DeviceDisplay d) {
        mItemList.add(d);
        notifyDataSetChanged();
    }

    public void remove(CategorizeBulbsActivity.DeviceDisplay deviceDisplay) {
        mItemList.remove(deviceDisplay);
        notifyDataSetChanged();
    }

    public int getPosition(CategorizeBulbsActivity.DeviceDisplay d) {
        return mItemList.indexOf(d);
    }

    /*public void insert(CategorizeBulbsActivity.DeviceDisplay d, int position) {
        if (position < mItemList.size())
        {
            mItemList.add(position, d);
            notifyDataSetChanged();
        }
    }*/

    public void insert(CategorizeBulbsActivity.DeviceDisplay d, int position) {
        try {
            mItemList.add(position, d);
            notifyDataSetChanged();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends DragItemAdapter<CategorizeBulbsActivity.DeviceDisplay, ViewHolder>.ViewHolder
            implements View.OnClickListener {

        public TextView mText;
        private boolean isCat;

        public ViewHolder(final View itemView, boolean isCat) {
            super(itemView, mGrabHandleId);
            mText = (TextView) itemView.findViewById(R.id.title);
            this.isCat = isCat;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view1) {
            final int itemPosition = dragListView.getRecyclerView().getChildLayoutPosition(view1);
            if (isCat)
            {
                final CategorizeBulbsActivity.DeviceDisplay deviceDisplay = mItemList.get(itemPosition);
                final Dialog view = new Dialog(view1.getContext(), R.style.Theme_Dialog);
                view.setContentView(R.layout.dialog_edit_category);
                view.setCancelable(true);
                view.show();
                view.getWindow().setLayout(Utils.dp2px(view1.getContext()), ViewGroup.LayoutParams.WRAP_CONTENT);
                view.getWindow().setGravity(Gravity.CENTER);
                final EditText name_et = (EditText) view.findViewById(R.id.edittext2);
                name_et.setText(deviceDisplay.getCategory().getName());
                final Spinner type_spinner = (Spinner) view.findViewById(R.id.type_spinner);
                ArrayList<String> types = new ArrayList<String>();
                int pos = 0;
                for (int i = 0; i < GroupType.values().length; i++)
                {
                    if (GroupType.values()[i] != GroupType.NONE)
                        types.add(GroupType.values()[i].name());
                    if (deviceDisplay.getCategory().getType() == GroupType.values()[i])
                        pos = i;
                }
                if (pos >= types.size())
                    pos = 0;
                GroupTypesAdapter groupTypesAdapter = new GroupTypesAdapter(view1.getContext(),
                        R.layout.row_group_type,
                        types);
                type_spinner.setAdapter(groupTypesAdapter);
                type_spinner.setSelection(pos);
                RelativeLayout edit = (RelativeLayout) view.findViewById(R.id.edit);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        String name = name_et.getText().toString().trim();
                        if (name.length() == 0)
                            return;
                        GroupType groupType = GroupType.valueOf((String) type_spinner.getSelectedItem());
                        deviceDisplay.getCategory().setName(name);
                        deviceDisplay.getCategory().setType(groupType);
                        mItemList.remove(itemPosition);
                        mItemList.add(itemPosition, deviceDisplay);
                        notifyDataSetChanged();
                        view.dismiss();
                    }
                });
                RelativeLayout delete = (RelativeLayout) view.findViewById(R.id.delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        mItemList.remove(itemPosition);
                        notifyDataSetChanged();
                        view.dismiss();
                    }
                });
            }
            else
            {
                final CategorizeBulbsActivity.DeviceDisplay deviceDisplay = mItemList.get(itemPosition);
                final String ws = "http://" + deviceDisplay.getIP() + "/api/";
                ServerCoordinator.getInstance()
                        .setBulbCredentials(
                                Constants.DEFAULT_USERNAME,
                                Constants.DEFAULT_PASSWORD,
                                ws
                        )
                        .notifyBulb();
                final Dialog view = new Dialog(view1.getContext(), R.style.Theme_Dialog);
                view.setContentView(R.layout.dialog_edit_bulb);
                view.setCancelable(true);
                view.show();
                view.getWindow().setLayout(Utils.dp2px(view1.getContext()), ViewGroup.LayoutParams.WRAP_CONTENT);
                view.getWindow().setGravity(Gravity.CENTER);
                final EditText name_et = (EditText) view.findViewById(R.id.edittext2);
                name_et.setText(deviceDisplay.toString());
                RelativeLayout edit = (RelativeLayout) view.findViewById(R.id.edit);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        String name = name_et.getText().toString().trim();
                        if (name.length() == 0)
                            return;
                        ServerCoordinator.getInstance()
                                .setBulbCredentials(
                                        Constants.DEFAULT_USERNAME,
                                        Constants.DEFAULT_PASSWORD,
                                        ws
                                )
                                .renameBulb(name);
                        categorizeBulbsActivity.fillBulbs();
                        view.dismiss();
                    }
                });

            }
        }
    }
}
