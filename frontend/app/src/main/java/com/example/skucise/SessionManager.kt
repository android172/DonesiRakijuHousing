package com.example.skucise

import android.content.Context
import android.content.SharedPreferences

class SessionManager {
    companion object {
        private var sharedPreferences : SharedPreferences? = null
        var currentUser: User?
            get() {
                if (sharedPreferences == null) throw Exception("ERROR :: SessionManager:: Session not loaded!!")
                val id = sharedPreferences!!.getInt("id", 0)
                if (id == 0) return null
                return User(id = id)
            }
            private set(value) {
                if (sharedPreferences == null) throw Exception("ERROR :: SessionManager:: Session not loaded!!")
                sharedPreferences!!.edit().apply {
                    putInt("id", value?.id ?: 0)
                    apply()
                }
            }
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

        fun startSession(new_token : String, user: User) {
            if (token != null) {
                throw Exception("ERROR :: Error in session :: Session already started.")
            }
            token = new_token
            currentUser = user
        }

        fun isActive() : Boolean {
            return token == null
        }

        fun stopSession() {
            token = null
            currentUser = null
        }
    }
}