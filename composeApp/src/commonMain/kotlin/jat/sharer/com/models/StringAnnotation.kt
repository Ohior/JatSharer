package jat.sharer.com.models

import androidx.compose.ui.text.SpanStyle

data class StringAnnotation(
    val text:String,
    val style: SpanStyle = SpanStyle(),
    val key:Int? = null,
    val onClick:((Int?)->Unit)? = null
)
