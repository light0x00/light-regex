package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.common.Unicode
import io.github.light0x00.lightregex.common.utf16Iterator
import org.junit.jupiter.api.Test

class UTF16IteratorTest {
    @Test
    fun test() {
        val str = "h😄a😡"
        val utf16Iterator = utf16Iterator(str)

        for (i in utf16Iterator) {
            println(Unicode.toString(i)+"${utf16Iterator.currentUnitIndex}")
        }

    }

    @Test
    fun test2(){
        val str = "happy 😄 anger 😡 sorrow 😞 joy😇"
        val seq = utf16Iterator(str).asSequence() + sequenceOf(Unicode.EOF)
        for(i in seq){
            println(i)
        }
    }

}