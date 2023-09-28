
package com.groza.Stereobliss.activities;

import android.os.Bundle;

import com.groza.Stereobliss.R;
import com.groza.Stereobliss.utils.ThemeUtils;

public class StereoblissAboutActivity extends GenericActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stereobliss_about);

        getWindow().setStatusBarColor(ThemeUtils.getThemeColor(this, R.attr.odyssey_color_primary_dark));
    }

    @Override
    void onServiceConnected() {

    }

    @Override
    void onServiceDisconnected() {

    }
}
