Webit Script
====

*this is a new project, lot of things need to be done, stay tuned!*

**Webit Script** is a template-like script and engine, all writen with Java.

It's grammar is very like Javascript, and with `<% %>` `${ }` like in JSP


## How to use(如何使用)



## Grammar(语法)

Hello word

    Hello ${"world"}


Structors

+ `<% .code inside.. %> plain text outside`
+ `${ .. expression .. }` 
+ escape `\\` to `\` , `\<%` to `<%`, `\${` to `${`
+ Node: 只有紧挨 `<%` `${` 的 `\` 才需要转义 , 
+ 转义窍门: 偶数个 `\` 会 打印 一半数量的`\` 并进入代码段, 
            奇数 会 打印 (count-1)/2 个 `\` 然后打印被转移的符号。
            
            
            

Commit in code

the same in Javascript
+ `//` line commit
+ `/* */` block commit



Key Words

+ `var`
+ `function`
+ `echo`
+ `native`
....

Operators

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

