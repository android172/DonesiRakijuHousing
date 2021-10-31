package com.example.skucise

import android.view.View
import android.widget.TextView

class Util  {
    companion object {

        class ErrorReport constructor(private val reportTo: TextView) {
            fun reportError(message: String) {
                reportTo.text = message
                reportTo.visibility = View.VISIBLE
            }
        }
    }
}