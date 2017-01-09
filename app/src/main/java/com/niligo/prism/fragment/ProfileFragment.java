package com.niligo.prism.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.niligo.prism.NiligoPrismApplication;
import com.niligo.prism.R;
import com.niligo.prism.activity.GroupConfigActivity;
import com.niligo.prism.activity.MainActivity;
import com.niligo.prism.callback.GetPowerCallback;
import com.niligo.prism.model.GroupBulbBean;
import com.niligo.prism.model.ProfileBean;
import com.niligo.prism.util.Utils;
import com.niligo.prism.widget.CategoryRow;

import java.util.ArrayList;

/**
 * Created by mahdi on 9/15/15 AD.
 */
public class ProfileFragment extends BaseFragment {


    private LinearLayout lin;
    private ArrayList<GroupBulbBean> groupBulbBeens;
    private ArrayList<CategoryRow> categoryRows;
    private ProfileBean profileBean;
    private MenuItem toggleGroupsItem;
    private static final int COUNT = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mViewHome = inflater.inflate(R.layout.fragment_profile, container, false);

        profileBean = NiligoPrismApplication.getInstance().getCurrentProfile();
        if (profileBean == null)
            return mViewHome;

        lin = (LinearLayout) mViewHome.findViewById(R.id.lin);

        categoryRows = new ArrayList<>();
        groupBulbBeens = profileBean.getGroupBulbBeens();
        if (groupBulbBeens != null)
        {
            lin.removeAllViews();
            for (final GroupBulbBean groupBulbBean : groupBulbBeens)
            {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.category, lin, false);

                final CategoryRow categoryRow = (CategoryRow) view.findViewById(R.id.category_row);

                int size = groupBulbBeens.size();
                int height;

                if (size <= COUNT)
                    height = (Utils.getScreenSize().height - Utils.getActionbarHeight((AppCompatActivity) getActivity()) - Utils.getStatusbarHeight((AppCompatActivity) getActivity()))/ size;
                else
                    height = (Utils.getScreenSize().height - Utils.getActionbarHeight((AppCompatActivity) getActivity()) - Utils.getStatusbarHeight((AppCompatActivity) getActivity())) / COUNT;

                categoryRow.setGroupBulbBean(getActivity(), groupBulbBean, height);
                categoryRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), GroupConfigActivity.class);
                        intent.putExtra("group", groupBulbBean);
                        startActivity(intent);
                    }
                });

                categoryRows.add(categoryRow);
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = height;
                view.setLayoutParams(layoutParams);
                lin.addView(view);

            }
        }



        ((MainActivity) getActivity()).setOnBackPressedListener(null);
        return mViewHome;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile, menu);
        toggleGroupsItem = menu.findItem(R.id.refresh);
        if(groupBulbBeens != null && groupBulbBeens.size() < 2)
            toggleGroupsItem.setVisible(false);

        View view = MenuItemCompat.getActionView(toggleGroupsItem);
        if (view != null)
        {
            final SwitchCompat switchCompat = (SwitchCompat) view.findViewById(R.id.toggle_all_groups);
            boolean isOn = false;
            for (CategoryRow categoryRow : categoryRows)
            {
                if (!isOn)
                    isOn = categoryRow.isOn();
            }
            switchCompat.setChecked(isOn);
            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                    for (CategoryRow categoryRow : categoryRows)
                    {
                        categoryRow.setPower(isChecked);
                    }
                }
            });
        }
    }


}