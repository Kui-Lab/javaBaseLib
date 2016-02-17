package kuilab_com.lang.function;

import java.lang.reflect.Method;
/**<pre>
 * 使用方法：
 * 在普通函数中创建一个ClosureFunction的匿名子类，
 * 并带有一个方法，名字不是“getLambda”即可，比如：
 * {@code public Object aLambda(){} ;}。
 * 执行getLambda()方法会得到一个Method实例。
 * 这个实例就是制作成闭包函数的aLambda函数。
 * 也可以写多个方法，然后以方法名指定一个方法来制成闭包函数。
 * 也就是{@link #getLambda(String)}
 * 
 * 示例：
 * Method lambda =( new ClosureFunction( ){
 *		public Object m1( Object arg1, int arg2 ){
 *			//执行逻辑……
 *			return "闭包函数返回值：" ;
 *		}
 *	} ).getLambda() ;
 * 
 * </pre>
 */
public abstract class ClosureFunction {
	protected ClosureFunction(){
	}
		
	/**如果只写一个方法，那么默认用那个方法生成闭包函数，也就不需要指定方法名。<br/>
	 * 为了使语法简洁，省略了throw声明。**/
	public Method getLambda(){
		Method[] ms= this.getClass().getDeclaredMethods() ;
		Method lambda = ms[0] ;
		try { lambda = MethodWrapper.wrapMethod( this, lambda ) ;
						} catch (TypeMismatchException e) {}//方法是本来的类方法，所以不会抛这个错。
		return lambda ;
	}
	
	/**获取指定名称的方法制作成的闭包函数。</br>
	 * 为了使语法简洁，省略了throw声明。**/
	public Method getLambda( String name ){
		Method m = MethodSupplementer.getMethod( this, name ).get( 0 ) ;
		Method lambda = null ;
		try { lambda = MethodWrapper.wrapMethod( this, m ) ;
						} catch (TypeMismatchException e) {}//方法是本来的类方法，所以不会抛这个错。
		return lambda ;
	}
	
}
