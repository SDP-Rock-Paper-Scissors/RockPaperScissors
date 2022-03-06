package ch.epfl.sweng.rps.db

class MockDatabase : FirestoreDatabase() {

    override fun getCurrentUid(): String {
        return "this-is-my-uid"
    }
}