/* */

package com.groza.Stereobliss.fragments;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.groza.Stereobliss.adapter.TracksAdapter;
import com.groza.Stereobliss.models.PlaylistModel;
import com.groza.Stereobliss.models.Trackmodel.TrackModel;

import com.groza.Stereobliss.R;
import com.groza.Stereobliss.activities.GenericActivity;
import com.groza.Stereobliss.playbackservice.storage.OdysseyDatabaseManager;
import com.groza.Stereobliss.utils.PreferenceHelper;
import com.groza.Stereobliss.utils.ThemeUtils;
import com.groza.Stereobliss.viewmodels.GenericViewModel;
import com.groza.Stereobliss.viewmodels.PlaylistTrackViewModel;

import java.util.List;

public class PlaylistTracksFragment extends StereoblissFragment<TrackModel> implements AdapterView.OnItemClickListener {

    public static final String TRACK_REMOVED_KEY = PlaylistTracksFragment.class.getSimpleName() + "::" + "trackRemovedKey";
    public static final String TRACK_REMOVED_PLAYLIST_ID = PlaylistTracksFragment.class.getSimpleName() + "::" + "trackRemovedPlaylistId";
    public static final String TRACK_REMOVED_TRACK_POSITION = PlaylistTracksFragment.class.getSimpleName() + "::" + "trackRemovedTrackPosition";

    /**
     * Key values for arguments of the fragment
     */
    private static final String ARG_PLAYLISTMODEL = "playlistmodel";

    /**
     * The information of the displayed playlist
     */
    private PlaylistModel mPlaylistModel;

    /**
     * Action to execute when the user selects an item in the list
     */
    private PreferenceHelper.LIBRARY_TRACK_CLICK_ACTION mClickAction;

    public static PlaylistTracksFragment newInstance(@NonNull final PlaylistModel playlistModel) {
        final Bundle args = new Bundle();
        args.putParcelable(ARG_PLAYLISTMODEL, playlistModel);

        final PlaylistTracksFragment fragment = new PlaylistTracksFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_refresh, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get listview
        mListView = view.findViewById(R.id.list_refresh_listview);

        // get swipe layout
        mSwipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        // set swipe colors
        mSwipeRefreshLayout.setColorSchemeColors(ThemeUtils.getThemeColor(requireContext(), R.attr.colorAccent),
                ThemeUtils.getThemeColor(requireContext(), R.attr.colorPrimary));
        // set swipe refresh listener
        mSwipeRefreshLayout.setOnRefreshListener(this::refreshContent);

        mAdapter = new TracksAdapter(getActivity());

        // Disable sections
        mAdapter.enableSections(false);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);

        // get empty view
        mEmptyView = view.findViewById(R.id.empty_view);

        // set empty view message
        ((TextView) view.findViewById(R.id.empty_view_message)).setText(R.string.empty_tracks_message);

        registerForContextMenu(mListView);

        // activate options menu in toolbar
        setHasOptionsMenu(true);

        Bundle args = requireArguments();

        mPlaylistModel = args.getParcelable(ARG_PLAYLISTMODEL);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        mClickAction = PreferenceHelper.getClickAction(sharedPreferences, requireContext());

