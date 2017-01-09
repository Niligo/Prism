package com.niligo.prism.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by mahdi on 8/17/16.
 */
public class ProfileBean implements Parcelable {

    private int id;
    private String name;
    private ArrayList<GroupBulbBean> groupBulbBeens;
    private String ssid;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ProfileBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProfileBean(int id, String name, ArrayList<GroupBulbBean> groupBulbBeens, String ssid) {
        this.id = id;
        this.name = name;
        this.groupBulbBeens = groupBulbBeens;
        this.ssid = ssid;
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

    public ArrayList<GroupBulbBean> getGroupBulbBeens() {
        return groupBulbBeens;
    }

    public void setGroupBulbBeens(ArrayList<GroupBulbBean> groupBulbBeens) {
        this.groupBulbBeens = groupBulbBeens;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeTypedList(this.groupBulbBeens);
        dest.writeString(this.ssid);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    protected ProfileBean(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.groupBulbBeens = in.createTypedArrayList(GroupBulbBean.CREATOR);
        this.ssid = in.readString();
        this.selected = in.readByte() != 0;
    }

    public static final Creator<ProfileBean> CREATOR = new Creator<ProfileBean>() {
        @Override
        public ProfileBean createFromParcel(Parcel source) {
            return new ProfileBean(source);
        }

        @Override
        public ProfileBean[] newArray(int size) {
            return new ProfileBean[size];
        }
    };
}
