package com.example.utlikotlin

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.AlarmManager
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.InetAddresses
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.forEachIndexed
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.time.*
import java.time.format.DateTimeFormatter

fun Int.dp(context: Context) = this * context.resources.displayMetrics.density

fun Int.toDigit(digit: Int): String {
    var num = this.toString()

    if (digit > num.length) {
        num = "0".repeat(digit - num.length) + num
    }

    return num
}

fun Long.toSystemLocalTime() = this.toSystemLocalDateTime().toLocalTime()

fun Long.toSystemLocalDate() = this.toSystemLocalDateTime().toLocalDate()

fun LocalDateTime.toSystemMillis() = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun Long.toSystemLocalDateTime() = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()

fun Long.toUtcLocalDateTime() = Instant.ofEpochMilli(this).atZone(ZoneId.of("UTC")).toLocalDateTime()

fun Long.toFormattedDateTimeString(format: String): String {
    val systemDateTime = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault())

    return systemDateTime.format(DateTimeFormatter.ofPattern(format))
}

fun Double.roundDecimal(digit: Int) = "%,.${digit}f".format(this)

fun Float.roundDecimal(digit: Int) = "%,.${digit}f".format(this)

fun String.toBytes() = this.toByteArray(Charset.forName("GBK"))

fun String.isEmailAddress() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

@RequiresApi(Build.VERSION_CODES.Q)
fun String.isIpAddress() = InetAddresses.isNumericAddress(this)

fun String.toEncryptedString(): String = Base64.encodeToString(this.toByteArray(), Base64.NO_PADDING)

fun String.toDecryptedString() = Base64.decode(this, Base64.NO_PADDING).decodeToString()

fun String.toDateTimeLong(dateTimeFormat: String): Long {
    val dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat)

    return LocalDateTime.parse(this, dateTimeFormatter).toSystemMillis()
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

fun <T> List<T>.mapButReplace(targetItem: T, newItem: T) = map {
    if (it == targetItem) {
        newItem
    } else {
        it
    }
}

fun View.indexOfParent() = (this.parent as ViewGroup).indexOfChild(this)

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

fun ViewGroup.getCheckedIndexes(): List<Int> {
    val checkedIndexes = mutableListOf<Int>()

    this.forEachIndexed { index, view ->
        if ((view as CompoundButton).isChecked) {
            checkedIndexes.add(index)
        }
    }

    return checkedIndexes
}

fun RecyclerView.smoothScrollToTop() = smoothScrollToPosition(0)

fun RecyclerView.scrollToTop() = scrollToPosition(0)

fun ImageView.load(url: String) {
    val uri = url.toUri().buildUpon().scheme("https").build()

    Glide.with(context).load(uri).into(this)
}

fun ImageView.load(uri: Uri) = Glide.with(context).load(uri).into(this)

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

fun TextInputEditText.focusOnLast() {
    requestFocus()

    setSelection(text.toString().length)
}

fun AlertDialog.showIfNotShowing() {
    if (!isShowing) {
        show()
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

fun Fragment.hideUpButton() = (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

fun Fragment.getConnectivityManager() = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

fun Fragment.getNotificationManager() = requireContext().getSystemService(NotificationManager::class.java) as NotificationManager

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

fun Fragment.pickPhoto(request: ActivityResultLauncher<Intent>) {
    val intent = Intent(Intent.ACTION_PICK).apply {
        type = "image/*"
    }

    request.launch(intent)
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

fun Fragment.isGPSOn(): Boolean {
    val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun Fragment.requestGPSOn(request: ActivityResultLauncher<IntentSenderRequest>) {
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

fun Fragment.isLandscapeMode() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Fragment.rotateScreen() {
    if (isLandscapeMode()) {
        setPortraitMode()
    } else {
        setLandscapeMode()
    }
}

fun Fragment.setLandscapeMode() {
    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

fun Fragment.setPortraitMode() {
    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

fun Fragment.setReverseLandscapeMode() {
    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
}

fun Fragment.setReversePortraitMode() {
    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
}

fun Fragment.setFullScreenMode(isEnable: Boolean) {
    if (isEnable) {
        val systemUiFlag = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        requireActivity().window.decorView.systemUiVisibility = systemUiFlag

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    } else {
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }
}

fun Fragment.setFragmentResult(requestKeyResId: Int, result: Bundle) {
    parentFragmentManager.setFragmentResult(getString(requestKeyResId), result)
}

fun Fragment.setFragmentResultListener(requestKeyResId: Int, listener: (String, Bundle) -> Unit) {
    parentFragmentManager.setFragmentResultListener(getString(requestKeyResId), this, listener)
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

fun LifecycleOwner.isConfigChanging() = (this as Fragment).requireActivity().isChangingConfigurations

fun Intent.isResolvable(context: Context) = resolveActivity(context.packageManager) != null

fun AndroidViewModel.getConnectivityManager(): ConnectivityManager {
    return (getApplication() as Context).getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}

fun AndroidViewModel.getAssets() = (getApplication() as Context).assets

fun AndroidViewModel.getResources() = (getApplication() as Context).resources

fun AndroidViewModel.getAlarmManager() = (getApplication() as Context).getSystemService(Context.ALARM_SERVICE) as AlarmManager

fun AndroidViewModel.getInteger(resId: Int) = (getApplication() as Context).resources.getInteger(resId)

fun AndroidViewModel.getString(resId: Int) = (getApplication() as Context).getString(resId)

fun AndroidViewModel.getString(resId: Int, vararg formatArgs: Any) = (getApplication() as Context).getString(resId, *formatArgs)

fun AndroidViewModel.showToast(text: String) = Toast.makeText(getApplication(), text, Toast.LENGTH_SHORT).show()

fun AndroidViewModel.showToast(resId: Int) = Toast.makeText(getApplication(), resId, Toast.LENGTH_SHORT).show()

fun AndroidViewModel.showToastLong(text: String) = Toast.makeText(getApplication(), text, Toast.LENGTH_LONG).show()

fun AndroidViewModel.showToastLong(resId: Int) = Toast.makeText(getApplication(), resId, Toast.LENGTH_LONG).show()

fun AndroidViewModel.getRawUri(resId: Int) = "android.resource://${(getApplication() as Context).packageName}/$resId".toUri()

fun AndroidViewModel.getRawUris(arrayResId: Int): List<Uri> {
    val rawUris = mutableListOf<Uri>()
    val raws = (getApplication() as Context).resources.obtainTypedArray(arrayResId)

    for (index in 0 until raws.length()) {
        val resourceId = raws.getResourceId(index, 0)

        rawUris.add(getRawUri(resourceId))
    }

    raws.recycle()

    return rawUris
}

suspend fun AndroidViewModel.readFileFromAssets(path: String): String? {
    try {
        return getAssets().open(path).bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        Log.v("readFileFromAssets()", "Error: [${e.javaClass.simpleName}]: ${e.message.toString()}")
        return null
    }
}

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