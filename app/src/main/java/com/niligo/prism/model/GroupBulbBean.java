package com.niligo.prism.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.niligo.prism.Constants;
import com.niligo.prism.ServerCoordinator;
import com.niligo.prism.callback.GetColorCallback;
import com.niligo.prism.callback.GetPowerCallback;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by mahdi on 8/17/16.
 */
public class GroupBulbBean implements Parcelable {

    private int id;
    private String name = "";
    private GroupType type;
    private ArrayList<BulbBean> bulbBeens = new ArrayList<>();
    private int profile_id;

    public GroupBulbBean() {
    }

    public GroupBulbBean(int id, String name, GroupType type, int profile_id) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.profile_id = profile_id;
    }

    public GroupBulbBean(int id, String name, GroupType type, ArrayList<BulbBean> bulbBeens, int profile_id) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.bulbBeens = bulbBeens;
        this.profile_id = profile_id;
    }

    public void setPower(boolean on)
    {
        for (BulbBean bulbBean : bulbBeens)
        {
            bulbBean.setPower(on);
        }
    }

    public ColorBean getColor()
    {
        if (bulbBeens.size() == 0)
            return null;

        return ServerCoordinator.getInstance()
                .setBulbCredentials(
                        bulbBeens.get(0).getUser(),
                        bulbBeens.get(0).getPass(),
                        bulbBeens.get(0).getWS()
                ).getColor();

    }



    public void addBulbBeen(BulbBean bulbBean)
    {
        bulbBeens.add(bulbBean);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public ArrayList<BulbBean> getBulbBeens() {
        return bulbBeens;
    }

    public void setBulbBeens(ArrayList<BulbBean> bulbBeens) {
        this.bulbBeens = bulbBeens;
    }

    public int getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(int profile_id) {
        this.profile_id = profile_id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeTypedList(this.bulbBeens);
        dest.writeInt(this.profile_id);
    }

    protected GroupBulbBean(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : GroupType.values()[tmpType];
        this.bulbBeens = in.createTypedArrayList(BulbBean.CREATOR);
        this.profile_id = in.readInt();
    }

    public static final Creator<GroupBulbBean> CREATOR = new Creator<GroupBulbBean>() {
        @Override
        public GroupBulbBean createFromParcel(Parcel source) {
            return new GroupBulbBean(source);
        }

        @Override
        public GroupBulbBean[] newArray(int size) {
            return new GroupBulbBean[size];
        }
    };
}
