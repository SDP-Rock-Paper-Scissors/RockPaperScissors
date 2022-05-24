package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.remote.Env
import ch.epfl.sweng.rps.services.ServiceLocator.TestServiceLocator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class ProdServiceLocatorTest {

    @Test
    fun testCurrentEnv() {
        val serviceLocatorProd = ServiceLocator.getInstance(env = Env.Prod)
        assertEquals(Env.Prod, serviceLocatorProd.env)

        val serviceLocatorTest = ServiceLocator.getInstance(env = Env.Test)
        assertEquals(Env.Test, serviceLocatorTest.env)

        assertSame(serviceLocatorProd, ServiceLocator.getInstance(env = Env.Prod))
        assertSame(serviceLocatorTest, ServiceLocator.getInstance(env = Env.Test))

        assertEquals(serviceLocatorProd::class, ProdServiceLocator::class)
        assertEquals(serviceLocatorTest::class, TestServiceLocator::class)

        assertEquals(
            ServiceLocator.getInstance(Env.Test).repository,
            ServiceLocator.localRepository
        )
    }
}