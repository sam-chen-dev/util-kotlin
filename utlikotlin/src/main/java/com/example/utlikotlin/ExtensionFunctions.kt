package com.example.utlikotlin

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Dialog
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.AnimationDrawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.InetAddresses
import android.net.Uri
import android.os.Environment
import android.os.Parcelable
import android.os.PowerManager
import android.provider.MediaStore
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.util.SparseBooleanArray
import android.util.TypedValue
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
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.util.forEach
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.security.MessageDigest
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
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

fun <T> List<T>.range(fromIndex: Int, toIndex: Int) = this.subList(fromIndex, toIndex + 1)

fun <T> List<T>.replace(targetItem: T, newItem: T) = map {
    if (it == targetItem) {
        newItem
    } else {
        it
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

fun Context.getConnectivityManager() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

fun Context.getNotificationManager() = getSystemService(NotificationManager::class.java) as NotificationManager

fun Context.cancelNotification() = getSystemService(NotificationManager::class.java).cancelAll()

fun Context.closeNotificationPanel() {
    val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)

    sendBroadcast(intent)
}

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

fun Fragment.hideUpButton() = (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

fun Fragment.getConnectivityManager() = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

fun Fragment.getNotificationManager() = requireContext().getSystemService(NotificationManager::class.java) as NotificationManager

fun Fragment.getPowerManager() = requireContext().getSystemService(Context.POWER_SERVICE) as PowerManager

fun Fragment.getFragmentById(id: Int) = childFragmentManager.findFragmentById(id)

fun Fragment.getMapFragmentById(id: Int) = childFragmentManager.findFragmentById(id) as SupportMapFragment

fun Fragment.showToast(text: String) = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()

fun Fragment.showToast(resId: Int) = Toast.makeText(requireContext(), resId, Toast.LENGTH_SHORT).show()

fun Fragment.showToastLong(text: String) = Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()

fun Fragment.showToastLong(resId: Int) = Toast.makeText(requireContext(), resId, Toast.LENGTH_LONG).show()

fun Fragment.showSnackbar(text: String) {
    val view = requireActivity().findViewById<View>(android.R.id.content)

    Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()
}

fun Fragment.setTitle(title: String) {
    (requireActivity() as AppCompatActivity).supportActionBar?.title = title
}

fun Fragment.pickPhoto(launcher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Intent.ACTION_PICK).apply {
        type = "image/*"
    }

    launcher.launch(intent)
}

fun Fragment.pickAndSavePhoto(requestCode: Int) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

    intent.type = "image/*"

    startActivityForResult(intent, requestCode)
}

fun Fragment.takeAndSavePicture(requestCode: Int, imageUri: Uri) {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

    startActivityForResult(intent, requestCode)
}

fun Fragment.sendEmail(recipient: String, subject: String, message: String) {
    val intent = Intent(Intent.ACTION_SENDTO, "mailto:".toUri()).apply {
        putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, message)
    }

    startActivity(intent)
}

fun Fragment.isGpsOn(): Boolean {
    val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun Fragment.requestGpsOn(request: ActivityResultLauncher<IntentSenderRequest>) {
    val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    val settingRequest = LocationSettingsRequest.Builder().run {
        addLocationRequest(locationRequest)
        build()
    }

    val settingsClient = LocationServices.getSettingsClient(requireContext())
    val task = settingsClient.checkLocationSettings(settingRequest)

    task.addOnFailureListener {
        val intentSender = (it as ResolvableApiException).resolution.intentSender
        val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()

        request.launch(intentSenderRequest)
    }
}

fun Fragment.isIgnoreBatteryOptimizationPermissionGranted(): Boolean {
    return getPowerManager().isIgnoringBatteryOptimizations(requireContext().packageName)
}

fun Fragment.requestIgnoreBatteryOptimizationPermission(launcher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
        data = "package:${requireContext().packageName}".toUri()
    }

    launcher.launch(intent)
}

