package io.github.light0x00.lightregex.automata

import io.github.light0x00.lightregex.common.Unicode

/**
 * @author light
 * @since 2023/4/12
 */
interface IInput {
    fun match(char: Int): Boolean
}

class EOFInput : IInput {
    override fun match(char: Int): Boolean {
        return char == Unicode.EOF
    }

    override fun toString(): String {
        return "EOF"
    }
}

data class SingleInput(val char: Int) : IInput {
    override fun match(char: Int): Boolean {
        return this.char == char
    }

    override fun toString(): String {
        return if (char == Unicode.EOF) "EOF" else Character.toString(char)
    }
}

data class RangeInput(val range: IIntRange) : IInput {

    override fun match(char: Int): Boolean {
        return char in range
    }

    override fun toString(): String {
        return range.toString(UNICODE_INT_RANGE_TO_STRING)
    }

}