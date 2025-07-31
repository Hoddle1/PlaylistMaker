package com.example.playlistmaker.presentation.ui.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.ui.playlist.entity.PlaylistTracksState
import com.example.playlistmaker.presentation.ui.playlist.entity.ShareEvent
import com.example.playlistmaker.presentation.ui.playlist.entity.UiEvent
import com.example.playlistmaker.presentation.util.Utils
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    private val playlistTracks = MutableLiveData<PlaylistTracksState>()
    val tracksState: LiveData<PlaylistTracksState> = playlistTracks

    private val playlistLiveData = MutableLiveData<Playlist>()
    val playlistState: LiveData<Playlist> get() = playlistLiveData

    private val uiEvent = MutableLiveData<UiEvent>()
    val uiEventLiveData: LiveData<UiEvent> = uiEvent

    private val shareEvent = MutableLiveData<ShareEvent>()
    val shareEventLiveData: LiveData<ShareEvent> get() = shareEvent

    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            playlistLiveData.postValue(playlist)
            loadTracks(playlist.trackIds)
        }
    }

    private fun loadTracks(trackIds: List<Int>) {
        viewModelScope.launch {
            playlistInteractor.getTracksForPlaylist(trackIds).collect { tracks ->
                playlistTracks.postValue(
                    PlaylistTracksState(
                        tracks = tracks,
                        totalDurationMinutes = getDuration(tracks)
                    )
                )
            }
        }
    }

    fun showDeleteDialog(track: Track, message: String) {
        uiEvent.value = UiEvent.ShowDialog(
            message = message,
            onConfirm = { confirmDeleteTrack(track) },
            onCancel = { cancelAction() }
        )
    }

    fun deletePlaylist(message: String) {
        uiEvent.value = UiEvent.ShowDialog(
            message = message,
            onConfirm = { confirmDeletePlaylist() },
            onCancel = { cancelAction() }
        )
    }

    private fun confirmDeletePlaylist() {
        viewModelScope.launch {
            playlistLiveData.value?.let {
                playlistInteractor.deletePlaylist(it.id)
            }
            uiEvent.value = UiEvent.CloseScreen
        }
    }

    private fun confirmDeleteTrack(track: Track) {
        viewModelScope.launch {
            val resultPlaylist = playlistLiveData.value?.let {
                playlistInteractor.removeTrackFromPlaylist(it.id, track)
            }

            if (resultPlaylist !== null) {
                loadTracks(resultPlaylist.trackIds)
            }

            uiEvent.value = UiEvent.DismissDialog
        }
    }

    private fun cancelAction() {
        uiEvent.value = UiEvent.DismissDialog
    }

    fun sharePlaylist() {
        tracksState.value?.let { tracksState ->
            val tracks = tracksState.tracks

            if (tracks.isEmpty()) {
                shareEvent.value = ShareEvent.ShowToast(
                    "В этом плейлисте нет списка треков, которым можно поделиться"
                )
            } else {
                val playlist = playlistLiveData.value
                if (playlist !== null) {
                    val shareText = buildShareText(playlist, tracks)
                    shareEvent.value = ShareEvent.ShareText(shareText)
                }
            }
        }
    }

    private fun getDuration(tracks: List<Track>): Int {
        val totalMillis = tracks.sumOf {
            val millis = Utils.convertTimeToMillis(it.trackTimeMillis)
            millis
        }

        val totalSeconds = totalMillis / 1000
        val minutes = (totalSeconds % 3600) / 60
        return minutes
    }

    private fun buildShareText(playlist: Playlist, tracks: List<Track>): String {
        val builder = StringBuilder()
        builder.append("Плейлист: ${playlist.name}\n")
        playlist.description?.takeIf { it.isNotBlank() }?.let {
            builder.append("$it\n")
        }
        builder.append("Треки:\n")
        tracks.forEachIndexed { index, track ->
            builder.append("${index + 1}. ${track.artistName} - ${track.trackName}\n")
        }
        return builder.toString().trim()
    }
}