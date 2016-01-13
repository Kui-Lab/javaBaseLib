package kuilab_com.xJava.function;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import kuilab_com.KuilabErrorResource;
import kuilab_com.xJava.TypeUtil;

public class OverloadedMethod {
	public static IInvokable getOverloadsAsMethod( Object host, String methodName ) throws MethodNotFoundException{
		return getOverloadsAsMethod( host, methodName, null ) ;
	}
	
	public static IInvokable getOverloadsAsMethod( Object host, String methodName, Class<Reference<?>> ref ) throws MethodNotFoundException{
		Method[] ms = null ;
		Class<?> cla = host.getClass() ;
		if( host instanceof Class<? > ){
		}else{
		}
		ms = cla.getDeclaredMethods() ;
		//Method[] msr = new Method[]{} ;
		List<Method> mss = new ArrayList<Method>() ;
		for( Method m : ms){
			if( m.getName().equals( methodName ) ){
				mss.add( m ) ;
			}
		}
		if( mss.size() == 0 ){
			throw new MethodNotFoundException( methodName ) ;
		}
		MethodBody body = new MethodBody( host, mss ) ;
		return body ;
	}
	
	public static Function<Object[], ?> getOverloadAsFunc( Object host, String methodName ) throws Exception{
		IInvokable m = OverloadedMethod.getOverloadsAsMethod( host, methodName ) ;
		Function<Object[], Object> func = ( Object...arg )->{
			Exception er = null ;
			try {
				return m.invoke( null, arg ) ;
			} catch (IllegalArgumentException | InvocationTargetException e ) {
				er = e ;
			}
			try {
				throw er ;
			} catch ( Exception e ) {
				e.printStackTrace();
			}
			return null ;
		} ;
		return func ;
	}
	
	public static Object getOverloadsAsOne( Class<? extends Method> a ){
		
		return null ;
	}
	
	protected static Method[] getOverloads(){
		//new Method( ) ;
		Collection<?>[] aa = new Collection<?>[] { } ;
		Method[] ms = new Method[]{} ;
		return ms ;
	}
	
//	public static Object getOverloadsAsOne( Class<? extends Function<?, ?>> a ){
//		
//		return null ;
//	}
	
	protected static boolean allowNull( Class<?> type ){
		if( type.isPrimitive() )
			return false ;
		if( TypeUtil.isWrapType( type ) )
			return false ;
		return true ;
	}
	
	static boolean matchWithNull( Class<?>[] spr, Class<?>[] cur ){
		int cl = cur.length ;
		for( int i=0; i<cl; i++ ){
			Class<?> estp = spr[ i ] ;
			Class<?> ectp = cur[ i ] ;
			if( estp.equals( ectp ) ){
				continue ;
			}else
				return false ;
		}
		int sl = spr.length ;
		for( int i=cl; i<sl; i++ ){
			Class<?> estp = spr[i] ;
			if( allowNull( estp ) ){//可接受空值。
				continue ;
			}else
				return false ;
		}
		return false ;
	}
	
	//TODO skipLast参数可以改为长度个数限制？
	static boolean matchParams( Class<?>[] spr, Class<?>[] cur, boolean skipLast ) {
		int sn = spr.length ;
		int cn = cur.length ;
//		if( sn == 0 ){
//			
//		}
		
		int n = skipLast ? sn-1 : sn ; 
		for( int i=0; i<n; i++ ){
			Class<?> esc = spr[ i ] ;
			Class<?> ecc = cur[ i ] ;
			if( esc.equals( Object.class ) ) 
				continue ;
			if( ecc == null ){//实参为空。
				if( esc.isPrimitive() )///八种基本类型不能接受null。
					return false ;
				//if( TypeUtil.isWrapType( esc ) )///封装类型也不接受null。
					//return false ;
				continue ;
			}
			if( allowArgType( esc, ecc ) ){
				continue ;
			}else
				return false ;
		}//end for() ;
		return true ; 
	}
	
	protected static boolean allowArgType( Class<?> param, Class<?> arg ){
		if( arg.isAssignableFrom( param ) )
			return true ;
		if( param.isPrimitive() ){
			if( TypeUtil.isWrappedType( arg, param ) )	///原始类型可接受封装类型，但是封装类型不是从原始类型继承而来。
				return true ;							///所以要专门判断。
		}
		return false ;
	}
    