fun Fragment.isCameraPermissionGranted(): Boolean {
    val result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)

    return result == PackageManager.PERMISSION_GRANTED
}

fun Fragment.requestCameraPermission(launcher: ActivityResultLauncher<String>) {
    launcher.launch(Manifest.permission.CAMERA)
}

fun Fragment.isNotificationPermissionGranted(): Boolean {
    val result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)

    return result == PackageManager.PERMISSION_GRANTED
}

fun Fragment.requestNotificationPermission(launcher: ActivityResultLauncher<String>) {
    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
}

fun Fragment.requestPermissions(request: ActivityResultLauncher<Array<String>>, permissions: Array<String>) = request.launch(permissions)

fun Fragment.isAllPermissionsGranted(permissions: Array<String>) = permissions.all {
    ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
}

fun Fragment.openWebsite(url: String) {
    val uri = url.toUri().buildUpon().scheme("https").build()
    val intent = Intent(Intent.ACTION_VIEW, uri)

    startActivity(intent)
}

fun Fragment.openApp(packageName: String) {
    val intent = requireContext().packageManager.getLaunchIntentForPackage(packageName)

    intent?.let {
        startActivity(it)
    }
}

fun Fragment.showAppOnPlayStore(packageName: String) {
    val uri = "https://play.google.com/store/apps/details?id=$packageName".toUri()

    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.android.vending")
    }

    startActivity(intent)
}

fun Fragment.showAppOnAppStore(packageName: String) {
    val uri = "market://details?id=$packageName".toUri()
    val intent = Intent(Intent.ACTION_VIEW, uri)

    startActivity(intent)
}

fun Fragment.openAssetsFile(fileName: String) {
    val file = getAssetsFile(fileName)
    val fileUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(fileUri, "application/${file.extension}")

        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    startActivity(intent)
}

fun Fragment.getAssetsFile(fileName: String): File {
    val file = File(requireContext().filesDir, fileName)

    if (!file.exists()) {
        file.outputStream().use { outputStream ->
            requireContext().assets.open(fileName).use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    return file
}

fun Fragment.getImageUri(fileName: String, folderName: String): Uri {
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$folderName")
    }

    return requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
}

fun Fragment.deleteImage(imageUri: Uri) = requireContext().contentResolver.delete(imageUri, null, null)

fun Fragment.getRawUri(resId: Int) = "android.resource://${requireContext().packageName}/$resId".toUri()

fun Fragment.getRawUris(arrayResId: Int): List<Uri> {
    val rawUris = mutableListOf<Uri>()
    val raws = requireContext().resources.obtainTypedArray(arrayResId)

    for (index in 0 until raws.length()) {
        val resourceId = raws.getResourceId(index, 0)

        rawUris.add(getRawUri(resourceId))
    }

    raws.recycle()

    return rawUris
}

fun Fragment.isConfigChanging() = requireActivity().isChangingConfigurations

fun Fragment.isLandscapeMode() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Fragment.setPortraitMode() {
    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

fun Fragment.setLandscapeMode() {
    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

fun Fragment.hideSystemBars() {
    WindowCompat.getInsetsController(requireActivity().window, requireActivity().window.decorView).apply {
        hide(WindowInsetsCompat.Type.systemBars())

        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

fun Fragment.setReverseLandscapeMode() {
    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
}

fun Fragment.setReversePortraitMode() {
    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
}

fun Fragment.setActionBar(toolbar: MaterialToolbar) {
    (requireActivity() as AppCompatActivity).apply {
        supportActionBar?.hide()

        setSupportActionBar(toolbar)
    }
}

fun Fragment.showTimePicker(titleResId: Int, timeInSystemMillis: Long, confirmClickAction: (Long) -> Unit) {
    val systemLocalTime = timeInSystemMillis.toSystemLocalTime()

    val timePicker = MaterialTimePicker.Builder().run {
        setTimeFormat(TimeFormat.CLOCK_12H)
        setTitleText(getString(titleResId))
        setHour(systemLocalTime.hour)
        setMinute(systemLocalTime.minute)
        build()
    }

    timePicker.addOnPositiveButtonClickListener {
        val localDate = timeInSystemMillis.toSystemLocalDate()
        val localTime = LocalTime.of(timePicker.hour, timePicker.minute)
        val localDateTime = LocalDateTime.of(localDate, localTime)

        confirmClickAction(localDateTime.toSystemMillis())
    }

    timePicker.show(childFragmentManager, "")
}

fun Fragment.showDatePicker(titleResId: Int, dateInSystemMillis: Long, confirmClickAction: (Long) -> Unit) {
    val datePicker = MaterialDatePicker.Builder.datePicker().run {
        setTitleText(getString(titleResId))
        setSelection(dateInSystemMillis.toSystemLocalDateTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli())
        build()
    }

    datePicker.addOnPositiveButtonClickListener {
        val pickedDateInSystemMillis = datePicker.selection!!.toUtcLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val localDate = pickedDateInSystemMillis.toSystemLocalDate()
        val localTime = dateInSystemMillis.toSystemLocalTime()
        val localDateTime = LocalDateTime.of(localDate, localTime)

        confirmClickAction(localDateTime.toSystemMillis())
    }

    datePicker.show(childFragmentManager, "")
}

fun Fragment.finishOnBackPressed() = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
    requireActivity().finish()
}

fun Fragment.goHomeOnBackPressed() = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
    }

    startActivity(intent)
}

fun Fragment.closeDrawerOnBackPressed(drawer: DrawerLayout) {
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
        if (drawer.isOpen) {
            drawer.close()
        } else {
            requireActivity().finish()
        }
    }
}

fun Fragment.goToPreviousPageOnBackPressed(viewPager: ViewPager2, duration: Long) {
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
        if (viewPager.currentItem != 0) {
            viewPager.setCurrentItem(viewPager.currentItem - 1, duration)
        } else {
            requireActivity().finish()
        }
    }
}

