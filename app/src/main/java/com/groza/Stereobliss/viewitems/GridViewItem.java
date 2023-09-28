/* */

package com.groza.Stereobliss.viewitems;

import android.content.Context;
import android.widget.TextView;

import com.groza.Stereobliss.adapter.ScrollSpeedAdapter;
import com.groza.Stereobliss.models.AlbumModel;

import com.groza.Stereobliss.R;

import com.groza.Stereobliss.models.ArtistModel;

public class GridViewItem extends GenericImageViewItem {
    private static final String TAG = GridViewItem.class.getSimpleName();

    private final TextView mTitleView;

    public static GridViewItem createAlbumItem(final Context context, final ScrollSpeedAdapter adapter) {
        return new GridViewItem(context, adapter);
    }

    public static GridViewItem createArtistItem(final Context context, final ScrollSpeedAdapter adapter) {
        return new GridViewItem(context, adapter);
    }

    /**
     * Constructor of the gridview.
     *
     * @param context The current context.
     * @param adapter The scroll speed adapter for cover loading.
     */
    private GridViewItem(final Context context, final ScrollSpeedAdapter adapter) {
        super(context, R.layout.gridview_item, R.id.grid_item_cover_image, R.id.grid_item_view_switcher, adapter);

        mTitleView = findViewById(R.id.grid_item_title);
    }

    /**
     * Extracts the information from a album model.
     *
     * @param album The current album model.
     */
    public void setAlbum(final AlbumModel album) {
        mTitleView.setText(album.getAlbumName());
    }

    /**
     * Extracts the information from a artist model.
     *
     * @param artist The current artist model.
     */
    public void setArtist(final ArtistModel artist) {
        mTitleView.setText(artist.getArtistName());
    }
}
