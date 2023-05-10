[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) 
[![Maven 
Central](https://maven-badges.herokuapp.com/maven-central/io.github.light0x00/light-regex/badge.svg)
](https://repo1.maven.org/maven2/io/github/light0x00/light-regex/)[![Java support](https://img.shields.io/badge/Java-11+-green?logo=java&logoColor=white)](https://openjdk.java.net/)

English | [中文](./README.zh.md)

A regular expression engine implementation base on [Subset Construction Method](https://en.wikipedia.org/wiki/Powerset_construction). In addition to the baisc functionality,  it exports the API to generate AST, NFA, DFA, which is the underlying principle of the implementation.

## Usage

```xml
<dependency>
    <groupId>io.github.light0x00</groupId>
    <artifactId>light-regex</artifactId>
    <version>0.0.2</version>
</dependency>
```

Step 1, Given an expression and an input string

```kotlin
val regex = LightRegex("to be[\\w,\\s]+to be")
val input = "to be, or not to be, that's the question"
```

Step 2, Match and get the reuslt returned, which is an `IntRange` containing the start index and end index of the matched substring.

```kotlin
val result = regex.match(input)
```

Output the matching result above, we got the substring "to be, or not to be":

```kotlin
println(input.substring(result.start, result.endInclusive + 1))
/*
=========output=========
Matched
to be, or not to be
*/
```

## Features

### Shorthand Character Classes

Frequently-used range matching, such as decimal and word, can be represented by shorhand notations `\d` ,`\w` respectively . Additionally, space can be represented `\s`.

The following matches one or more words or space.

```kotlin
val regex = LightRegex("[\\w\\s]+")
```

### Repetition operator

To specify the repeatition times for a character or a character range like `[a-z]`, or a parentheses expression like `(a|b|[0-9])` , use the following operator :

- `?`	repeat once or zero times
- `*` repeat zero or more times
- `+`, repeat once or more
- `{m,n}` repeat at least `m` , and at most `n` times 
- `{m}` repeat exactly `m` times
- `{m,}` repeat `m` or more times

The following match a string containing 2 to 4  "a" or "b" :

```kotlin
val regex = LightRegex("(a|b){2,4}$")

println(regex.match("aa")!=null) //true
println(regex.match("ab")!=null) //true
println(regex.match("abab")!=null) //true

println(regex.match("aaaaa")!=null) //false
println(regex.match("bbbbb")!=null) //false
println(regex.match("ababa")!=null) //false
```

### Range matching

Range matching like `[a-d]` and `[abcd]` are supported. The former means to match a character in the  range of Unicode code point between `0x61-0x64` , and the latter means to match a character that is one of the letters `a`,`b`,`c`,`d`

To match word or number or space:

```kotlin
val str = "happy😄 anger😡 sorrow😞 joy😇"

val regex = LightRegex("[a-zA-Z0-9\\s]") //can also be written in [\w\s]
val matches = regex.matchAll(str)

for (m in matches) {
	print(str.substring(m.start, m.endInclusive + 1))
}
/*
=========output=========
happy anger sorrow joy
*/
```

Also, the unicode range matching is supported, written as `[\u{0x0000}-\u{0xFFFF}]`.  

The folowing matches a Unicode block named [Emoticons](https://en.wikipedia.org/wiki/Emoticons_(Unicode_block)):

```kotlin
val str = "happy😄 anger😡 sorrow😞 joy😇";

val regex = LightRegex("[\\u{1F600}-\\u{1F644}]")
val matches = regex.matchAll(str)

for (m in matches) {
	println(str.substring(m.start, m.endInclusive + 1))
}
/*
=========output=========
😄
😡
😞
😇
*/
```

### Anchors

To specify matching from the start or to the end of a input sequecne, use `^`, `$` respectively.

The following matches a sentence end of "language"

```kotlin
val regex = LightRegex("[\\w\\s]+language$")

//match 1
val str = "Kotlin is an elegant language"
val range = regex.match(str, eager = true)

if (range != null)
	println("Match found: "+str.substring(range.first, range.endInclusive + 1))
else
	println("Match not found")
/*
=========output=========
Match found: Kotlin is an elegant language
*/


//match 2
val str2 = "Kotlin is an elegant language,but lack of Union Type like Typescript"
val range2 = regex.match(str2, eager = true)

if (range2 != null)
	println("Match found:"+str2.substring(range2.first, range2.endInclusive + 1))
else
	println("Match not found")

/*
=========output=========
Match not found
*/
```


## Principle under the hood

Given a pattern `(a|b)*abb$`, it firstly be parsed as a AST(Abstract syntax tree), as shown below:

![alternative text](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/light0x00/light-regex/master/doc/ast.plantuml)

Whereafter, determine the first set and follow set for each leaf node. In term of Compiler Principle, the leaf nodes of AST named `Terminal`. Each terminal (leaf node) is a NFA state. In the following diagram we mark each terminal with a interger identifier, which corrsponeding to a NFA state the latter will mention.

![alternative text](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/light0x00/light-regex/master/doc/ast_with_first_follow.plantuml)

According to the first set, follow set, we have known that given a state, what input can reach it, and what input on it can reach the next state. Therefore, we got the following NFA:

![alternative text](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/light0x00/light-regex/master/doc/nfa.plantuml)

In the end, we use the well-known Subset Construction Algorithem ,to convert the NFA to the DFA. As shown below, each DFA state contains a set of NFA states.

![alternative text](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/light0x00/light-regex/master/doc/dfa.plantuml)

## Underlying API

The following shows the built-in APIs to do the things mentioned above, parsing AST, generating NFA, NFA to DFA , and the APIs to generate the visualized representation (Plant UML based).

```kotlin
val ast = RegexSupport.parseAsAST("^(a|b)*abb$")
val nfa = RegexSupport.astToNFA(ast)
val dfa = RegexSupport.nfaToDFA(nfa)

println("===================AST===================")
println(RegexVisualizer.astToPlantUML(ast))
println("===================NFA===================")
println(RegexVisualizer.nfaToPlantUML(nfa))
println("===================DFA===================")
println(RegexVisualizer.dfaToPlantUML(dfa))
println("===================AST with  first/follow set===================")
println(RegexVisualizer.astToPlantUML(ast))
```

```
===================AST===================
hide empty description
title AST of (((((a|b)*)a)b)b)
state 0 as "RegExpr"
0: (((((a|b)*)a)b)b)
0: first={ a→1 , b→2 , a→3 }
state 1 as "AndExpr"
1: (((((a|b)*)a)b)b)
1: first={ a→1 , b→2 , a→3 }
1: follow={ EOF→Accept }
state 2 as "AndExpr"
2: ((((a|b)*)a)b)
2: first={ a→1 , b→2 , a→3 }
2: follow={ b→5 }
state 3 as "AndExpr"
3: (((a|b)*)a)
3: first={ a→1 , b→2 , a→3 }
3: follow={ b→4 }
state 4 as "UnaryExpr"
4: ((a|b)*)
4: first={ a→1 , b→2 }
4: follow={ a→3 }
state 5 as "OrExpr"
5: (a|b)
5: first={ a→1 , b→2 }
5: follow={ a→3 , a→1 , b→2 }
state 6 as "<1>SingleToken"
6: a
6: first={ a→1 }
6: follow={ a→3 , a→1 , b→2 }
state 7 as "<2>SingleToken"
7: b
7: first={ b→2 }
7: follow={ a→3 , a→1 , b→2 }
state 8 as "<3>SingleToken"
8: a
8: first={ a→3 }
8: follow={ b→4 }
state 9 as "<4>SingleToken"
9: b
9: first={ b→4 }
9: follow={ b→5 }
state 10 as "<5>SingleToken"
10: b
10: first={ b→5 }
10: follow={ EOF→Accept }
state 11 as "Accept"
11: 
11: first={ EOF→Accept }
5-down->6
5-down->7
4-down->5
3-down->4
3-down->8
2-down->3
2-down->9
1-down->2
1-down->10
0-down->1
0-down->11

===================NFA===================
hide empty description
state 1
state 2
state 3
state 4
state 5
[*]-down->1 : a
[*]-down->2 : b
[*]-down->3 : a
1-down->3 : a
1-down->1 : a
1-down->2 : b
2-down->3 : a
2-down->1 : a
2-down->2 : b
3-down->4 : b
4-down->5 : b
5-down->[*] : EOF
===================DFA===================
hide empty description
state 5: Accept
state 2: 2
state 1: 1,3
state 3: 2,4
state 4: 2,5
[*]-down-> 1 : a 
[*]-down-> 2 : b 
2-down-> 1 : a 
2-down-> 2 : b 
1-down-> 1 : a 
1-down-> 3 : b 
3-down-> 1 : a 
3-down-> 4 : b 
4-down-> 1 : a 
4-down-> 2 : b 
4-down-> 5 : EOF 
===================AST with  first/follow set===================
hide empty description
title AST of (((((a|b)*)a)b)b)
state 0 as "RegExpr"
0: (((((a|b)*)a)b)b)
0: first={ a→1 , b→2 , a→3 }
state 1 as "AndExpr"
1: (((((a|b)*)a)b)b)
1: first={ a→1 , b→2 , a→3 }
1: follow={ EOF→Accept }
state 2 as "AndExpr"
2: ((((a|b)*)a)b)
2: first={ a→1 , b→2 , a→3 }
2: follow={ b→5 }
state 3 as "AndExpr"
3: (((a|b)*)a)
3: first={ a→1 , b→2 , a→3 }
3: follow={ b→4 }
state 4 as "UnaryExpr"
4: ((a|b)*)
4: first={ a→1 , b→2 }
4: follow={ a→3 }
state 5 as "OrExpr"
5: (a|b)
5: first={ a→1 , b→2 }
5: follow={ a→3 , a→1 , b→2 }
state 6 as "<1>SingleToken"
6: a
6: first={ a→1 }
6: follow={ a→3 , a→1 , b→2 }
state 7 as "<2>SingleToken"
7: b
7: first={ b→2 }
7: follow={ a→3 , a→1 , b→2 }
state 8 as "<3>SingleToken"
8: a
8: first={ a→3 }
8: follow={ b→4 }
state 9 as "<4>SingleToken"
9: b
9: first={ b→4 }
9: follow={ b→5 }
state 10 as "<5>SingleToken"
10: b
10: first={ b→5 }
10: follow={ EOF→Accept }
state 11 as "Accept"
11: 
11: first={ EOF→Accept }
5-down->6
5-down->7
4-down->5
3-down->4
3-down->8
2-down->3
2-down->9
1-down->2
1-down->10
0-down->1
0-down->11
```
