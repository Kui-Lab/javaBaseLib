# 用于Java的基本库( 闭包、兰布达等…… )。The base lib for Java.
 javaBaseLib( Closure| Lambda etc ) by 夔堂.
[官网：kuilab.com](http://www.kuilab.com) 。有更多文档。
##
目前主要包含的内容：
无需JDK/JRE8即可使用的函数式编程工具包。
最大的优点是产生的闭包对象是SDK原生类Method，
对于调用闭包函数的代码没有侵入性！
可以优雅的实现事件机制。
（官方SDK中一直没有提供的事件发送者实现，这是程序员Java一大痛，而我们也将尽快提供一个）。
```Java
Method f = MethodWrapper.wrapMethod( foo, methodName ) ;//制作函数体
f.invoke( null, arg ) ;//方法对象可任意传递并执行，且无需再输入第一个参数。
```
另外提供了将有重载的函数重新封装成一个Method对象的功能。

###
对于ReflactionFactory.newMethod()的一些参数，目前我也没有搜索到任何官方或非官方的说明文档。
如果你了解，可联系我们，或许可以使它更强。

----------------------------------------------------------