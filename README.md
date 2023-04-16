[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) 
[![Maven 
Central](https://maven-badges.herokuapp.com/maven-central/io.github.light0x00/light-regex/badge.svg)
](https://repo1.maven.org/maven2/io/github/light0x00/light-regex/)[![Java support](https://img.shields.io/badge/Java-11+-green?logo=java&logoColor=white)](https://openjdk.java.net/)

## Usages

```xml
<dependency>
		<groupId>io.github.light0x00</groupId>
		<artifactId>light-regex</artifactId>
		<version>0.0.1-beta</version>
</dependency>
```



```java
var regex = new LightRegex("(a|b)*abb");
```



#### Repetition operator

? + {m,n} {m} {m,}

#### Range matching

to match word or number or space:

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

to match a range of unicode codepoint:

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



