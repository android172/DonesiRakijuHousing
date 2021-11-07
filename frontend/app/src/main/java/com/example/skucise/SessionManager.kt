package com.example.skucise

import android.content.Context
import android.content.SharedPreferences

class SessionManager {
    companion object {
        var sharedPreferences : SharedPreferences? = null
        var token: String?
            get() {
                if (sharedPreferences == null) throw Exception("ERROR :: SessionManager:: Session not loaded!!")
                return sharedPreferences!!.getString("token", null)
            }
            private set(value) {
                if (sharedPreferences == null) throw Exception("ERROR :: SessionManager:: Session not loaded!!")
                sharedPreferences!!.edit().apply {
                    putString("token", value)
                    apply()
                }
            }

        fun loadSession(context: Context) {
            sharedPreferences = context.getSharedPreferences("SessionManager", 0)
        }

        fun startSession(new_token : String) {
            if (token != null) {
                throw Exception("ERROR :: Error in session :: Session already started.")
            }
            token = new_token
        }

        fun isActive() : Boolean {
            return token == null
        }

        fun stopSession() {
            token = null
        }
    }
}