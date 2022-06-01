package ch.epfl.sweng.rps.utils

import androidx.lifecycle.lifecycleScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.sweng.rps.ActivityScenarioRuleWithSetup
import ch.epfl.sweng.rps.MainActivity
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.internal.toHexString
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SuspendResultTest {

    @get:Rule
    val rule = ActivityScenarioRuleWithSetup.default(MainActivity::class.java)

    @Test
    fun testBasicUseCases(): Unit = runTest(UnconfinedTestDispatcher()) {
        assertIs<SuspendResult.Failure<Int>>(guardSuspendable<Int> { throw Exception() })
        assertIs<SuspendResult.Success<Int>>(guardSuspendable { 1 })

        assertEquals(1, guardSuspendable { 1 }.getOrThrow())
        assertThrows<ArrayIndexOutOfBoundsException> { guardSuspendable { throw ArrayIndexOutOfBoundsException() }.getOrThrow() }

        assertEquals(1, guardSuspendable { 1 }.asData?.value)
        assertNull(guardSuspendable { throw Exception() }.asData?.value)

        assertEquals(3, guardSuspendable { 1 }.then { it + 2 }.getOrThrow())
        assertIs<SuspendResult.Failure<Int>>(guardSuspendable<Int> { throw  Exception() }.then { it + 2 })

        guardSuspendable { 1 }.whenIs(
            {
                assertEquals(1, it.value)
            },
            {
                throw AssertionError("Should not be called")
            }
        )
        guardSuspendable { throw ChangeNotifier.ListenerException("", null) }.whenIs(
            {
                throw AssertionError("Should not be called")
            },
            {
                assertThrows<ChangeNotifier.ListenerException> { it.getOrThrow() }
            }
        )
    }

    @Test
    fun testShowSnackbarIfError() {
        val text = System.currentTimeMillis().toHexString()
        rule.scenario.onActivity {
            it.lifecycleScope.launchWhenStarted {
                guardSuspendable { throw Exception(text) }.showSnackbarIfError(
                    it,
                    callback = object :
                        BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onShown(transientBottomBar: Snackbar?) {
                            super.onShown(transientBottomBar)
                            assertTrue(transientBottomBar?.isShown ?: false)
                        }
                    })
            }
        }
    }

    @Test
    fun testWhenIsWithShowSnackbar() {
        val text = System.currentTimeMillis().toHexString()
        rule.scenario.onActivity {
            it.lifecycleScope.launchWhenStarted {
                guardSuspendable { throw Exception(text) }.whenIs(
                    {
                        throw AssertionError("Should not be called")
                    },
                    SuspendResult.showSnackbar(it, callback = object :
                        BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onShown(transientBottomBar: Snackbar?) {
                            super.onShown(transientBottomBar)
                            assertTrue(transientBottomBar?.isShown ?: false)
                        }
                    }) {}
                )
            }
        }
    }

    @Test
    fun testWhenOrSnackbar() {
        val text = System.currentTimeMillis().toHexString()
        rule.scenario.onActivity {
            it.lifecycleScope.launchWhenStarted {
                guardSuspendable { throw Exception(text) }.whenOrSnackbar(
                    activity = it,
                    callback = object :
                        BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onShown(transientBottomBar: Snackbar?) {
                            super.onShown(transientBottomBar)
                            assertTrue(transientBottomBar?.isShown ?: false)
                        }
                    }
                ) {
                    throw AssertionError("Should not be called")
                }
            }
        }
    }
}