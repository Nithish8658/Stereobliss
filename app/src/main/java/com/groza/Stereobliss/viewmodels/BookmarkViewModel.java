/* */

package com.groza.Stereobliss.viewmodels;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.groza.Stereobliss.models.BookmarkModel;

import com.groza.Stereobliss.R;

import com.groza.Stereobliss.playbackservice.storage.OdysseyDatabaseManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class BookmarkViewModel extends GenericViewModel<BookmarkModel> {

    /**
     * Flag if a header element should be inserted.
     */
    private final boolean mAddHeader;

    private BookmarkViewModel(@NonNull final Application application, final boolean addHeader) {
        super(application);

        mAddHeader = addHeader;
    }

    @Override
    void loadData() {
        new BookmarkLoaderTask(this).execute();
    }

    private static class BookmarkLoaderTask extends AsyncTask<Void, Void, List<BookmarkModel>> {

        private final WeakReference<BookmarkViewModel> mViewModel;

        BookmarkLoaderTask(final BookmarkViewModel viewModel) {
            mViewModel = new WeakReference<>(viewModel);
        }

        @Override
        protected List<BookmarkModel> doInBackground(Void... voids) {
            final BookmarkViewModel model = mViewModel.get();

            if (model != null) {
                final Application application = model.getApplication();

                final List<BookmarkModel> bookmarks = new ArrayList<>();

                if (model.mAddHeader) {
                    // add a dummy bookmark for the choose bookmark dialog
                    // this bookmark represents the action to create a new bookmark in the dialog
                    bookmarks.add(new BookmarkModel(-1, application.getString(R.string.create_new_bookmark), -1));
                }
                bookmarks.addAll(OdysseyDatabaseManager.getInstance(application).getBookmarks());

                return bookmarks;
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<BookmarkModel> result) {
            final BookmarkViewModel model = mViewModel.get();

            if (model != null) {
                model.setData(result);
            }
        }
    }

    public static class BookmarkViewModelFactory implements ViewModelProvider.Factory {

        private final Application mApplication;

        private final boolean mAddHeader;

        public BookmarkViewModelFactory(final Application application, final boolean addHeader) {
            mApplication = application;
            mAddHeader = addHeader;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new BookmarkViewModel(mApplication, mAddHeader);
        }
    }
}
