package com.example.utlikotlin

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.AnimationDrawable
import android.net.InetAddresses
import android.net.Uri
import android.os.Parcelable
import android.util.Log
import android.util.Patterns
import android.util.SparseBooleanArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.URLUtil
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.util.forEach
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.security.MessageDigest
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.math.abs

fun Int.dp(context: Context) = this * context.resources.displayMetrics.density

fun Int.toDigit(digit: Int): String {
    var num = this.toString()

    if (digit > num.length) {
        num = "0".repeat(digit - num.length) + num
    }

    return num
}

fun Long.toIntId() = (this % Int.MAX_VALUE).toInt()

fun Long.toSystemLocalTime() = this.toSystemLocalDateTime().toLocalTime()

fun Long.toSystemLocalDate() = this.toSystemLocalDateTime().toLocalDate()

fun LocalDateTime.toSystemMillis() = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun Long.toSystemLocalDateTime() = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()

fun Long.toUtcLocalDateTime() = Instant.ofEpochMilli(this).atZone(ZoneId.of("UTC")).toLocalDateTime()

fun Long.toFormattedDateTimeString(format: String): String {
    val systemDateTime = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault())

    return systemDateTime.format(DateTimeFormatter.ofPattern(format))
}

fun String.toBytes() = this.toByteArray(Charset.forName("GBK"))

fun String.isValidEmailAddress() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidIpAddress() = InetAddresses.isNumericAddress(this)

fun String.isValidUrl() = URLUtil.isValidUrl(this)

fun String.uppercaseFirstLetter() = this.replaceFirstChar(Char::uppercase)

fun String.toDateTimeLong(dateTimeFormat: String): Long {
    val dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat)

    return LocalDateTime.parse(this, dateTimeFormatter).toSystemMillis()
}

fun String.toSecretKey(): SecretKey {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val secretKeyData = messageDigest.digest(this.toByteArray())

    return SecretKeySpec(secretKeyData, "AES")
}

fun Bitmap.toEscBytes() = EscBitmapHelper.getBytes(this)

fun OutputStream.write(text: String) = this.write(text.toBytes())

fun OutputStream.writeln(text: String) = this.write("$text\n".toBytes())

fun OutputStream.write(bitmap: Bitmap) = this.write(bitmap.toEscBytes())

fun InputStream.toBitmap() = BitmapFactory.decodeStream(this)

fun Uri.toBitmap(context: Context): Bitmap? {
    val bitmap: Bitmap

    return try {
        val inputStream = context.contentResolver.openInputStream(this)

        inputStream.use {
            bitmap = BitmapFactory.decodeStream(it)
        }

        bitmap
    } catch (e: Exception) {
        Log.e("Uri.toBitmap()", "Image not found.")

        null
    }
}

fun SparseBooleanArray.toIndexes(): List<Int> {
    val indexes = mutableListOf<Int>()

    forEach { key, value ->
        indexes.add(key)
    }

    return indexes
}

fun View.indexOfParent() = (this.parent as ViewGroup).indexOfChild(this)

fun View.next(): View {
    val currentIndex = (this.parent as ViewGroup).indexOfChild(this)
    val nextIndex = currentIndex + 1

    return (this.parent as ViewGroup).getChildAt(nextIndex)
}

fun View.previous(): View {
    val currentIndex = (this.parent as ViewGroup).indexOfChild(this)
    val previousIndex = currentIndex - 1

    return (this.parent as ViewGroup).getChildAt(previousIndex)
}

fun View.isLast(): Boolean {
    val currentIndex = (this.parent as ViewGroup).indexOfChild(this)

    return currentIndex == (this.parent as ViewGroup).childCount - 1
}

fun View.isFirst(): Boolean {
    val currentIndex = (this.parent as ViewGroup).indexOfChild(this)

    return currentIndex == 0
}

fun View.scale(value: Float, duration: Long) {
    val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, value)
    val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, value)

    val animator = ObjectAnimator.ofPropertyValuesHolder(this, scaleX, scaleY)

    animator.duration = duration

    animator.start()
}

fun View.isTouched(motionEvent: MotionEvent): Boolean {
    val rect = Rect()

    this.getGlobalVisibleRect(rect)

    return rect.contains(motionEvent.rawX.toInt(), motionEvent.rawY.toInt())
}

fun View.slideDown() {
    val layoutParams = layoutParams as CoordinatorLayout.LayoutParams
    val behavior = layoutParams.behavior as HideBottomViewOnScrollBehavior

    behavior.slideDown(this)
}

fun View.slideUp() {
    val layoutParams = layoutParams as CoordinatorLayout.LayoutParams
    val behavior = layoutParams.behavior as HideBottomViewOnScrollBehavior

    behavior.slideUp(this)
}

fun View.startGradientAnimation(transitionDuration: Int) {
    (background as AnimationDrawable).apply {
        setExitFadeDuration(transitionDuration)
        start()
    }
}

fun View.getXmlHeight(): Int {
    measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

    return measuredHeight
}

fun ViewGroup.getCheckedIndexes(): List<Int> {
    val checkedIndexes = mutableListOf<Int>()

    this.forEachIndexed { index, view ->
        if ((view as CompoundButton).isChecked) {
            checkedIndexes.add(index)
        }
    }

    return checkedIndexes
}

