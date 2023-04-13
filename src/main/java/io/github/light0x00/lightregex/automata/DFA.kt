package io.github.light0x00.lightregex.automata

val D_START_STATE = DFAState(0, setOf(START_STATE))

/**
 * @author light
 * @since 2023/4/7
 */
class DFA {
    val statesMap = HashMap<DFAState, DFAState>()
    val tranTable: MutableMap<DFAState, List<DTransition>> = HashMap()
    val states
        get() = statesMap.values

    fun addTrans(state: DFAState, trans: List<DTransition>) {
        statesMap[state] = state
        tranTable[state] = trans
        statesMap.putAll(trans.map { it.to to it.to })
    }

}

class DFAState(var id: Int? = null, val nStates: Set<NState>) {

    constructor(nfaStates: Set<NState>) : this(null, nfaStates)

    override fun equals(other: Any?): Boolean {
        return other is DFAState && other.nStates == nStates
    }

    override fun hashCode(): Int {
        return nStates.hashCode()
    }

    override fun toString(): String {
        return "$id ${
            nStates.joinToString(
                transform = { it.toString() },
                prefix = "{",
                postfix = "}",
                separator = ","
            )
        }"
    }


}

class DTransition(val input: IInput, val toNStates: Set<NState>) {
    lateinit var to: DFAState

    override fun toString(): String {

        return "$input->(${if (this::to.isInitialized) to.toString() else toNStates.joinToString(separator = ",")})"
    }
}

