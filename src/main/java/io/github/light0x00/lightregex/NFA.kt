package io.github.light0x00.lightregex

/**
 * @author light
 * @since 2023/4/6
 */
val START_STATE = NFAState(Int.MIN_VALUE)
val ACCEPT_STATE = NFAState(Int.MAX_VALUE)

class NFA(startTran: List<Transition>) {
    val states = HashSet<NFAState>()
    val transitionTable = HashMap<NFAState, List<Transition>>()

    init {
        states.add(START_STATE)
        transitionTable[START_STATE] = startTran
        states.add(ACCEPT_STATE)
    }

    fun addState(state: NFAState, trans: List<Transition>) {
        states.add(state)
        transitionTable[state] = trans
    }

    fun getTransitions(from: NFAState, input: String): List<Transition> {
        val trans = transitionTable.get(from) ?: throw LightRegexException("Unknown state:$from")
        return trans.filter {
            when (it.input) {
                is LiteralInput -> {
                    it.input.literal == input
                }
                is AnyInput -> {
                    true
                }
                else -> throw LightRegexException("Unknown input type:" + it.input.javaClass)
            }
        }
    }

}

data class NFAState(val id: Int) {
    override fun toString(): String {
        return id.toString()
    }
}

interface Input

class AnyInput() : Input {
    override fun toString(): String {
        return "Any"
    }
}

class EOFInput() : Input {
    override fun toString(): String {
        return "EOF"
    }
}

data class LiteralInput(val literal: String) : Input {
    override fun toString(): String {
        return literal
    }
}

data class CharSequenceInput(val chars: CharArray) : Input
data class CharRangeInput(val from: Char, val to: Char) : Input

data class Transition(val input: Input, val to: NFAState) {
    override fun toString(): String = input.toString() + "→" + to.id
}