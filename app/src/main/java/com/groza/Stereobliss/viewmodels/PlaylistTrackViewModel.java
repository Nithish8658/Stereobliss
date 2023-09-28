/* */

package com.groza.Stereobliss.viewmodels;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.groza.Stereobliss.models.FileModel;
import com.groza.Stereobliss.models.PlaylistModel;
import com.groza.Stereobliss.models.Trackmodel.TrackModel;
import com.groza.Stereobliss.utils.PlaylistParser;
import com.groza.Stereobliss.utils.PlaylistParserFactory;
import com.groza.Stereobliss.playbackservice.storage.OdysseyDatabaseManager;
import com.groza.Stereobliss.utils.MusicLibraryHelper;

import java.lang.ref.WeakReference;
import java.util.List;

public class PlaylistTrackViewModel extends GenericViewModel<TrackModel> {

    /**
     * The playlistModel that contains all information to load the playlist tracks.
     */
    private final PlaylistModel mPlaylistModel;

    private PlaylistTrackViewModel(@NonNull final Application application, final PlaylistModel playlistModel) {
        super(application);

        mPlaylistModel = playlistModel;
    }

    @Override
    void loadData() {
        new PlaylistLoaderTask(this).execute();
    }

    private static class PlaylistLoaderTask extends AsyncTask<Void, Void, List<TrackModel>> {

        private final WeakReference<PlaylistTrackViewModel> mViewModel;

        PlaylistLoaderTask(final PlaylistTrackViewModel viewModel) {
            mViewModel = new WeakReference<>(viewModel);
        }

        @Override
        protected List<TrackModel> doInBackground(Void... voids) {
            final PlaylistTrackViewModel model = mViewModel.get();

            if (model != null) {

                final PlaylistModel playlist = model.mPlaylistModel;
                final Application application = model.getApplication();

                switch (playlist.getPlaylistType()) {
                    case MEDIASTORE:
                        return MusicLibraryHelper.getTracksForPlaylist(playlist.getPlaylistId(), application);
                    case ODYSSEY_LOCAL:
                        return OdysseyDatabaseManager.getInstance(application).getTracksForPlaylist(playlist.getPlaylistId());
                    case FILE:
                        PlaylistParser parser = PlaylistParserFactory.getParser(new FileModel(playlist.getPlaylistPath()));
                        if (parser == null) {
                            return null;
                        }
                        return parser.parseList(model.getApplication());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<TrackModel> result) {
            final PlaylistTrackViewModel model = mViewModel.get();

            if (model != null) {
                model.setData(result);
            }
        }
    }

    public static class PlaylistTrackViewModelFactory implements ViewModelProvider.Factory {

        private final Application mApplication;

        private final PlaylistModel mPlaylistModel;

        public PlaylistTrackViewModelFactory(final Application application, final PlaylistModel playlistModel) {
            mApplication = application;
            mPlaylistModel = playlistModel;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new PlaylistTrackViewModel(mApplication, mPlaylistModel);
        }
    }
}
