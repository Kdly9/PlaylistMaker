package com.example.playlistmaker.player.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.api.MediaInteractor
import com.example.playlistmaker.player.ui.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MusicService : Service(),
    AudioPlayerControl {

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private val mediaPlayerInteractor: MediaInteractor by inject()

    private var timerJob: Job? = null
    private var songUrl = ""
    private var artistName = ""
    private var trackName = ""
    private var notificationsAreDisplayed = false
    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.CompletionAction)
    private val playerState = _playerState.asStateFlow()

    override fun getPlayerState(): StateFlow<PlayerState> {
        return playerState
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Main + SupervisorJob()).launch {
            while (mediaPlayerInteractor.mediaIsPlaying()) {
                _playerState.value =
                    PlayerState.Start(currentPosition = mediaPlayerInteractor.getCurrentPosition())
                delay(UPDATE_TIME)
            }
        }
    }

    private val binder = MusicServiceBinder()
    override fun onBind(intent: Intent?): IBinder {
        songUrl = intent?.getStringExtra("song_url") ?: ""
        trackName = intent?.getStringExtra("track_name") ?: ""
        artistName = intent?.getStringExtra("artist_name") ?: ""
        mediaPlayerInteractor.preparePlayer(
            songUrl,
            object : MediaInteractor.Completion {
                override fun completionAction() {
                    _playerState.value = PlayerState.CompletionAction
                    timerJob?.cancel()
                }

                override fun errorPrepare() {
                }

                override fun startPlayer() {
                    _playerState.value =
                        PlayerState.Start(currentPosition = mediaPlayerInteractor.getCurrentPosition())
                }

                override fun pausePlayer() {
                    timerJob?.cancel()
                    _playerState.value = PlayerState.Paused
                }

                override fun paused() {
                    startTimer()
                }
            })
        createNotificationChannel()
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        timerJob?.cancel()
        mediaPlayerInteractor.reset()
        return super.onUnbind(intent)
    }


    override fun controlPlayer() {
        mediaPlayerInteractor.playbackControl()
    }

    override fun pausePlayer() {
        mediaPlayerInteractor.pausePlayer()
    }

    override fun release() {
        timerJob?.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
             NOTIFICATION_CHANNEL_ID,
            "Music service",
             NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Service for playing music"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun showNotification() {
        if (_playerState.value is PlayerState.Start) {
            ServiceCompat.startForeground(
                this,
                SERVICE_NOTIFICATION_ID,
                createServiceNotification(),
                getForegroundServiceTypeConstant()
            )
            notificationsAreDisplayed = true
        }
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    override fun hideNotification() {
        if (notificationsAreDisplayed){
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
            notificationsAreDisplayed = false
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        if(mediaPlayerInteractor.mediaIsPlaying()){
            mediaPlayerInteractor.stop()
        }
        mediaPlayerInteractor.reset()
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText("$artistName - $trackName")
            .setSmallIcon(R.drawable.ic_play)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    companion object {
        private const val UPDATE_TIME = 200L
        private const val SERVICE_NOTIFICATION_ID = 100
        private const val NOTIFICATION_CHANNEL_ID = "music_service_channel"
    }

}