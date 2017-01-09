package com.niligo.prism.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by mahdi on 10/14/16.
 */

public class ColorFavesBean implements Parcelable {

    private ColorFaveBean fave1;
    private ColorFaveBean fave2;
    private ColorFaveBean fave3;
    private ColorFaveBean fave4;
    private ColorFaveBean fave5;

    public ColorFavesBean(ColorFaveBean fave1, ColorFaveBean fave2, ColorFaveBean fave3, ColorFaveBean fave4, ColorFaveBean fave5) {
        this.fave1 = fave1;
        this.fave2 = fave2;
        this.fave3 = fave3;
        this.fave4 = fave4;
        this.fave5 = fave5;
    }

    public ColorFavesBean() {
    }

    public void setFave1(ColorFaveBean fave1) {
        this.fave1 = fave1;
    }

    public void setFave2(ColorFaveBean fave2) {
        this.fave2 = fave2;
    }

    public void setFave3(ColorFaveBean fave3) {
        this.fave3 = fave3;
    }

    public void setFave4(ColorFaveBean fave4) {
        this.fave4 = fave4;
    }

    public void setFave5(ColorFaveBean fave5) {
        this.fave5 = fave5;
    }

    public ColorFaveBean getFave1() {
        return fave1;
    }

    public ColorFaveBean getFave2() {
        return fave2;
    }

    public ColorFaveBean getFave3() {
        return fave3;
    }

    public ColorFaveBean getFave4() {
        return fave4;
    }

    public ColorFaveBean getFave5() {
        return fave5;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.fave1, flags);
        dest.writeParcelable(this.fave2, flags);
        dest.writeParcelable(this.fave3, flags);
        dest.writeParcelable(this.fave4, flags);
        dest.writeParcelable(this.fave5, flags);
    }

    protected ColorFavesBean(Parcel in) {
        this.fave1 = in.readParcelable(ColorFaveBean.class.getClassLoader());
        this.fave2 = in.readParcelable(ColorFaveBean.class.getClassLoader());
        this.fave3 = in.readParcelable(ColorFaveBean.class.getClassLoader());
        this.fave4 = in.readParcelable(ColorFaveBean.class.getClassLoader());
        this.fave5 = in.readParcelable(ColorFaveBean.class.getClassLoader());
    }

    public static final Creator<ColorFavesBean> CREATOR = new Creator<ColorFavesBean>() {
        @Override
        public ColorFavesBean createFromParcel(Parcel source) {
            return new ColorFavesBean(source);
        }

        @Override
        public ColorFavesBean[] newArray(int size) {
            return new ColorFavesBean[size];
        }
    };
}

