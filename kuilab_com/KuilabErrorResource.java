package kuilab_com;

import java.util.Arrays;


public class KuilabErrorResource {
	
	public static String methodArgMismatch( Object method, Object[] args ){
		String s = "没有找到参数类型能匹配的重载方法体";//.replaceFirst( "${}", replacement)
		if( method != null )
			s = "由指定的方法名\"" +method.toString() +"\" "+ s ; 
		if( args != null )
			return s += Arrays.toString( args ) ;
		return s ;
	}
	
	public static String methodNotFound( String methodName ){
		return "没有找到名为${1}的方法。".replace( "${1}", methodName ) ;
	}
}
