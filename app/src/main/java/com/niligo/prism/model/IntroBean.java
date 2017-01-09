package com.niligo.prism.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mahdi on 7/19/2016 AD.
 */
public class IntroBean implements Parcelable {

    private int id;

    public IntroBean(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
    }

    protected IntroBean(Parcel in) {
        this.id = in.readInt();
    }

    public static final Parcelable.Creator<IntroBean> CREATOR = new Parcelable.Creator<IntroBean>() {
        @Override
        public IntroBean createFromParcel(Parcel source) {
            return new IntroBean(source);
        }

        @Override
        public IntroBean[] newArray(int size) {
            return new IntroBean[size];
        }
    };
}
