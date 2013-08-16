Webit Script
====

*this is a new project, lot of things need to be done, stay tuned!*

**Webit Script** is a template-like script and engine, all writen with Java.

It's grammar is very like Javascript, and with `<% %>` `${ }` like in JSP


## How to use(如何使用)

### Maven

~~~~~
    <repositories>
        <repository>
            <id>webit-script</id>
            <name>Webit script</name>
            <url>http://zqq90.github.io/maven/</url>
        </repository>
    </repositories>
    <dependencies>
        ...
        <dependency>
            <groupId>com.github.zqq90.webit-script</groupId>
            <artifactId>webit-script</artifactId>
            <version>0.8.0-SNAPSHOT</version>
        </dependency>
        ...
    </dependencies>
~~~~~

### Add jar

 + `webit-script-[version].jar`
  
 + `jodd-[version].jar`

### Download: [Jar][jar]   [Javadoc][doc]   [Sources][sources]
*current version 0.8.0-SNAPSHOT*


### Code like this
~~~~~
    Engine engine = Engine.getEngine("/webit-script-config.props", extraSettingsMap);
    Template template = engine.getTemplate("/your/template/path/filename.ext");
    ...
    template.merge(parametersMap, outputStream); 
    //template.merge(parametersMap, writer);
~~~~~

## Config(配置)

+ 配置文件格式: Use Jodd-props, see:[Jodd Props doc][url_props_doc]
  `Tips: Java-Properties also works`
+ 匹配支持  "/webit-script-*.props"
+ 多文件支持 "/webit-script-config1.props,/webit-script-config2.props"
+ 可选额外参数: extraSettingsMap
+ 默认配置: [webitl-default.props] [default_config]

## Grammar(语法)

### Hello word
~~~~~
    Hello ${"world"}
~~~~~

~~~~~
<%
var books; //List
{
    for(book in books){
%>${for.iter.index}. ${book.name}:${book.price} ${for.iter.hasNext?null,"\n\n"}
<%
    }
}
{
    //this is a function
    var func =function(a, b){
        return a + b + arguments[3];
    };
    echo func("a", "b", "c");
    echo '\n';
}
{

    @import java.lang.System;
    var now = native System.currentTimeMillis();
    
    echo "Now: ";
    echo now();
    echo '\n';
}
{
    @import java.util.Map;
    @import java.util.List;
    @import java.util.ArrayList;

    var map_put = native Map.put(Object,Object);

    var map = {
        1:1,
        "key2":"value2",
        3: 2+1
    };
    
    map@map_put(4,4);
    map_put(map,5,5);
    
    for(key, value : map){
        echo key + ":" +value + "\n";
    }

    //
}
%>
~~~~~

更多实例可见:[测试模板] [tests]

### 结构

+ 脚本必须都放在`<% %>` 内. `<% .code inside.. %> plain text outside`
+ 替换占位符`${}` 只能允许带返回值的单个表达式,只能在脚本块`<% %>` 以外使用.`${ .. expression .. }` 
+ 转义字符 `\\` to `\` , `\<%` to `<%`, `\${` to `${`
+ Node: 只有紧挨 `<%` `${` 的 `\` 才需要转义 , 
+ 转义窍门: 偶数个 `\` 会 打印 一半数量的`\` 并进入代码段, 
            奇数 会 打印 (count-1)/2 个 `\` 然后打印被转移的符号。
            

### 注释

+ `//` 单行注释
+ `/* */` 块注释


### 关键字
~~~~~
    var  super  this
    if  else
    switch  case  default
    for  do  while  break  continue 
    function  return
    import  include  echo
    native  new  @import
~~~~~

### 保留的关键字
~~~~~
    static  instanceof  class  const  final
    throw  try  catch  finally
~~~~~

### 操作符
*与Java 保持一致，顺序按优先级从高到低*
~~~~~
[]  .  ()  @ (新增)
=> (新增,重定向输出)
!  ~  ++  --  – (取负)
*  /  %
+  -
<<  >>  >>>
<  <=  >  >=
^
|
&&
||
?: (条件运算符)
=  +=  -=  *=  /=  %=  ^=  <<=  >>=  >>>=
~~~~~

### 语句
+ 结尾分号不能省略

### 作用域 (代码段) {}
+ 作用域引用上层变量
+ 本层作用域变量不会影响上层
+ **模板传入的变量仅在该作用域查找同名变量并赋值**
*1. 调用模板传入的变量; 2.import 返回的变量*

*于声明 Map的 区别 在于 1.代码段内部为语句组成 2.Map 是 表达式对*

### 变量
#### 变量声明 var
~~~~~
    var a；
    var a, b,
          c=0, d="d" ;
~~~~~

#### 变量名规则
+ 先声明 后使用，所有变量 必须全部声明
+ 
+ 对大小写敏感
+ 不能使用关键字
+ 仅能包含 0-9a-zA-Z
+ 特殊变量名: 
++ super. 用于 取得指定上层且仅该层作用域的变量, 可嵌套`super.super.a`
++ this. 用于 取得本层且仅本层作用域的变量, 可嵌套`this.this.a`
++ for.iter 用于 最近一层for循环的 迭代状态对象, 可使用"super" "this" 限定作用域


## Requirements(依赖)

+ `Jodd-core`
+ `Jodd-bean`
+ `Jodd-props`
+ `Jodd-petite`


## Contributing(贡献)

+ 国际化: 联系我
+ code & doc: fork -> pull requests
+ idea & bug report: [github-issue] [new_issue]
+ donate:


## License(许可证)
 
**Webit Script** is released under the BSD License. See the bundled LICENSE file for
details.
**Webit Script** 依据 BSD许可证发布。详细请看捆绑的 LICENSE 文件。

[jar]: http://zqq90.github.io/maven/com/github/zqq90/webit-script/webit-script-0.8.0-SNAPSHOT.jar
[sources]: http://zqq90.github.io/maven/com/github/zqq90/webit-script/webit-script-0.8.0-SNAPSHOT-sources.jar
[doc]: http://zqq90.github.io/maven/com/github/zqq90/webit-script/webit-script-0.8.0-SNAPSHOT-javadoc.jar
[url_props_doc]: http://jodd.org/doc/props.html
[tests]: https://github.com/zqq90/webit-script/tree/master/webit-script/src/test/resources/webit/script/test/tmpls
[default_config]: https://github.com/zqq90/webit-script/blob/master/webit-script/src/main/resources/webitl-default.props
[new_issue]: https://github.com/zqq90/webit-script/issues/new

