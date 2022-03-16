package ch.epfl.sweng.rps.auth

import ch.epfl.sweng.rps.models.User

abstract class Authenticator(callback: (String)->Unit) {
    abstract fun signInWithGoogle()
    abstract fun signInAnonymously() : String
}