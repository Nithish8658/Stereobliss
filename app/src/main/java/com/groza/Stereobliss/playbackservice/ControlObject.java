/* */

package com.groza.Stereobliss.playbackservice;

import com.groza.Stereobliss.models.PlaylistModel;
import com.groza.Stereobliss.models.Trackmodel.TrackModel;

/**
 * Message object which get passed between PlaybackServiceInterface ->
 * PlaybackServiceHandler
 *
 * @author hendrik
 */
public class ControlObject {

    public enum PLAYBACK_ACTION {
        ODYSSEY_PLAY, ODYSSEY_TOGGLEPAUSE, ODYSSEY_NEXT, ODYSSEY_PREVIOUS, ODYSSEY_SEEKTO, ODYSSEY_JUMPTO, ODYSSEY_REPEAT, ODYSSEY_RANDOM,
        ODYSSEY_ENQUEUETRACK, ODYSSEY_PLAYTRACK, ODYSSEY_DEQUEUETRACK, ODYSSEY_DEQUEUETRACKS,
        ODYSSEY_PLAYALLTRACKS,
        ODYSSEY_RESUMEBOOKMARK, ODYSSEY_DELETEBOOKMARK, ODYSSEY_CREATEBOOKMARK,
        ODYSSEY_SAVEPLAYLIST, ODYSSEY_CLEARPLAYLIST, ODYSSEY_SHUFFLEPLAYLIST,
        ODYSSEY_ENQUEUEPLAYLIST, ODYSSEY_PLAYPLAYLIST,
        ODYSSEY_ENQUEUEFILE, ODYSSEY_PLAYFILE,
        ODYSSEY_PLAYDIRECTORY, ODYSSEY_ENQUEUEDIRECTORYANDSUBDIRECTORIES, ODYSSEY_PLAYDIRECTORYANDSUBDIRECTORIES,
        ODYSSEY_ENQUEUEALBUM, ODYSSEY_PLAYALBUM,
        ODYSSEY_ENQUEUERECENTALBUMS, ODYSSEY_PLAYRECENTALBUMS,
        ODYSSEY_ENQUEUEARTIST, ODYSSEY_PLAYARTIST,
        ODYSSEY_START_SLEEPTIMER, ODYSSEY_CANCEL_SLEEPTIMER,
        ODYSSEY_SET_SMARTRANDOM
    }

    private PLAYBACK_ACTION mAction;
    private boolean mBoolparam;
    private int mIntparam;
    private String mStringparam;
    private String mSecondStringParam;
    private TrackModel mTrack;
    private long mLongParam;
    private PlaylistModel mPlaylist;

    public ControlObject(PLAYBACK_ACTION action) {
        mAction = action;
    }

    public ControlObject(PLAYBACK_ACTION action, int param) {
        mIntparam = param;
        mAction = action;
    }

    public ControlObject(PLAYBACK_ACTION action, String param) {
        mStringparam = param;
        mAction = action;
    }

    public ControlObject(PLAYBACK_ACTION action, String param, String param2) {
        mStringparam = param;
        mSecondStringParam = param2;
        mAction = action;
    }

    public ControlObject(PLAYBACK_ACTION action, long longParam, String param) {
        mLongParam = longParam;
        mStringparam = param;
        mAction = action;
    }

    public ControlObject(PLAYBACK_ACTION action, long longParam, String param, int intParam) {
        mLongParam = longParam;
        mStringparam = param;
        mIntparam = intParam;
        mAction = action;
    }

    public ControlObject(PLAYBACK_ACTION action, String param, String param2, int intParam) {
        mStringparam = param;
        mSecondStringParam = param2;
        mIntparam = intParam;
        mAction = action;
    }

    public ControlObject(PLAYBACK_ACTION action, String param, boolean boolParam) {
        mAction = action;
        mStringparam = param;
        mBoolparam = boolParam;
    }

    public ControlObject(PLAYBACK_ACTION action, TrackModel track, boolean boolParam) {
        mAction = action;
        mTrack = track;
        mBoolparam = boolParam;
    }

    public ControlObject(PLAYBACK_ACTION action, long param) {
        mAction = action;
        mLongParam = param;
    }

    public ControlObject(PLAYBACK_ACTION action, long param, boolean boolParam) {
        mAction = action;
        mLongParam = param;
        mBoolparam = boolParam;
    }

    public ControlObject(PLAYBACK_ACTION action, long longParam, String stringParam, String stringParam2) {
        mAction = action;
        mLongParam = longParam;
        mStringparam = stringParam;
        mSecondStringParam = stringParam2;
    }

    public ControlObject(PLAYBACK_ACTION action, String stringParam, int intParam) {
        mAction = action;
        mStringparam = stringParam;
        mIntparam = intParam;
    }

    public ControlObject(PLAYBACK_ACTION action, PlaylistModel playlist) {
        mAction = action;
        mPlaylist = playlist;
    }

    public ControlObject(PLAYBACK_ACTION action, PlaylistModel playlist, int intParam) {
        mAction = action;
        mPlaylist = playlist;
        mIntparam = intParam;
    }

    public PLAYBACK_ACTION getAction() {
        return mAction;
    }

    public String getStringParam() {
        return mStringparam;
    }

    public String getSecondStringParam() {
        return mSecondStringParam;
    }

    public int getIntParam() {
        return mIntparam;
    }

    public boolean getBoolParam() {
        return mBoolparam;
    }

    public long getLongParam() {
        return mLongParam;
    }

    public TrackModel getTrack() {
        return mTrack;
    }

    public PlaylistModel getPlaylist() {
        return mPlaylist;
    }
}
