

package com.groza.Stereobliss.utils;


import android.content.Context;
import android.net.Uri;

import com.groza.Stereobliss.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatHelper {
    private static final String LUCENE_SPECIAL_CHARACTERS_REGEX = "([\\+\\-\\!\\(\\)\\{\\}\\[\\]\\^\\\"\\~\\*\\?\\:\\\\\\/])";

    /**
     * Helper method to uniformly format length strings in Odyssey.
     *
     * @param context the current context to resolve the string id of the template
     * @param length  Length value in milliseconds
     * @return the formatted string, usable in the ui
     */
    public static String formatTracktimeFromMS(final Context context, final long length) {

        String retVal;

        int seconds = (int) (length / 1000);

        int hours = seconds / 3600;

        int minutes = (seconds - (hours * 3600)) / 60;

        seconds = seconds - (hours * 3600) - (minutes * 60);

        if (hours == 0) {
            retVal = String.format(Locale.getDefault(), context.getString(R.string.track_duration_short_template), minutes, seconds);
        } else {
            retVal = String.format(Locale.getDefault(), context.getString(R.string.track_duration_long_template), hours, minutes, seconds);
        }

        return retVal;
    }

    /**
     * Helper method to format the mediastore track number to a disc number string
     *
     * @param trackNumber the tracknumber from the mediastore
     * @return the formatted string, usable in the ui
     */
    public static String formatDiscNumber(final int trackNumber) {

        if (trackNumber == -1) {
            return "";
        }

        return String.format("%02d", trackNumber / 1000);
    }

    /**
     * Helper method to format the mediastore track number to a track number string
     *
     * @param trackNumber the tracknumber from the mediastore
     * @return the formatted string, usable in the ui
     */
    public static String formatTrackNumber(final int trackNumber) {

        if (trackNumber == -1) {
            return "";
        }

        return String.format("%02d", trackNumber % 1000);
    }

    /**
     * Helper method to format a timestamp to a uniformly format date string in Odyssey.
     *
     * @param timestamp The timestamp in milliseconds
     * @return the formatted string, usable in the ui
     */
    public static String formatTimeStampToString(final long timestamp) {
        Date date = new Date(timestamp);
        // Create a locale based formatted DateTime string
        return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault()).format(date);
    }

    /**
     * Helper method to create a {@link Uri} from the given uri String.
     * <p>
     * This method will check if the given uri needs to be encoded to support special characters
     * like :, %, # etc. and will add a file scheme if scheme is missing.
     *
     * @param uri the path to the media file as String
     * @return The encoded {@link Uri}.
     */
    public static Uri encodeURI(final String uri) {
        Uri encodedUri = Uri.parse(Uri.encode(uri, "/"));

        String scheme = encodedUri.getScheme();

        if (scheme != null) {
            return encodedUri;
        } else {
            return Uri.parse("file://" + Uri.encode(uri, "/"));
        }
    }

    /**
     * Escapes Apache lucene special characters (e.g. used by MusicBrainz) for URLs.
     * See:
     * https://lucene.apache.org/core/4_3_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html
     *
     * @param input Input string
     * @return Escaped string
     */
    public static String escapeSpecialCharsLucene(String input) {
        String retVal = input.replaceAll("(\\&\\&)", "\\\\&\\\\&");
        retVal = retVal.replaceAll("(\\|\\|)", "\\\\|\\\\|");

        retVal = retVal.replaceAll(LUCENE_SPECIAL_CHARACTERS_REGEX, "\\\\$1");
        return retVal;
    }
}
