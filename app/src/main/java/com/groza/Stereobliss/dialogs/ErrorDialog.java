/* */

package com.groza.Stereobliss.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.groza.Stereobliss.R;

public class ErrorDialog extends DialogFragment {

    private static final String ARG_ERRORDIALOGTITLE = "errordialogtitle";

    private static final String ARG_ERRORDIALOGMESSAGE = "errordialogmessage";

    /**
     * Returns a new ErrorDialog with title and message id as arguments
     */
    public static ErrorDialog newInstance(@StringRes final int title, @StringRes final int message) {
        final Bundle args = new Bundle();
        args.putInt(ErrorDialog.ARG_ERRORDIALOGTITLE, title);
        args.putInt(ErrorDialog.ARG_ERRORDIALOGMESSAGE, message);

        final ErrorDialog fragment = new ErrorDialog();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Create the dialog to show an occured error
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());

        // read arguments to identify the error title and message
        Bundle mArgs = requireArguments();

        int titleId = mArgs.getInt(ARG_ERRORDIALOGTITLE, -1);
        int messageId = mArgs.getInt(ARG_ERRORDIALOGMESSAGE, -1);

        String dialogTitle = (titleId == -1) ? "" : getString(titleId);
        String dialogMessage = (messageId == -1) ? "" : getString(messageId);

        builder.setTitle(dialogTitle).setMessage(dialogMessage)
                .setNegativeButton(R.string.error_dialog_ok_action, (dialog, id) -> dismiss());

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
