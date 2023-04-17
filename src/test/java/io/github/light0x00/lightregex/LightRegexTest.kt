package io.github.light0x00.lightregex

import org.junit.jupiter.api.Test

/**
 * @author light
 * @since 2023/4/14
 */
class LightRegexTest {

    @Test
    fun test() {
        val regex = LightRegex("(a|b)*abb")
        val result = regex.match("ababbabbabb", true)
        println(result)
    }

    @Test
    fun test2() {
        val regex = LightRegex("(\\w\\d\\s){3}")
//        val result = regex.match("a12A3",0, eager = true)
        val result = regex.match("a1 b1 c1 ")

        println(result)
    }

    @Test
    fun testEmoji() {
        //U+1F600..U+1F64F
        val regex = LightRegex("[\\u{1F600}-\\u{1F644}]")
        val str = "happy😄 anger😡 sorrow😞 joy😇";
        val matches = regex.matchAll(str)
        for (m in matches) {
            println(str.substring(m.start, m.endInclusive + 1))
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testRangeMatch() {
        //U+1F600..U+1F64F
//        val str = "LOL😄,sounds good👀,to plant a sakura tree🌸"

        val regex = LightRegex("[a-zA-Z0-9\\s]")
        val str = "happy😄 anger😡 sorrow😞 joy😇"
        val matches = regex.matchAll(str)

        for (range in matches) {
            print(str.substring(range.first, range.endInclusive + 1))
        }
    }

    @Test
    fun testMatchFromStart() {
        val str = "Kotlin is an elegant language"

        val regex = LightRegex("^Kotlin[\\w\\s]+")
        val range = regex.match(str, eager = true)

        if (range != null)
            println("Match found:" + str.substring(range.first, range.endInclusive + 1))
    }

    @Test
    fun testMatchToEnd() {

        val regex = LightRegex("[\\w\\s]+language$")

        //match 1
        val str = "Kotlin is an elegant language"
        val range = regex.match(str, eager = true)

        if (range != null)
            println("Match found: " + str.substring(range.first, range.endInclusive + 1))
        else
            println("Match not found")


        //match 2
        val str2 = "Kotlin is an elegant language,but lack of Union Type like Typescript"
        val range2 = regex.match(str2, eager = true)

        if (range2 != null)
            println("Match found:" + str2.substring(range2.first, range2.endInclusive + 1))
        else
            println("Match not found")
    }

    @Test
    fun usage() {
        val regex = LightRegex("to be[\\w,\\s]{1,}to be")

        val input = "to be, or not to be, that's the question"

        val range = regex.match(input)

        if (range != null) {
            println("Matched")
            println(input.substring(range.start, range.endInclusive + 1))
        } else {
            println("Not matched")
        }
    }

    @Test
    fun printAST_NFA_DFA(){
        val ast = RegexSupport.parseAsAST("^(a|b)*abb$")
        val nfa = RegexSupport.astToNFA(ast)
        val dfa = RegexSupport.nfaToDFA(nfa)

        println(RegexVisualizer.nfaToPlantUML(nfa))
        println()
        println(RegexVisualizer.dfaToPlantUML(dfa))
        println()
        println(RegexVisualizer.astToPlantUML(ast))
    }

    @Test
    fun testMNExpression(){
        val regex = LightRegex("(a|b){2,4}$")

        println(regex.match("aa")!=null) //true
        println(regex.match("ab")!=null) //true
        println(regex.match("abab")!=null) //true

        println(regex.match("aaaaa")!=null) //false
        println(regex.match("bbbbb")!=null) //false
        println(regex.match("ababa")!=null) //false
    }
}

