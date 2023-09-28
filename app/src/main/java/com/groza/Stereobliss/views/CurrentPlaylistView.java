/* */

package com.groza.Stereobliss.views;

import android.content.Context;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.groza.Stereobliss.models.Trackmodel.TrackModel;

import com.groza.Stereobliss.R;
import com.groza.Stereobliss.adapter.CurrentPlaylistAdapter;
import com.groza.Stereobliss.playbackservice.NowPlayingInformation;
import com.groza.Stereobliss.playbackservice.PlaybackServiceConnection;
import com.groza.Stereobliss.utils.ScrollSpeedListener;

public class CurrentPlaylistView extends LinearLayout implements AdapterView.OnItemClickListener {

    private final ListView mListView;
    private final Context mContext;

    @Nullable
    private CurrentPlaylistAdapter mCurrentPlaylistAdapter;

    @Nullable
    private PlaybackServiceConnection mPlaybackServiceConnection;

    private boolean mHideArtwork;

    public CurrentPlaylistView(Context context) {
        this(context, null);
    }

    /**
     * Set up the layout of the view.
     */
    public CurrentPlaylistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.list_linear, this, true);

        // get listview
        mListView = this.findViewById(R.id.list_linear_listview);

        mListView.setOnItemClickListener(this);

        mContext = context;
    }

    /**
     * Set the PBSServiceConnection object.
     * This will create a new Adapter.
     */
    public void registerPBServiceConnection(PlaybackServiceConnection playbackServiceConnection) {
        if (playbackServiceConnection == null) {
            return;
        }
        mPlaybackServiceConnection = playbackServiceConnection;

        mCurrentPlaylistAdapter = new CurrentPlaylistAdapter(mContext, mPlaybackServiceConnection);
        mCurrentPlaylistAdapter.hideArtwork(mHideArtwork);

        mListView.setAdapter(mCurrentPlaylistAdapter);
        mListView.setOnScrollListener(new ScrollSpeedListener(mCurrentPlaylistAdapter));

        // set the selection to the current track, so the list view will positioned appropriately
        try {
            mListView.setSelection(mPlaybackServiceConnection.getPBS().getCurrentIndex());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unregisterPBSeviceConnection() {
        mListView.setAdapter(null);
        mPlaybackServiceConnection = null;
        mCurrentPlaylistAdapter = null;
    }

    /**
     * Play the selected track.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            if (mPlaybackServiceConnection != null) {
                mPlaybackServiceConnection.getPBS().jumpTo(position);
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Return the type (section track or normal track) of the view for the selected item.
     *
     * @param position The position of the selected item.
     * @return The {@link CurrentPlaylistAdapter.VIEW_TYPES} of the view for the selected item.
     */
    public CurrentPlaylistAdapter.VIEW_TYPES getItemViewType(int position) {
        if (mCurrentPlaylistAdapter != null) {
            return CurrentPlaylistAdapter.VIEW_TYPES.values()[mCurrentPlaylistAdapter.getItemViewType(position)];
        } else {
            return CurrentPlaylistAdapter.VIEW_TYPES.TYPE_TRACK_ITEM;
        }
    }

    /**
     * The playlist has changed so update the view.
     */
    public void playlistChanged(NowPlayingInformation info) {
        if (mCurrentPlaylistAdapter != null) {
            mCurrentPlaylistAdapter.updateState(info);
        }
        // set the selection to the current track, so the list view will positioned appropriately
        mListView.setSelection(info.getPlayingIndex());
    }

    /**
     * Removes the selected track from the playlist.
     *
     * @param position The position of the track in the playlist.
     */
    public void removeTrack(int position) {
        try {
            if (mPlaybackServiceConnection != null) {
                mPlaybackServiceConnection.getPBS().dequeueTrack(position);
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Removes the selected section from the playlist.
     *
     * @param position The position of the section in the playlist.
     */
    public void removeSection(int position) {
        try {
            if (mPlaybackServiceConnection != null) {
                mPlaybackServiceConnection.getPBS().dequeueTracks(position);
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Enqueue the selected track as next track in the playlist.
     *
     * @param position The position of the track in the playlist.
     */
    public void enqueueTrackAsNext(int position) {
        if (mPlaybackServiceConnection == null || mCurrentPlaylistAdapter == null) {
            return;
        }
        // save track
        TrackModel track = (TrackModel) mCurrentPlaylistAdapter.getItem(position);

        // remove track from playlist
        removeTrack(position);

        try {
            // enqueue removed track as next
            mPlaybackServiceConnection.getPBS().enqueueTrack(track, true);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Return the album id for the selected track.
     *
     * @param position The position of the track in the playlist.
     */
    public long getAlbumId(int position) {
        if (mCurrentPlaylistAdapter != null) {
            TrackModel clickedTrack = (TrackModel) mCurrentPlaylistAdapter.getItem(position);
            return clickedTrack.getTrackAlbumId();
        }
        return -1;
    }

    /**
     * Return the selected artist title for the selected track.
     *
     * @param position The position of the track in the playlist.
     */
    public String getArtistTitle(int position) {
        if (mCurrentPlaylistAdapter != null) {
            TrackModel clickedTrack = (TrackModel) mCurrentPlaylistAdapter.getItem(position);
            return clickedTrack.getTrackArtistName();
        }
        return "";
    }

    public void hideArtwork(boolean enable) {
        mHideArtwork = enable;
        if (mCurrentPlaylistAdapter != null) {
            mCurrentPlaylistAdapter.hideArtwork(enable);
        }
    }
}
