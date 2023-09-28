/* */

package com.groza.Stereobliss.fragments;

import android.database.DataSetObserver;
import android.os.Parcelable;
import android.view.View;
import android.widget.AbsListView;

import androidx.annotation.NonNull;

import com.groza.Stereobliss.adapter.GenericSectionAdapter;
import com.groza.Stereobliss.models.GenericModel;

import java.util.List;

abstract public class StereoblissFragment<T extends GenericModel> extends StereoblissBaseFragment<T> {

    /**
     * The reference to the possible abstract list view
     */
    protected AbsListView mListView;

    /**
     * Holds the state of the list view to restore the scroll position.
     */
    private Parcelable mListViewState;

    /**
     * The reference to the possible empty view which should replace the list view if no data is available
     */
    protected View mEmptyView;

    /**
     * The generic adapter for the view model
     */
    protected GenericSectionAdapter<T> mAdapter;

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

        mAdapter.registerDataSetObserver(mDataSetObserver);

        getContent();

        mTrimmingEnabled = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mTrimmingEnabled = true;

        mAdapter.unregisterDataSetObserver(mDataSetObserver);

        if (mListView != null) {
            mListViewState = mListView.onSaveInstanceState();
        }
    }

    @Override
    void swapModel(List<T> model) {
        // Transfer the data to the adapter so that the views can use it
        mAdapter.swapModel(model);

        if (model != null && mListView != null && mListViewState != null) {
            mListView.onRestoreInstanceState(mListViewState);
            mListViewState = null;
        }

        updateView();
    }

    /**
     * Method to apply a filter to the view model of the fragment.
     */
    public void applyFilter(@NonNull String filter) {
        mAdapter.applyFilter(filter.trim());
    }

    /**
     * Method to remove a previous set filter.
     */
    public void removeFilter() {
        mAdapter.removeFilter();
    }

    /**
     * Method to show or hide the listview according to the state of the adapter.
     */
    private void updateView() {
        if (mListView != null && mEmptyView != null) {
            if (mAdapter.isEmpty()) {
                // show empty message
                mListView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                // show list view
                mListView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }
        }
    }


    /**
     * Private observer class to keep informed if the dataset of the adapter has changed.
     * This will trigger an update of the view.
     */
    private class OdysseyDataSetObserver extends DataSetObserver {

        @Override
        public void onInvalidated() {
            super.onInvalidated();

            updateView();
        }

        @Override
        public void onChanged() {
            super.onChanged();

            updateView();
        }
    }
}