    protected static boolean matchVarArg( Class<?>[] param, Class<?>[] args ){
    	if( matchParams( param, args, true ) ){
    	}else
    		return false ;
    	Class<?> arrType = param[ param.length-1 ] ;
    	for( int i=param.length-1; i<args.length-1; i++ ){
    		if( allowArgType( arrType, args[ i ] ) ) {
    			//continue ;
    		}else
    			return false ;
    	}
    	return true ;
    }
	
//	public static boolean matchParams( Class<?>[] sper, Class<?>[] cur, boolean autoFillNull ) throws Exception{
//		List<Class<?>> scv = Arrays.asList( sper ) ;
//		List<Class<?>> ccv = Arrays.asList( cur ) ;
//		return matchParams( scv, ccv, autoFillNull ) ;
//	}
	
	protected static List<List<Class<?>>> getMatchesWithNull( List<Class<?>> cur, Class<?>[] evMtd ){
		int i = 0 ;
		List<List<Class<?>>> ret ;
		for( Class<?> eCla : cur ){
			eCla.isAssignableFrom( evMtd[i] ) ;
			i++ ;
		}
		
		return null ;
	}
	
//	protected static Object tryPossibles( Class<?>[] args, List<Method> ms ){
//		
//		return null ;
//	}
//	protected static Object tryPossibles( List<Class<?>> ex, List<List<Class<?>>> methodD ){
//		
//		return null ;
//	}
	
	protected static final Object[] EMPTY_arr = new Object[]{} ;
}

class MethodBody implements IInvokable {

	protected List<Method> noVarArg ;//= new Method[]{} ;
	protected List<Method> varArg ;
	protected Reference<?> host ;
	protected List<List<Class<?>>> cache ;
	protected Method lastInvoke ;//TODO
	protected Map<Method,Class<?>[]> param = new HashMap<Method, Class<?>[]>() ;
	
	protected boolean allowApplyHost = false ;
	protected boolean autoAppendNull = true ;
	protected boolean cached = false ;
	
	public MethodBody( Object host, List<Method> methods ){
		this( host, methods, false ) ;
	}
	public MethodBody( Object host, List<Method> methods, boolean autoAppendNull ){
		//ms = methods ;
		this.host = new WeakReference<Object>( host ) ;
		this.autoAppendNull = autoAppendNull ;
		scan( methods ) ;
	}
	
	protected void scan( List<Method> ms ){
		varArg = new ArrayList<Method>() ;
		noVarArg = new ArrayList<Method>() ;
		for( Method m : ms ){
			Class<?>[] ptp= m.getParameterTypes() ;
			if( m.isVarArgs() ) //最后一个参数是可变参数。
				varArg.add( m ) ;
			else
				noVarArg.add( m ) ;
			param.put( m, ptp ) ;
		}
	}
	
