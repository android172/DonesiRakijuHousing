package com.example.skucise

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.TextView
import java.io.File
import android.provider.OpenableColumns




class Util  {
    companion object {

        class ErrorReport constructor(private val reportTo: TextView) {
            fun reportError(message: String) {
                reportTo.text = message
                reportTo.visibility = View.VISIBLE
            }
        }


        // Information from file uri
        fun Context.getFileName(uri: Uri): String {
            var result: String? = null
            if (uri.scheme == "content") {
                contentResolver.query(uri, null, null, null, null)?.let { cursor ->
                    cursor.run {
                        if (cursor.moveToFirst()) {
                            result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        }
                    }.also { cursor.close() }
                }
            }
            if (result == null) {
                result = uri.path
                val cut = result!!.lastIndexOf('/')
                if (cut != -1) {
                    result = result?.substring(cut + 1)
                }
            }
            return result!!
        }

        /* get actual file name or extension */
        fun Context.getFileExtension(uri: Uri): String? = when (uri.scheme) {
            // get file extension
            ContentResolver.SCHEME_FILE -> File(uri.path!!).extension
            // get actual name of file
            //ContentResolver.SCHEME_FILE -> File(uri.path!!).name
            ContentResolver.SCHEME_CONTENT -> getCursorContent(uri)
            else -> null
        }

        private fun Context.getCursorContent(uri: Uri): String? = try {
            contentResolver.query(uri, null, null, null, null)?.let { cursor ->
                cursor.run {
                    val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
                    if (moveToFirst()) mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
                    // case for get actual name of file
                    //if (moveToFirst()) getString(getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    else null
                }.also { cursor.close() }
            }
        } catch (e: Exception) {
            null
        }
    }
}