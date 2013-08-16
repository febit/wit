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

You can download Webit-Script here: [Jar][jar] [Javadoc][doc]  [Sources][sources]
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

## Grammar(语法)

### Hello word
~~~~~
    Hello ${"world"}
~~~~~

### Structors

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

+ `var`
+ `function`
+ `echo`
+ `native`


### 保留的关键字



### 操作符

+ ``



## Requirements(依赖)

+ `Jodd-core`
+ `Jodd-bean`
+ `Jodd-props`
+ `Jodd-petite`


## Contributing(贡献)

+ <谁来帮我翻译>
+ <code> 
+ <idea>
+ <bug>
+ <doc>
+ <donate>


## License(许可证)
 
**Webit Script** is released under the BSD License. See the bundled LICENSE file for
details.
**Webit Script** 依据 BSD许可证发布。详细请看捆绑的 LICENSE 文件。


[jar]: http://zqq90.github.io/maven/com/github/zqq90/webit-script/webit-script-0.8.0-SNAPSHOT.jar
[sources]: http://zqq90.github.io/maven/com/github/zqq90/webit-script/webit-script-0.8.0-SNAPSHOT-sources.jar
[doc]: http://zqq90.github.io/maven/com/github/zqq90/webit-script/webit-script-0.8.0-SNAPSHOT-javadoc.jar
[url_props_doc]: http://jodd.org/doc/props.html

