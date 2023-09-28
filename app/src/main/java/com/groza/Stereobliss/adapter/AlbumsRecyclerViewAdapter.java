/* */

package com.groza.Stereobliss.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.groza.Stereobliss.R;
import com.groza.Stereobliss.artwork.ArtworkManager;
import com.groza.Stereobliss.models.AlbumModel;
import com.groza.Stereobliss.utils.ThemeUtils;
import com.groza.Stereobliss.viewitems.GenericImageViewItem;
import com.groza.Stereobliss.viewitems.GenericViewItemHolder;
import com.groza.Stereobliss.viewitems.GridViewItem;
import com.groza.Stereobliss.viewitems.ListViewItem;

public class AlbumsRecyclerViewAdapter extends GenericRecyclerViewAdapter<AlbumModel, GenericViewItemHolder> implements ArtworkManager.onNewAlbumImageListener {

    private final ArtworkManager mArtworkManager;

    private final boolean mHideArtwork;

    private final boolean mUseList;

    /**
     * the size of the item in pixel
     * this will be used to adjust griditems and select a proper dimension for the image loading process
     */
    private int mItemSize;

    public AlbumsRecyclerViewAdapter(final Context context, final boolean useList) {
        super();

        mArtworkManager = ArtworkManager.getInstance(context);

        mUseList = useList;
        if (mUseList) {
            mItemSize = (int) context.getResources().getDimension(R.dimen.material_list_item_height);
        } else {
            mItemSize = (int) context.getResources().getDimension(R.dimen.grid_item_height);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mHideArtwork = sharedPreferences.getBoolean(context.getString(R.string.pref_hide_artwork_key), context.getResources().getBoolean(R.bool.pref_hide_artwork_default));
    }

    @NonNull
    @Override
    public GenericViewItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GenericImageViewItem view;

        if (mUseList) {
            view = ListViewItem.createAlbumItem(parent.getContext(), this);

            // set a selectable background manually
            view.setBackgroundResource(ThemeUtils.getThemeResourceId(parent.getContext(), R.attr.selectableItemBackground));
        } else {
            view = GridViewItem.createAlbumItem(parent.getContext(), this);

            // apply custom layout params to ensure that the griditems have equal size
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemSize);
            view.setLayoutParams(layoutParams);
        }

        return new GenericViewItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewItemHolder holder, int position) {
        final AlbumModel album = getItem(position);

        holder.setAlbum(album);

        if (!mUseList) {
            // for griditems adjust the height each time data is set
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = mItemSize;
            holder.itemView.setLayoutParams(layoutParams);
        }

        if (!mHideArtwork) {
            // This will prepare the view for fetching the image from the internet if not already saved in local database.
            holder.prepareArtworkFetching(mArtworkManager, album);

            // Check if the scroll speed currently is already 0, then start the image task right away.
            if (mScrollSpeed == 0) {
                holder.setImageDimensions(mItemSize, mItemSize);
                holder.startCoverImageTask();
            }
        }

        // We have to set this to make the context menu working with recycler views.
        holder.itemView.setLongClickable(true);
    }

    /**
     * Sets the itemsize for each item.
     * This value will adjust the height of a griditem and will be used for image loading.
     * Calling this method will notify any registered observers that the data set has changed.
     *
     * @param size The new size in pixel.
     */
    @Override
    public void setItemSize(int size) {
        mItemSize = size;

        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getAlbumId();
    }

    @Override
    public void newAlbumImage(AlbumModel album) {
        notifyDataSetChanged();
    }
}
