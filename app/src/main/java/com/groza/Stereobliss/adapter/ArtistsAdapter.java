/* */

package com.groza.Stereobliss.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.preference.PreferenceManager;

import com.groza.Stereobliss.R;
import com.groza.Stereobliss.artwork.ArtworkManager;
import com.groza.Stereobliss.models.ArtistModel;
import com.groza.Stereobliss.viewitems.GridViewItem;
import com.groza.Stereobliss.viewitems.ListViewItem;

public class ArtistsAdapter extends GenericSectionAdapter<ArtistModel> implements ArtworkManager.onNewArtistImageListener {
    private static final String TAG = ArtistsAdapter.class.getSimpleName();

    private final Context mContext;

    private final ArtworkManager mArtworkManager;

    private final boolean mUseList;

    private int mListItemHeight;

    private final boolean mHideArtwork;

    public ArtistsAdapter(final Context context, final boolean useList) {
        super();

        mContext = context;

        mUseList = useList;
        if (mUseList) {
            mListItemHeight = (int) context.getResources().getDimension(R.dimen.material_list_item_height);
        }

        mArtworkManager = ArtworkManager.getInstance(context);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mHideArtwork = sharedPreferences.getBoolean(context.getString(R.string.pref_hide_artwork_key), context.getResources().getBoolean(R.bool.pref_hide_artwork_default));
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ArtistModel artist = getItem(position);

        if (mUseList) {
            ListViewItem listItem;
            // Check if a view can be recycled
            if (convertView != null) {
                listItem = (ListViewItem) convertView;
            } else {
                listItem = ListViewItem.createArtistItem(mContext, this);
            }

            listItem.setArtist(artist);

            if (!mHideArtwork) {
                // This will prepare the view for fetching the image from the internet if not already saved in local database.
                listItem.prepareArtworkFetching(mArtworkManager, artist);

                // Check if the scroll speed currently is already 0, then start the image task right away.
                if (mScrollSpeed == 0) {
                    listItem.setImageDimension(mListItemHeight, mListItemHeight);
                    listItem.startCoverImageTask();
                }
            }
            return listItem;
        } else {
            GridViewItem gridItem;
            ViewGroup.LayoutParams layoutParams;
            int width = ((GridView) parent).getColumnWidth();

            // Check if a view can be recycled
            if (convertView != null) {
                gridItem = (GridViewItem) convertView;
                gridItem.setArtist(artist);

                layoutParams = gridItem.getLayoutParams();
                layoutParams.height = width;
                layoutParams.width = width;
            } else {
                // Create new view if no reusable is available
                gridItem = GridViewItem.createArtistItem(mContext, this);
                gridItem.setArtist(artist);

                layoutParams = new android.widget.AbsListView.LayoutParams(width, width);
            }

            // Make sure to reset the layoutParams in case of change (rotation for example)
            gridItem.setLayoutParams(layoutParams);

            if (!mHideArtwork) {
                // This will prepare the view for fetching the image from the internet if not already saved in local database.
                gridItem.prepareArtworkFetching(mArtworkManager, artist);

                // Check if the scroll speed currently is already 0, then start the image task right away.
                if (mScrollSpeed == 0) {
                    gridItem.setImageDimension(width, width);
                    gridItem.startCoverImageTask();
                }
            }
            return gridItem;
        }
    }

    @Override
    public void newArtistImage(ArtistModel artist) {
        notifyDataSetChanged();
    }
}
