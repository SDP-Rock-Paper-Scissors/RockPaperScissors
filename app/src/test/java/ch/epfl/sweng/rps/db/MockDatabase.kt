package ch.epfl.sweng.rps.db

class MockDatabase(val mockedCurrentUid: String) : FirestoreDatabase() {


    override fun getCurrentUid(): String {
        return mockedCurrentUid
    }
}