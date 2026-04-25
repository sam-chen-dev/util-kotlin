package com.example.utlikotlin

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.google.pay.button.ButtonType
import com.google.pay.button.PayButton

@Composable
fun Text(
    resId: Int,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    modifier: Modifier = Modifier
) {
    Text(stringResource(resId), color = color, fontSize = fontSize, modifier = modifier)
}

@Composable
fun Text(
    text: String,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    modifier: Modifier = Modifier
) {
    Text(text, color = color, fontSize = fontSize, modifier = modifier)
}

@Composable
fun Icon(resId: Int, contentDescription: String, tint: Color = LocalContentColor.current) {
    Icon(painterResource(resId), contentDescription, tint = tint)
}

@Composable
fun IconButton(
    resId: Int,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = LocalContentColor.current,
    modifier: Modifier = Modifier
) {
    IconButton(onClick, modifier) {
        Icon(resId, contentDescription, tint)
    }
}

@Composable
fun IconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = LocalContentColor.current,
    modifier: Modifier = Modifier
) {
    IconButton(onClick, modifier) {
        Icon(imageVector, contentDescription, tint = tint)
    }
}

@Composable
fun IconButton(text: String, onClick: () -> Unit) {
    IconButton(onClick) {
        Text(text)
    }
}

@Composable
fun FilledIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = LocalContentColor.current
) {
    FilledIconButton(onClick) {
        Icon(imageVector, contentDescription, tint = tint)
    }
}

@Composable
fun OutlinedIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = LocalContentColor.current
) {
    OutlinedIconButton(onClick) {
        Icon(imageVector, contentDescription, tint = tint)
    }
}

@Composable
fun TonalIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = LocalContentColor.current
) {
    FilledTonalIconButton(onClick) {
        Icon(imageVector, contentDescription, tint = tint)
    }
}

@Composable
fun Button(text: String, onClick: () -> Unit, isEnabled: Boolean = true) {
    Button(onClick, enabled = isEnabled) {
        Text(text)
    }
}

@Composable
fun TextButton(resId: Int, onClick: () -> Unit) {
    TextButton(onClick) {
        Text(resId)
    }
}

@Composable
fun OutlinedButton(resId: Int, onClick: () -> Unit) {
    OutlinedButton(onClick) {
        Text(resId)
    }
}

@Composable
fun TonalButton(resId: Int, onClick: () -> Unit) {
    FilledTonalButton(onClick) {
        Text(resId)
    }
}

@Composable
fun Image(resId: Int, contentDescription: String, contentScale: ContentScale, modifier: Modifier = Modifier) {
    Image(painterResource(resId), contentDescription, contentScale = contentScale, modifier = modifier)
}

@Composable
fun CoilImage(url: String, contentDescription: String, contentScale: ContentScale, modifier: Modifier = Modifier) {
    AsyncImage(url, contentDescription, contentScale = contentScale, modifier = modifier)
}

@Composable
fun BoxScope.SnackbarHost(hostState: SnackbarHostState) {
    SnackbarHost(
        hostState = hostState,
        modifier = Modifier.align(Alignment.BottomCenter)
    )
}

@Composable
fun GooglePayButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    PayButton(
        onClick = onClick,
        allowedPaymentMethods = "[]",
        type = ButtonType.Pay,
        radius = 8.dp,
        modifier = modifier
    )
}

@Composable
fun AssistChip(label: String, icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    AssistChip(
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription, Modifier.size(AssistChipDefaults.IconSize)) },
        onClick = onClick
    )
}

@Composable
fun FilterChip(isSelected: Boolean, label: String, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        label = { Text(label) },
        onClick = onClick
    )
}

@Composable
fun SetSystemBarsStyle(isDarkStatusBar: Boolean, isDarkNavigationBar: Boolean) {
    val lightSystemBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
    val darkSystemBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)

    val statusBarStyle = if (isDarkStatusBar) darkSystemBarStyle else lightSystemBarStyle
    val navigationBarStyle = if (isDarkNavigationBar) darkSystemBarStyle else lightSystemBarStyle

    (LocalActivity.current as ComponentActivity?)?.enableEdgeToEdge(statusBarStyle, navigationBarStyle)
}

fun showToast(context: Context, text: String) = Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

fun showToast(context: Context, resId: Int) = Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()

fun showToastLong(context: Context, text: String) = Toast.makeText(context, text, Toast.LENGTH_LONG).show()

fun showToastLong(context: Context, resId: Int) = Toast.makeText(context, resId, Toast.LENGTH_LONG).show()