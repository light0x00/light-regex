package io.github.light0x00.lightregex.automata

/**
 * @author light
 * @since 2023/4/6
 */
val START_STATE = NState(Int.MIN_VALUE)
val ACCEPT_STATE = NState(Int.MAX_VALUE)

class NFA() {
    val states = HashSet<NState>()
    val tranTable = HashMap<NState, List<NTransition>>()

    constructor(startTran: List<NTransition>) : this() {
        tranTable[START_STATE] = startTran
    }

    fun addTrans(state: NState, trans: List<NTransition>) {
        states.add(state)
        tranTable[state] = trans
    }

}

data class NState(val id: Int) {
    override fun toString(): String {
        return if (this == START_STATE) "Start" else if (this == ACCEPT_STATE) "Accept" else id.toString()
    }
}

class NTransition(val input: IInput, val to: NState) {
    override fun toString(): String = input.toString() + "→" + if (to == ACCEPT_STATE) "Accept" else to.id
}
