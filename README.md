Webit Script  
====
<a target="_blank" href="http://shang.qq.com/wpa/qunwpa?idkey=7be9d8a59a8533b7c2837bdc22295b4b47c65384eda323971cf5f3b9943ad9db"><img border="0" src="http://pub.idqqimg.com/wpa/images/group.png" alt="QQ群: 302505483" title="QQ群: 302505483"></a>
<a target="_blank" href="https://bitdeli.com/free"><img border="0" src="https://d2weczhvl823v0.cloudfront.net/zqq90/webit-script/trend.png" alt="Bitdeli Badge" title="Bitdeli Badge"></a>
<a target="_blank" href="https://travis-ci.org/zqq90/webit-script"><img border="0" src="https://api.travis-ci.org/zqq90/webit-script.png" alt="Build Status" title="Build Status"></a>

This is a template engine, all writen in Java, **support Java 5+**.

## Maven

~~~~~xml
<dependency>
  <groupId>com.github.zqq90.webit-script</groupId>
  <artifactId>webit-script</artifactId>
  <version>1.5</version>
</dependency>
~~~~~

## Yeah, only one jar

> `webit-script-1.5.jar` *<300KB*

## How to use

~~~~~java
Engine engine = Engine.create("/webit-script-config.props", extraSettings);
Template template = engine.getTemplate("/your/template/path/filename.ext");
template.merge(parametersMap, outputStream); 
//template.merge(parametersMap, writer);
~~~~~

## Hello Webit Script

~~~~~javascript
Hello Webit Script!
<%
var books;
{
    for(book : books){
%>
${for.iter.index}.《${book.name}》 ￥${book.price}
<%
    }
}
{
    //this is a function
    var func = function(a, b){
        return a + b + arguments[3];
    };
    echo func("a", "b", "c");
    echo '\n';
}
{
    var map = {
        1: 1,
        "key2": "value2",
        3: 2 + 1
    };
    map[5] = 2 + 3;
    for(key, value : map){
        echo key + ":" +value + "\n";
    }
}
%>
~~~~~

> [More examples][tests]

## Official Support
+ Jodd Madvoc
+ JFinal
+ Spring MVC
+ Struts2
+ Servlet & Filter

> [Demo][mvc-demo]

## Performance(性能)

~~~~~
// Waiting update
~~~~~


## License
 
**Webit Script** is released under the BSD License. See the bundled [LICENSE file][license] for
details.

> **Webit Script** 依据 BSD许可证发布。详细请看 [LICENSE][license] 文件。

## Third-party Licenses

+ **Jodd-props**  under the BSD License. [License file][jodd_license]
+ **ASM**  under the BSD License.[License file][asm_license]

## Bug report

> [github-issue][new_issue_github]
> [OSC-issue][new_issue_osc]

[mvc-demo]: https://github.com/zqq90/webitscript-mvc-demo
[tests]: https://github.com/zqq90/webit-script/tree/master/webit-script/src/test/resources/webit/script/test/tmpls

[new_issue_github]: https://github.com/zqq90/webit-script/issues/new
[new_issue_osc]: http://git.oschina.net/zqq90/webit-script/issues/new

[license]: https://github.com/zqq90/webit-script/blob/master/LICENSE
[jodd_license]: http://jodd.org/license.html
[asm_license]: http://asm.ow2.org/license.html

