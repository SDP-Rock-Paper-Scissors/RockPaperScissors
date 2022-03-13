package ch.epfl.sweng.rps.db

class RepositoryException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)

    class UserNotLoggedIn : Exception {
        constructor() : super("User not logged in")
        constructor(uid: String?) : super("User $uid not logged in")
        constructor(cause: Throwable) : super("User not logged in", cause)
    }

    class ForbiddenOperationException : Exception {
        constructor() : super("Forbidden operation")
        constructor(message: String) : super("Forbidden operation: $message")
        constructor(cause: Throwable) : super(cause)
    }
}