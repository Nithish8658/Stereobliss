/* */

package com.groza.Stereobliss.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.groza.Stereobliss.activities.StereoblissMainActivity;
import com.groza.Stereobliss.dialogs.ErrorDialog;
import com.groza.Stereobliss.dialogs.RandomIntelligenceDialog;
import com.groza.Stereobliss.dialogs.SeekBackwardsStepSizeDialog;
import com.groza.Stereobliss.utils.FileExplorerHelper;

import com.groza.Stereobliss.R;
import com.groza.Stereobliss.activities.GenericActivity;
import com.groza.Stereobliss.listener.ToolbarAndFABCallback;
import com.groza.Stereobliss.utils.ThemeUtils;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Callback for artwork request
     */
    private OnArtworkSettingsRequestedCallback mArtworkCallback;

    /**
     * Callback to setup toolbar and fab
     */
    private ToolbarAndFABCallback mToolbarAndFABCallback;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    /**
     * Called to do initial creation of a fragment.
     * <p/>
     * This method will setup a listener to start the system audio equalizer.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // add listener to open equalizer
        Preference openEqualizer = findPreference(getString(R.string.pref_open_equalizer_key));
        openEqualizer.setOnPreferenceClickListener(preference -> {
            // Start the equalizer
            Intent startEqualizerIntent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
            startEqualizerIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, requireContext().getPackageName());
            startEqualizerIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);

            try {
                startEqualizerIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, ((GenericActivity) requireActivity()).getPlaybackService().getAudioSessionID());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            try {
                getActivity().startActivity(startEqualizerIntent);
            } catch (ActivityNotFoundException e) {
                ErrorDialog equalizerNotFoundDlg = ErrorDialog.newInstance(R.string.dialog_equalizer_not_found_title, R.string.dialog_equalizer_not_found_message);
                equalizerNotFoundDlg.show(requireActivity().getSupportFragmentManager(), "EqualizerNotFoundDialog");
            }

            return true;
        });

        // add listener to open artwork settings
        Preference openArtwork = findPreference(getString(R.string.pref_artwork_settings_key));
        openArtwork.setOnPreferenceClickListener(preference -> {
            mArtworkCallback.openArtworkSettings();
            return true;
        });

        // add listener to clear the default directory
        Preference clearDefaultDirectory = findPreference(getString(R.string.pref_clear_default_directory_key));
        clearDefaultDirectory.setOnPreferenceClickListener(preference -> {
            SharedPreferences.Editor sharedPrefEditor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit();
            sharedPrefEditor.putString(getString(R.string.pref_file_browser_root_dir_key), FileExplorerHelper.getInstance().getStorageVolumes(getContext()).get(0));
            sharedPrefEditor.apply();
            return true;
        });

        Preference backwardsSeek = findPreference(getString(R.string.pref_seek_backwards_key));
        backwardsSeek.setOnPreferenceClickListener(preference -> {
            SeekBackwardsStepSizeDialog dialog = new SeekBackwardsStepSizeDialog();
            dialog.show(requireActivity().getSupportFragmentManager(), "Volume steps");
            return true;
        });

        Preference randomIntelligencePreference = findPreference(getString(R.string.pref_smart_random_key_int));
        randomIntelligencePreference.setOnPreferenceClickListener(preference -> {
            RandomIntelligenceDialog dialog = new RandomIntelligenceDialog();
            dialog.show(requireActivity().getSupportFragmentManager(), "Random Intelligence dialog");
            return true;
        });
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
            mArtworkCallback = (OnArtworkSettingsRequestedCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnArtworkSettingsRequestedCallback");
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
     * Register listener and setup the toolbar.
     */
    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        if (mToolbarAndFABCallback != null) {
            // set toolbar behaviour and title
            mToolbarAndFABCallback.setupToolbar(getString(R.string.fragment_title_settings), false, true, false);
            // set up play button
            mToolbarAndFABCallback.setupFAB(null);
        }
    }

    /**
     * Called when the Fragment is no longer resumed.
     * <p/>
     * Unregister listener.
     */
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Create the preferences from an xml resource file
     */
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.stereobliss_main_settings);
        PreferenceManager.setDefaultValues(requireActivity(), R.xml.stereobliss_main_settings, false);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        // we have to set the background color at this point otherwise we loose the ripple effect
        view.setBackgroundColor(ThemeUtils.getThemeColor(requireContext(), R.attr.odyssey_color_background));

        return view;
    }

    /**
     * Called when a shared preference is changed, added, or removed.
     * <p/>
     * This method will restart the activity if the theme was changed.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_theme_key)) || key.equals(getString(R.string.pref_dark_theme_key))) {
            Intent intent = requireActivity().getIntent();
            intent.putExtra(StereoblissMainActivity.MAINACTIVITY_INTENT_EXTRA_REQUESTEDVIEW, StereoblissMainActivity.REQUESTEDVIEW.SETTINGS.ordinal());
            requireActivity().finish();
            startActivity(intent);
        } else if (key.equals(getString(R.string.pref_hide_media_on_lockscreen_key))) {
            try {
                boolean hideMediaOnLockscreen = sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_hide_media_on_lockscreen_default));
                ((GenericActivity) requireActivity()).getPlaybackService().hideMediaOnLockscreenChanged(hideMediaOnLockscreen);
            } catch (RemoteException e) {
            }
        } else if (key.equals(getString(R.string.pref_smart_random_key_int))) {
            try {
                ((GenericActivity) requireActivity()).getPlaybackService().setSmartRandom(sharedPreferences.getInt(key, getResources().getInteger(R.integer.pref_smart_random_default)));
            } catch (RemoteException e) {
            }
        }
    }

    public interface OnArtworkSettingsRequestedCallback {
        void openArtworkSettings();
    }
}
