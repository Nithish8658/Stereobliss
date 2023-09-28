/* */

package com.groza.Stereobliss.dialogs;


import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.groza.Stereobliss.R;
import com.groza.Stereobliss.playbackservice.PlaybackServiceConnection;

public class RandomIntelligenceDialog extends DialogFragment implements SeekBar.OnSeekBarChangeListener {

    private TextView mDialogLabel;
    private TextView mExplanationLabel;

    private int mIntelligenceFactor;

    private PlaybackServiceConnection mPBSConnection;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());

        final LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View seekView = inflater.inflate(R.layout.dumbness_select_dialog, null);

        SeekBar seekBar = seekView.findViewById(R.id.volume_seekbar);
        mDialogLabel = seekView.findViewById(R.id.dialog_text);
        mExplanationLabel = seekView.findViewById(R.id.dialog_explanation);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        mIntelligenceFactor = sharedPreferences.getInt(getString(R.string.pref_smart_random_key_int), getResources().getInteger(R.integer.pref_smart_random_default));

        seekBar.setProgress(mIntelligenceFactor);
        seekBar.setOnSeekBarChangeListener(this);

        updateLabels();

        builder.setView(seekView);

        builder.setPositiveButton(R.string.error_dialog_ok_action, ((dialog, which) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(getString(R.string.pref_smart_random_key_int), mIntelligenceFactor);
            editor.apply();

            try {
                mPBSConnection.getPBS().setSmartRandom(mIntelligenceFactor);
            } catch (RemoteException ignored) {
            }

            dismiss();
        }));
        builder.setNegativeButton(R.string.dialog_action_cancel, (dialog, which) -> dismiss());

        mPBSConnection = new PlaybackServiceConnection(requireContext());
        mPBSConnection.openConnection();

        return builder.create();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mIntelligenceFactor = progress;
        updateLabels();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void updateLabels() {
        if (mIntelligenceFactor == 100) {
            mDialogLabel.setText(getString(R.string.preference_random_intelligence_dialog_title, mIntelligenceFactor));
            mExplanationLabel.setText(getString(R.string.preference_random_intelligence_more_intelligent));
        } else if (mIntelligenceFactor > 60) {
            mDialogLabel.setText(getString(R.string.preference_random_intelligence_dialog_title, mIntelligenceFactor));
            mExplanationLabel.setText(getString(R.string.preference_random_intelligence_intelligent));
        } else if (mIntelligenceFactor > 40) {
            mDialogLabel.setText(getString(R.string.preference_random_intelligence_dialog_title, mIntelligenceFactor));
            mExplanationLabel.setText(getString(R.string.preference_random_intelligence_balanced));
        } else if (mIntelligenceFactor > 0) {
            mDialogLabel.setText(getString(R.string.preference_random_intelligence_dialog_title, mIntelligenceFactor));
            mExplanationLabel.setText(getString(R.string.preference_random_intelligence_dumb));
        } else {
            mDialogLabel.setText(getString(R.string.preference_random_intelligence_dialog_title, mIntelligenceFactor));
            mExplanationLabel.setText(getString(R.string.preference_random_intelligence_dumber));
        }
    }
}
