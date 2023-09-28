

package com.groza.Stereobliss.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ArtistModel implements GenericModel, Parcelable {

    /**
     * The name of the artist
     */
    private final String mArtistName;

    /**
     * Unique id to identify the artist in the mediastore
     */
    private final long mArtistID;

    private String mMBId;

    private boolean mImageFetching;

    /**
     * Constructs a ArtistModel instance with the given parameters.
     */
    public ArtistModel(String name, long artistId) {
        if (name != null) {
            mArtistName = name;
        } else {
            mArtistName = "";
        }

        mArtistID = artistId;
    }

    protected ArtistModel(Parcel in) {
        mArtistName = in.readString();
        mArtistID = in.readLong();
        mMBId = in.readString();
        mImageFetching = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mArtistName);
        dest.writeLong(mArtistID);
        dest.writeString(mMBId);
        dest.writeByte((byte) (mImageFetching ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ArtistModel> CREATOR = new Creator<ArtistModel>() {
        @Override
        public ArtistModel createFromParcel(Parcel in) {
            return new ArtistModel(in);
        }

        @Override
        public ArtistModel[] newArray(int size) {
            return new ArtistModel[size];
        }
    };

    /**
     * Return the name of the artist
     */
    public String getArtistName() {
        return mArtistName;
    }

    /**
     * Return the unique artist id
     */
    public long getArtistID() {
        return mArtistID;
    }

    /**
     * Return the ArtistModel as a String for debugging purposes.
     */
    @NonNull
    @Override
    public String toString() {
        return "Artist: " + getArtistName();
    }

    public void setMBId(String mbid) {
        mMBId = mbid;
    }

    public String getMBId() {
        return mMBId;
    }

    /**
     * Return the section title for the ArtistModel
     * <p/>
     * The section title is the name of the artist.
     */
    @Override
    public String getSectionTitle() {
        return mArtistName;
    }

    public synchronized void setFetching(boolean fetching) {
        mImageFetching = fetching;
    }

    public synchronized boolean getFetching() {
        return mImageFetching;
    }

    @Override
    public boolean equals(Object artist) {
        if (null == artist) {
            return false;
        }
        if (artist instanceof ArtistModel) {
            return mArtistID == ((ArtistModel) artist).mArtistID && mArtistName.equals(((ArtistModel) artist).mArtistName);
        } else {
            return false;
        }
    }
}
