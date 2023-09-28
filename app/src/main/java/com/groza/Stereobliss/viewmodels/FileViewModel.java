/* */

package com.groza.Stereobliss.viewmodels;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.groza.Stereobliss.models.FileModel;
import com.groza.Stereobliss.utils.PermissionHelper;

import java.lang.ref.WeakReference;
import java.util.List;

public class FileViewModel extends GenericViewModel<FileModel> {

    /**
     * The parent directory.
     */
    private final FileModel mCurrentDirectory;

    private FileViewModel(@NonNull final Application application, final FileModel directory) {
        super(application);

        mCurrentDirectory = directory;
    }

    @Override
    void loadData() {
        new FileLoaderTask(this).execute();
    }

    private static class FileLoaderTask extends AsyncTask<Void, Void, List<FileModel>> {

        private final WeakReference<FileViewModel> mViewModel;

        FileLoaderTask(final FileViewModel viewModel) {
            mViewModel = new WeakReference<>(viewModel);
        }

        @Override
        protected List<FileModel> doInBackground(Void... voids) {
            final FileViewModel model = mViewModel.get();

            if (model != null) {
                return PermissionHelper.getFilesForDirectory(model.getApplication(), model.mCurrentDirectory);
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<FileModel> result) {
            final FileViewModel model = mViewModel.get();

            if (model != null) {
                model.setData(result);
            }
        }
    }

    public static class FileViewModelFactory implements ViewModelProvider.Factory {

        private final Application mApplication;

        private final FileModel mCurrentDirectory;

        public FileViewModelFactory(final Application application, final FileModel directory) {
            mApplication = application;
            mCurrentDirectory = directory;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new FileViewModel(mApplication, mCurrentDirectory);
        }
    }
}
