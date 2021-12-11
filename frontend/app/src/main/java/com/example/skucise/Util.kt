package com.example.skucise

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.LabeledIntent
import android.net.Uri
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.TextView
import java.io.File
import android.provider.OpenableColumns




class Util  {
    companion object {

        // Loading dialog
        class LoadingDialog(val activity: Activity) {

            @SuppressLint("InflateParams")
            private var loadingDialog = AlertDialog
                .Builder(activity)
                .setView(activity.layoutInflater.inflate(R.layout.dialog_loading, null))
                .setCancelable(false)
                .create()

            fun start() {
                loadingDialog.show()
            }

            fun dismiss() {
                loadingDialog.dismiss()
            }
        }


        // Send email
        fun startEmailAppIntent(activity: Activity) {
            val emailIntent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"))
            val packageManager = activity.packageManager

            val activitiesHandlingEmails = packageManager.queryIntentActivities(emailIntent, 0)

            if (activitiesHandlingEmails.isEmpty())
                return

            // use the first email package to create the chooserIntent
            val firstEmailPackageName = activitiesHandlingEmails.first().activityInfo.packageName
            val firstEmailInboxIntent = packageManager.getLaunchIntentForPackage(firstEmailPackageName)
            val emailAppChooserIntent = Intent.createChooser(firstEmailInboxIntent, "")

            // created UI for other email packages and add them to the chooser
            val emailInboxIntents = mutableListOf<LabeledIntent>()
            for (i in 1 until activitiesHandlingEmails.size) {
                val activityHandlingEmail = activitiesHandlingEmails[i]
                val packageName = activityHandlingEmail.activityInfo.packageName
                val intent = packageManager.getLaunchIntentForPackage(packageName)
                emailInboxIntents.add(
                    LabeledIntent(
                        intent,
                        packageName,
                        activityHandlingEmail.loadLabel(packageManager),
                        activityHandlingEmail.icon
                    )
                )
            }
            val extraEmailInboxIntents = emailInboxIntents.toTypedArray()
            val emailChooserIntent = emailAppChooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraEmailInboxIntents)
            activity.startActivity(emailChooserIntent)
        }

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