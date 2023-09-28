/* */

package com.groza.Stereobliss.utils;


import com.groza.Stereobliss.models.FileModel;

public class PlaylistParserFactory {
    public static PlaylistParser getParser(FileModel playlistFile) {
        String path = playlistFile.getPath();
        if (path.toLowerCase().endsWith("m3u")) {
            return new M3UParser(playlistFile);
        } else if (path.toLowerCase().endsWith("pls")) {
            return new PLSParser(playlistFile);
        }
        return null;
    }
}
