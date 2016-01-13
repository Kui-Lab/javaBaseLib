package kuilab_com.xJava;

import java.util.HashMap;
import java.util.Map;

public class TypeUtil {
	
	public static boolean is( Object obj, Class<?> type ){
		return 
				obj.getClass().isAssignableFrom( type ) ;
	}
	
	public static boolean isWrapType( Class<?> type ){
		return primitiveAndWrapType_map.containsKey( type ) ;
	}
	
	public static boolean isWrappedType( Class<?> type, Class<?> primitive ){
		try{
			if( primitiveAndWrapType_map.get( type ).equals( primitive ) )
				return true ;
		}catch( Exception e ){
		}
		return false ;
	}
	
	//public static boolean isPrimitiveType( Class<?> primitive, Class<?> w ){
		
	
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
	
	protected static Map<Class<?>,Class<?>> primitiveAndWrapType_map ;
	static{
		primitiveAndWrapType_map = new HashMap<Class<?>, Class<?>>() ;
		//Map<Object,?> ref = primitiveAndWrapType_map ;
		primitiveAndWrapType_map.put( Byte.class, byte.class ) ;
		primitiveAndWrapType_map.put( Short.class, short.class ) ;
		primitiveAndWrapType_map.put( Integer.class, int.class ) ;
		primitiveAndWrapType_map.put( Long.class, long.class ) ;
		primitiveAndWrapType_map.put( Float.class, float.class ) ;
		primitiveAndWrapType_map.put( Double.class, double.class ) ;
		primitiveAndWrapType_map.put( Character.class, char.class ) ;
		primitiveAndWrapType_map.put( Boolean.class, boolean.class ) ;
	}
}
