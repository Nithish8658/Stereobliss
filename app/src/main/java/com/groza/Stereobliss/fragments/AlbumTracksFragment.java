/* */

package com.groza.Stereobliss.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.groza.Stereobliss.adapter.TracksRecyclerViewAdapter;
import com.groza.Stereobliss.artwork.ArtworkManager;
import com.groza.Stereobliss.listener.OnArtistSelectedListener;
import com.groza.Stereobliss.listener.ToolbarAndFABCallback;
import com.groza.Stereobliss.models.AlbumModel;
import com.groza.Stereobliss.models.Trackmodel.TrackModel;
import com.groza.Stereobliss.views.StereoblissRecyclerView;

import com.groza.Stereobliss.R;
import com.groza.Stereobliss.activities.GenericActivity;
import com.groza.Stereobliss.models.ArtistModel;
import com.groza.Stereobliss.utils.CoverBitmapLoader;
import com.groza.Stereobliss.utils.MusicLibraryHelper;
import com.groza.Stereobliss.utils.PreferenceHelper;
import com.groza.Stereobliss.utils.ThemeUtils;
import com.groza.Stereobliss.viewitems.GenericViewItemHolder;
import com.groza.Stereobliss.viewmodels.GenericViewModel;
import com.groza.Stereobliss.viewmodels.TrackViewModel;

public class AlbumTracksFragment extends StereoblissRecyclerFragment<TrackModel, GenericViewItemHolder> implements CoverBitmapLoader.CoverBitmapReceiver, ArtworkManager.onNewAlbumImageListener, StereoblissRecyclerView.OnItemClickListener {
    private static final String TAG = AlbumTracksFragment.class.getSimpleName();
    /**
     * Listener to open an artist
     */
    private OnArtistSelectedListener mArtistSelectedCallback;

    /**
     * Key values for arguments of the fragment
     */
    private static final String ARG_ALBUMMODEL = "albummodel";

    private static final String ARG_BITMAP = "bitmap";

    /**
     * The information of the displayed album
     */
    private AlbumModel mAlbum;

    private CoverBitmapLoader mBitmapLoader;

    private Bitmap mBitmap = null;

    private boolean mHideArtwork;

    /**
     * Action to execute when the user selects an item in the list
     */
    private PreferenceHelper.LIBRARY_TRACK_CLICK_ACTION mClickAction;

