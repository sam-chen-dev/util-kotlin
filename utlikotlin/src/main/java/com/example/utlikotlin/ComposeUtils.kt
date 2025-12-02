package com.example.utlikotlin

import android.content.Context
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SetSystemBarsStyle(isDarkStatusBar: Boolean, isDarkNavigationBar: Boolean) {
    val lightSystemBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
    val darkSystemBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)

    val statusBarStyle = if (isDarkStatusBar) darkSystemBarStyle else lightSystemBarStyle
    val navigationBarStyle = if (isDarkNavigationBar) darkSystemBarStyle else lightSystemBarStyle

    (LocalActivity.current as ComponentActivity?)?.enableEdgeToEdge(statusBarStyle, navigationBarStyle)
}

@Composable
fun SimpleButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick, modifier) {
        Text(text)
    }
}

fun showToast(context: Context, text: String) = Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

fun showToast(context: Context, resId: Int) = Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()

fun showToastLong(context: Context, text: String) = Toast.makeText(context, text, Toast.LENGTH_LONG).show()

fun showToastLong(context: Context, resId: Int) = Toast.makeText(context, resId, Toast.LENGTH_LONG).show()