
package com.groza.Stereobliss.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.groza.Stereobliss.R;

public class PreferenceHelper {
    public enum LIBRARY_TRACK_CLICK_ACTION {
        ACTION_ADD_SONG,
        ACTION_PLAY_SONG,
        ACTION_PLAY_SONG_NEXT,
        ACTION_CLEAR_AND_PLAY,
    }

    public static LIBRARY_TRACK_CLICK_ACTION getClickAction(SharedPreferences prefs, Context context) {
        String clickActionPref = prefs.getString(context.getString(R.string.pref_library_click_action_key), context.getString(R.string.pref_library_click_action_default));
        if (clickActionPref.equals(context.getString(R.string.pref_library_click_action_add_key))) {
            return LIBRARY_TRACK_CLICK_ACTION.ACTION_ADD_SONG;
        } else if (clickActionPref.equals(context.getString(R.string.pref_library_click_action_play_key))) {
            return LIBRARY_TRACK_CLICK_ACTION.ACTION_PLAY_SONG;
        } else if (clickActionPref.equals(context.getString(R.string.pref_library_click_action_play_next_key))) {
            return LIBRARY_TRACK_CLICK_ACTION.ACTION_PLAY_SONG_NEXT;
        } else if (clickActionPref.equals(context.getString(R.string.pref_library_click_action_clear_and_play))) {
            return LIBRARY_TRACK_CLICK_ACTION.ACTION_CLEAR_AND_PLAY;
        }
        return LIBRARY_TRACK_CLICK_ACTION.ACTION_ADD_SONG;
    }
}