fun Fragment.exitFullscreenModeOnBackPressed() = with(requireActivity().onBackPressedDispatcher) {
    addCallback(viewLifecycleOwner) {
        if (isLandscapeMode()) {
            setPortraitMode()
        } else {
            remove()
            onBackPressed()
        }
    }
}

fun Fragment.getStatusBarHeight(): Int {
    val statusBarHeight = resources.getIdentifier("status_bar_height", "dimen", "android")

    return resources.getDimensionPixelSize(statusBarHeight)
}

fun Fragment.getActionBarHeight(): Int {
    val typeValue = TypedValue()

    requireContext().theme.resolveAttribute(android.R.attr.actionBarSize, typeValue, true)

    return TypedValue.complexToDimensionPixelSize(typeValue.data, resources.displayMetrics)
}

fun Fragment.getTabLayoutHeight(resId: Int): Int {
    val tabLayout = parentFragment!!.requireView().findViewById(resId) as TabLayout

    return tabLayout.height
}

fun Fragment.getClickableSpan(colorResId: Int, isUnderLined: Boolean, action: () -> Unit) = object : ClickableSpan() {
    override fun onClick(p0: View) = action()

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)

        ds.color = requireContext().getColor(colorResId)
        ds.isUnderlineText = isUnderLined
    }
}

