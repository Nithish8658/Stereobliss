/* */

package com.groza.Stereobliss.viewmodels;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.groza.Stereobliss.models.AlbumModel;

import com.groza.Stereobliss.R;

import com.groza.Stereobliss.utils.MusicLibraryHelper;

import java.lang.ref.WeakReference;
import java.util.List;

public class AlbumViewModel extends GenericViewModel<AlbumModel> {

    /**
     * The artist id if albums of a specific artist should be loaded.
     */
    private final long mArtistID;

    /**
     * Load only the recent albums.
     */
    private final boolean mLoadRecent;

    private AlbumViewModel(@NonNull final Application application, final long artistId, final boolean loadRecent) {
        super(application);

        mArtistID = artistId;
        mLoadRecent = loadRecent;
    }

    @Override
    void loadData() {
        new AlbumLoaderTask(this).execute();
    }

    private static class AlbumLoaderTask extends AsyncTask<Void, Void, List<AlbumModel>> {

        private final WeakReference<AlbumViewModel> mViewModel;

        AlbumLoaderTask(final AlbumViewModel viewModel) {
            mViewModel = new WeakReference<>(viewModel);
        }

        @Override
        protected List<AlbumModel> doInBackground(Void... voids) {
            final AlbumViewModel model = mViewModel.get();

            if (model != null) {
                final Application application = model.getApplication();

                if (model.mArtistID == -1) {
                    if (model.mLoadRecent) {
                        // load recent albums
                        return MusicLibraryHelper.getRecentAlbums(application);
                    } else {
                        // load all albums
                        return MusicLibraryHelper.getAllAlbums(application);
                    }
                } else {
                    // load all albums from the given artist

                    // Read order preference
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(application);
                    String orderKey = sharedPref.getString(application.getString(R.string.pref_album_sort_order_key), application.getString(R.string.pref_artist_albums_sort_default));

                    return MusicLibraryHelper.getAllAlbumsForArtist(model.mArtistID, orderKey, application);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<AlbumModel> result) {
            final AlbumViewModel model = mViewModel.get();

            if (model != null) {
                model.setData(result);
            }
        }
    }

    public static class AlbumViewModelFactory implements ViewModelProvider.Factory {

        private final Application mApplication;

        private final long mArtistID;

        private final boolean mLoadRecent;

        private AlbumViewModelFactory(final Application application, final long artistId, final boolean loadRecent) {
            mApplication = application;
            mArtistID = artistId;
            mLoadRecent = loadRecent;
        }

        public AlbumViewModelFactory(final Application application, final boolean loadRecent) {
            this(application, -1, loadRecent);
        }

        public AlbumViewModelFactory(final Application application, final long artistId) {
            this(application, artistId, false);
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new AlbumViewModel(mApplication, mArtistID, mLoadRecent);
        }
    }
}