fun CollapsingToolbarLayout.showCollapsedTitleOnly(collapsedTitle: String) {
    val appBarLayout = parent as AppBarLayout

    appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
        title = if (abs(verticalOffset) == appBarLayout.totalScrollRange) collapsedTitle else ""
    }
}

fun RecyclerView.isAtTop() = !canScrollVertically(-1)

fun RecyclerView.isAtBottom() = !canScrollVertically(1)

fun RecyclerView.scrollToTop() = smoothScrollToPosition(0)

fun RecyclerView.scrollToBottom() = adapter?.let { smoothScrollToPosition(it.itemCount - 1) }

fun RecyclerView.showDivider() = addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

fun RecyclerView.getState() = layoutManager?.onSaveInstanceState()

fun RecyclerView.setState(state: Parcelable?) = layoutManager?.onRestoreInstanceState(state)

fun NestedScrollView.scrollToTop() = scrollTo(0, 0)

fun ImageView.copy(xOffset: Int, yOffSet: Int): ImageView {
    val currentImageView = this
    val newImageView = ImageView(context).apply {
        layoutParams = ViewGroup.LayoutParams(currentImageView.width, currentImageView.height)

        val position = IntArray(2)
        currentImageView.getLocationOnScreen(position)
        x = position[0].toFloat() - xOffset
        y = position[1].toFloat() - yOffSet

        setImageDrawable(currentImageView.drawable)
    }

    return newImageView
}

fun CardView.mapColor(arrayResId: Int, colorIndex: Int) {
    val colors = resources.obtainTypedArray(arrayResId)
    val color = context.getColor(colors.getResourceId(colorIndex, 0))

    setCardBackgroundColor(color)

    colors.recycle()
}

fun PopupWindow.build(contentView: View) = this.apply {
    setContentView(contentView)
    setBackgroundDrawable(null)

    isOutsideTouchable = true
    isFocusable = true
}

fun PopupWindow.showAsAbove(anchorView: View) = this.showAsDropDown(anchorView, 0, -anchorView.height * 4)

fun ImageButton.setEnableWithEffect(isEnable: Boolean) {
    if (isEnable) {
        isEnabled = true
        imageAlpha = 0xFF
    } else {
        isEnabled = false
        imageAlpha = 0x3F
    }
}

fun Button.setIconResource(resId: Int) = (this as MaterialButton).setIconResource(resId)

fun EditText.enableScroll() = setOnTouchListener { view, event ->
    view.parent.requestDisallowInterceptTouchEvent(true)

    if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
        view.parent.requestDisallowInterceptTouchEvent(false)
    }

    false
}

fun EditText.requestFocusAndShowKeyboard(alertDialog: AlertDialog) {
    requestFocus()

    alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
}

fun EditText.setTextAndMoveCursorToLast(value: String) {
    setText(value)
    setSelection(text.toString().length)
}

fun AutoCompleteTextView.setData(data: List<String>) {
    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, data)

    setAdapter(adapter)
}

fun AlertDialog.getPositiveButton() = getButton(DialogInterface.BUTTON_POSITIVE)

fun AlertDialog.showIfNotShowing() {
    if (!isShowing) {
        show()
    }
}

fun Dialog.extendToBottomEdge(isDarkNavigationBar: Boolean) {
    WindowInsetsControllerCompat(window!!, window!!.decorView).isAppearanceLightNavigationBars = !isDarkNavigationBar

    window!!.setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )

    setOnShowListener {
        val bottomSheet = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)!!

        ViewCompat.setOnApplyWindowInsetsListener(bottomSheet) { view, insets ->
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val keyboardInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = if (keyboardInsets.bottom == 0) systemBarInsets.bottom else keyboardInsets.bottom

            view.setPadding(0, 0, 0, bottomPadding)

            WindowInsetsCompat.CONSUMED
        }
    }
}

fun Dialog.setNavigationBarStyle(isDarkNavigationBar: Boolean) {
    WindowInsetsControllerCompat(window!!, window!!.decorView).isAppearanceLightNavigationBars = !isDarkNavigationBar
}

fun BottomNavigationView.setSelectedItemIndex(index: Int) {
    selectedItemId = menu[index].itemId
}

fun BottomNavigationView.setOnItemSelectListener(onSelect: (Int) -> Unit) {
    setOnItemSelectedListener { item ->
        onSelect(menu.children.indexOf(item))

        true
    }
}

fun Intent.isResolvable(context: Context) = resolveActivity(context.packageManager) != null

fun DownloadManager.getDownloadedFile(id: Long): DownloadedFile {
    var url = ""
    var status = DownloadStatus.RUNNING

    val query = DownloadManager.Query().setFilterById(id)
    val cursor = query(query)

    cursor.use {
        if (it.moveToFirst()) {
            val uriColumnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_URI)
            val statusColumnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)

            if (uriColumnIndex < 0 || statusColumnIndex < 0) {
                return@use
            }

            url = cursor.getString(uriColumnIndex)

            val statusIndex = cursor.getInt(statusColumnIndex)

            status = when (statusIndex) {
                DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.SUCCESSFUL

                else -> DownloadStatus.FAILED
            }
        }
    }

    return DownloadedFile(url, status)
}