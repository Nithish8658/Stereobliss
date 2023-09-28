/* */

package com.groza.Stereobliss.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.groza.Stereobliss.R;
import com.groza.Stereobliss.listener.OnSaveDialogListener;

public class SaveDialog extends DialogFragment {

    private static final String ARG_OBJECTTYPE = "objecttype";

    /**
     * ENUM to determine the object type
     */
    public enum OBJECTTYPE {
        PLAYLIST, BOOKMARK
    }

    /**
     * Listener to save the object of the current type
     */
    OnSaveDialogListener mSaveCallback;

    /**
     * Flag to remove the dialogtext on first clicking
     */
    private boolean mFirstClick;

    /**
     * Returns a new SaveDialog with the given OBJECTTYPE as argument
     */
    public static SaveDialog newInstance(OBJECTTYPE type) {

        Bundle args = new Bundle();
        args.putInt(SaveDialog.ARG_OBJECTTYPE, type.ordinal());

        SaveDialog fragment = new SaveDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mSaveCallback = (OnSaveDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnSaveDialogListener");
        }
    }

    /**
     * Create the dialog to save an object of the current type.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());

        // read arguments to identify type of the object which should be saved
        Bundle mArgs = requireArguments();
        final OBJECTTYPE type = OBJECTTYPE.values()[mArgs.getInt(ARG_OBJECTTYPE)];

        String dialogTitle = "";
        String editTextDefaultTitle = "";

        if (type != null) {
            // set textfield titles according to type
            switch (type) {
                case PLAYLIST:
                    dialogTitle = getString(R.string.dialog_save_playlist);
                    editTextDefaultTitle = getString(R.string.default_playlist_title);
                    break;
                case BOOKMARK:
                    dialogTitle = getString(R.string.dialog_create_bookmark);
                    editTextDefaultTitle = getString(R.string.default_bookmark_title);
                    break;
            }
        }

        // create edit text for title
        final EditText editTextTitle = new EditText(builder.getContext());
        editTextTitle.setText(editTextDefaultTitle);
        // Add a listener that just removes the text on first clicking
        editTextTitle.setOnClickListener(v -> {
            if (!mFirstClick) {
                editTextTitle.setText("");
                mFirstClick = true;
            }
        });
        builder.setView(editTextTitle);

        builder.setMessage(dialogTitle).setPositiveButton(R.string.dialog_action_save, (dialog, id) -> {
            // accept title and call callback method
            String objectTitle = editTextTitle.getText().toString();
            mSaveCallback.onSaveObject(objectTitle, type);
        }).setNegativeButton(R.string.dialog_action_cancel, (dialog, id) -> {
            // User cancelled the dialog dont save object
            requireDialog().cancel();
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
