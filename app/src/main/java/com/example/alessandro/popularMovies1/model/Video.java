package com.example.alessandro.popularMovies1.model;

import android.os.Parcel;
import android.os.Parcelable;

// Video class that is used to store Video data
public class Video implements Parcelable {

    static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
    private final String mId;
    private final String mKey;
    private final String mName;

    public Video(String id, String key, String name) {
        mId = id;
        mKey = key;
        mName = name;
    }

    private Video(Parcel in) {
        mId = in.readString();
        mKey = in.readString();
        mName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(mId);
        dest.writeString(mKey);
        dest.writeString(mName);
    }

    public String getId() {
        return mId;
    }

    public String getKey() {
        return mKey;
    }

    public String getName() {
        return mName;
    }

}
