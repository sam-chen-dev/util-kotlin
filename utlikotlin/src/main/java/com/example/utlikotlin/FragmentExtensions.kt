package com.example.utlikotlin

import android.Manifest
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.MediaStore
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.io.File
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

fun Fragment.getConnectivityManager() = requireContext().getSystemService(ConnectivityManager::class.java)

fun Fragment.getNotificationManager() = requireContext().getSystemService(NotificationManager::class.java)

fun Fragment.getPowerManager() = requireContext().getSystemService(PowerManager::class.java)

@RequiresApi(Build.VERSION_CODES.S)
fun Fragment.getVibratorManager() = requireContext().getSystemService(VibratorManager::class.java)

fun Fragment.getFragmentById(id: Int) = childFragmentManager.findFragmentById(id)

fun Fragment.getMapFragmentById(id: Int) = childFragmentManager.findFragmentById(id) as SupportMapFragment

fun Fragment.hideUpButton() = (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

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
    val locationManager = requireContext().getSystemService(LocationManager::class.java)

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

@RequiresApi(Build.VERSION_CODES.S)
fun Fragment.isBluetoothPermissionGranted(): Boolean {
    val result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN)

    return result == PackageManager.PERMISSION_GRANTED
}

@RequiresApi(Build.VERSION_CODES.S)
fun Fragment.requestBluetoothPermission(launcher: ActivityResultLauncher<Array<String>>) {
    launcher.launch(
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    )
}

fun Fragment.isBluetoothEnabled(): Boolean {
    val bluetoothManager = requireContext().getSystemService(BluetoothManager::class.java)

    return bluetoothManager.adapter.isEnabled
}

fun Fragment.requestEnableBluetooth(launcher: ActivityResultLauncher<Intent>) {
    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

    launcher.launch(intent)
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

fun Fragment.getVibrator(): Vibrator {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getVibratorManager().defaultVibrator
    } else {
        requireContext().getSystemService(Vibrator::class.java)
    }
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

fun Fragment.copyToClipboard(text: String) {
    val clipboardManager = requireContext().getSystemService(ClipboardManager::class.java)
    val clipData = ClipData.newPlainText(UUID.randomUUID().toString(), text)

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

fun Fragment.setupEdgeToEdge2(
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
        val keyboardInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
        val topPadding = if (topView == null) systemBarInsets.top else 0
        val bottomPadding = if (bottomView == null) {
            if (keyboardInsets.bottom == 0) systemBarInsets.bottom else keyboardInsets.bottom
        } else 0

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