    synchronized public Object invoke( Object host, Object[] args ) throws IllegalArgumentException, InvocationTargetException{
        if( args == null )
        	args = OverloadedMethod.EMPTY_arr ;
    	int ARG_n = args.length ;
    	//List<Object> argsL = Arrays.asList( args ) ;
    	//List<Class<?>> argC = new ArrayList<Class<?>>() ;
    	Class<?>[] argC = new Class[ ARG_n ] ;
    	
    	int tailNull0 = 0 ;//末尾的空值个数。
    	int lastNoNull = -1 ;//非空的最后一个参数。
    	int inclNull = 0 ;
    	//预处理参数。
		    	for( int ii= ARG_n-1; ii>=0; ii-- ){
		    		Object eArg = args[ ii ] ;
					if( lastNoNull < 0 ){//未找到最后一个非空。
						if( eArg == null ){
		    				tailNull0 ++ ;
						}else{
		    				lastNoNull = ii ;//这是最后一个非空。%%这句多余？
		    			}
		    		}else{//已找到最后一个非空。
		    			if( eArg == null ){
		    				inclNull ++ ;
		    			}
		    		}
					argC[ii] = ( eArg == null )? null : eArg.getClass() ;
		    	}
		    	//ArrayList<?> al ;
		    	
		    	lastNoNull = ARG_n - tailNull0 ;
    	//重构上面的。
//		    	for( int ii=ARG_n ; ii>=0; ii-- ){
//		    		if( args[ ii ] == null ){
//		    			if( 0 < lastNoNull ){//未找到最后一个非空。
//		    				//tailNull0 ++ ;
//		    			}else{//已找到最后一个非空。
//		    				inclNull0 ++ ;
//		    			}
//		    			argC.add( null ) ;
//		    		}else{
//		    			if( 0 < lastNoNull ){//未找到最后一个非空。
//		    				tailNull0 = ii ;//这是最后一个非空。
//		    			}else{//已找到最后一个非空。
//		    			}
//		    			argC.add( 0, args[ii].getClass() ) ;
//		    		}
//		    	}
//		    	lastNoNull = ARG_n - tailNull0 ;
    	//检查null。
//		    	int iNull = args.length ;
//		    	int i = ARG_n -1 ;
//		    	for( ; i>= 0; i -- ){
//		    		if( args[i] == null ){
//		    			iNull = i ;
//		    		}else{
//		    			break ;
//		    		}
//		    	}
//		int tailNull = args.length*2 - iNull ;
//				for( ; i>=0 ;i-- ){
//					if( args[i] == null )
//						inclNull ++ ;
//				}
    	
    	List<Method> match = new ArrayList<Method>() ;
    	//boolean endWithNull = tailNull0 > 0 ;
    	//查找与实参个数相等的重载体。
	    	for( Method em : noVarArg ){
	    		Class<?>[] empt = param.get( em ) ; //em.getParameterTypes() ;
	    		//List<Class<?>> emptCp = Arrays.asList( empt ) ;
	    		if( empt.length == args.length ){
	    			try {
						if( OverloadedMethod.matchParams( empt, argC, false ) ){
							match.add( em ) ;
							if( inclNull > 0 ){//实参包含null所以要尝试所有可能的。
								
							}else{//实参不包含null，匹配到一个即是。
								break ;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
	    		}else{//参数个数不符。
	    			
	    		}
	    	}
    	System.out.println( "匹配的方法个数:"+match.size() ) ;
    	//所属对象相关逻辑。
		    	if( allowApplyHost ){
		    		if( host == null ){
		    			host = this.host ;
		    		}else{
		    		}
		    	}else
		    		host = this.host ;
				if( host instanceof Reference<?> )
					host = ( ( Reference<?> ) this.host ).get() ;
		    	if( match.size() > 0 ){
		    		for( Method mi : match ){
		    			try {
		    				return mi.invoke( host, args ) ;
		    			} catch ( IllegalArgumentException|IllegalAccessException|InvocationTargetException e ) {
		    				//System.out.println( "modif:"+mi.getModifiers() ) ;
		    			}
		    		}
		    	}
    	//System.out.println( match.size()+"个方法体全部执行失败。" );
    	//尝试在实参后加null。
		    	if( autoAppendNull ){
			    	for( Method em : noVarArg ){
			    		Class<?>[] empt = param.get( em ) ;
			    		//List<Class<?>> emptCp = Arrays.asList( empt ) ;
			    		if( match.indexOf( em ) > 0 )
			    			continue ; //尝试过的匹配，忽略。
			    		if( empt.length > ARG_n ){
			    			try {
								if( OverloadedMethod.matchWithNull( empt, argC ) ){
									try{//TODO 要不要添加null?测试一下？
										return em.invoke( host, args ) ;
									}catch( IllegalArgumentException|IllegalAccessException|InvocationTargetException e ){
									}
								}
							} catch ( Exception e ) {
								e.printStackTrace();
							}
			    		}else{
			    			
			    		}
			    	}
		    	}
    	//尝试可能是可变参数的重载体。
		    	for( Method em : varArg ){
		    		Class<?>[] tp = param.get( em ) ;
		    		if( tp.length <= ARG_n ){
		    			if( OverloadedMethod.matchVarArg( tp, argC ) ){
		    				try{
		    					Object r = em.invoke( host, args ) ;
		    					//validVarArg.add( em ) ;
								return r ;
							}catch( IllegalArgumentException e ){
								continue ;
		    				}catch( IllegalAccessException|InvocationTargetException e ){
								continue ;
							}catch( Exception e ){
								break ;
							}
		    			}
		    		}
		    	}
    	
    	String serr = KuilabErrorResource.methodArgMismatch( getName(), args );
    	throw new InvocationTargetException( null, serr ) ;
    }
    
    public Object invoke( Object...args ) throws IllegalArgumentException, InvocationTargetException, IllegalAccessException{
    	return invoke( null, args) ;
    }
	public Object invoke( List<?> args )throws IllegalArgumentException, InvocationTargetException, IllegalAccessException{
		return invoke( null, args) ;
	}  
    
    public String toString(){//TODO
    	return super.toString() ;
    }
    
    public String getName(){
    	if( noVarArg.size() > 0 )
    		return noVarArg.get(0).getName() ;
    	return varArg.get( 0 ).getName() ;
    }
}
