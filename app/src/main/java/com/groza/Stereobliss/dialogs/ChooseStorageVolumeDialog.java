/* */

package com.groza.Stereobliss.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.groza.Stereobliss.utils.FileExplorerHelper;

import com.groza.Stereobliss.R;
import com.groza.Stereobliss.listener.OnDirectorySelectedListener;

import java.util.List;

public class ChooseStorageVolumeDialog extends DialogFragment {

    /**
     * Listener to choose the storage volume
     */
    private OnDirectorySelectedListener mDirectorySelectedCallback;

    /**
     * Adapter used for the list of available storage volumes
     */
    private ArrayAdapter<String> mStorageVolumesAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mDirectorySelectedCallback = (OnDirectorySelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnDirectorySelectedListener");
        }
    }

    /**
     * Create the dialog to choose the current visible storage volume in the FilesFragment.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // get the list of available storage volumes
        List<String> storageVolumes = FileExplorerHelper.getInstance().getStorageVolumes(getContext());

        // Use the Builder class for convenient dialog construction
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());

        builder.setTitle(R.string.dialog_choose_storage_volume_title);
        mStorageVolumesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, storageVolumes);
        builder.setAdapter(mStorageVolumesAdapter, (dialogInterface, item) -> mDirectorySelectedCallback.onDirectorySelected(mStorageVolumesAdapter.getItem(item), true));

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
