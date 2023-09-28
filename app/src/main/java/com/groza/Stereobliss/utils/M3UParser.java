

package com.groza.Stereobliss.utils;


import android.content.Context;
import android.net.Uri;

import com.groza.Stereobliss.models.FileModel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class M3UParser extends PlaylistParser {
    private static final String TAG = M3UParser.class.getSimpleName();


    public M3UParser(FileModel playlistFile) {
        super(playlistFile);
    }

    @Override
    public ArrayList<String> getFileURLsFromFile(Context context) {
        Uri uri = FormatHelper.encodeURI(mFile.getPath());

        InputStream inputStream;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        if (null == inputStream) {
            return new ArrayList<>();
        }

        BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream));

        String line = "";
        ArrayList<String> urls = new ArrayList<>();
        do {
            try {
                line = bufReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (line == null || line.startsWith("#")) {
                // ignore those lines
                continue;
            }

            urls.add(line);
        } while (line != null);


        return urls;
    }
}
