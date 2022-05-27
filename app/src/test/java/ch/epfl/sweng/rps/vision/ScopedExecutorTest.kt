package ch.epfl.sweng.rps.vision

import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class ScopedExecutorTest {

    var executor = Executors.newSingleThreadExecutor()
    val executorScoped = ScopedExecutor(executor)

    @Test
    fun testExecutorScoped() {
        executorScoped.shutdown()
        executorScoped.execute(Runnable {
            run() {
                while (true) {
                }
            }
        })

        assert(!executor.isShutdown)
    }

}