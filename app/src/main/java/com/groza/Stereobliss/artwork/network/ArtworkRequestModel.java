/* */

package com.groza.Stereobliss.artwork.network;

import android.net.Uri;

import com.groza.Stereobliss.models.AlbumModel;
import com.groza.Stereobliss.models.GenericModel;
import com.groza.Stereobliss.models.ArtistModel;
import com.groza.Stereobliss.utils.FormatHelper;

public class ArtworkRequestModel {

    public enum ArtworkRequestType {
        ALBUM,
        ARTIST
    }

    private final GenericModel mModel;

    private final ArtworkRequestType mType;

    public ArtworkRequestModel(ArtistModel artistModel) {
        this(artistModel, ArtworkRequestType.ARTIST);
    }

    public ArtworkRequestModel(AlbumModel albumModel) {
        this(albumModel, ArtworkRequestType.ALBUM);
    }

    private ArtworkRequestModel(GenericModel model, ArtworkRequestType type) {
        mModel = model;
        mType = type;
    }

    public ArtworkRequestType getType() {
        return mType;
    }

    public void setMBId(final String mbid) {
        switch (mType) {
            case ALBUM:
                ((AlbumModel) mModel).setMBId(mbid);
                break;
            case ARTIST:
                ((ArtistModel) mModel).setMBId(mbid);
                break;
        }
    }

    public String getAlbumName() {
        String albumName = null;

        switch (mType) {
            case ALBUM:
                albumName = ((AlbumModel) mModel).getAlbumName();
                break;
            case ARTIST:
                break;
        }

        return albumName;
    }

    public String getArtistName() {
        String artistName = null;

        switch (mType) {
            case ALBUM:
                artistName = ((AlbumModel) mModel).getArtistName();
                break;
            case ARTIST:
                artistName = ((ArtistModel) mModel).getArtistName();
                break;
        }

        return artistName;
    }

    public String getEncodedAlbumName() {
        String encodedAlbumName = null;

        switch (mType) {
            case ALBUM:
                encodedAlbumName = Uri.encode(((AlbumModel) mModel).getAlbumName());
                break;
            case ARTIST:
                break;
        }

        return encodedAlbumName;
    }

    public String getLuceneEscapedEncodedAlbumName() {
        String escapedAlbumName = null;

        switch (mType) {
            case ALBUM:
                escapedAlbumName = FormatHelper.escapeSpecialCharsLucene(((AlbumModel) mModel).getAlbumName());
                break;
            case ARTIST:
                break;
        }

        return Uri.encode(escapedAlbumName);
    }

    public String getEncodedArtistName() {
        String encodedArtistName = null;

        switch (mType) {
            case ALBUM:
                encodedArtistName = Uri.encode(((AlbumModel) mModel).getArtistName());
                break;
            case ARTIST:
                encodedArtistName = Uri.encode(((ArtistModel) mModel).getArtistName().replaceAll("/", " "));
                break;
        }

        return encodedArtistName;
    }

    public String getLuceneEscapedEncodedArtistName() {
        String escapedArtistName = null;

        switch (mType) {
            case ALBUM:
                escapedArtistName = FormatHelper.escapeSpecialCharsLucene(((AlbumModel) mModel).getArtistName());
                break;
            case ARTIST:
                escapedArtistName = FormatHelper.escapeSpecialCharsLucene(((ArtistModel) mModel).getArtistName());
                break;
        }

        return Uri.encode(escapedArtistName);
    }

    public GenericModel getGenericModel() {
        return mModel;
    }

    public String getLoggingString() {
        String loggingString = "";

        switch (mType) {
            case ALBUM:
                loggingString = ((AlbumModel) mModel).getAlbumName() + "-" + ((AlbumModel) mModel).getArtistName();
                break;
            case ARTIST:
                loggingString = ((ArtistModel) mModel).getArtistName();
                break;
        }

        return loggingString;
    }
}