        // setup observer for the live data
        getViewModel().getData().observe(getViewLifecycleOwner(), this::onDataReady);
    }

    @Override
    GenericViewModel<TrackModel> getViewModel() {
        return new ViewModelProvider(this, new PlaylistTrackViewModel.PlaylistTrackViewModelFactory(requireActivity().getApplication(), mPlaylistModel)).get(PlaylistTrackViewModel.class);
    }

    /**
     * Called when the fragment resumes.
     * Reload the data, setup the toolbar and create the PBS connection.
     */
    @Override
    public void onResume() {
        if (mToolbarAndFABCallback != null) {
            // set toolbar behaviour and title
            mToolbarAndFABCallback.setupToolbar(mPlaylistModel.getPlaylistName(), false, false, false);
            // Enable FAB correctly for now, can be disabled later
            mToolbarAndFABCallback.setupFAB(v -> playPlaylist(0));
        }
        super.onResume();
    }

    @Override
    protected void onDataReady(List<TrackModel> model) {
        super.onDataReady(model);

        if (mToolbarAndFABCallback != null) {
            // set up play button
            if (mAdapter.isEmpty()) {
                mToolbarAndFABCallback.setupFAB(null);
            } else {
                mToolbarAndFABCallback.setupFAB(v -> playPlaylist(0));
            }
        }
    }

    /**
     * Play the playlist from the current position.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (mClickAction) {
            case ACTION_ADD_SONG:
                enqueueTrack(position, false);
                break;
            case ACTION_PLAY_SONG:
                playTrack(position);
                break;
            case ACTION_PLAY_SONG_NEXT:
                enqueueTrack(position, true);
                break;
            case ACTION_CLEAR_AND_PLAY:
                playPlaylist(position);
                break;
        }
    }

    /**
     * Create the context menu.
     */
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_playlist_tracks_fragment, menu);

        if (mPlaylistModel.getPlaylistType() != PlaylistModel.PLAYLIST_TYPES.ODYSSEY_LOCAL) {
            // Hide remove track for all non odyssey playlists as it is unsupported
            menu.findItem(R.id.fragment_playlist_tracks_action_remove).setVisible(false);
        }
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

        if (itemId == R.id.fragment_album_tracks_action_play) {
            playTrack(info.position);
            return true;
        } else if (itemId == R.id.fragment_playlist_tracks_action_enqueue) {
            enqueueTrack(info.position, false);
            return true;
        } else if (itemId == R.id.fragment_playlist_tracks_action_enqueueasnext) {
            enqueueTrack(info.position, true);
            return true;
        } else if (itemId == R.id.fragment_playlist_tracks_action_remove) {
            removeTrackFromPlaylist(info.position);
            return true;
        }

        return super.onContextItemSelected(item);
    }

    /**
     * Initialize the options menu.
     * Be sure to call {@link #setHasOptionsMenu} before.
     *
     * @param menu         The container for the custom options menu.
     * @param menuInflater The inflater to instantiate the layout.
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.options_menu_playlist_tracks_fragment, menu);

        // get tint color
        int tintColor = ThemeUtils.getThemeColor(requireContext(), R.attr.odyssey_color_text_accent);

        Drawable drawable = menu.findItem(R.id.action_add_playlist_tracks).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, tintColor);
        menu.findItem(R.id.action_add_playlist_tracks).setIcon(drawable);

        super.onCreateOptionsMenu(menu, menuInflater);
    }

    /**
     * Hook called when an menu item in the options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return True if the hook was consumed here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add_playlist_tracks) {
            enqueuePlaylist();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Call the PBS to enqueue the entire playlist.
     */
    private void enqueuePlaylist() {
        try {
            // add the playlist
            ((GenericActivity) requireActivity()).getPlaybackService().enqueuePlaylist(mPlaylistModel);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * Call the PBS to play the entire playlist and start with the selected track.
     * A previous playlist will be cleared.
     *
     * @param position the position of the selected track in the adapter
     */
    private void playPlaylist(int position) {

        try {
            ((GenericActivity) requireActivity()).getPlaybackService().playPlaylist(mPlaylistModel, position);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Call the PBS to enqueue the selected track and then play it.
     *
     * @param position the position of the selected track in the adapter
     */
    private void playTrack(int position) {
        TrackModel track = mAdapter.getItem(position);

        try {
            ((GenericActivity) requireActivity()).getPlaybackService().playTrack(track, false);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Call the PBS to enqueue the selected track.
     *
     * @param position the position of the selected track in the adapter
     */
    private void enqueueTrack(int position, boolean asNext) {

        TrackModel track = mAdapter.getItem(position);

        try {
            ((GenericActivity) requireActivity()).getPlaybackService().enqueueTrack(track, asNext);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * Remove the selected track from the playlist in the mediastore.
     *
     * @param position the position of the selected track in the adapter
     */
    private void removeTrackFromPlaylist(int position) {
        boolean reloadData = false;

        if (mPlaylistModel.getPlaylistType() == PlaylistModel.PLAYLIST_TYPES.ODYSSEY_LOCAL) {
            reloadData = OdysseyDatabaseManager.getInstance(getContext()).removeTrackFromPlaylist(mPlaylistModel.getPlaylistId(), position);
        }

        if (reloadData) {
            // reload data
            refreshContent();

            Bundle result = new Bundle();
            result.putLong(TRACK_REMOVED_PLAYLIST_ID, mPlaylistModel.getPlaylistId());
            result.putInt(TRACK_REMOVED_TRACK_POSITION, position);

            getParentFragmentManager().setFragmentResult(TRACK_REMOVED_KEY, result);
        }
    }
}
