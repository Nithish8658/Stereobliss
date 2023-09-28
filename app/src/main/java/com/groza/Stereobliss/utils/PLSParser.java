/* */

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

public class PLSParser extends PlaylistParser {
    private static final String TAG = PLSParser.class.getSimpleName();


    public PLSParser(FileModel file) {
        super(file);
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

            if (line == null || !line.startsWith("File")) {
                // ignore those lines
                continue;
            }

            urls.add(line.substring(line.indexOf('=') + 1));
        } while (line != null);


        return urls;
    }
}
