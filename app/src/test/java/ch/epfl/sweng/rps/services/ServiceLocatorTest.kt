package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.db.Env
import org.junit.Assert.*
import org.junit.Test

class ServiceLocatorTest {


    @Test
    fun testCurrentEnv() {
        val serviceLocatorProd = ServiceLocator.getInstance(env = Env.Prod)
        assertEquals(Env.Prod, serviceLocatorProd.currentEnv())

        val serviceLocatorDev = ServiceLocator.getInstance(env = Env.Dev)
        assertEquals(Env.Dev, serviceLocatorDev.currentEnv())
        assertTrue(serviceLocatorProd === ServiceLocator.getInstance(env = Env.Prod))
        assertTrue(serviceLocatorDev === ServiceLocator.getInstance(env = Env.Dev))
    }




}