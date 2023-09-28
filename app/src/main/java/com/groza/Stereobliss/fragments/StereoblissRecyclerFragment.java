/* */

package com.groza.Stereobliss.fragments;

import android.view.View;
import android.view.ViewTreeObserver;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.groza.Stereobliss.adapter.GenericRecyclerViewAdapter;
import com.groza.Stereobliss.models.GenericModel;
import com.groza.Stereobliss.views.StereoblissRecyclerView;

import com.groza.Stereobliss.R;

import com.groza.Stereobliss.utils.GridItemDecoration;

import java.util.List;

abstract public class StereoblissRecyclerFragment<T extends GenericModel, VH extends RecyclerView.ViewHolder> extends StereoblissBaseFragment<T> {

    /**
     * The reference to the possible recyclerview
     */
    protected StereoblissRecyclerView mRecyclerView;

    /**
     * The reference to the possible empty view which should replace the list view if no data is available
     */
    protected View mEmptyView;

    /**
     * The generic adapter for the view model
     */
    protected GenericRecyclerViewAdapter<T, VH> mRecyclerAdapter;

    /**
     * Observer to be notified if the dataset of the adapter changed.
     */
    private OdysseyDataSetObserver mDataSetObserver;

    /**
     * Called when the fragment resumes.
     * <p/>
     * Create the PBS connection, reload the data and start the refresh indicator if a refreshlayout exists.
     */
    @Override
    public void onResume() {
        super.onResume();

        if (null == mDataSetObserver) {
            mDataSetObserver = new OdysseyDataSetObserver();
        }

        mRecyclerAdapter.registerAdapterDataObserver(mDataSetObserver);

        getContent();

        mTrimmingEnabled = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mTrimmingEnabled = true;

        mRecyclerAdapter.unregisterAdapterDataObserver(mDataSetObserver);
    }

    @Override
    void swapModel(List<T> model) {
        // Transfer the data to the adapter so that the views can use it
        mRecyclerAdapter.swapModel(model);
    }

    /**
     * Method to show or hide the recyclerview according to the state of the adapter.
     */
    private void updateView() {
        if (mRecyclerView != null) {
            if (mRecyclerAdapter.getItemCount() > 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Method to setup the recyclerview with a linear layout manager and a default item decoration.
     * Make sure to call this method after the recyclerview was set.
     */
    protected void setLinearLayoutManagerAndDecoration() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    /**
     * Method to setup the recyclerview with a grid layout manager and a spacing item decoration.
     * Make sure to call this method after the recyclerview was set and has
     * a valid {@link GenericRecyclerViewAdapter} adapter.
     * <p>
     * This method will also add an observer to adjust the spancount of the grid after an orientation change.
     */
    protected void setGridLayoutManagerAndDecoration() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        final int halfSpacingOffsetPX = getResources().getDimensionPixelSize(R.dimen.grid_half_spacing);
        final int spacingOffsetPX = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        final GridItemDecoration gridItemDecoration = new GridItemDecoration(spacingOffsetPX, halfSpacingOffsetPX);
        mRecyclerView.addItemDecoration(gridItemDecoration);

        // add an observer to set the spancount after the layout was inflated in order to get a dynamic spancount related to the available space.
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final int recyclerViewWidth = mRecyclerView.getWidth();

                if (recyclerViewWidth > 0) {
                    // layout finished so remove observer
                    mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    final float gridItemWidth = getResources().getDimensionPixelSize(R.dimen.grid_item_height);

                    // the minimum spancount should always be 2
                    final int newSpanCount = Math.max((int) Math.floor(recyclerViewWidth / gridItemWidth), 2);

                    final GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
                    layoutManager.setSpanCount(newSpanCount);

                    mRecyclerView.requestLayout();

                    // pass the columnWidth to the adapter to adjust the size of the griditems
                    final int columnWidth = recyclerViewWidth / newSpanCount;
                    ((GenericRecyclerViewAdapter<?, ?>) mRecyclerView.getAdapter()).setItemSize(columnWidth);
                }
            }
        });
    }

    /**
     * Private observer class to keep informed if the dataset of the adapter has changed.
     * This will trigger an update of the view.
     */
    private class OdysseyDataSetObserver extends RecyclerView.AdapterDataObserver {

        @Override
        public void onChanged() {
            super.onChanged();

            updateView();
        }
    }
}
