/* */

package com.groza.Stereobliss.viewmodels;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.groza.Stereobliss.R;
import com.groza.Stereobliss.models.ArtistModel;
import com.groza.Stereobliss.utils.MusicLibraryHelper;

import java.lang.ref.WeakReference;
import java.util.List;

public class ArtistViewModel extends GenericViewModel<ArtistModel> {

    private ArtistViewModel(@NonNull final Application application) {
        super(application);
    }

    @Override
    void loadData() {
        new ArtistLoaderTask(this).execute();
    }

    private static class ArtistLoaderTask extends AsyncTask<Void, Void, List<ArtistModel>> {

        private final WeakReference<ArtistViewModel> mViewModel;

        ArtistLoaderTask(final ArtistViewModel viewModel) {
            mViewModel = new WeakReference<>(viewModel);
        }

        @Override
        protected List<ArtistModel> doInBackground(Void... voids) {
            final ArtistViewModel model = mViewModel.get();

            if (model != null) {
                final Application application = model.getApplication();

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(application);
                boolean showAlbumArtistsOnly = sharedPref.getBoolean(application.getString(R.string.pref_album_artists_only_key), application.getResources().getBoolean(R.bool.pref_album_artists_only_default));

                return MusicLibraryHelper.getAllArtists(showAlbumArtistsOnly, application);
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<ArtistModel> result) {
            final ArtistViewModel model = mViewModel.get();

            if (model != null) {
                model.setData(result);
            }
        }
    }

    public static class ArtistViewModelFactory implements ViewModelProvider.Factory {

        private final Application mApplication;

        public ArtistViewModelFactory(final Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ArtistViewModel(mApplication);
        }
    }
}
