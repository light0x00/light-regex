package io.github.light0x00.lightregex

/**
 * @author light
 * @since 2023/4/6
 */
val START_STATE = NFAState(Int.MIN_VALUE)
val ACCEPT_STATE = NFAState(Int.MAX_VALUE)

class NFA() {
    val states = HashSet<NFAState>()
    val tranTable = HashMap<NFAState, List<Transition>>()

    constructor(startTran: List<Transition>) : this() {
        tranTable[START_STATE] = startTran
    }

    fun addTrans(state: NFAState, trans: List<Transition>) {
        states.add(state)
        tranTable[state] = trans
    }

//    fun getTrans(state: NFAState, input: Char): List<Transition> {
//        val trans = tranTable.get(state) ?: throw LightRegexException("Unknown state:$state")
//        return trans.filter {
//            it.input.match(input)
//        }
//    }

}

data class NFAState(val id: Int) {
    override fun toString(): String {
        return id.toString()
    }
}

interface Input {
    fun match(char: Int): Boolean
}

class AnyInput : Input {
    override fun match(char: Int): Boolean {
        return true
    }

    override fun toString(): String {
        return "Any"
    }
}

class EOFInput : Input {
    override fun match(char: Int): Boolean {
        return char == Unicode.EOF
    }

    override fun toString(): String {
        return "EOF"
    }
}

data class LiteralInput(val char: Int) : Input {
    override fun match(char: Int): Boolean {
        return this.char == char
    }

    override fun toString(): String {
        return Character.toString(char)
    }
}

data class CharSetInput(val chars: IntArray) : Input {
    override fun match(char: Int): Boolean {
        return chars.contains(char)
    }
}

data class CharRangeInput(val from: Int, val to: Int) : Input {
    override fun match(char: Int): Boolean {
        return char in from..to
    }
}

open class Transition(val input: Input, val to: NFAState) {
    override fun toString(): String = input.toString() + "→" + if (to == ACCEPT_STATE) "Accept" else to.id
}

class TimesRangeTransition(
    input: Input,
    to: NFAState,
    val timesMin: Int,
    val timesMax: Int? = null,
    val infinite: Boolean = false
) :
    Transition(input, to) {

    constructor(
        tran: Transition,
        timesMin: Int,
        timesMax: Int? = null,
        infinite: Boolean = false
    ) : this(tran.input, tran.to, timesMin, timesMax, infinite)

    override fun toString(): String {
        val times = if (infinite) ",∞" else if (timesMax == null) "$timesMin" else "$timesMin,$timesMax"
        return super.toString() + "($times)"
    }
}