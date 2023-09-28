/* */

package com.groza.Stereobliss.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.groza.Stereobliss.models.BookmarkModel;
import com.groza.Stereobliss.viewitems.ListViewItem;

public class BookmarksAdapter extends GenericSectionAdapter<BookmarkModel> {

    private final Context mContext;

    public BookmarksAdapter(Context context) {
        super();

        mContext = context;
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
        BookmarkModel bookmark = getItem(position);

        ListViewItem listViewItem;
        // Check if a view can be recycled
        if (convertView != null) {
            listViewItem = (ListViewItem) convertView;
        } else {
            listViewItem = ListViewItem.createBookmarkItem(mContext, this);
        }

        listViewItem.setBookmark(bookmark);

        return listViewItem;
    }
}
