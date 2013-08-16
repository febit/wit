Webit Script
====


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
    // !!! Engine 并不会被缓存, 请根据需要自行实现 Engine的单例模式
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
    @import java.util.Map;

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

+ 单行注释 `//` 
+ 块注释 `/* */` 


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
+ 同一作用域不能重复声明变量
+ **模板传入的变量仅在该作用域查找同名变量并赋值**

*1. 调用模板传入的变量; 2.import 返回的变量*

### 变量
#### 变量声明 var

~~~~~
var a;
var a, b, c=0, d="d";
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

### 数据结构
#### 拥有动态类

~~~~~
var x                 //   null
var x2 = 6;        //  数字
var x3 = "Bill";   //  字符串
var x4 = 'a';       //  字符
var x5 = {};        // Map
~~~~~

#### 字符串
+ 转义，`\\` `\"` `\'` `\n` `\r` `\t` `\f` `\b`
+ 允许换行，行后转义字符 可屏蔽该换行符

~~~~~ 
var string = "第一行  \
这还是在第一行
这是第二行\n第三行\n
这是第五行，第四行是空行
"；
~~~~~ 

### 数字

~~~~~ 
var x1=34;  //Integer
var x2=34L;  //Long
var x3=34.00; //Double
var x4=34.00D;  //Double
var x5=34.00F;  //Float

var x6 = 0b10101;  //二进制
var x7 = 0123; //八进制
var x8 = 0x1A; //十六进制
~~~~~ 

### 布尔

~~~~~ 
var x=true;
var y=false;
~~~~~ 

### 数组
*现只能用native的方法 引用java 数组*

~~~~~ 
// 引入生成数组的 native 方法
var new_int_array = native [int];
var new_Object_array = native [Object];
var new_DateTime_array = native [java.util.DateTime];

//得到定长数组
var int_array = new_int_array(5); //长度为5 的int数组
var objects = new_Object_array(5);//长度为5 的Object数组

var a;
a = objects[4];
objects[4]=4; // 自动 装箱为Integer 然后放入数组
var len = objects.length; //数组长度
len = objects.size; //数组长度

//不定长数组 可使用Java提供的List实现
var new_list = native new java.util.ArrayList();
var list_add = native java.util.List.add(Object);

var list = new_list();
list@list_add(0); 
list@list_add(1);

var a = list[0];
list[0] = "zero";
list[1] = "a string";
~~~~~ 

### Map

~~~~~ 
var map = {};
var map2 = {1:1,"2","2"};
map["key"] = "a string value";

var value = map[1];
value = map["2"];
value = map["key"];
~~~~~ 

### Java对象
#### 声明

~~~~~
var new_list = native new java.util.ArrayList();
var list = new_list();
var list2 = new_list();
~~~~~

#### 访问属性

~~~~
var book;
var name = book.name; // book.getName();
book.name = "new name"; //book.setName("new name"); 
~~~~

#### 访问方法
*访问方法必须事先native导入成本地函数*

~~~~~
var list_add = native java.util.List.add(Object);

list@list_add(0);
list_add(list, 1);
~~~~~

*访问静态方法*

~~~~~
var now = native java.lang.System.currentTimeMillis();
echo now();
~~~~~
 
### 函数

#### 声明
+ 格式同java
+ 可变参数, 
+ 可通过 arguments 获得所有传入的参数, java类型为 Object[]
+ 可访问父层作用域
+ 函数内部可嵌套函数


~~~~~
var outSideVar;
var a;

var myFunc = function(arg1, arg2){
    var arg3 = arguments[3]; // 如果没有将抛出异常,最好通过 arguments.size确认
    outSideVar = "a new "; //可访问
    var a = 0; //内部变量
    super.a ++ ; //访问上层变量
    var func = function(){ ... }; //内部嵌套函数
}; //不要忘了分号！！！
~~~~~

#### 导入Java内的 方法
+ 仅可导入公共类的公共方法, 包括静态方法 和 成员方法
+ 可使用`@import` 导入类名 或者包名 用法同Java里的 `import`, 以简化输入

~~~~~
@import  java.lang.System; //实际上默认已经导入  java.lang.* 只是掩饰使用方法
@import  java.util.*;
var now = native java.lang.System.currentTimeMillis();
var list_add = native List.add(Object);
var new_list = native new add(Object); // 导入 构造函数
~~~~~

### 调用
+ 可变参数个数, 多余函数同样会被传入, 多余函数是否被使用 取决于函数本身
+ 缺少的参数 自动 null 填充, *为了良好的设计 不建议使用缺少函数自动填充*
+ 可使用@ 将第一个参数 外置

~~~~~
func(arg1, arg2);
//等同于
arg1 @ func(arg2);

list_add(list, item);
//等同于
list@list_add(item);
~~~~~


### 重定向输出符 `=>`
+ 作用: 将指定 范围 产生的输出流 重定向到 指定变量
+ 意义: 可以延后输出
+ **使用对象: 1. 代码段；  2. 函数调用**
+ 数据格式: 使用OutputStream 时, 为 byte[] ; 使用 Writer 时, 为String.

~~~~~
var out;
var book;

//代码段 输出重定向
{
echo "a String";
>${book.name} <
} => out; //不要忘了分号！！！
// "a String" 以及 book.name 都将输出到 out
~~~~~

~~~~~
var out;
// 函数 输出重定向
func() => out;
//由于 `=>` 具有较高的优先级，也可以这么些
var a = arg1@func() => out +1; 
//此时 a为 func()+1 , func() 本次执行的输出内容赋值给out
~~~~~


### import & include
+ 区别: import  将把调用模板的 上层变量 推入调用层的当前已存在变量
+ 共同点: 都会在调用位置产生 输出
+ 将使用默认的Loader 加载模板，可使用相对路径或绝对路径
+ 可跟随 一个 map 格式的传参
+ 模板名可以动态生成

~~~~~
//相对路径
import "./book-head.wtl";
//等同于 
import "book-head.wtl";

//绝对路径
import "/copyright.wtl";

//动态模板名
var style = "";
import "book-list-"+ style  +".wtl";

//可传入参数 函数同样也可以作为变量传输
var func = function(){}; 
var book;
import "book-head.wtl"  {"book": book, "func":func};
~~~~~


### 其他



## 性能

+ 缺省开启ASM构建Native 调用减少反射, 不同于将整个模板编译成Java字节码,该方法不会造成无限制的perm溢出;
+ 解析之后的Template AST会放入缓存, 检测到模板源文件改变时将重新家在资源并解析;
+ 性能测试结果比较理想, 待比较权威的模版测试程序;
+ 使用OutputStream 输出时, 选择 SimpleTextStatmentFactory 将会预先将纯文本根据缺省编码编码成字节流. 

## SPI

+ ResourceLoader  模板资源加载
+ Resolver  Bean属性解析
+ TextStatmentFactory  对模板纯文本的存贮以及输出形式进行管理


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

