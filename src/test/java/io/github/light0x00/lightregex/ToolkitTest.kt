package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.common.ITraversable
import io.github.light0x00.lightregex.common.Unicode
import io.github.light0x00.lightregex.common.traversePostOrder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author light
 * @since 2023/3/29
 */
class ToolkitTest {

    @Test
    fun testUnicodeHexToString() {
        Unicode.unicodeHexToString("1F914").also {
            println(it)
            Assertions.assertEquals("🤔", it)
        }
        Unicode.unicodeHexToString("4E2D").also {
            println(it)
            Assertions.assertEquals("中", it)
        }
        Unicode.unicodeHexToString("56FD").also {
            println(it)
            Assertions.assertEquals("国", it)
        }
    }

    class Node(val data: Int, override vararg val children: Node) : ITraversable<Node> {

    }

    @Test
    fun testPostOrderTraverse() {
        val tree = Node(
            1,
            Node(2,
                Node(4),
                Node(5),
                Node(6),
            ),
            Node(3,Node(7)),
        )

        traversePostOrder(tree) {
            println(it.data)
        }
    }
}