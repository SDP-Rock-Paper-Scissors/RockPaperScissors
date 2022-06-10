package ch.epfl.sweng.rps

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.models.remote.*
import ch.epfl.sweng.rps.remote.Env
import ch.epfl.sweng.rps.remote.FirebaseReferences
import ch.epfl.sweng.rps.remote.FirebaseRepository
import ch.epfl.sweng.rps.remote.Repository
import ch.epfl.sweng.rps.services.FirebaseGameService
import ch.epfl.sweng.rps.services.GameService.GameServiceException
import ch.epfl.sweng.rps.services.ProdServiceLocator
import ch.epfl.sweng.rps.services.ServiceLocator
import ch.epfl.sweng.rps.utils.FirebaseEmulatorsUtils
import ch.epfl.sweng.rps.utils.europeWest1
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class FirebaseTests {
    private lateinit var db: Repository


    @Before
    fun setUp() {
        db = ServiceLocator.getInstance(Env.Test).repository

        for (env in Env.values()) {
            ServiceLocator.getInstance(env).disposeAllGameServices()
        }
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
        FirebaseAuth.getInstance().signOut()
    }

    @After
    fun tearDown() {
        FirebaseApp.clearInstancesForTest()
        unmockkAll()
    }

    @Test
    fun testEuropeWest1() {
        assertDoesNotThrow { Firebase.europeWest1 }
    }

    @Test
    fun testServiceLocator() {
        val serviceLocatorProd = ServiceLocator.getInstance(env = Env.Prod)
        assertEquals(Env.Prod, serviceLocatorProd.env)

        val serviceLocatorDev = ServiceLocator.getInstance(env = Env.Test)
        assertEquals(Env.Test, serviceLocatorDev.env)

        assertEquals(
            serviceLocatorProd.repository, serviceLocatorProd.repository
        )
    }

    inline fun <reified T> mockQuerySnapshot(data: Map<String, Pair<T, Map<String, Any?>>>): QuerySnapshot {
        val querySnapshot = mockk<QuerySnapshot>()
        every { querySnapshot.toObjects(any<Class<T>>()) } returns data.values.map { it.first }
        every { querySnapshot.documents } returns data.map { e ->
            mockk<DocumentSnapshot>().apply {
                every { this@apply.data } returns e.value.second
                every { toObject(any<Class<T>>()) } returns e.value.first
                every { toObject<T>() } returns e.value.first
                every { id } returns e.key
                val k = slot<String>()
                every { this@apply.get(capture(k)) } answers {
                    e.value.second[k.captured]
                }
            }
        }
        return querySnapshot
    }

    inline fun <reified T> mockDocumentSnapshot(
        id: String, data: Pair<T, Map<String, Any?>>
    ): DocumentSnapshot {
        val documentSnapshot = mockk<DocumentSnapshot>()
        every { documentSnapshot.data } returns data.second
        val k = slot<String>()
        every { documentSnapshot.get(capture(k)) } answers {
            data.second[k.captured]
        }
        every { documentSnapshot.toObject(any<Class<T>>()) } returns data.first
        every { documentSnapshot.toObject<T>() } returns data.first
        every { documentSnapshot.id } returns id
        return documentSnapshot
    }

    fun <T> mockTask(value: T) = Tasks.forResult(value)
    inline fun <reified T : Any> mockCollection(
        id: String, data: Map<String, Pair<T, Map<String, Any?>>>
    ): CollectionReference {
        val mock = mockk<CollectionReference>()
        every { mock.get() } returns mockTask(mockQuerySnapshot(data))
        every { mock.id } returns id
        val identities = listOf<MockKMatcherScope.() -> Query>(
            { mock.whereArrayContains(any<String>(), any()) },
            { mock.whereEqualTo(any<String>(), any()) },
            { mock.whereGreaterThan(any<String>(), any()) },
            { mock.whereGreaterThanOrEqualTo(any<String>(), any()) },
            { mock.whereLessThan(any<String>(), any()) },
            { mock.whereLessThanOrEqualTo(any<String>(), any()) },
            { mock.whereNotEqualTo(any<String>(), any()) },
            { mock.orderBy(any<String>(), any()) },
            { mock.orderBy(any<String>()) },
            { mock.limit(any()) },
        )
        identities.forEach { every(it) returns mock }
        val s = slot<String>()
        every { mock.document(capture(s)) } answers {
            mockk<DocumentReference>().apply {
                every { this@apply.id } returns s.captured
                every { get() } returns mockTask(mockDocumentSnapshot(s.captured, data[s.captured]!!))
            }
        }
        return mock
    }

    /*
    * class FirebaseReferences {
    private val root = FirebaseFirestore.getInstance()
    private val storageRoot = FirebaseStorage.getInstance().reference

    /**
     * Thefriend request collection.
     */
    val usersFriendRequest = root.collection("friend_requests")

    /**
     * Theusers collection.
     */
    val usersCollection = root.collection("users")

    /**
     * Theprofile pictures folder.
     */
    val profilePicturesFolder = storageRoot.child("profile_pictures")

    /**
     * Thegames collection.
     */
    val gamesCollection = root.collection("games")

    /**
     * Thescores collection.
     */
    val scoresCollection = root.collection("scores")

    /**
     * Returns the collection of invitations for the given user
     * with [uid].
     */
    fun invitationsOfUid(uid: String) = usersCollection
        .document(uid)
        .collection("invitations")
}
*/
    fun mockFirebaseReferences(): FirebaseReferences {
        val m = mockk<FirebaseReferences>()
        every { m.usersFriendRequest } returns mockCollection(
            "friend_requests", mapOf("" to (FriendRequest(users = listOf("me", "you")) to mapOf()))
        )
        every { m.usersCollection } returns mockCollection("users", mapOf("" to (User() to mapOf())))
        every { m.gamesCollection } returns mockCollection(
            "games", mapOf(
                "rps" to (Game.Rps() to mapOf(
                    "edition" to GameMode.GameEdition.RockPaperScissors.name,
                ))
            )
        )
        every { m.scoresCollection } returns mockCollection("scores", mapOf("" to (TotalScore() to mapOf())))
        every { m.profilePicturesFolder } returns mockk()
        return m
    }

    @Test
    fun testFirebaseRepo(): Unit = runBlocking {
        val fb = mockFirebaseReferences()
        val auth = mockk<FirebaseAuth>()
        every { auth.currentUser } returns mockk<FirebaseUser>().apply { every { uid } returns "me" }
        val repo = FirebaseRepository.createInstance(fb, auth)

        assertEquals(User(), repo.getUser("").getOrThrow())
        assertEquals(Game.Rps(), repo.games.getGame("rps"))
        assertEquals(listOf(Game.Rps()), repo.games.gamesOfUser("me"))
        assertEquals(listOf(Game.Rps()), repo.games.myActiveGames())
        assertEquals(listOf(TotalScore()), repo.games.getLeaderBoardScore(""))
        assertEquals("me", repo.getCurrentUid())
        assertEquals(listOf(FriendRequest(users = listOf("me", "you"))), repo.friends.listFriendRequests())
        assertEquals(listOf("you"), repo.friends.getFriends())
    }

    @Test
    fun testGameService() {
        val serviceLocatorProd = ServiceLocator.getInstance(env = Env.Prod) as ProdServiceLocator
        assertEquals("1234", serviceLocatorProd.getGameServiceForGame("1234", start = false).gameId)
        assertTrue(
            serviceLocatorProd.getGameServiceForGame(
                "1", start = false
            ) === serviceLocatorProd.getGameServiceForGame("1", start = false)
        )

        val service: FirebaseGameService = serviceLocatorProd.getGameServiceForGame("1234", start = false)

        val throwingActions: List<suspend (service: FirebaseGameService) -> Unit> = listOf(
            { it.refreshGame() },
            { it.addRound() },
            { it.playHand(Hand.PAPER) },
        )

        for (action in throwingActions) {
            assertThrows(Exception::class.java) {
                runBlocking {
                    action(service)
                }
            }
        }

        service.dispose()

        assertTrue(service.isDisposed)
    }

    @Test
    fun disposingGameServices() {
        val serviceLocator = ServiceLocator.getInstance(env = Env.Prod) as ProdServiceLocator
        val service: FirebaseGameService = serviceLocator.getGameServiceForGame("1234", start = false)
        assertEquals(listOf("1234"), serviceLocator.cachedGameServices)
        assertFalse(service.isDisposed)
        serviceLocator.disposeAllGameServices()
        assertTrue(service.isDisposed)
        assertEquals(emptyList<String>(), serviceLocator.cachedGameServices)
    }

    @Test
    fun disposingGameServices2() {
        val serviceLocator = ServiceLocator.getInstance(env = Env.Prod) as ProdServiceLocator
        serviceLocator.disposeAllGameServices()
        val service: FirebaseGameService = serviceLocator.getGameServiceForGame("1234", start = false)
        assertEquals(listOf("1234"), serviceLocator.cachedGameServices)
        assertFalse(service.isDisposed)
        service.dispose()
        assertTrue(service.isDisposed)
        serviceLocator.disposeAllGameServices()
        assertTrue(service.isDisposed)
        assertEquals(emptyList<String>(), serviceLocator.cachedGameServices)
    }

    @Test
    fun usingAfterDisposedThrows() {
        val serviceLocator = ServiceLocator.getInstance(env = Env.Prod) as ProdServiceLocator
        val service: FirebaseGameService = serviceLocator.getGameServiceForGame("1234", start = false)
        service.dispose()
        assertTrue(service.isDisposed)
        val throwingActions: List<suspend (service: FirebaseGameService) -> Unit> = listOf(
            { it.refreshGame() },
            { it.addRound() },
            { it.playHand(Hand.PAPER) },
            { it.currentRound },
            { it.currentGame },
            { it.startListening() },
            { it.stopListening() },
        )

        for (action in throwingActions) {
            assertThrows(GameServiceException::class.java) {
                runBlocking {
                    action(service)
                }
            }
        }

        assertNull(service.error)

    }

    @Test
    fun testFirebaseReferences() {
        val firebase = (ServiceLocator.getInstance(env = Env.Prod) as ProdServiceLocator).firebaseReferences
        assertEquals("games", firebase.gamesCollection.path)

        assertEquals("/profile_pictures", firebase.profilePicturesFolder.path)
    }

    @Test
    fun testEmulator() {
        FirebaseEmulatorsUtils.useEmulators()
        assertEquals(true, FirebaseEmulatorsUtils.emulatorUsed)
    }
}
