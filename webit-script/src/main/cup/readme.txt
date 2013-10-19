1. debug RESULT
[replace]
//RESULT_DEBUG:
=>


2. Optimize 1:
[replace reg]
RESULT = ([^;]*?);[^;]*return RESULT;
=>
return $1;


3. Optimize 2:

like:
webit.script.core.java_cup.runtime.Symbol stat$Symbol = CUP$Parser$stack.peek(0);
Statment stat = (Statment) stat$Symbol.value;
return stat;
[replace reg]
webit\.script\.core\.java_cup\.runtime\.Symbol ([a-zA-Z_$]*?)\$Symbol = CUP\$Parser\$stack\.peek\(([0-9]+)\);[\r\n\t\t ]*([a-zA-Z_$]*?) \1 = \(\3\) \1\$Symbol\.value;[\r\n\t\t ]*return \1;
=>
return CUP\$Parser\$stack.peek($2).value;


4. Optimize 3:
//Object RESULT;
