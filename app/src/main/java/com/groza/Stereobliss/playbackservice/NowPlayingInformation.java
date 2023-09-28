/* */

package com.groza.Stereobliss.playbackservice;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.groza.Stereobliss.models.Trackmodel.TrackModel;



public final class NowPlayingInformation implements Parcelable {

    // Parcel data
    private final PlaybackService.PLAYSTATE mPlayState;
    private final int mPlayingIndex;
    private final PlaybackService.REPEATSTATE mRepeat;
    private final PlaybackService.RANDOMSTATE mRandom;
    private final int mPlaylistLength;
    @NonNull
    private final TrackModel mCurrentTrack;

    public static Parcelable.Creator<NowPlayingInformation> CREATOR = new Parcelable.Creator<NowPlayingInformation>() {

        @Override
        public NowPlayingInformation createFromParcel(Parcel source) {
            PlaybackService.PLAYSTATE playState = PlaybackService.PLAYSTATE.values()[source.readInt()];
            int playingIndex = source.readInt();
            PlaybackService.REPEATSTATE repeat = PlaybackService.REPEATSTATE.values()[source.readInt()];
            PlaybackService.RANDOMSTATE random = PlaybackService.RANDOMSTATE.values()[source.readInt()];
            int playlistlength = source.readInt();
            TrackModel currentTrack = source.readParcelable(TrackModel.class.getClassLoader());
            return new NowPlayingInformation(playState, playingIndex, repeat, random, playlistlength, currentTrack);
        }

        @Override
        public NowPlayingInformation[] newArray(int size) {
            return new NowPlayingInformation[size];
        }
    };

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public NowPlayingInformation() {
        mPlayState = PlaybackService.PLAYSTATE.STOPPED;
        mPlayingIndex = -1;
        mRepeat = PlaybackService.REPEATSTATE.REPEAT_OFF;
        mRandom = PlaybackService.RANDOMSTATE.RANDOM_OFF;
        mPlaylistLength = 0;
        mCurrentTrack = new TrackModel();
    }

    public NowPlayingInformation(PlaybackService.PLAYSTATE playing, int playingIndex, PlaybackService.REPEATSTATE repeat, PlaybackService.RANDOMSTATE random, int playlistlength, @NonNull TrackModel currentTrack) {
        mPlayState = playing;
        mPlayingIndex = playingIndex;
        mRepeat = repeat;
        mRandom = random;
        mPlaylistLength = playlistlength;
        mCurrentTrack = currentTrack;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPlayState.ordinal());
        dest.writeInt(mPlayingIndex);
        dest.writeInt(mRepeat.ordinal());
        dest.writeInt(mRandom.ordinal());
        dest.writeInt(mPlaylistLength);
        dest.writeParcelable(mCurrentTrack, flags);
    }

    public PlaybackService.PLAYSTATE getPlayState() {
        return mPlayState;
    }

    @NonNull
    @Override
    public String toString() {
        return "Playstate: " + mPlayState.name() + " index: " + mPlayingIndex + "repeat: " + mRepeat + "random: " + mRandom + "playlistlength: " + mPlaylistLength + "track: " + mCurrentTrack;
    }

    public int getPlayingIndex() {
        return mPlayingIndex;
    }

    public PlaybackService.REPEATSTATE getRepeat() {
        return mRepeat;
    }

    public PlaybackService.RANDOMSTATE getRandom() {
        return mRandom;
    }

    public int getPlaylistLength() {
        return mPlaylistLength;
    }

    @NonNull
    public TrackModel getCurrentTrack() {
        return mCurrentTrack;
    }

}
