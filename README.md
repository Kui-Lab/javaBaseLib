# 用于Java的基本库( 函数式编程工具包 )。The base lib for Java.
JavaBaseLib( Functional Programming )
 by 夔堂.
[官网：kuilab.com](http://www.kuilab.com) 。有更多文档。
##目前主要包含的内容：
<pre>函数式编程工具包（闭包、Lambda等）。
最大的优点是产生的闭包对象是SDK原生类Method，
对于调用闭包函数的代码没有侵入性！且无需JDK/JRE8。
可以优雅的实现事件机制。</pre>
```Java
Method f = MethodWrapper.wrapMethod( foo, methodName ) ;//制作函数体
f.invoke( null, arg ) ;//方法对象可任意传递并执行，且无需再输入第一个参数。
```
增加及增强特性：
*将有重载的函数重新封装成一个Method对象的功能。
*生成的函数体可以获取本身的的引用。

###issue
对于ReflactionFactory.newMethod()的一些参数，目前我也没有找到任何官方或非官方的说明文档。
如果你了解，可联系我们，或许可以使它更强。
