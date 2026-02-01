package com.shivam.downn.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

fun Context.createMultipartBodyPart(uri: Uri, partName: String, fileName: String = "image.jpg"): MultipartBody.Part? {
    return try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val bytes = inputStream.readBytes()
            val mediaType = contentResolver.getType(uri)?.toMediaTypeOrNull()
            val requestFile = bytes.toRequestBody(mediaType)
            MultipartBody.Part.createFormData(partName, fileName, requestFile)
        }
    } catch (e: Exception) {
        // Handle exception, maybe log it
        null
    }
}