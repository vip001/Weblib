package com.vip1002.jsbridge.interfaces;


import android.os.Parcel;
import android.os.Parcelable;

public class AidlError implements Parcelable {
    public int code;
    public String message;
    public String extra;


    public AidlError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    protected AidlError(Parcel in) {
        code = in.readInt();
        message = in.readString();
        extra = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(message);
        dest.writeString(extra);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AidlError> CREATOR = new Creator<AidlError>() {
        @Override
        public AidlError createFromParcel(Parcel in) {
            return new AidlError(in);
        }

        @Override
        public AidlError[] newArray(int size) {
            return new AidlError[size];
        }
    };
}
