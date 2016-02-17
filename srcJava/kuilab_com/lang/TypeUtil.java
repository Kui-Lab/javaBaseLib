package kuilab_com.lang;

import java.util.HashMap;
import java.util.Map;

public class TypeUtil {
	
	public static boolean is( Object obj, Class<?> type ){
		return 
				obj.getClass().isAssignableFrom( type ) ;
	}
	
	public static boolean isWrapType( Class<?> type ){
		if( type.toString().indexOf( "Character" ) > 0 ){
			System.out.println( wrapTypeToPrimitive_map.containsKey( type ) ? "Character应当找到" : "发生Character bug！" ) ;
		}
		Object dbg = wrapTypeToPrimitive_map.containsKey( type ) ;
		dbg = wrapTypeToPrimitive_map.get( type ) ;
		return wrapTypeToPrimitive_map.containsKey( type ) ;
	}
	
	public static boolean isWrappedType( Class<?> type, Class<?> primitive ){
		try{
			if( wrapTypeToPrimitive_map.get( type ).equals( primitive ) )
				return true ;
		}catch( Exception e ){
		}
		return false ;
	}
	
	public static boolean isPrimitiveType( Class<?> primitive, Class<?> type ){
		try{
			if( wrapTypeToPrimitive_map.get( type ).equals( primitive ) ){
				return true ;
			}
		}catch( Exception e ){
		}
		return false ;
	}
	
	public static Class<?> getWrapType( Class<?> primitive ){
		return primitiveTypeToWrap_map.get( primitive ) ;
	}
		
	
	/**
		byte    	Byte  
		short     	Short  
		int     	Integer  
		long       	Long  
		float       Float  
		double     	Double  
		char       	Character  
		boolean		Boolean
	 * **/
	
	protected static Map<Class<?>,Class<?>> wrapTypeToPrimitive_map ;
	static{
		wrapTypeToPrimitive_map = new HashMap<Class<?>, Class<?>>(8) ;
		//Map<Object,?> ref = wrapTypeToPrimitive_map ;
		wrapTypeToPrimitive_map.put( Byte.class, byte.class ) ;
		wrapTypeToPrimitive_map.put( Short.class, short.class ) ;
		wrapTypeToPrimitive_map.put( Integer.class, int.class ) ;
		wrapTypeToPrimitive_map.put( Long.class, long.class ) ;
		wrapTypeToPrimitive_map.put( Character.class, char.class ) ;
		wrapTypeToPrimitive_map.put( Float.class, float.class ) ;
		wrapTypeToPrimitive_map.put( Double.class, double.class ) ;
		wrapTypeToPrimitive_map.put( Boolean.class, boolean.class ) ;
	}
	
	protected static Map<Class<?>,Class<?>> primitiveTypeToWrap_map ;
	static{
		primitiveTypeToWrap_map = new HashMap<Class<?>, Class<?>>(8) ;
		//wrapTypeToPrimitive_map.put( byte.class, Byte.class)
		Map<Class<?>, Class<?>> w2p = wrapTypeToPrimitive_map ;
		for( Class<?> wrapCla : wrapTypeToPrimitive_map.keySet() ){
			primitiveTypeToWrap_map.put( w2p.get( wrapCla ), wrapCla ) ;
		}
	}
	
}
