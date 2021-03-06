package com.example.alessandro.popularMovies1.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

// Movie class that is used to store Movie data
public class Movie implements Parcelable {

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    private final String mId;
    private final String mTitle;
    private final String mReleaseDate;
    private final String mVoteAverage;
    private final String mOverview;
    private final Uri mPosterUri;
    private Video[] mVideos;
    private Review[] mReviews;

    public Movie(String id, String title, String releaseDate, String voteAverage, String
            overview, Uri posterUri) {
        mId = id;
        mTitle = title;
        mReleaseDate = releaseDate;
        mVoteAverage = voteAverage;
        mOverview = overview;
        mPosterUri = posterUri;
    }

    private Movie(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mReleaseDate = in.readString();
        mVoteAverage = in.readString();
        mOverview = in.readString();

        Object tempObj = in.readValue(Movie.class.getClassLoader());
        if (tempObj instanceof Uri) {
            mPosterUri = (Uri) tempObj;
        } else {
            mPosterUri = null;
        }

        mVideos = in.createTypedArray(Video.CREATOR);
        mReviews = in.createTypedArray(Review.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mReleaseDate);
        dest.writeString(mVoteAverage);
        dest.writeString(mOverview);
        dest.writeValue(mPosterUri);
        if (mVideos != null) {
            dest.writeTypedArray(mVideos, 0);
        }
        if (mReviews != null) {
            dest.writeTypedArray(mReviews, 0);
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public String getId() {
        return mId;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public String getOverview() {
        return mOverview;
    }

    public Uri getPosterUri() {
        return mPosterUri;
    }

    public Video[] getVideos() {
        return mVideos;
    }

    public void setVideos(Video[] videos) {
        mVideos = videos;
    }

    public Review[] getReviews() {
        return mReviews;
    }

    public void setReviews(Review[] reviews) {
        mReviews = reviews;
    }
}
