/* */

package com.groza.Stereobliss.viewmodels;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.groza.Stereobliss.models.PlaylistModel;

import com.groza.Stereobliss.R;

import com.groza.Stereobliss.playbackservice.storage.OdysseyDatabaseManager;
import com.groza.Stereobliss.utils.MusicLibraryHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistViewModel extends GenericViewModel<PlaylistModel> {

    /**
     * Flag if a header element should be inserted.
     */
    private final boolean mAddHeader;

    /**
     * Flag if only playlists in the odyssey db should be loaded.
     * Needed for saving/overriding playlists.
     */
    private final boolean mOnlyOdysseyPlaylists;

    private PlaylistViewModel(@NonNull final Application application, final boolean addHeader, final boolean onlyOdysseyPlaylists) {
        super(application);

        mAddHeader = addHeader;
        mOnlyOdysseyPlaylists = onlyOdysseyPlaylists;
    }

    @Override
    void loadData() {
        new PlaylistLoaderTask(this).execute();
    }

    private static class PlaylistLoaderTask extends AsyncTask<Void, Void, List<PlaylistModel>> {

        private final WeakReference<PlaylistViewModel> mViewModel;

        PlaylistLoaderTask(final PlaylistViewModel viewModel) {
            mViewModel = new WeakReference<>(viewModel);
        }

        @Override
        protected List<PlaylistModel> doInBackground(Void... voids) {
            final PlaylistViewModel model = mViewModel.get();

            if (model != null) {
                final Application application = model.getApplication();

                List<PlaylistModel> playlists = new ArrayList<>();

                if (model.mAddHeader) {
                    // add a dummy playlist for the choose playlist dialog
                    // this playlist represents the action to create a new playlist in the dialog
                    playlists.add(new PlaylistModel(application.getString(R.string.create_new_playlist), -1, PlaylistModel.PLAYLIST_TYPES.CREATE_NEW));
                }

                if (!model.mOnlyOdysseyPlaylists) {
                    // add playlists from the mediastore
                    playlists.addAll(MusicLibraryHelper.getAllPlaylists(application));
                }

                // add playlists from odyssey local storage
                playlists.addAll(OdysseyDatabaseManager.getInstance(application).getPlaylists());

                // sort the playlist
                Collections.sort(playlists, (p1, p2) -> {
                    // make sure that the place holder for a new playlist is always at the top
                    if (p1.getPlaylistType() == PlaylistModel.PLAYLIST_TYPES.CREATE_NEW) {
                        return -1;
                    }

                    if (p2.getPlaylistType() == PlaylistModel.PLAYLIST_TYPES.CREATE_NEW) {
                        return 1;
                    }

                    return p1.getPlaylistName().compareToIgnoreCase(p2.getPlaylistName());
                });

                return playlists;
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<PlaylistModel> result) {
            final PlaylistViewModel model = mViewModel.get();

            if (model != null) {
                model.setData(result);
            }
        }
    }

    public static class PlaylistViewModelFactory implements ViewModelProvider.Factory {

        private final Application mApplication;

        private final boolean mAddHeader;

        private final boolean mOnlyOdysseyPlaylists;

        public PlaylistViewModelFactory(final Application application, final boolean addHeader, final boolean onlyOdysseyPlaylists) {
            mApplication = application;
            mAddHeader = addHeader;
            mOnlyOdysseyPlaylists = onlyOdysseyPlaylists;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new PlaylistViewModel(mApplication, mAddHeader, mOnlyOdysseyPlaylists);
        }
    }
}
