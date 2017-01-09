package com.niligo.prism.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mahdi on 9/24/16.
 */

public class ForgotPassResponseBean extends ResponseBean implements Parcelable {

    private int error_code;
    private String error_message;

    public ForgotPassResponseBean(int error_code, String error_message) {
        this.error_code = error_code;
        this.error_message = error_message;
    }

    public int getError_code() {
        return error_code;
    }

    public String getError_message() {
        return error_message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.error_code);
        dest.writeString(this.error_message);
    }

    protected ForgotPassResponseBean(Parcel in) {
        this.error_code = in.readInt();
        this.error_message = in.readString();
    }

    public static final Creator<ForgotPassResponseBean> CREATOR = new Creator<ForgotPassResponseBean>() {
        @Override
        public ForgotPassResponseBean createFromParcel(Parcel source) {
            return new ForgotPassResponseBean(source);
        }

        @Override
        public ForgotPassResponseBean[] newArray(int size) {
            return new ForgotPassResponseBean[size];
        }
    };
}
