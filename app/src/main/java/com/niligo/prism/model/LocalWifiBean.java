package com.niligo.prism.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mahdi on 8/9/16.
 */
public class LocalWifiBean implements Parcelable {
    private int id;
    private String ssid;
    private boolean selected;

    public LocalWifiBean(int id, String ssid, boolean selected) {
        this.id = id;
        this.ssid = ssid;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public String getSsid() {
        return ssid;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.ssid);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    protected LocalWifiBean(Parcel in) {
        this.id = in.readInt();
        this.ssid = in.readString();
        this.selected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<LocalWifiBean> CREATOR = new Parcelable.Creator<LocalWifiBean>() {
        @Override
        public LocalWifiBean createFromParcel(Parcel source) {
            return new LocalWifiBean(source);
        }

        @Override
        public LocalWifiBean[] newArray(int size) {
            return new LocalWifiBean[size];
        }
    };
}
