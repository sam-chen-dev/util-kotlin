package com.example.utlikotlin

import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.NavHostFragment

fun AppCompatActivity.getNavHostFragmentById(id: Int) = supportFragmentManager.findFragmentById(id) as NavHostFragment

fun AppCompatActivity.hideActionBar() = supportActionBar?.hide()

fun AppCompatActivity.showActionBar() = supportActionBar?.show()

fun AppCompatActivity.showToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

fun AppCompatActivity.showToast(resId: Int) = Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()

fun AppCompatActivity.showToastLong(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

fun AppCompatActivity.showToastLong(resId: Int) = Toast.makeText(this, resId, Toast.LENGTH_LONG).show()

fun AppCompatActivity.setupEdgeToEdge(
    contentView: View,
    statusBarBackgroundView: View? = null,
    navigationBarBackgroundView: View? = null,
    isDarkStatusBar: Boolean = false,
    isDarkNavigationBar: Boolean = false
) {
    val lightSystemBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
    val darkSystemBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)

    val statusBarStyle = if (isDarkStatusBar) darkSystemBarStyle else lightSystemBarStyle
    val navigationBarStyle = if (isDarkNavigationBar) darkSystemBarStyle else lightSystemBarStyle

    enableEdgeToEdge(statusBarStyle, navigationBarStyle)

    ViewCompat.setOnApplyWindowInsetsListener(contentView) { view, insets ->
        val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val topPadding = if (statusBarBackgroundView == null) systemBarInsets.top else 0
        val bottomPadding = if (navigationBarBackgroundView == null) systemBarInsets.bottom else 0

        view.setPadding(0, topPadding, 0, bottomPadding)

        statusBarBackgroundView?.updateLayoutParams {
            height = systemBarInsets.top
        }

        navigationBarBackgroundView?.updateLayoutParams {
            height = systemBarInsets.bottom
        }

        WindowInsetsCompat.CONSUMED
    }
}

fun AppCompatActivity.setupEdgeToEdge(
    contentView: View,
    statusBarBackgroundView: View,
    topNavigationBarBackgroundView: View,
    bottomNavigationBarBackgroundView: View,
    isDarkStatusBar: Boolean,
    isDarkNavigationBar: Boolean
) {
    val lightSystemBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
    val darkSystemBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)

    val statusBarStyle = if (isDarkStatusBar) darkSystemBarStyle else lightSystemBarStyle
    val navigationBarStyle = if (isDarkNavigationBar) darkSystemBarStyle else lightSystemBarStyle

    enableEdgeToEdge(statusBarStyle, navigationBarStyle)

    ViewCompat.setOnApplyWindowInsetsListener(contentView) { view, insets ->
        val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

        statusBarBackgroundView.updateLayoutParams {
            height = systemBarInsets.top
        }

        topNavigationBarBackgroundView.updateLayoutParams {
            height = systemBarInsets.bottom
        }

        bottomNavigationBarBackgroundView.updateLayoutParams {
            height = systemBarInsets.bottom
        }

        WindowInsetsCompat.CONSUMED
    }
}