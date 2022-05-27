package ch.epfl.sweng.rps.vision


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class FrameMetadataTest {

    @Test
    fun getCheckAttributes() {
        val frameData: FrameMetadata = FrameMetadata.Builder().setHeight(20)
            .setWidth(40).setRotation(0).build()

        assertEquals(20, frameData.height)
        assertEquals(40, frameData.width)
        assertEquals(0, frameData.rotation)
    }
}