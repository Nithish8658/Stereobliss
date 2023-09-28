/* */

package com.groza.Stereobliss.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.groza.Stereobliss.models.FileModel;
import com.groza.Stereobliss.viewitems.ListViewItem;

public class FilesAdapter extends GenericSectionAdapter<FileModel> {

    private final Context mContext;

    public FilesAdapter(Context context) {
        super();

        mContext = context;
        enableSections(false);
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FileModel file = getItem(position);

        ListViewItem listViewItem;
        // Check if a view can be recycled
        if (convertView != null) {
            listViewItem = (ListViewItem) convertView;
        } else {
            // Create new view if no reusable is available
            listViewItem = ListViewItem.createFileItem(mContext, this);
        }

        listViewItem.setFile(file);

        return listViewItem;
    }
}
