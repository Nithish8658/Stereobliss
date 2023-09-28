/* */

package com.groza.Stereobliss.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.groza.Stereobliss.R;
import com.groza.Stereobliss.utils.ThemeUtils;

import java.util.Timer;
import java.util.TimerTask;


public class AlbumArtistView extends ViewSwitcher {
    private static final String TAG = AlbumArtistView.class.getSimpleName();

    /**
     * Constant value of the period used to switch images
     */
    private static final int VIEW_SWITCH_TIME = 7500;

    ImageView mAlbumImage;
    ImageView mArtistImage;

    boolean mAlbumImageAvailable;
    boolean mArtistImageAvailable;

    Timer mSwitchTimer;

    public AlbumArtistView(Context context) {
        this(context, null);
    }

    public AlbumArtistView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Set animation for a smooth image transition
        setInAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        setOutAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));

        // Create two image views
        mAlbumImage = new ImageView(context);
        mArtistImage = new ImageView(context);

        // Add them as a child
        addView(mAlbumImage);
        addView(mArtistImage);

        mSwitchTimer = null;

        // Make sure the placeholder image is shown
        clearAlbumImage();
    }

    /**
     * Stops the image switching if the window is not visible to the user anymore.
     *
     * @param visibility Visibilty value of the view View.VISIBLE, ...
     */
    @Override
    public void onWindowVisibilityChanged(int visibility) {
        if (visibility == VISIBLE) {
            // Start the switching
            imagesChanged();
        } else {
            // Window hidden, stop switching
            if (mSwitchTimer != null) {
                mSwitchTimer.cancel();
                mSwitchTimer.purge();
                mSwitchTimer = null;
            }
        }
    }

    /**
     * Stops the image switching if the window is not visible to the user anymore.
     *
     * @param visibility Visibility value of the view View.VISIBLE, ...
     */
    @Override
    public void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (visibility == VISIBLE) {
            // Start the switching
            imagesChanged();
        } else {
            // Window hidden, stop switching
            if (mSwitchTimer != null) {
                mSwitchTimer.cancel();
                mSwitchTimer.purge();
                mSwitchTimer = null;
            }
        }
    }


    /**
     * Sets the album image to the given bitmap
     *
     * @param image {@link Bitmap} to show as an album cover
     */
    public void setAlbumImage(Bitmap image) {
        mAlbumImage.setImageBitmap(image);

        mAlbumImageAvailable = true;

        imagesChanged();
    }

    /**
     * Sets the artist image to the given bitmap.
     *
     * @param image {@link Bitmap} to show as an artist image
     */
    public void setArtistImage(Bitmap image) {
        mArtistImage.setImageBitmap(image);

        mArtistImageAvailable = true;
        imagesChanged();
    }

    /**
     * Shows the placeholder image.
     */
    public void clearAlbumImage() {
        // get tint color
        int tintColor = ThemeUtils.getThemeColor(getContext(), R.attr.odyssey_color_text_background_primary);

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.music, null);

        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTintList(drawable, ColorStateList.valueOf(tintColor));
        }

        mAlbumImage.setImageDrawable(drawable);
        mAlbumImageAvailable = false;
        imagesChanged();
    }

    /**
     * Hides the artist image and switches back to the album image.
     */
    public void clearArtistImage() {
        mArtistImage.setImageBitmap(null);
        mArtistImageAvailable = false;
        imagesChanged();
    }

    public void imagesChanged() {
        if (mAlbumImageAvailable && mArtistImageAvailable) {
            if (mSwitchTimer == null) {
                mSwitchTimer = new Timer();
                mSwitchTimer.schedule(new SwitchTask(), VIEW_SWITCH_TIME, VIEW_SWITCH_TIME);
            } else {
                // Toggle displayed image
                setDisplayedChild(getDisplayedChild() == 0 ? 1 : 0);
            }
        } else if (mAlbumImageAvailable) {
            // Only one image available
            if (mSwitchTimer != null) {
                mSwitchTimer.cancel();
                mSwitchTimer.purge();
                mSwitchTimer = null;
            }
            if (getDisplayedChild() == 1) {
                setDisplayedChild(0);
            }
        } else if (mArtistImageAvailable) {
            // Only one image available
            if (mSwitchTimer != null) {
                mSwitchTimer.cancel();
                mSwitchTimer.purge();
                mSwitchTimer = null;
            }
            if (getDisplayedChild() == 0) {
                setDisplayedChild(1);
            }
        } else {
            // Show placeholder image instead and cancel switching
            setDisplayedChild(0);
            // No image available, cancel task
            if (mSwitchTimer != null) {
                mSwitchTimer.cancel();
                mSwitchTimer.purge();
                mSwitchTimer = null;
            }
        }
    }

    /**
     * {@link TimerTask} that periodically switches between images
     */
    private class SwitchTask extends TimerTask {

        @Override
        public void run() {
            post(AlbumArtistView.this::imagesChanged);
        }
    }
}