    public static AlbumTracksFragment newInstance(@NonNull final AlbumModel albumModel, @Nullable final Bitmap bitmap) {
        final Bundle args = new Bundle();
        args.putParcelable(ARG_ALBUMMODEL, albumModel);
        if (bitmap != null) {
            args.putParcelable(ARG_BITMAP, bitmap);
        }

        final AlbumTracksFragment fragment = new AlbumTracksFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycler_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerAdapter = new TracksRecyclerViewAdapter(false);

        // get listview
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.addOnItemClicklistener(this);

        setLinearLayoutManagerAndDecoration();

        registerForContextMenu(mRecyclerView);

        // get empty view
        mEmptyView = view.findViewById(R.id.empty_view);

        // set empty view message
        ((TextView) view.findViewById(R.id.empty_view_message)).setText(R.string.empty_tracks_message);

        // set up toolbar
        Bundle args = requireArguments();

        mAlbum = args.getParcelable(ARG_ALBUMMODEL);
        mBitmap = args.getParcelable(ARG_BITMAP);

        setHasOptionsMenu(true);

        mBitmapLoader = new CoverBitmapLoader(requireContext(), this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        mHideArtwork = sharedPreferences.getBoolean(requireContext().getString(R.string.pref_hide_artwork_key), requireContext().getResources().getBoolean(R.bool.pref_hide_artwork_default));
        mClickAction = PreferenceHelper.getClickAction(sharedPreferences, requireContext());

        // setup observer for the live data
        getViewModel().getData().observe(getViewLifecycleOwner(), this::onDataReady);
    }

    @Override
    GenericViewModel<TrackModel> getViewModel() {
        return new ViewModelProvider(this, new TrackViewModel.TrackViewModelFactory(requireActivity().getApplication(), mAlbum.getAlbumId())).get(TrackViewModel.class);
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
            mArtistSelectedCallback = (OnArtistSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnArtistSelectedListener");
        }

        try {
            mToolbarAndFABCallback = (ToolbarAndFABCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement ToolbarAndFABCallback");
        }
    }

    /**
     * Called when the fragment resumes.
     * <p/>
     * Set up toolbar and play button.
     */
    @Override
    public void onResume() {
        super.onResume();

        if (mToolbarAndFABCallback != null) {
            // set toolbar behaviour and title
            // set up play button
            mToolbarAndFABCallback.setupFAB(v -> playAlbum(0));

            if (!mHideArtwork && mBitmap == null) {
                mToolbarAndFABCallback.setupToolbar(mAlbum.getAlbumName(), false, false, false);
                final View rootView = getView();
                if (rootView != null) {
                    getView().post(() -> {
                        int width = rootView.getMeasuredWidth();
                        mBitmapLoader.getAlbumImage(mAlbum, width, width);
                    });
                }
            } else if (!mHideArtwork) {
                // Reuse image
                mToolbarAndFABCallback.setupToolbar(mAlbum.getAlbumName(), false, false, true);
                mToolbarAndFABCallback.setupToolbarImage(mBitmap);
                final View rootView = getView();
                if (rootView != null) {
                    getView().post(() -> {
                        int width = rootView.getMeasuredWidth();
                        // Image too small
                        if (mBitmap.getWidth() < width) {
                            mBitmapLoader.getAlbumImage(mAlbum, width, width);
                        }
                    });
                }
            } else {
                mToolbarAndFABCallback.setupToolbar(mAlbum.getAlbumName(), false, false, false);
            }
        }

        ArtworkManager.getInstance(getContext()).registerOnNewAlbumImageListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        ArtworkManager.getInstance(getContext()).unregisterOnNewAlbumImageListener(this);
    }

    /**
     * Play the album from the current position.
     */
    @Override
    public void onItemClick(int position) {
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
                playAlbum(position);
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
        inflater.inflate(R.menu.context_menu_album_tracks_fragment, menu);
    }

    /**
     * Hook called when an menu item in the context menu is selected.
     *
     * @param item The menu item that was selected.
     * @return True if the hook was consumed here.
     */
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        StereoblissRecyclerView.RecyclerViewContextMenuInfo info =
                (StereoblissRecyclerView.RecyclerViewContextMenuInfo) item.getMenuInfo();

        if (info == null) {
            return super.onContextItemSelected(item);
        }

        final int itemId = item.getItemId();

        if (itemId == R.id.fragment_album_tracks_action_play) {
            playTrack(info.position);
            return true;
        } else if (itemId == R.id.fragment_album_tracks_action_enqueue) {
            enqueueTrack(info.position, false);
            return true;
        } else if (itemId == R.id.fragment_album_tracks_action_enqueueasnext) {
            enqueueTrack(info.position, true);
            return true;
        } else if (itemId == R.id.fragment_album_tracks_action_showartist) {
            showArtist(info.position);
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
        menuInflater.inflate(R.menu.options_menu_album_tracks_fragment, menu);

        // get tint color
        int tintColor = ThemeUtils.getThemeColor(requireContext(), R.attr.odyssey_color_text_accent);

        Drawable drawable = menu.findItem(R.id.action_add_album).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, tintColor);
        menu.findItem(R.id.action_add_album).setIcon(drawable);

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
        final int itemId = item.getItemId();

        if (itemId == R.id.action_reset_artwork) {
            mToolbarAndFABCallback.setupToolbar(mAlbum.getAlbumName(), false, false, false);
            ArtworkManager.getInstance(getContext()).resetImage(mAlbum);
            return true;
        } else if (itemId == R.id.action_add_album) {
            enqueueAlbum();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        requireArguments().remove(ARG_BITMAP);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Open a fragment for the artist of the selected album.
     *
     * @param position the position of the selected album in the adapter
     */
    private void showArtist(int position) {
        // identify current artist

        TrackModel clickedTrack = mRecyclerAdapter.getItem(position);
        String artistTitle = clickedTrack.getTrackArtistName();

        long artistId = MusicLibraryHelper.getArtistIDFromName(artistTitle, getActivity());

        // Send the event to the host activity
        mArtistSelectedCallback.onArtistSelected(new ArtistModel(artistTitle, artistId), null);
    }

    /**
     * Call the PBS to enqueue the selected track.
     *
     * @param position the position of the selected track in the adapter
     * @param asNext   flag if the track should be enqueued as next
     */
    private void enqueueTrack(int position, boolean asNext) {

        TrackModel track = mRecyclerAdapter.getItem(position);

        try {
            ((GenericActivity) requireActivity()).getPlaybackService().enqueueTrack(track, asNext);
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
        TrackModel track = mRecyclerAdapter.getItem(position);

        try {
            ((GenericActivity) requireActivity()).getPlaybackService().playTrack(track, false);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Call the PBS to enqueue the complete album.
     */
    private void enqueueAlbum() {
        // Enqueue complete album

        // Read order preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String trackOrderKey = sharedPref.getString(getString(R.string.pref_album_tracks_sort_order_key), getString(R.string.pref_album_tracks_sort_default));

        try {
            ((GenericActivity) requireActivity()).getPlaybackService().enqueueAlbum(mAlbum.getAlbumId(), trackOrderKey);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Call the PBS to play the complete album and starts with the selected track.
     * A previous playlist will be cleared.
     *
     * @param position the position of the selected track in the adapter
     */
    private void playAlbum(int position) {
        // clear playlist and play current album

        // Read order preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String trackOrderKey = sharedPref.getString(getString(R.string.pref_album_tracks_sort_order_key), getString(R.string.pref_album_tracks_sort_default));

        try {
            ((GenericActivity) requireActivity()).getPlaybackService().playAlbum(mAlbum.getAlbumId(), trackOrderKey, position);
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @Override
    public void receiveAlbumBitmap(final Bitmap bm) {
        if (bm != null && mToolbarAndFABCallback != null) {
            requireActivity().runOnUiThread(() -> {
                // set toolbar behaviour and title
                mToolbarAndFABCallback.setupToolbar(mAlbum.getAlbumName(), false, false, true);
                // set toolbar image
                mToolbarAndFABCallback.setupToolbarImage(bm);
                requireArguments().putParcelable(ARG_BITMAP, bm);
            });
        }
    }

    @Override
    public void receiveArtistBitmap(final Bitmap bm) {

    }

    @Override
    public void newAlbumImage(AlbumModel album) {
        if (album.equals(mAlbum)) {
            if (!mHideArtwork) {
                int width = requireView().getMeasuredWidth();
                mBitmapLoader.getAlbumImage(mAlbum, width, width);
            }
        }
    }
}
