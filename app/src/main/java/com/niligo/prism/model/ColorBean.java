package com.niligo.prism.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mahdi on 9/20/16.
 */

public class ColorBean implements Parcelable {

    private String color;
    private int fade;
    private int power;

    public ColorBean(String color, int fade, int power) {
        this.color = color;
        this.fade = fade;
        this.power = power;
    }

    public String getColor() {
        return "#" + color;
    }

    public int getFade() {
        return fade;
    }

    public int getPower() {
        return power;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.color);
        dest.writeInt(this.fade);
        dest.writeInt(this.power);
    }

    protected ColorBean(Parcel in) {
        this.color = in.readString();
        this.fade = in.readInt();
        this.power = in.readInt();
    }

    public static final Parcelable.Creator<ColorBean> CREATOR = new Parcelable.Creator<ColorBean>() {
        @Override
        public ColorBean createFromParcel(Parcel source) {
            return new ColorBean(source);
        }

        @Override
        public ColorBean[] newArray(int size) {
            return new ColorBean[size];
        }
    };
}