fun Fragment.getSpannableString(stringResId: Int, span: Any, start: Int, end: Int) = SpannableString(getString(stringResId)).apply {
    setSpan(span, start, end + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun Fragment.copyToClipboard(label: String, text: String) {
    val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(label, text)

    clipboardManager.setPrimaryClip(clipData)
}

fun Fragment.setupEdgeToEdge(
    contentView: View,
    topView: View?,
    bottomView: View?,
    navigationView: NavigationView?,
    isDarkStatusBar: Boolean,
    isDarkNavigationBar: Boolean
) {
    val lightSystemBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
    val darkSystemBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)

    val statusBarStyle = if (isDarkStatusBar) darkSystemBarStyle else lightSystemBarStyle
    val navigationBarStyle = if (isDarkNavigationBar) darkSystemBarStyle else lightSystemBarStyle

    requireActivity().enableEdgeToEdge(statusBarStyle, navigationBarStyle)

    val topViewOriginalHeight = topView?.getXmlHeight() ?: 0
    val bottomViewOriginalHeight = bottomView?.getXmlHeight() ?: 0

    ViewCompat.setOnApplyWindowInsetsListener(contentView) { view, insets ->
        val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val topPadding = if (topView == null) systemBarInsets.top else 0
        val bottomPadding = if (bottomView == null) systemBarInsets.bottom else 0

        topView?.apply {
            updateLayoutParams {
                height = topViewOriginalHeight + systemBarInsets.top
            }

            setPadding(0, systemBarInsets.top, 0, 0)
        }

        bottomView?.apply {
            updateLayoutParams {
                height = bottomViewOriginalHeight + systemBarInsets.bottom
            }

            setPadding(0, 0, 0, systemBarInsets.bottom)
        }

        navigationView?.setPadding(0, systemBarInsets.top, 0, systemBarInsets.bottom)

        view.setPadding(0, topPadding, 0, bottomPadding)

        WindowInsetsCompat.CONSUMED
    }
}

fun Fragment.setupEdgeToEdge(
    contentView: View,
    subContentView: View,
    isDarkNavigationBar: Boolean
) {
    val lightSystemBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
    val darkSystemBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)

    val statusBarStyle = darkSystemBarStyle
    val navigationBarStyle = if (isDarkNavigationBar) darkSystemBarStyle else lightSystemBarStyle

    requireActivity().enableEdgeToEdge(statusBarStyle, navigationBarStyle)

    ViewCompat.setOnApplyWindowInsetsListener(contentView) { view, insets ->
        val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

        subContentView.setPadding(0, 0, 0, systemBarInsets.bottom)

        view.setPadding(0, systemBarInsets.top, 0, 0)

        WindowInsetsCompat.CONSUMED
    }
}

fun Fragment.setSystemBarsStyle(isDarkStatusBar: Boolean, isDarkNavigationBar: Boolean) {
    val lightSystemBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
    val darkSystemBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)

    val statusBarStyle = if (isDarkStatusBar) darkSystemBarStyle else lightSystemBarStyle
    val navigationBarStyle = if (isDarkNavigationBar) darkSystemBarStyle else lightSystemBarStyle

    requireActivity().enableEdgeToEdge(statusBarStyle, navigationBarStyle)
}

fun Fragment.setSharedElementTransition() {
    val transitionInflater = TransitionInflater.from(requireContext())

    sharedElementEnterTransition = transitionInflater.inflateTransition(android.R.transition.move)
}

fun Intent.isResolvable(context: Context) = resolveActivity(context.packageManager) != null

fun <T> MutableSharedFlow<T>.emit(coroutineScope: CoroutineScope, value: T) = coroutineScope.launch {
    emit(value)
}

fun <T> Flow<T>.collect(coroutineScope: CoroutineScope, action: suspend (T) -> Unit) = coroutineScope.launch {
    collect { action(it) }
}

fun <T> Flow<T>.collectOnCreated(lifecycleOwner: LifecycleOwner, action: suspend (T) -> Unit) = lifecycleOwner.lifecycleScope.launch {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
        collect { action(it) }
    }
}

fun <T> Flow<T>.collectOnStarted(lifecycleOwner: LifecycleOwner, action: suspend (T) -> Unit) = lifecycleOwner.lifecycleScope.launch {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        collect { action(it) }
    }
}

fun <R> Flow<R>.toStateFlow(coroutineScope: CoroutineScope, initialValue: R) = stateIn(coroutineScope, SharingStarted.Lazily, initialValue)

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