/* */

package com.groza.Stereobliss.listener;

import android.graphics.Bitmap;
import android.view.View;

public interface ToolbarAndFABCallback {
    void setupFAB(View.OnClickListener listener);

    void setupToolbar(String title, boolean scrollingEnabled, boolean drawerIndicatorEnabled, boolean showImage);

    void setupToolbarImage(Bitmap bm);
}
