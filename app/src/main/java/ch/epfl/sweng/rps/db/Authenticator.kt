package ch.epfl.sweng.rps.db

import ch.epfl.sweng.rps.models.User

interface Authenticator {
    fun signInWithGoogle() : String
    fun signInAnonymously() : String
}