/* */

package com.groza.Stereobliss.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class AlbumModel implements GenericModel, Parcelable {

    /**
     * The name of the album
     */
    private final String mAlbumName;

    /**
     * The url for the album cover
     */
    @Deprecated
    private final String mAlbumArtURL;

    /**
     * The name of the artist for the current album
     */
    private final String mArtistName;

    /**
     * Unique id to identify the album in the mediastore.
     */
    private final long mAlbumId;

    /**
     * The date as an integer when this album was added to the device
     */
    private final int mDateAdded;

    private String mMBId;

    private boolean mImageFetching;

    public AlbumModel(String name, String albumArtURL, String artistName, long albumId, int dateAdded) {
        if (name != null) {
            mAlbumName = name;
        } else {
            mAlbumName = "";
        }

        if (albumArtURL != null) {
            mAlbumArtURL = albumArtURL;
        } else {
            mAlbumArtURL = "";
        }

        if (artistName != null) {
            mArtistName = artistName;
        } else {
            mArtistName = "";
        }

        mAlbumId = albumId;

        mDateAdded = dateAdded;
    }

    /**
     * Constructs a AlbumModel instance with the given parameters.
     */
    public AlbumModel(String name, String albumArtURL, String artistName, long albumId) {
        this(name, albumArtURL, artistName, albumId, -1);
    }

    /**
     * Constructs a AlbumModel from a Parcel.
     * <p>
     * see {@link Parcelable}
     */
    protected AlbumModel(Parcel in) {
        mAlbumName = in.readString();
        mAlbumArtURL = in.readString();
        mArtistName = in.readString();
        mDateAdded = in.readInt();
        mAlbumId = in.readLong();
        mMBId = in.readString();
        mImageFetching = in.readByte() != 0;
    }

    /**
     * Provide CREATOR field that generates a AlbumModel instance from a Parcel.
     * <p/>
     * see {@link Parcelable}
     */
    public static final Creator<AlbumModel> CREATOR = new Creator<AlbumModel>() {
        @Override
        public AlbumModel createFromParcel(Parcel in) {
            return new AlbumModel(in);
        }

        @Override
        public AlbumModel[] newArray(int size) {
            return new AlbumModel[size];
        }
    };

    /**
     * Return the name of the album
     */
    public String getAlbumName() {
        return mAlbumName;
    }

    /**
     * Return the url for the album cover
     * <p>
     * This method shouldn't be used on devices running Android 10 or later.
     */
    @Deprecated
    public String getAlbumArtURL() {
        return mAlbumArtURL;
    }

    /**
     * Return the name of the related artist
     */
    public String getArtistName() {
        return mArtistName;
    }

    public int getDateAdded() {
        return mDateAdded;
    }

    public long getAlbumId() {
        return mAlbumId;
    }

    /**
     * Return the AlbumModel as a String for debugging purposes.
     */
    @NonNull
    @Override
    public String toString() {
        return "Album: " + getAlbumName() + " from: " + getArtistName();
    }

    @Override
    public boolean equals(Object album) {
        if (null == album) {
            return false;
        }
        if (album instanceof AlbumModel) {
            return mAlbumId == ((AlbumModel) album).mAlbumId && mAlbumName.equals(((AlbumModel) album).mAlbumName)
                    && mArtistName.equals(((AlbumModel) album).mArtistName);
        } else {
            return false;
        }
    }

    /**
     * Return the section title for the AlbumModel
     * <p/>
     * The section title is the name of the album.
     */
    @Override
    public String getSectionTitle() {
        return mAlbumName;
    }

    public void setMBId(String mbid) {
        mMBId = mbid;
    }

    public String getMBId() {
        return mMBId;
    }


    public synchronized void setFetching(boolean fetching) {
        mImageFetching = fetching;
    }

    public synchronized boolean getFetching() {
        return mImageFetching;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     * <p/>
     * see {@link Parcelable}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     * <p/>
     * see {@link Parcelable}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAlbumName);
        dest.writeString(mAlbumArtURL);
        dest.writeString(mArtistName);
        dest.writeInt(mDateAdded);
        dest.writeLong(mAlbumId);
        dest.writeString(mMBId);
        dest.writeByte((byte) (mImageFetching ? 1 : 0));
    }
}
