package com.example.cooktok.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun persistImage(context: Context, uri: Uri): String {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return ""
    val file = File(context.filesDir, "recipe_${System.currentTimeMillis()}.jpg")

    inputStream.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }

    return file.absolutePath // return full file path
}
