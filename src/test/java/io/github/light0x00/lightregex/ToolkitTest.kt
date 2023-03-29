package io.github.light0x00.lightregex

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author light
 * @since 2023/3/29
 */
class ToolkitTest {

    @Test
    fun testUnicodeHexToString() {
        unicodeHexToString("1F914").also {
            println(it)
            Assertions.assertEquals("🤔", it)
        }
        unicodeHexToString("4E2D").also {
            println(it)
            Assertions.assertEquals("中", it)
        }
        unicodeHexToString("56FD").also {
            println(it)
            Assertions.assertEquals("国", it)
        }
    }
}