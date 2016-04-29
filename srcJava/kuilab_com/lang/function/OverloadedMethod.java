package kuilab_com.lang.function;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import kuilab_com.lang.TypeUtil;
import kuilab_com.lang.supplements.ReturningException;
import sun.reflect.MethodAccessor;
import sun.reflect.ReflectionFactory;

public class OverloadedMethod {
	/**
	 * <pre>
	 * NO: 不做优化。
	 * LAST: 每次执行时先尝试上次执行的重载体。
	 * MOST：每次执行时先尝试最多被执行的重载体。
	 * <pre/>
	 * **/
	public static enum OptimizeStrategy{
		NO, LAST, MOST ;
	}
	
	protected static ReflectionFactory RFLC_factory = ReflectionFactory.getReflectionFactory() ;
	
	protected static Options DFT_option = new Options() ;
	
	public static Options getDefaultOption(){
		return DFT_option ;
	}
	//需要使用软/弱引用时，要拿这个反射出他们的构造函数。
	//protected static Class<?>[] REF_CONSTCT_ARG = new Class<?>[]{ Object.class } ;
	
	public static class Options{
		public static Options newOne(){
			return new Options() ;
		}
		
		public Method buildMethod( Object host, String methodName ) throws MethodNotFoundException{
			return OverloadedMethod.getOverloadsAsMethod( host, methodName, this ) ;
		}
		
//		public Options setRefType( Class< ? extends Reference<?> > v ){
//			refType = v ;
//			return this ;
//		}
//		protected Class< ? extends Reference<?> > refType ;
		
		public Options setAutoAppendNull( boolean v ){
			autoAppendNull = v ;
			return this ;
		}
		protected boolean autoAppendNull ;
		
		public Options setAllowApplyHost( boolean v ){
			allowApplyHost = v ;
			return this ;
		}
		protected boolean allowApplyHost ;
		
		/**
		 * @param v 默认为假，即方法没有重载时直接返回原方法。设置为真则即使没有重载也返回包装方法。
		 */
		public Options setForceWrapToCombined( boolean v ){
			forceWrapToCombined = v ;
			return this ;
		}
		protected boolean forceWrapToCombined = false ;
		
		public Options setOptimizeStrategy( OptimizeStrategy a ){
			optimizeStrategy = a ;
			return this ;
		}
		protected OptimizeStrategy optimizeStrategy = OptimizeStrategy.NO ;
		
		public Options setWrappMethod( Method wrappMeansEncapsulating ){
			wrapper = wrappMeansEncapsulating ;
			return this ;
		}
		protected Method wrapper ;
		
		@Override
		protected Object clone() throws CloneNotSupportedException {
			return clone_() ;
		}
		public Options clone_(){
			return new Options().
					setAllowApplyHost( allowApplyHost ).
					setAutoAppendNull( autoAppendNull).
					setForceWrapToCombined( forceWrapToCombined ).
					setOptimizeStrategy( optimizeStrategy ).
					setWrappMethod( wrapper ) ;
		}
	}
	
