package com.niligo.prism.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mahdi on 4/12/16 AD.
 */
public class NavigationDrawerBean implements Parcelable {

    private int id;
    private String title;
    private int icon;
    private boolean isSelected;


    public NavigationDrawerBean(int id, String title, int icon, boolean isSelected) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.isSelected = isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public boolean isSelected() {
        return isSelected;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.icon);
        dest.writeByte(isSelected ? (byte) 1 : (byte) 0);
    }

    protected NavigationDrawerBean(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.icon = in.readInt();
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<NavigationDrawerBean> CREATOR = new Creator<NavigationDrawerBean>() {
        @Override
        public NavigationDrawerBean createFromParcel(Parcel source) {
            return new NavigationDrawerBean(source);
        }

        @Override
        public NavigationDrawerBean[] newArray(int size) {
            return new NavigationDrawerBean[size];
        }
    };
}
