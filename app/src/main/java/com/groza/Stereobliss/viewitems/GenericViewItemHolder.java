/* */

package com.groza.Stereobliss.viewitems;

import androidx.recyclerview.widget.RecyclerView;

import com.groza.Stereobliss.artwork.ArtworkManager;
import com.groza.Stereobliss.models.AlbumModel;
import com.groza.Stereobliss.models.Trackmodel.TrackModel;

public class GenericViewItemHolder extends RecyclerView.ViewHolder {

    public GenericViewItemHolder(final GenericImageViewItem itemView) {
        super(itemView);
    }

    public void prepareArtworkFetching(final ArtworkManager artworkManager, final AlbumModel album) {
        ((GenericImageViewItem) itemView).prepareArtworkFetching(artworkManager, album);
    }

    public void startCoverImageTask() {
        ((GenericImageViewItem) itemView).startCoverImageTask();
    }

    public void setImageDimensions(final int width, final int height) {
        ((GenericImageViewItem) itemView).setImageDimension(width, height);
    }

    public void setAlbumTrack(final TrackModel trackModel, final boolean mShowDiscNumber) {
        ((ListViewItem) itemView).setAlbumTrack(trackModel, mShowDiscNumber);
    }

    public void setAlbum(final AlbumModel album) {
        if (itemView instanceof ListViewItem) {
            ((ListViewItem) itemView).setAlbum(album);
        } else if (itemView instanceof GridViewItem) {
            ((GridViewItem) itemView).setAlbum(album);
        }
    }
}
