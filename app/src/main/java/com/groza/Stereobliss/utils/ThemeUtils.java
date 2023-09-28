
package com.groza.Stereobliss.utils;

import android.content.Context;
import android.util.TypedValue;

public class ThemeUtils {

    /**
     * returns the color for the given theme attribute
     *
     * @param context        the current context to resolve the attribute id
     * @param attributeColor the requested theme attribute id
     * @return the requested color as an integer
     */
    public static int getThemeColor(final Context context, final int attributeColor) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attributeColor, value, true);
        return value.data;
    }

    /**
     * returns the resource id for the given theme attribute
     *
     * @param context     the current context to resolve the attribute id
     * @param attributeId the requested theme attribute id
     * @return the resolved resource id as an integer
     */
    public static int getThemeResourceId(final Context context, final int attributeId) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attributeId, value, true);
        return value.resourceId;
    }
}
