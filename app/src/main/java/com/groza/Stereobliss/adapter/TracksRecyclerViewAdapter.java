/* */

package com.groza.Stereobliss.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.groza.Stereobliss.models.Trackmodel.TrackModel;

import com.groza.Stereobliss.R;

import com.groza.Stereobliss.utils.ThemeUtils;
import com.groza.Stereobliss.viewitems.GenericViewItemHolder;
import com.groza.Stereobliss.viewitems.ListViewItem;

import java.util.List;

public class TracksRecyclerViewAdapter extends GenericRecyclerViewAdapter<TrackModel, GenericViewItemHolder> {

    private final boolean mShouldShowDiscNumber;

    private boolean mShowDiscNumber;

    public TracksRecyclerViewAdapter(final boolean shouldShowDiscNumber) {
        super();
        mShouldShowDiscNumber = shouldShowDiscNumber;
    }

    @NonNull
    @Override
    public GenericViewItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListViewItem view = ListViewItem.createAlbumTrackItem(parent.getContext(), this);

        // set a selectable background manually
        view.setBackgroundResource(ThemeUtils.getThemeResourceId(parent.getContext(), R.attr.selectableItemBackground));
        return new GenericViewItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewItemHolder holder, int position) {
        final TrackModel track = getItem(position);

        holder.setAlbumTrack(track, mShowDiscNumber);

        // We have to set this to make the context menu working with recycler views.
        holder.itemView.setLongClickable(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getTrackId();
    }

    @Override
    public void setItemSize(int size) {
        // method only needed if adapter supports grid view
    }

    @Override
    public void swapModel(List<TrackModel> data) {
        super.swapModel(data);

        // check if list contains multiple discs
        if (mShouldShowDiscNumber && data != null && !data.isEmpty()) {
            mShowDiscNumber = (data.get(0).getTrackNumber() / 1000) != (data.get(data.size() - 1).getTrackNumber() / 1000);
        }
    }
}