	public static Method getOverloadsAsMethod( Object host, String methodName ) throws MethodNotFoundException{
		return getOverloadsAsMethod( host, methodName, null ) ;
	}
	/**
	 * 忽略没有被重载就情况，因为使用时应当使用{@link MethodWrapper#wrapMethod()}，
	 * 由它来调用这个方法，没有重载的方法它会自己处理返回，用简单的MethodAccessor，而不是combined MethodAccessor。
	 * **/
	public static Method getOverloadsAsMethod( Object host, String methodName, Options opt ) throws MethodNotFoundException{
		if( opt == null )
			opt = (Options) DFT_option.clone_() ;
		Method invokeBody = MethodCopy.copyMethod( DEF_invokeBody ) ;
		opt.setWrappMethod( invokeBody ) ;
		@SuppressWarnings("unused")
		MethodAccessor accssBody = null ;
		try {
			accssBody = combineUp( host, methodName, opt ) ;
		} catch ( ReturningException e ) {
			if( opt.forceWrapToCombined )
				accssBody = RFLC_factory.getMethodAccessor( ( (Method) e.getReturningValue() ) ) ;
			else
				return ( Method ) e.getReturningValue() ;
		}
		RFLC_factory.setMethodAccessor( invokeBody, accssBody ) ;
			//这句本该由MethodAccessor自己执行，但是它没有持有ReflectionFactory实例，所以在这里执行了。
		return invokeBody ;//有重载的情况下必须new一个Method
	}
	
//	public static MethodAccessor combineUp( Object host, String methodName )
//			throws MethodNotFoundException, ReturningException{
//		return combineUp( host, methodName, null ) ;
//	}//懒得给OptmzCombinedMethodAccessor加一个赋值wrapInvoker的方法，所以就不提供这个了。当然使用时依旧可以输入null。
	/**
	 * @param host 即Method函数体执行时的“this”所指。
	 * @param methodName
	 * @param opt
	 * @return {@link MethodAccessor} 是代理真实方法的包装类，JVM在运行时为Method对象创建所需的MethodAccessor。
	 * @throws MethodNotFoundException
	 * @throws ReturningException <pre>在forceWrapToCombined的情况下，
	 * 如果只找到一个重载体，那么通过抛这个错直接返回它，
	 * 也就是Method，而不是返回一个{@link OptmzCombinedMethodAccessor}。</pre>
	 */
	public static MethodAccessor combineUp( Object host, String methodName, Options opt ) 
			throws MethodNotFoundException, ReturningException{
		Method[] ms = null ;
		Class<?> cla = host.getClass() ;
		if( host instanceof Class<? > ){//TODO
		}else{
		}
		ms = cla.getDeclaredMethods() ;
		List<Method> mss = new ArrayList<Method>() ;
		for( Method m : ms){
			if( m.getName().equals( methodName ) ){
				mss.add( m ) ;
			}
		}
		if( mss.size() == 0 ){
			throw new MethodNotFoundException( methodName ) ;
		}
		if( mss.size() == 1 ){
			throw new ReturningException( mss.get( 0 ) ) ;
		}
		OptmzCombinedMethodAccessor accssBody = new OptmzCombinedMethodAccessor( host, mss, opt.wrapper, opt ) ;
		return accssBody ;
	}
	
	protected static Method[] getOverloads(){//TODO getOverloads()
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
		if( type.equals( Character.class ) ){
			System.out.println( "debug Character:" +
			TypeUtil.getWrapType( char.class ).equals( type ) ) ;
		}
		if( TypeUtil.isWrapType( type ) )
			return true ;
		return true ;
	}
	
	static boolean matchWithNull( Class<?>[] spr, Class<?>[] cur ){
		int cl = cur.length ;
		for( int i=0; i<cl; i++ ){
			Class<?> estp = spr[ i ] ;
			Class<?> ectp = cur[ i ] ;
			if( allowArgType( estp, ectp ) ){
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
		return true ;
	}
	
	//TODO skipLast参数可以改为长度个数限制？
	static boolean matchParams( Class<?>[] spr, Class<?>[] cur, boolean skipLast ) {
		int sn = spr.length ;
		int cn = cur.length ;
//		if( sn == 0 ){
		
		int n = skipLast ? sn-1 : sn ; 
		for( int i=0; i<n; i++ ){
			Class<?> esc = spr[ i ] ;
			Class<?> ecc = cur[ i ] ;
			if( allowArgType( esc, ecc ) ){
				continue ;
			}else
				return false ;
		}//end for() ;
		return true ; 
	}
	
	protected static boolean allowArgType( Class<?> param, Class<?> arg ){
		if( param.equals( Object.class ) ) 
			return true ;
		if( arg == null ){//实参为空。
			if( param.isPrimitive() )///八种基本类型不能接受null。
				return false ;
			//if( TypeUtil.isWrapType( esc ) )///封装类型也不接受null。
				//return false ;
			return true ;
		}
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
	
//	protected static List<List<Class<?>>> getMatchesWithNull( List<Class<?>> cur, Class<?>[] evMtd ){
//		int i = 0 ;
//		List<List<Class<?>>> ret ;
//		for( Class<?> eCla : cur ){
//			eCla.isAssignableFrom( evMtd[i] ) ;
//			i++ ;
//		}
//		return null ;
//	}
    
    /**
     * 需要返回一个Method对象被调用，由它执行代理MethodAccessor。
     * **/
    public Object invokeBody( Object... args){
    	return null ;
    }
	
    public static boolean equalWrapperMethod( Method m ){
    	return DEF_invokeBody.equals( m ) ; 
    }
    
    /**
     * 用来供{@link #equalWrapperMethod(Method)}使用。
     */
    protected static Method DEF_invokeBody = newInvokeBody() ;
    
    protected static Method newInvokeBody(){
    	try {
    		Method body = OverloadedMethod.class.getDeclaredMethod( "invokeBody", new Class<?>[]{ Object[].class } ) ;
			return body ;
		} catch ( NoSuchMethodException | SecurityException e ) {
			//throw new ProgrammingMistakenException( "" ) ;
			return null ;
		}
    }
}

