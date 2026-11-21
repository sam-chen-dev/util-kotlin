package com.example.utlikotlin

import android.content.ContentResolver
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

fun ExoPlayer.setSourceAndPlay(url: String) {
    setMediaItem(MediaItem.fromUri(url))
    prepare()
    play()
}

fun ExoPlayer.setSourceAndPlay(resId: Int) {
    val uri = Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE).path(resId.toString()).build()

    setMediaItem(MediaItem.fromUri(uri))
    prepare()
    play()
}