package com.example.statusbartemp.LogicAndData

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.core.content.ContextCompat

@Composable
fun ClickableUrlText(context : Context, url : String) {
    val text = buildAnnotatedString {
        pushStringAnnotation(
            tag = "URL",
            annotation = url
        )
        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
            append(url)
        }
        pop()
    }

    Text(
        text = text,
        modifier = Modifier.clickable {
            // Handle click event to open the URL in the default browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            ContextCompat.startActivity(context, intent, null)
        }
    )
}