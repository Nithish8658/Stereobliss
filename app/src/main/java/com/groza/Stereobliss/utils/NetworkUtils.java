/* */

package com.groza.Stereobliss.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class NetworkUtils {

    /**
     * Checks the current network state if an artwork download is allowed.
     *
     * @param context     The current context to resolve the networkinfo
     * @param onlyUseWifi Flag if only a wifi connection is a valid network state
     * @return true if a download is allowed else false
     */
    public static boolean isDownloadAllowed(final Context context, final boolean onlyUseWifi) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final Network activeNetwork = cm.getActiveNetwork();

            if (activeNetwork != null) {
                final NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(activeNetwork);

                if (networkCapabilities != null) {
                    final boolean isWifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);

                    return !(onlyUseWifi && !isWifi);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            final NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null) {
                final boolean isWifi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET;

                return !(onlyUseWifi && !isWifi);
            } else {
                return false;
            }
        }
    }
}
