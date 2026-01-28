package com.example.utlikotlin

import android.content.Context
import android.media.MediaPlayer

class AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun playOrPause(context: Context, file: Any, isOverlaid: Boolean, isPauseReset: Boolean = true) {
        if (mediaPlayer == null || isOverlaid) {
            mediaPlayer = createMediaPlayer(context, file, isOverlaid)

            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
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

    private fun createMediaPlayer(context: Context, file: Any, isOverlaid: Boolean) = MediaPlayer().apply {
        when (file) {
            is String -> context.assets.openFd(file).use {
                setDataSource(it.fileDescriptor, it.startOffset, it.length)
            }

            is Int -> context.resources.openRawResourceFd(file).use {
                setDataSource(it.fileDescriptor, it.startOffset, it.length)
            }

            else -> throw IllegalArgumentException()
        }

        setOnCompletionListener {
            if (isOverlaid) {
                release()
            }
        }
    }
}