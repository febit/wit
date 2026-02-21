Febit Wit
====

 [![BSD License](http://img.shields.io/badge/license-BSD-blue.svg)](https://github.com/febit/wit/blob/master/LICENSE)
 [![Build Status](https://api.travis-ci.org/febit/wit.png)](https://travis-ci.org/febit/wit)

This is a script wit, all written in Java, **support Java 17+**.

## How to use

+ Maven: 

```xml
<dependency>
    <groupId>org.febit.wit</groupId>
    <artifactId>wit-core</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

+ or Gradle

```
implementation 'org.febit.wit:wit-core:3.0.0-SNAPSHOT'
```

+ Config Wit engine, load script and eval:

```java
Wit wit = Wit.builder()
    .loader(Loaders.fileSystem() ... )
    .build();
Script script = wit.script("/demo.wit");
script.eval(params, out);
```

## Hello Wit

```js
Hello Wit!
<%
var books
{
    for(book : books){
%>
${for.iter.index}.《${book.name}》 ￥${book.price}
<%
    }
}
{
    var func = function(a, b){
        return a + b + arguments[3]
    }
    echo func("a", "b", "c")
}
{
    var map = {
        books,
        1: 1,
        "key2": "value2",
        3: 2 + 1
    }
    map[5] = 2 + 3
    map.~put("6", 2*3)
    for(key, value : map){
        echo key + ":" +value + "\n"
    }
}
%>
```

> [More examples][tests]

## License
 
**Febit Wit** is released under the BSD License. See the bundled [LICENSE file][license] for
details.

## Third-party Licenses

+ **ASM** under the BSD License.[License file][asm_license]

## Bug report

> [github-issue][new_issue_github]

[tests]: https://github.com/febit/wit/tree/master/wit-core/src/test/resources/org/febit/wit/test/tmpls

[new_issue_github]: https://github.com/febit/wit/issues/new

[license]: https://github.com/febit/wit/blob/master/LICENSE
[asm_license]: http://asm.ow2.org/license.html

