package com.example.utlikotlin.extensions.annotatedString

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle

fun AnnotatedString.Builder.append(text: String, url: String) = withLink(LinkAnnotation.Url(url)) {
    append(text)
}

fun AnnotatedString.Builder.append(text: String, color: Color) = withStyle(SpanStyle(color)) {
    append(text)
}

fun AnnotatedString.Builder.append(text: String, color: Color, onCLick: () -> Unit) = withLink(
    LinkAnnotation.Clickable(
        tag = text,
        styles = TextLinkStyles(
            style = SpanStyle(color),
            pressedStyle = SpanStyle(textDecoration = TextDecoration.Underline)
        ),
        linkInteractionListener = { onCLick() }
    )
) {
    append(text)
}