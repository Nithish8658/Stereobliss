/* */

package com.groza.Stereobliss.fragments;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.groza.Stereobliss.adapter.BookmarksAdapter;
import com.groza.Stereobliss.models.BookmarkModel;

import com.groza.Stereobliss.R;
import com.groza.Stereobliss.activities.GenericActivity;
import com.groza.Stereobliss.playbackservice.storage.OdysseyDatabaseManager;
import com.groza.Stereobliss.viewmodels.BookmarkViewModel;
import com.groza.Stereobliss.viewmodels.GenericViewModel;

public class BookmarksFragment extends StereoblissFragment<BookmarkModel> implements AdapterView.OnItemClickListener {

    public static BookmarksFragment newInstance() {
        return new BookmarksFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_linear, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get listview
        mListView = view.findViewById(R.id.list_linear_listview);

        mAdapter = new BookmarksAdapter(getActivity());

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);

        // get empty view
        mEmptyView = view.findViewById(R.id.empty_view);

        // set empty view message
        ((TextView) view.findViewById(R.id.empty_view_message)).setText(R.string.empty_bookmarks_message);

        registerForContextMenu(mListView);

        // setup observer for the live data
        getViewModel().getData().observe(getViewLifecycleOwner(), this::onDataReady);
    }

    @Override
    GenericViewModel<BookmarkModel> getViewModel() {
        return new ViewModelProvider(this, new BookmarkViewModel.BookmarkViewModelFactory(requireActivity().getApplication(), false)).get(BookmarkViewModel.class);
    }

    /**
     * Called when the fragment resumes.
     * Reload the data, setup the toolbar and create the PBS connection.
     */
    @Override
    public void onResume() {
        super.onResume();

        if (mToolbarAndFABCallback != null) {
            // set toolbar behaviour and title
            mToolbarAndFABCallback.setupToolbar(getString(R.string.fragment_title_bookmarks), false, true, false);
            // set up play button
            mToolbarAndFABCallback.setupFAB(null);
        }
    }

    /**
     * Play the clicked bookmark.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        resumeBookmark(position);
    }

    /**
     * Create the context menu.
     */
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_bookmarks_fragment, menu);
    }

    /**
     * Hook called when an menu item in the context menu is selected.
     *
     * @param item The menu item that was selected.
     * @return True if the hook was consumed here.
     */
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (info == null) {
            return super.onContextItemSelected(item);
        }

        final int itemId = item.getItemId();

        if (itemId == R.id.bookmarks_context_menu_action_resume) {
            resumeBookmark(info.position);
            return true;
        } else if (itemId == R.id.bookmarks_context_menu_action_delete) {
            deleteBookmark(info.position);
            return true;
        }

        return super.onContextItemSelected(item);
    }

    /**
     * Call the PBS to play the selected bookmark.
     * A previous playlist will be cleared.
     *
     * @param position the position of the selected bookmark in the adapter
     */
    private void resumeBookmark(int position) {
        // identify current bookmark
        BookmarkModel bookmark = mAdapter.getItem(position);

        // resume state
        try {
            ((GenericActivity) requireActivity()).getPlaybackService().resumeBookmark(bookmark.getId());
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Call the PBS to delete the selected bookmark.
     *
     * @param position the position of the selected bookmark in the adapter
     */
    private void deleteBookmark(int position) {
        // identify current bookmark
        BookmarkModel bookmark = mAdapter.getItem(position);

        OdysseyDatabaseManager.getInstance(getContext()).removeState(bookmark.getId());

        refreshContent();
    }
}
