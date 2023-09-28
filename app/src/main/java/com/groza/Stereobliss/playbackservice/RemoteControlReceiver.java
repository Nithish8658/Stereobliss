/* */

package com.groza.Stereobliss.playbackservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.groza.Stereobliss.BuildConfig;

public class RemoteControlReceiver extends BroadcastReceiver {
    private static final String TAG = "OdysseyRemoteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {

            final KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (event.getAction() == KeyEvent.ACTION_UP) {

                if (BuildConfig.DEBUG) {
                    Log.v(TAG, "Received key: " + event);
                }

                Intent nextIntent;

                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        nextIntent = new Intent(PlaybackService.ACTION_NEXT);
                        nextIntent.setPackage(context.getPackageName());
                        context.sendBroadcast(nextIntent);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        nextIntent = new Intent(PlaybackService.ACTION_PREVIOUS);
                        nextIntent.setPackage(context.getPackageName());
                        context.sendBroadcast(nextIntent);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        nextIntent = new Intent(PlaybackService.ACTION_TOGGLEPAUSE);
                        nextIntent.setPackage(context.getPackageName());
                        context.sendBroadcast(nextIntent);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        nextIntent = new Intent(PlaybackService.ACTION_PLAY);
                        nextIntent.setPackage(context.getPackageName());
                        context.sendBroadcast(nextIntent);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        nextIntent = new Intent(PlaybackService.ACTION_PAUSE);
                        nextIntent.setPackage(context.getPackageName());
                        context.sendBroadcast(nextIntent);
                        break;
                }
            }
        }
    }
}
