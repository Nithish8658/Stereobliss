/* */

package com.groza.Stereobliss.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.groza.Stereobliss.models.Trackmodel.TrackModel;
import com.groza.Stereobliss.viewitems.ListViewItem;

import java.util.List;

public class TracksAdapter extends GenericSectionAdapter<TrackModel> {

    private final Context mContext;

    private boolean mShowDiscNumber;

    private final boolean mShouldShowDiscNumber;

    public TracksAdapter(Context context) {
        this(context, false);
    }

    public TracksAdapter(Context context, boolean shouldShowDiscNumber) {
        super();

        mContext = context;
        mShouldShowDiscNumber = shouldShowDiscNumber;
    }

    @Override
    public void swapModel(List<TrackModel> data) {
        super.swapModel(data);

        // check if list contains multiple discs
        if (mShouldShowDiscNumber && data != null && !data.isEmpty()) {
            mShowDiscNumber = (data.get(0).getTrackNumber() / 1000) != (data.get(data.size() - 1).getTrackNumber() / 1000);
        }
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

        TrackModel track = getItem(position);

        ListViewItem listViewItem;
        // Check if a view can be recycled
        if (convertView != null) {
            listViewItem = (ListViewItem) convertView;
        } else {
            listViewItem = ListViewItem.createAlbumTrackItem(mContext, this);
        }

        listViewItem.setAlbumTrack(track, mShowDiscNumber);

        return listViewItem;
    }
}
