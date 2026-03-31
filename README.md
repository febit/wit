Febit Wit
====

[![Apache-2.0 License](https://img.shields.io/badge/license-apache-blue.svg)][license]

A script/template engine implemented in pure Java (Java 17+).

## How to use

+ Maven

```xml
<dependency>
    <groupId>org.febit.wit</groupId>
    <artifactId>wit-core</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

+ Gradle

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
%>Hello Wit!<%
var books;
{
    for(book : books) {
%>
${for.iter.index + 1}.《${book.name}》 ￥${book.price}
<%
    }
}
{
    var func = function(a, b) {
        return a + b + arguments[3];
    };
    echo func("a", "b", "c");
}
{
    var map = {
        books,
        1: 1,
        "key2": "value2",
        3: 2 + 1
    };
    map[5] = 2 + 3;
    map.~put("6", 2*3);
    
    for(key, value : map) {
        echo key + ":" +value + "\n";
    }
}
%>
```

> [More examples][tests]

## License

**Febit Wit 3** is released under the Apache-2.0 license. See the bundled [LICENSE file][license] for
details.

## Third-party Licenses

+ **ASM** under the BSD License.[License file][asm_license]

## Bug report

> [github-issue][new_issue_github]

[tests]: https://github.com/febit/wit/tree/main/wit-core/src/test/resources/org/febit/wit/test/tmpls

[new_issue_github]: https://github.com/febit/wit/issues/new

[license]: https://github.com/febit/wit/blob/main/LICENSE.txt

[asm_license]: https://asm.ow2.io/license.html

