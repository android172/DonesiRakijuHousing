package com.example.skucise

class SessionManager {
    companion object {
        private var token: String? = null

        fun startSession(new_token : String) {
            if (token != null) {
                throw Exception("ERROR :: Error in session :: Session already started.")
            }
            token = new_token
        }

        fun stopSession() {
            token = null
        }

        fun getToken(): String? {
            if (token == null) {
                throw Exception("ERROR :: Error in session :: There is no token, session needs to be started.")
            }
            return token
        }
    }
}