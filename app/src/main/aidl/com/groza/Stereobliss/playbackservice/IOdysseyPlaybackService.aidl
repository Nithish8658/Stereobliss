package com.groza.Stereobliss.playbackservice;

// Declare any non-default types here with import statements
import com.groza.Stereobliss.models.TrackModel;
import com.groza.Stereobliss.models.PlaylistModel;
import com.groza.Stereobliss.playbackservice.NowPlayingInformation;

interface IOdysseyPlaybackService {

    // Controls the player with predefined actions
    void playURI(String uri);
    void next();
    void previous();
    void togglePause();
    void shufflePlaylist();
    void playAllTracks(String filterString);

    /**
     * position = position in current track ( in seconds)
     */
    void seekTo(int position);

    // save current playlist in odyssey db
    void savePlaylist(String name);

    // enqueue a playlist from mediastore/odyssey db/file
    void enqueuePlaylist(in PlaylistModel playlist);
    void playPlaylist(in PlaylistModel playlist, int position);

    // enqueue all tracks of an album from mediastore
    void enqueueAlbum(long albumId, String orderKey);
    void playAlbum(long albumId, String orderKey, int position);

    void enqueueRecentAlbums();
    void playRecentAlbums();

    // enqueue all tracks of an artist from mediastore
    void enqueueArtist(long artistId, String albumOrderKey, String trackOrderKey);
    void playArtist(long artistId, String albumOrderKey, String trackOrderKey);

    /**
     * position = playlist position of jump target
     */
    void jumpTo(int position);

    void toggleRandom();
    void toggleRepeat();

    void enqueueTrack(in TrackModel track, boolean asNext);
    void playTrack(in TrackModel track, boolean clearPlaylist);

    void dequeueTrack(int index);
    void dequeueTracks(int index);
    void clearPlaylist();

    // resume stack methods
    void resumeBookmark(long timestamp);
    void deleteBookmark(long timestamp);
    void createBookmark(String bookmarkTitle);

    // file explorer methods
    void enqueueFile(String filePath, boolean asNext);
    void playFile(String filePath, boolean clearPlaylist);

    void playDirectory(String directoryPath, int position);
    void enqueueDirectoryAndSubDirectories(String directoryPath, String filterString);
    void playDirectoryAndSubDirectories(String directoryPath, String filterString);

    // Information getters

    int getAudioSessionID();
    int getPlaylistSize();
    // return the current index
    int getCurrentIndex();
    // Returns time of current playing title
    int getTrackPosition();
    // return the current nowplayinginformation or null if state is stopped
    NowPlayingInformation getNowPlayingInformation();
    TrackModel getPlaylistSong(int index);
    // If currently playing return this song otherwise null
    TrackModel getCurrentSong();
    // return the working state of the pbs
    boolean isBusy();

    void hideArtworkChanged(boolean enabled);

    void hideMediaOnLockscreenChanged(boolean enabled);

    void changeAutoBackwardsSeekAmount(int amount);

    void setSmartRandom(int intelligenceFactor);

    void startSleepTimer(long durationMS, boolean stopAfterCurrent);
    void cancelSleepTimer();
    boolean hasActiveSleepTimer();
}
