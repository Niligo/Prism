package com.niligo.prism.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.GsonBuilder;
import com.niligo.prism.Constants;
import com.niligo.prism.NiligoPrismApplication;
import com.niligo.prism.R;
import com.niligo.prism.activity.ConfigureBulbsActivity;
import com.niligo.prism.activity.MainActivity;
import com.niligo.prism.callback.ProfileButtonClickCallbacks;
import com.niligo.prism.entity.ProfileEntity;
import com.niligo.prism.model.BulbBean;
import com.niligo.prism.model.ProfileBean;
import com.niligo.prism.util.Utils;
import com.niligo.prism.widget.ProfileButton;

import org.fourthline.cling.Main;

import java.util.List;


public class ProfilesFragment extends BaseFragment implements ProfileButtonClickCallbacks {

    private ProfileButton cat1, cat2, cat3, cat4, cat5, cat6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profiles, container, false);

        cat1 = (ProfileButton) view.findViewById(R.id.cat1);
        cat2 = (ProfileButton) view.findViewById(R.id.cat2);
        cat3 = (ProfileButton) view.findViewById(R.id.cat3);
        cat4 = (ProfileButton) view.findViewById(R.id.cat4);
        cat5 = (ProfileButton) view.findViewById(R.id.cat5);
        cat6 = (ProfileButton) view.findViewById(R.id.cat6);

        cat1.setProfileButtonClickCallbacks(this, 1);
        cat2.setProfileButtonClickCallbacks(this, 2);
        cat3.setProfileButtonClickCallbacks(this, 3);
        cat4.setProfileButtonClickCallbacks(this, 4);
        cat5.setProfileButtonClickCallbacks(this, 5);
        cat6.setProfileButtonClickCallbacks(this, 6);

        reload();

        ((MainActivity) getActivity()).setOnBackPressedListener(null);
        return view;
    }

    private void reload()
    {
        cat1.setEmpty();
        cat2.setEmpty();
        cat3.setEmpty();
        cat4.setEmpty();
        cat5.setEmpty();
        cat6.setEmpty();

        List<ProfileEntity> profileEntities = NiligoPrismApplication.getInstance().getDatabaseHelper().getProfileEntities();
        for (ProfileEntity profileEntity : profileEntities)
        {
            ProfileBean profileBean = new GsonBuilder().create().fromJson(profileEntity.getProfile_json(), ProfileBean.class);
            if (profileBean != null)
            {
                switch (profileBean.getId())
                {
                    case 1:
                        cat1.setFull(profileBean);
                        break;

                    case 2:
                        cat2.setFull(profileBean);
                        break;

                    case 3:
                        cat3.setFull(profileBean);
                        break;

                    case 4:
                        cat4.setFull(profileBean);
                        break;

                    case 5:
                        cat5.setFull(profileBean);
                        break;

                    case 6:
                        cat6.setFull(profileBean);
                        break;
                }
            }
        }

        ((MainActivity) getActivity()).updateNavigation();

    }

    @Override
    public void profileClick(boolean isFull, final int id) {

        if (isFull)
        {
            List<ProfileEntity> profileEntities = NiligoPrismApplication.getInstance().getDatabaseHelper().getProfileEntities();
            for (ProfileEntity profileEntity : profileEntities)
            {
                Log.e("tag", "entity = " + profileEntity.getProfile_json());
                ProfileBean profileBean = new GsonBuilder().create().fromJson(profileEntity.getProfile_json(), ProfileBean.class);
                if (profileBean != null)
                {
                    if (id == profileBean.getId())
                    {
                        NiligoPrismApplication.getInstance().setCurrentProfile(profileBean);
                        break;
                    }
                }
            }
            ((MainActivity) getActivity()).updateNavigation();
            fragmentTransaction(new ProfileFragment(), NiligoPrismApplication.getInstance().getCurrentProfile().getName());
        }
        else
        {
            final Dialog view = new Dialog(getActivity(), R.style.Theme_Dialog);
            view.setContentView(R.layout.dialog_create_profile);
            view.setCancelable(true);
            if(!getActivity().isFinishing())
                view.show();
            view.getWindow().setLayout(Utils.dp2px(getActivity()), ViewGroup.LayoutParams.WRAP_CONTENT);
            view.getWindow().setGravity(Gravity.CENTER);
            final EditText name_et = (EditText) view.findViewById(R.id.edittext2);
            RelativeLayout config = (RelativeLayout) view.findViewById(R.id.config);
            config.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    String name = name_et.getText().toString();
                    ProfileBean profileBean = new ProfileBean(id, name);
                    NiligoPrismApplication.getInstance().addProfile(profileBean);
                    Intent intent = new Intent(getActivity(), ConfigureBulbsActivity.class);
                    intent.putExtra("profileBean", profileBean);
                    view.dismiss();
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void profileLongClick(boolean isFull, int id) {

        if (!isFull)
            return;

        final Dialog view = new Dialog(getActivity(), R.style.Theme_Dialog);
        view.setContentView(R.layout.dialog_edit_profile);
        view.setCancelable(true);
        if(!getActivity().isFinishing())
            view.show();
        view.getWindow().setLayout(Utils.dp2px(getActivity()), ViewGroup.LayoutParams.WRAP_CONTENT);
        view.getWindow().setGravity(Gravity.CENTER);
        final EditText name_et = (EditText) view.findViewById(R.id.edittext2);
        List<ProfileEntity> profileEntities = NiligoPrismApplication.getInstance().getDatabaseHelper().getProfileEntities();
        ProfileBean profileBean = null;
        ProfileEntity profileEntity = null;
        for (ProfileEntity profileEntity1 : profileEntities)
        {
            ProfileBean profileBean1 = new GsonBuilder().create().fromJson(profileEntity1.getProfile_json(), ProfileBean.class);
            if (profileBean1 != null)
            {
                if (profileBean1.getId() == id)
                {
                    profileBean = profileBean1;
                    profileEntity = profileEntity1;
                    break;
                }
            }
        }
        if (profileBean == null)
            return;

        final ProfileBean profile = profileBean;
        final ProfileEntity profilee = profileEntity;
        name_et.setText(profile.getName());

        RelativeLayout edit = (RelativeLayout) view.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                String name = name_et.getText().toString().trim();
                if (name.length() == 0)
                    return;

                profile.setName(name);
                String element = new GsonBuilder().create().toJson(profile);
                profilee.setProfile_json(element);

                NiligoPrismApplication.getInstance().getDatabaseHelper().updateProfile(profilee);
                NiligoPrismApplication.getInstance().reloadCurrentProfile();
                ((MainActivity) getActivity()).updateNavigation();


                reload();
                view.dismiss();
            }
        });

        RelativeLayout delete = (RelativeLayout) view.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                NiligoPrismApplication.getInstance().getDatabaseHelper().deleteProfile(profilee);
                NiligoPrismApplication.getInstance().reloadCurrentProfile();
                ((MainActivity) getActivity()).updateNavigation();

                reload();
                view.dismiss();
            }
        });

    }
}

