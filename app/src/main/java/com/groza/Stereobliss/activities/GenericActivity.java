/* */

package com.groza.Stereobliss.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.groza.Stereobliss.R;
import com.groza.Stereobliss.playbackservice.IOdysseyPlaybackService;
import com.groza.Stereobliss.playbackservice.PlaybackServiceConnection;
import com.groza.Stereobliss.playbackservice.managers.PlaybackServiceStatusHelper;

public abstract class GenericActivity extends AppCompatActivity {

    public ProgressDialog mProgressDialog;

    private PBSOperationFinishedReceiver mPBSOperationFinishedReceiver = null;

    @Nullable
    private PlaybackServiceConnection mServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Read theme preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String themePref = sharedPref.getString(getString(R.string.pref_theme_key), getString(R.string.pref_theme_default));
        boolean darkTheme = sharedPref.getBoolean(getString(R.string.pref_dark_theme_key), getResources().getBoolean(R.bool.pref_theme_dark_default));
        if (themePref.equals(getString(R.string.pref_oleddark_key))) {
            setTheme(R.style.AppTheme_oledDark);
        } else {
            String name = darkTheme ? String.format("AppTheme.%s", themePref) : String.format("AppTheme.%s_light", themePref);
            setTheme(getResources().getIdentifier(name, "style", getPackageName()));
        }

        super.onCreate(savedInstanceState);

        // setup progressdialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.playbackservice_working));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(true);

        mServiceConnection = new PlaybackServiceConnection(getApplicationContext());

        // Create service connection
        mServiceConnection.setNotifier(new ServiceConnectionListener());

        // suggest that we want to change the music audio stream by hardware volume controls even
        // if no music is currently played
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mPBSOperationFinishedReceiver != null) {
            unregisterReceiver(mPBSOperationFinishedReceiver);
            mPBSOperationFinishedReceiver = null;
        }
        mPBSOperationFinishedReceiver = new PBSOperationFinishedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlaybackServiceStatusHelper.MESSAGE_IDLE);
        filter.addAction(PlaybackServiceStatusHelper.MESSAGE_WORKING);
        registerReceiver(mPBSOperationFinishedReceiver, filter);

        if (mServiceConnection != null) {
            mServiceConnection.openConnection();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mPBSOperationFinishedReceiver != null) {
            unregisterReceiver(mPBSOperationFinishedReceiver);
            mPBSOperationFinishedReceiver = null;
        }

        // Close connection to unbind from service to allow it to be stopped by the system
        if (mServiceConnection != null) {
            mServiceConnection.closeConnection();
        }
    }

    public IOdysseyPlaybackService getPlaybackService() throws RemoteException {
        if (mServiceConnection != null) {
            return mServiceConnection.getPBS();
        } else {
            throw new RemoteException();
        }
    }

    abstract void onServiceConnected();

    abstract void onServiceDisconnected();

    private class PBSOperationFinishedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (PlaybackServiceStatusHelper.MESSAGE_WORKING.equals(intent.getAction())) {
                runOnUiThread(() -> {
                    if (mProgressDialog != null) {
                        mProgressDialog.show();
                    }
                });
            } else if (PlaybackServiceStatusHelper.MESSAGE_IDLE.equals(intent.getAction())) {
                runOnUiThread(() -> {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                });
            }
        }
    }

    private class ServiceConnectionListener implements PlaybackServiceConnection.ConnectionNotifier {

        @Override
        public void onConnect() {
            try {
                if (mServiceConnection != null && mServiceConnection.getPBS().isBusy()) {
                    // pbs is still working so show the progress dialog again
                    mProgressDialog.show();
                } else {
                    // pbs is not working so dismiss the progress dialog
                    mProgressDialog.dismiss();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                // error occured so dismiss the progress dialog anyway
                mProgressDialog.dismiss();
            }

            onServiceConnected();
        }

        @Override
        public void onDisconnect() {
            // disconnected so dismiss dialog anyway
            mProgressDialog.dismiss();

            onServiceDisconnected();
        }
    }
}
