/* */

package com.groza.Stereobliss.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;

import androidx.core.content.ContextCompat;

import com.groza.Stereobliss.models.FileModel;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionHelper {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;

    /**
     * Permission safe call of the query method of the content resolver.
     *
     * @param context       The application context for the permission check and the access of the content resolver.
     * @param uri           The URI, using the content:// scheme, for the content to
     *                      retrieve.
     * @param projection    A list of which columns to return. Passing null will
     *                      return all columns, which is inefficient.
     * @param selection     A filter declaring which rows to return, formatted as an
     *                      SQL WHERE clause (excluding the WHERE itself). Passing null will
     *                      return all rows for the given URI.
     * @param selectionArgs You may include ?s in selection, which will be
     *                      replaced by the values from selectionArgs, in the order that they
     *                      appear in the selection. The values will be bound as Strings.
     * @param sortOrder     How to order the rows, formatted as an SQL ORDER BY
     *                      clause (excluding the ORDER BY itself). Passing null will use the
     *                      default sort order, which may be unordered.
     * @return A {@link Cursor} which is positioned before the first entry, or null if the user not granted the necessary permissions.
     */
    public static Cursor query(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        }

        return cursor;
    }

    /**
     * Permission safe call to get all music files in a given directory.
     *
     * @param context   The application context for the permission check.
     * @param directory The {@link FileModel} representing the parent directory.
     * @return The list of {@link FileModel} of all files in the given directory.
     */
    public static List<FileModel> getFilesForDirectory(final Context context, final FileModel directory) {
        List<FileModel> files = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            files = directory.listFilesSorted();
        }

        return files;
    }

    /**
     * Permission safe call to get all files for a given directory path.
     *
     * @param context       The application context for the permission check.
     * @param directoryPath The path to the directory.
     * @param filter        A {@link FilenameFilter} to filter only specific files.
     * @return A {@link List} of {@link File} in the directory that match the given {@link FilenameFilter}.
     */
    public static List<File> getFilesForDirectory(final Context context, final String directoryPath, final FilenameFilter filter) {
        List<File> filteredFiles = new ArrayList<>();

        final File directory = new File(directoryPath);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            File[] files = directory.listFiles(filter);

            if (files != null) {
                filteredFiles = Arrays.asList(files);
            }
        }

        return filteredFiles;
    }
}
