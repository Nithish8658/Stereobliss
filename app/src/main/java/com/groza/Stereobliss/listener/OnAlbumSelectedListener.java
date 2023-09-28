/* */

package com.groza.Stereobliss.listener;

import android.graphics.Bitmap;

import com.groza.Stereobliss.models.AlbumModel;

public interface OnAlbumSelectedListener {
    void onAlbumSelected(AlbumModel album, Bitmap bitmap);
}
