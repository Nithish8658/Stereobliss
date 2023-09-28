/* */

package com.groza.Stereobliss.playbackservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.groza.Stereobliss.playbackservice.IOdysseyPlaybackService;

public class PlaybackServiceConnection implements ServiceConnection {

    private static final String TAG = "ServiceConnection";

    /**
     * The service interface that is created when the connection is established.
     */
    private IOdysseyPlaybackService mPlaybackService;

    /**
     * Application context used for binding to the service
     */
    private final Context mApplicationContext;

    /**
     * Callback handler for connection state changes
     */
    private ConnectionNotifier mNotifier;

    public PlaybackServiceConnection(Context context) {
        mApplicationContext = context.getApplicationContext();
        mPlaybackService = null;
    }

    /**
     * Called when the connection is established successfully
     *
     * @param name    Name of the connected component
     * @param service Service that the connection was established to
     */
    @Override
    public synchronized void onServiceConnected(ComponentName name, IBinder service) {
        mPlaybackService = IOdysseyPlaybackService.Stub.asInterface(service);
        if (mPlaybackService != null && mNotifier != null) {
            mNotifier.onConnect();
        }
    }

    /**
     * Called when the service connection was disconnected for some reason (crash?)
     *
     * @param name Name of the closed component
     */
    @Override
    public synchronized void onServiceDisconnected(ComponentName name) {
        mPlaybackService = null;
        if (mNotifier != null) {
            mNotifier.onDisconnect();
        }
        //openConnection();
    }

    /**
     * This initiates the connection to the PlaybackService by binding to it
     */
    public void openConnection() {
        Intent serviceStartIntent = new Intent(mApplicationContext, PlaybackService.class);
        mApplicationContext.bindService(serviceStartIntent, this, Context.BIND_AUTO_CREATE);
    }

    /**
     * Disconnects the connection by unbinding from the service (not needed anymore)
     */
    public synchronized void closeConnection() {
        mApplicationContext.unbindService(this);
        mPlaybackService = null;
        if (mNotifier != null) {
            mNotifier.onDisconnect();
        }
    }

    public synchronized IOdysseyPlaybackService getPBS() throws RemoteException {
        if (mPlaybackService != null) {
            return mPlaybackService;
        } else {
            throw new RemoteException();
        }

    }

    /**
     * Sets an callback handler
     *
     * @param notifier Callback handler for connection state changes
     */
    public void setNotifier(ConnectionNotifier notifier) {
        mNotifier = notifier;
    }

    public interface ConnectionNotifier {
        void onConnect();

        void onDisconnect();
    }
}
