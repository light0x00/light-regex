[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) 
[![Maven 
Central](https://maven-badges.herokuapp.com/maven-central/io.github.light0x00/light-regex/badge.svg)
](https://repo1.maven.org/maven2/io/github/light0x00/light-regex/)[![Java support](https://img.shields.io/badge/Java-11+-green?logo=java&logoColor=white)](https://openjdk.java.net/)

一个正则引擎实现. 在基本的功能之外,还对外提供了底层的 API, 用于生成 AST, NFA, DFA.


## Usage

```xml
<dependency>
    <groupId>io.github.light0x00</groupId>
    <artifactId>light-regex</artifactId>
    <version>0.0.1</version>
</dependency>
```

给定一个表达式 `to be[\\w,\\s]+to be` 和一个字符串 "to be, or not to be, that's the question"

```kotlin
val regex = LightRegex("to be[\\w,\\s]+to be")
val input = "to be, or not to be, that's the question"
```

匹配并获得一个 `IntRange` 类型的返回结果, 表示匹配到的子串的起始索引和结束索引

```kotlin
val result = regex.match(input)
```

输出匹配到的子串,得到 "to be, or not to be"

```kotlin
println(input.substring(result.start, result.endInclusive + 1))
/*
=========输出=========
Matched
to be, or not to be
*/
```

## 特性

### 助记符

- \d 代表 `[0-9]`
- \w 代表 `[a-zA-Z0-9]`
- \s 	表示空格

下面的例子匹配一个或多个数字,或字母,或空格

```kotlin
val regex = LightRegex("[\\w\\s]+")
```

### 重复操作符

- `?`	重复0次或1次
- `*` 重复0次或多次
- `+`, 重复1次或多次
- `{m,n}` 重复至少 `m` 次 , 至多 `n` 次
- `{m}` repeat exactly `m` times
- `{m,}` repeat `m` or more times

The following match a string containing 2 to 4  "a" or "b"

下面的例子匹配一个由 2 到 4 个 "a" 或 "b" 构成的字符串:

```kotlin
val regex = LightRegex("(a|b){2,4}$")

println(regex.match("aa")!=null) //true
println(regex.match("ab")!=null) //true
println(regex.match("abab")!=null) //true

println(regex.match("aaaaa")!=null) //false
println(regex.match("bbbbb")!=null) //false
println(regex.match("ababa")!=null) //false
```

### 范围匹配

支持字面量范围 `[a-d]` , `[abcd]` 的范围匹配, 也支持直接基于 unicode 码点(code point) 的范围匹配, 写作 `[\u{0x0000}-\u{0xFFFF}]`

下面的例子匹配输入字符串中的所有字母、数字、空格(也即是去掉了 emoji):

```kotlin
val str = "happy😄 anger😡 sorrow😞 joy😇"

val regex = LightRegex("[a-zA-Z0-9\\s]") //也可以写作 [\w\s]
val matches = regex.matchAll(str)

for (m in matches) {
	print(str.substring(m.start, m.endInclusive + 1))
}
/*
=========输出=========
happy anger sorrow joy
*/
```

下面的例子匹配出所有的 [Emoticons 字符](https://en.wikipedia.org/wiki/Emoticons_(Unicode_block)):

```kotlin
val str = "happy😄 anger😡 sorrow😞 joy😇";

val regex = LightRegex("[\\u{1F600}-\\u{1F644}]")
val matches = regex.matchAll(str)

for (m in matches) {
	println(str.substring(m.start, m.endInclusive + 1))
}
/*
=========输出=========
😄
😡
😞
😇
*/
```

### 锚点

- `^` 表示从输入序列的第一个字符开始匹配
- `$` 表示匹配到输入序列的最后一个字符

## 底层原理

给定一个正则表达式 `(a|b)*abb$`, 它首先被解析为抽象语法树,如下所示:


![alternative text](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/light0x00/light-regex/master/doc/ast.plantuml)

随后,为每个叶子节点确定 first 集, follow 集, 用编译原理的属术语, AST 的叶子节点叫终结符(Terminal),下图中我们用数字标记每一个非终结符, 它们都对应一个 NFA 状态 (后文将提到).

![alternative text](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/light0x00/light-regex/master/doc/ast_with_first_follow.plantuml)

根据 First 集、Follow 集, 我们已经可以知道, 给定一个状态, 输入什么字符可以到达这个状态, 输入什么符号可以从这个状态转换到下一个状态. 所以,得到如下所示的 NFA

![alternative text](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/light0x00/light-regex/master/doc/nfa.plantuml)

最后,我们使用子集构造法,将 NFA 转为 DFA.

![alternative text](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/light0x00/light-regex/master/doc/dfa.plantuml)

## 底层 API

下面的代码展示了如何得到 AST 、NFA 、DFA, 以及转为 Plant UML 自动机.

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
