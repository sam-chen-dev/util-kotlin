package com.example.utlikotlin

import android.util.Base64

/*ByteArray <-> String*/
fun ByteArray.encodeToString() = Base64.encodeToString(this, Base64.DEFAULT)

fun String.decodeToByteArray() = Base64.decode(this, Base64.DEFAULT)