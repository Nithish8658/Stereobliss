/* */

package com.groza.Stereobliss.playbackservice;

public class StereoblissServiceState {

    public int mTrackNumber;
    public int mTrackPosition;
    public PlaybackService.RANDOMSTATE mRandomState;
    public PlaybackService.REPEATSTATE mRepeatState;

    public StereoblissServiceState() {
        mTrackNumber = -1;
        mTrackPosition = -1;
        mRandomState = PlaybackService.RANDOMSTATE.RANDOM_OFF;
        mRepeatState = PlaybackService.REPEATSTATE.REPEAT_OFF;
    }
}
