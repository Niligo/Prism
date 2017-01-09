package com.niligo.prism.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mahdi on 9/24/16.
 */

public class LoginResponseBean extends ResponseBean implements Parcelable {

    private int error_code;
    private String error_message;
    private String token;

    public LoginResponseBean(int error_code, String error_message, String token) {
        this.error_code = error_code;
        this.error_message = error_message;
        this.token = token;
    }

    public int getError_code() {
        return error_code;
    }

    public String getError_message() {
        return error_message;
    }

    public String getToken() {
        return token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.error_code);
        dest.writeString(this.error_message);
        dest.writeString(this.token);
    }

    protected LoginResponseBean(Parcel in) {
        this.error_code = in.readInt();
        this.error_message = in.readString();
        this.token = in.readString();
    }

    public static final Creator<LoginResponseBean> CREATOR = new Creator<LoginResponseBean>() {
        @Override
        public LoginResponseBean createFromParcel(Parcel source) {
            return new LoginResponseBean(source);
        }

        @Override
        public LoginResponseBean[] newArray(int size) {
            return new LoginResponseBean[size];
        }
    };
}
