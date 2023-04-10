package io.github.light0x00.lightregex

/**
 * @author light
 * @since 2023/4/7
 */
class DFA {
    private val states: MutableList<DFAState> = ArrayList()
    private val tranTable: MutableMap<DFAState, List<DFATransition>> = HashMap()

    fun addTrans(state: DFAState, trans: List<DFATransition>) {
        states.add(state)
        tranTable[state] = trans
    }

    fun getTrans(state: DFAState): List<DFATransition>? {
        return tranTable[state]
    }

//    fun next(state: DFAState, char: Char) : DFAState?{
//        val trans = tranTable.get(state) ?: return null
//        for(t in trans){
//            if(t.input.match(char))
//                return t.to
//        }
//        return null
//    }
}

class DFAState(val id: Int, val nStates: List<NFAState>) {

}

class DFATransition(val input: Input, val to: DFAState) {

}