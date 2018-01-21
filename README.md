Febit Wit
====

 [![BSD License](http://img.shields.io/badge/license-BSD-blue.svg)](https://github.com/febit/wit/blob/master/LICENSE)
 [![Build Status](https://api.travis-ci.org/febit/wit.png)](https://travis-ci.org/febit/wit)
 [![QQ Group: 302505483](http://pub.idqqimg.com/wpa/images/group.png)](http://shang.qq.com/wpa/qunwpa?idkey=7be9d8a59a8533b7c2837bdc22295b4b47c65384eda323971cf5f3b9943ad9db)

This is a template engine, all writen in Java, **support Java 8+**.

## How to use

+ Maven: 

```xml
<dependency>
    <groupId>org.febit.wit</groupId>
    <artifactId>wit-core</artifactId>
    <version>2.5.0-beta</version>
</dependency>
```

+ or Gradle

```
compile 'org.febit.wit:wit-core:2.5.0-beta'
```

+ demo:

```java
Engine engine = Engine.create();
Template template = engine.getTemplate("/demo.wit");
template.merge(params, out);
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

## Official Support

+ Jodd Madvoc
+ JFinal
+ Spring MVC
+ Struts2
+ Servlet & Filter

> [Demo][mvc-demo]


## License
 
**Febit Wit** is released under the BSD License. See the bundled [LICENSE file][license] for
details.

## Third-party Licenses

+ **ASM**  under the BSD License.[License file][asm_license]

## Bug report

> [github-issue][new_issue_github]

[mvc-demo]: https://github.com/febit/wit-mvc-demo
[tests]: https://github.com/febit/wit/tree/master/wit-core/src/test/resources/org/febit/wit/test/tmpls

[new_issue_github]: https://github.com/febit/wit/issues/new

[license]: https://github.com/febit/wit/blob/master/LICENSE
[jodd_license]: http://jodd.org/license.html
[asm_license]: http://asm.ow2.org/license.html

