/* */

package com.groza.Stereobliss.models;

public class BookmarkModel implements GenericModel {

    /**
     * Unique id to identify the bookmark
     */
    private final long mId;

    /**
     * The name of the bookmark
     */
    private final String mTitle;

    /**
     * The number of tracks in the bookmark
     */
    private final int mNumberOfTracks;

    /**
     * Constructs a BookmarkModel instance with the given parameters.
     */
    public BookmarkModel(long id, String title, int numberOfTracks) {
        if (title != null) {
            mTitle = title;
        } else {
            mTitle = "";
        }

        mId = id;
        mNumberOfTracks = numberOfTracks;
    }

    /**
     * Return the id of the bookmark
     */
    public long getId() {
        return mId;
    }

    /**
     * Return the name of the bookmark
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Return the number of tracks in the bookmark
     */
    public int getNumberOfTracks() {
        return mNumberOfTracks;
    }

    /**
     * Return the section title for the BookmarkModel
     * <p/>
     * The section title is the name of the bookmark.
     */
    @Override
    public String getSectionTitle() {
        return mTitle;
    }
}
