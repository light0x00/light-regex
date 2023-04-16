package io.github.light0x00.lightregex.common

interface UTF16Iterator : Iterator<Int> {
    val currentUnitIndex: Int
    val currentCodePointIndex: Int
}

/**
 * @param str 要迭代的字符串
 * @param fromUnitIndex 迭代起始位置,UTF-16 的单元索引,对应字符串的索引
 */
fun utf16Iterator(str: String, fromUnitIndex: Int = 0): UTF16Iterator {
    var unitIndex = fromUnitIndex

    /**
     * Unicode 码点索引
     */
    var codePointIndex = 0;

    return object : UTF16Iterator {
        override val currentUnitIndex: Int
            get() = unitIndex
        override val currentCodePointIndex: Int
            get() = codePointIndex

        override fun hasNext(): Boolean {
            return unitIndex < str.length
        }

        override fun next(): Int {
            val codePoint = str.codePointAt(unitIndex)
            if (codePoint > 0xFFFF) {
                unitIndex += 2;
            } else {
                unitIndex++
            }
            codePointIndex++
            return codePoint
        }

    }
}

fun utf16SequenceWithEOF(str: String, fromUnitIndex: Int = 0) = sequence<Triple<Int, IntRange, Int>> {
    var currentUnitIdx = fromUnitIndex

    /**
     * Unicode 码点索引
     */
    /**
     * Unicode 码点索引
     */
    var codePointIndex = 0
    while (currentUnitIdx < str.length) {
        val codePoint = str.codePointAt(currentUnitIdx)
        val unitNum = if (codePoint > 0xFFFF) 2 else 1

        yield(Triple(codePoint, IntRange(currentUnitIdx, currentUnitIdx + unitNum - 1), codePointIndex))

        currentUnitIdx += unitNum
        codePointIndex++
    }
    yield(Triple(Unicode.EOF, IntRange(currentUnitIdx, currentUnitIdx), codePointIndex))
}