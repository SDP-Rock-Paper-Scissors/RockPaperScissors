package ch.epfl.sweng.rps.auth

import ch.epfl.sweng.rps.models.User

abstract class Authenticator(callback: (User)->Unit) {
    abstract fun signInWithGoogle()
}