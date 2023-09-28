/* */

package com.groza.Stereobliss.listener;

import android.graphics.Bitmap;

import com.groza.Stereobliss.models.ArtistModel;

public interface OnArtistSelectedListener {
    void onArtistSelected(ArtistModel artist, Bitmap bitmap);
}
