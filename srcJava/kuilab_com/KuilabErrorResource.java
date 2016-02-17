package kuilab_com;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import kuilab_com.lang.supplements.ReturningException;


public class KuilabErrorResource {
	protected static final String SYMBOL_x = "{x}" ;
	
	public static String methodArgMismatch( Object method, Object[] args ){
		String s = "没有找到参数类型能匹配的重载方法体";//.replaceFirst( "${}", replacement)
		if( method != null )
			s = "由指定的方法名\"" +method.toString() +"\" "+ s ; 
		if( args != null )
			return s += Arrays.toString( args ) ;
		return s ;
	}
	
	public static String classAndMethodTypeMismatch( Object host, Object methodOrMethodClass ){
		Object methodCla = null ;
		if( methodOrMethodClass instanceof Method ){
			methodCla = ((Method) methodOrMethodClass ).getDeclaringClass() ;
		}else
			methodCla = methodOrMethodClass ;
		return "意图进行组装的目标类型{x}不是方法所在类{x1}的子类。".
				replace( SYMBOL_x, host.toString() ).
				replace( "{x1}", methodCla.toString() ) ;
	}
	
	public static String methodNotFound( String methodName ){
		return "没有找到名为${1}的方法。".replace( "${1}", methodName ) ;
	}
	
	public static String methodRecycled( String methodName ){
		return
		"方法无法执行：名为${1}的方法已经被自动销毁（因为使用了软引用或弱引用）。" ;
	}
	
	public static String methodHostMissing( String methodName ){
		return "方法${1}无法执行：未赋予其所属对象（this为空）".replace( "${1}", methodName ) ;
	}
	
	/**
	 * @see ReturningException ;
	 * **/
	public static String deviantReturning(){
		return "非正常的返回方式。" ;
	}
	
	public static String cantGetMethodAccessor(){
		return "获取方法原本的MethodAccessor失败。（通常先执行其一次再进行当前操作即可解决此问题）#rN7ek3pjBHUBs" ;
	}
	
	public static String specialException( Object message ){
		return "特殊错误"+message ;
	}
	
	public static String restrictType( Object types ){
		String ret = "只接受{x}参数。" ;
		//T[] xs = ( (T[]) types ) ;
		if( types == null ){
			return ret.replace( "{x}", "特定的" ) ;
		}
		if( types instanceof Collection ){
			if( ((Collection<?>)types).size() == 0 )
				return ret.replace( "{x}", "特定的" ) ;
			types = ((Collection<?>) types).toArray() ;
		}
		if( types.getClass().isArray() ){
			if( ((Object[]) types).length == 0 )
				return ret.replace( "{x}", "特定的" ) ;
			String str = "" ;
			String comma = ", " ;
			for( Object itm : (Object[])types ){
				if( itm instanceof Class<?> ){
					str += comma+((Class<?>)itm).getCanonicalName() ;
				}else
					str += comma+itm.toString() ;
			}
			str = str.substring( comma.length() ) ;
			return ret.replace( "{x}", "["+str+"]这些" );
		}
		return ret.replace( "{x}", types.toString() ) ;
	}
	
}
