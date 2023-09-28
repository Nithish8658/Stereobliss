/* */

package com.groza.Stereobliss.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.groza.Stereobliss.adapter.AlbumsAdapter;
import com.groza.Stereobliss.artwork.ArtworkManager;
import com.groza.Stereobliss.listener.OnAlbumSelectedListener;
import com.groza.Stereobliss.listener.ToolbarAndFABCallback;
import com.groza.Stereobliss.models.AlbumModel;

import com.groza.Stereobliss.R;
import com.groza.Stereobliss.activities.GenericActivity;
import com.groza.Stereobliss.utils.ScrollSpeedListener;
import com.groza.Stereobliss.utils.ThemeUtils;
import com.groza.Stereobliss.viewitems.GenericImageViewItem;

public abstract class GenericAlbumsFragment extends StereoblissFragment<AlbumModel> implements AdapterView.OnItemClickListener {

    /**
     * Listener to open an album
     */
    protected OnAlbumSelectedListener mAlbumSelectedCallback;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView;

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        final String viewAppearance = sharedPref.getString(getString(R.string.pref_view_library_key), getString(R.string.pref_library_view_default));

        final boolean useList = viewAppearance.equals(getString(R.string.pref_library_view_list_key));

        if (useList) {
            rootView = inflater.inflate(R.layout.list_refresh, container, false);
        } else {
            rootView = inflater.inflate(R.layout.grid_refresh, container, false);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        final String viewAppearance = sharedPref.getString(getString(R.string.pref_view_library_key), getString(R.string.pref_library_view_default));

        final boolean useList = viewAppearance.equals(getString(R.string.pref_library_view_list_key));

        if (useList) {
            // get listview
            mListView = view.findViewById(R.id.list_refresh_listview);
        } else {
            // get gridview
            mListView = view.findViewById(R.id.grid_refresh_gridview);
        }

        // get swipe layout
        mSwipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        // set swipe colors
        mSwipeRefreshLayout.setColorSchemeColors(ThemeUtils.getThemeColor(requireContext(), R.attr.colorAccent),
                ThemeUtils.getThemeColor(requireContext(), R.attr.colorPrimary));
        // set swipe refresh listener
        mSwipeRefreshLayout.setOnRefreshListener(this::refreshContent);

        mAdapter = new AlbumsAdapter(getActivity(), useList);

        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(new ScrollSpeedListener(mAdapter));
        mListView.setOnItemClickListener(this);

        // get empty view
        mEmptyView = view.findViewById(R.id.empty_view);

        // set empty view message
        ((TextView) view.findViewById(R.id.empty_view_message)).setText(R.string.empty_albums_message);

        // register for context menu
        registerForContextMenu(mListView);
    }

    @Override
    public void onResume() {
        super.onResume();

        ArtworkManager.getInstance(getContext()).registerOnNewAlbumImageListener((AlbumsAdapter) mAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();

        ArtworkManager.getInstance(getContext()).unregisterOnNewAlbumImageListener((AlbumsAdapter) mAdapter);
    }

    /**
     * Called when the fragment is first attached to its context.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mAlbumSelectedCallback = (OnAlbumSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnAlbumSelectedListener");
        }

        try {
            mToolbarAndFABCallback = (ToolbarAndFABCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement ToolbarAndFABCallback");
        }
    }

    /**
     * Callback when an item in the GridView was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // identify current album
        AlbumModel currentAlbum = mAdapter.getItem(position);

        Bitmap bitmap = null;

        // Check if correct view type, to be safe
        if (view instanceof GenericImageViewItem) {
            bitmap = ((GenericImageViewItem) view).getBitmap();
        }

        // send the event to the host activity
        mAlbumSelectedCallback.onAlbumSelected(currentAlbum, bitmap);
    }

    /**
     * Call the PBS to enqueue the selected album.
     *
     * @param position the position of the selected album in the adapter
     */
    protected void enqueueAlbum(int position) {
        // identify current album
        AlbumModel clickedAlbum = mAdapter.getItem(position);
        long albumId = clickedAlbum.getAlbumId();

        // Read order preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String trackOrderKey = sharedPref.getString(getString(R.string.pref_album_tracks_sort_order_key), getString(R.string.pref_album_tracks_sort_default));

        // enqueue album
        try {
            ((GenericActivity) requireActivity()).getPlaybackService().enqueueAlbum(albumId, trackOrderKey);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Call the PBS to play the selected album.
     * A previous playlist will be cleared.
     *
     * @param position the position of the selected album in the adapter
     */
    protected void playAlbum(int position) {
        // identify current album
        AlbumModel clickedAlbum = mAdapter.getItem(position);
        long albumId = clickedAlbum.getAlbumId();

        // Read order preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String trackOrderKey = sharedPref.getString(getString(R.string.pref_album_tracks_sort_order_key), getString(R.string.pref_album_tracks_sort_default));

        // play album
        try {
            ((GenericActivity) requireActivity()).getPlaybackService().playAlbum(albumId, trackOrderKey, 0);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
