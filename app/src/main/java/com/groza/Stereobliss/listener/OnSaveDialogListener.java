/* */

package com.groza.Stereobliss.listener;

import com.groza.Stereobliss.dialogs.SaveDialog;

public interface OnSaveDialogListener {
    void onSaveObject(String title, SaveDialog.OBJECTTYPE type);
}
