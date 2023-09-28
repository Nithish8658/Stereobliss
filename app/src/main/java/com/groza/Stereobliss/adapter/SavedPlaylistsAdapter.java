/* */

package com.groza.Stereobliss.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.groza.Stereobliss.models.PlaylistModel;
import com.groza.Stereobliss.viewitems.ListViewItem;

public class SavedPlaylistsAdapter extends GenericSectionAdapter<PlaylistModel> {

    private final Context mContext;

    public SavedPlaylistsAdapter(Context context) {
        super();

        mContext = context;
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

        PlaylistModel playlist = getItem(position);

        ListViewItem listViewItem;
        // Check if a view can be recycled
        if (convertView != null) {
            listViewItem = (ListViewItem) convertView;
        } else {
            listViewItem = ListViewItem.createSavedPlaylistItem(mContext, this);
        }

        listViewItem.setPlaylist(playlist);

        return listViewItem;
    }
}
