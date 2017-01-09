package com.niligo.prism.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mahdi on 10/14/16.
 */

public class ColorFaveBean implements Parcelable {
    private String color;
    private int pos;
    private long date;

    public ColorFaveBean(String color, int pos, long date) {
        this.color = color;
        this.pos = pos;
        this.date = date;
    }

    public String getColor() {
        return color;
    }

    public int getPos() {
        return pos;
    }

    public long getDate() {
        return date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.color);
        dest.writeInt(this.pos);
        dest.writeLong(this.date);
    }

    protected ColorFaveBean(Parcel in) {
        this.color = in.readString();
        this.pos = in.readInt();
        this.date = in.readLong();
    }

    public static final Creator<ColorFaveBean> CREATOR = new Creator<ColorFaveBean>() {
        @Override
        public ColorFaveBean createFromParcel(Parcel source) {
            return new ColorFaveBean(source);
        }

        @Override
        public ColorFaveBean[] newArray(int size) {
            return new ColorFaveBean[size];
        }
    };
}
