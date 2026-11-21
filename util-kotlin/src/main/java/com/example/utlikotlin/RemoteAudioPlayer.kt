package com.example.utlikotlin

import android.media.MediaPlayer

class RemoteAudioPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun playOrPause(url: String, isOverlaid: Boolean, isPauseReset: Boolean = true) {
        if (mediaPlayer == null || isOverlaid) {
            mediaPlayer = createMediaPlayer(url, isOverlaid)

            mediaPlayer!!.prepareAsync()

            mediaPlayer!!.setOnPreparedListener {
                mediaPlayer!!.start()
            }
        } else {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()

                if (isPauseReset) {
                    mediaPlayer!!.seekTo(0)
                }
            } else {
                mediaPlayer!!.start()
            }
        }
    }

    fun release() {
        mediaPlayer?.release()

        mediaPlayer = null
    }

    private fun createMediaPlayer(url: String, isOverlaid: Boolean) = MediaPlayer().apply {
        setDataSource(url)

        setOnCompletionListener {
            if (isOverlaid) {
                release()
            }
        }
    }
}