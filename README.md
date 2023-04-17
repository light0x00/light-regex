[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) 
[![Maven 
Central](https://maven-badges.herokuapp.com/maven-central/io.github.light0x00/light-regex/badge.svg)
](https://repo1.maven.org/maven2/io/github/light0x00/light-regex/)[![Java support](https://img.shields.io/badge/Java-11+-green?logo=java&logoColor=white)](https://openjdk.java.net/)

[中文文档](./README.zh.md)

A regular expression engine implementation. In addition to the baisc functionality,  it exports the API to generate AST, NFA, DFA, which is the underlying principle of the implementation.

## Usage

```xml
<dependency>
    <groupId>io.github.light0x00</groupId>
    <artifactId>light-regex</artifactId>
    <version>0.0.1</version>
</dependency>
```

Step 1, Given a expression and a input string

```kotlin
val regex = LightRegex("to be[\\w,\\s]+to be")
val input = "to be, or not to be, that's the question"
```

Step 2, Match and get the reuslt returned, which is a `IntRange` contataining the start index and end index of the matched substring.

```kotlin
val result = regex.match(input)
```

Ouput the matching result above, we got the substring "to be, or not to be":

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

The following shows the APIs to do the things mentioned above, and the built-in APIs to generate the visualized representation (Plant UML based).


```kotlin
val ast = RegexSupport.parseAsAST("^(a|b)*abb$") //get AST
val nfa = RegexSupport.astToNFA(ast)	//convert AST to NFA
val dfa = RegexSupport.nfaToDFA(nfa)	//convert NFA to DFA

println(RegexVisualizer.nfaToPlantUML(nfa)) //ouput AST representation plantuml based 
println()
println(RegexVisualizer.dfaToPlantUML(dfa)) //output NFA representation plantuml based
println()
println(RegexVisualizer.astToPlantUML(ast))  //output DFA representation plantuml based
```
