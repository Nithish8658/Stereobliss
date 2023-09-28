/* */

package com.groza.Stereobliss.utils;


import android.content.Context;

import com.groza.Stereobliss.models.FileModel;
import com.groza.Stereobliss.models.Trackmodel.TrackModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class PlaylistParser {
    private static final String TAG = PlaylistParser.class.getSimpleName();
    protected final FileModel mFile;

    PlaylistParser(FileModel playlistFile) {
        mFile = playlistFile;
    }

    /**
     * Tries to find a prefix for paths inside a playlist. If a user reuses
     * MPD playlist files and store them within is music directory this method
     * will try to locate the files by traversing the file path up until the / is reached.
     *
     * @param url Song url
     * @return Path prefix if found (or none if none is necessary)
     * @throws NoPrefixFoundException Thrown if the file could not be located with or without
     *                                prefix (probably a dead object inside the playlist file)
     */
    private String findPrefix(String url) throws NoPrefixFoundException {
        String pathPrefix = "";
        File tmpFile = new File(url);
        if (!tmpFile.exists()) {
            String plPath = mFile.getPath();
            plPath = plPath.substring(0, plPath.lastIndexOf('/'));
            while (!plPath.isEmpty()) {
                tmpFile = new File(plPath + '/' + url);
                if (!tmpFile.exists() && plPath.contains("/")) {
                    plPath = plPath.substring(0, plPath.lastIndexOf('/'));
                } else if (tmpFile.exists()) {
                    pathPrefix = plPath;
                    return pathPrefix;
                }
            }
        } else {
            return pathPrefix;
        }
        throw new NoPrefixFoundException();
    }

    /**
     * Parses the File URL list generated from the subclasses. Tries to check if a path prefix
     * is necessary. Generates a list of {@link TrackModel}.
     *
     * @param context Context used for {@link TrackModel} retrieval
     * @param urls    List of File URLs
     * @return ArrayList of {@link FileModel}
     */
    protected ArrayList<TrackModel> createTrackModels(Context context, ArrayList<String> urls) {
        ArrayList<TrackModel> retList = new ArrayList<>();

        boolean foundPrefix = false;
        String pathPrefix = "";

        Iterator<String> urlIterator = urls.iterator();

        while (urlIterator.hasNext()) {
            String url = urlIterator.next();

            // Check if prefix is found already, then skip prefix detection heuristic.
            if (!foundPrefix) {
                try {
                    pathPrefix = findPrefix(url);
                    foundPrefix = true;
                } catch (NoPrefixFoundException e) {
                    // File not found with or without prefix. Remove it from list
                    urlIterator.remove();
                    continue;
                }
            }

            File tempFile = new File(pathPrefix + '/' + url);
            if (!tempFile.exists()) {
                // File does not exists even with found prefix, remove it.
                urlIterator.remove();
            }
        }

        for (String url : urls) {
            FileModel tmpFile = new FileModel(pathPrefix + '/' + url);

            TrackModel tmpModel = FileExplorerHelper.getInstance().getTrackModelForFile(context, tmpFile);
            retList.add(tmpModel);
        }

        return retList;
    }

    public abstract ArrayList<String> getFileURLsFromFile(Context context);

    public ArrayList<TrackModel> parseList(Context context) {
        return createTrackModels(context, getFileURLsFromFile(context));
    }

    private static class NoPrefixFoundException extends Exception {

    }
}
