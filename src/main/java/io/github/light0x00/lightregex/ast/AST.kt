package io.github.light0x00.lightregex.ast

import io.github.light0x00.lightregex.automata.NState
import io.github.light0x00.lightregex.automata.NTransition
import io.github.light0x00.lightregex.common.ITraversable
import io.github.light0x00.lightregex.common.ICloneable

abstract class AST(override vararg val children: AST) : ITraversable<AST>, ICloneable<AST> {
    var id = -1;
    var state: NState? = null
    var firstSet: Set<NTransition> = emptySet()
    var followSet: Set<NTransition> = emptySet()
//    var indirectFollowSet: Set<Transition> = emptySet()
    var nullable: Boolean = false
}