package jat.sharer.com.utils

import kotlin.math.pow

object SnippetTools {
    fun formatByteSize(bytes: Long): String {
        if (bytes <= 0) return "0 bytes"

        val units = arrayOf("bytes", "kB", "MB", "GB", "TB", "PB", "EB")
        val digitGroups = (kotlin.math.log10(bytes.toDouble()) / kotlin.math.log10(1024.0)).toInt()

        val value = bytes / 1024.0.pow(digitGroups)
        val unit = units[digitGroups]
        return "${kotlin.math.round(value * 100) / 100.0}$unit"
    }
}