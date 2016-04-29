package kuilab_com.lang.function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import kuilab_com.KuilabErrorResource;
import sun.reflect.MethodAccessor;

public class MethodWrapper {
//	public static void setReflectionFactory( ReflectionFactory reflectionFactory ){
//		rf = reflectionFactory ;
//	}
//	protected static ReflectionFactory rf = ReflectionFactory.getReflectionFactory() ;

	/**
	 * 将执行方法的对象（即{@link Method#invoke()方法的第一个参数）预先装入Method对象，<br/>
	 * 这样Method即可无需执行主体对象即可执行，实现函数式编程。
	 * @param host
	 * @param methodName 如果指定的方法名具有多个重载体，则会将他们封装成一个。
	 * @return 无需对象即可执行的Method实例。
	 * @throws MethodNotFoundException
	 * @throws TypeMismatchException
	 */
	public static Method wrapMethod( Object host, String methodName ) throws MethodNotFoundException, TypeMismatchException{//
		Method m = null ;
		List<Method> lm = MethodSupplementer.getMethod( host, methodName ) ;
		if( lm.size() == 0 )
			throw new MethodNotFoundException( methodName ) ;
		if( lm.size() == 1 ){
			m = lm.get( 0 ) ;
			return wrapMethod( host, m ) ;
		}
		return OverloadedMethod.getOverloadsAsMethod( host , methodName ) ;
	}
	
	public static Method wrapMethod( Object host, Method method ) throws TypeMismatchException {
		Class<?> hostCla = host instanceof Class<?> ? (Class<?>)host : host.getClass() ;
		if( method.getDeclaringClass().isAssignableFrom( hostCla ) )
		{}else{
			throw new TypeMismatchException( 
					KuilabErrorResource.classAndMethodTypeMismatch( host, method ) ) ;
		}
		method.setAccessible( true ) ;//这句可能多余%%
		Method ret = MethodCopy.copyMethod( method ) ;
		WrappingMethodAccessor accss = new WrappingMethodAccessor( host, method, ret ) ;
		MethodCopy.rf.setMethodAccessor( ret, accss ) ;
		return ret ;
	}
	
	//TODO 用InvocationHandler+Proxy实现。
	/*public static void wrapMethod( Object host, Method method ) throws Exception{
		MethodAccessor maOrgn = RF.getMethodAccessor( method ) ;
		if( maOrgn instanceof InterceptingMethodAccessor ){
			System.out.println( "在给已经使用拦截MethodAccessor的方法进行包装。" ) ;//todo 调试语句。
		}else{
			//map.put( method, host ) ;
		}
		if( maOrgn == null ){
			////有些时候方法的MethodAccessor会在第一次调用时才由JVM创建，
			////这种情况下尝试这样创建。参考Method::acquireMethodAccessor()。
			maOrgn = ReflectionFactory.getReflectionFactory().newMethodAccessor( method ) ;
		}
		if( maOrgn == null ){
			throw new Exception( "MethodAccessor missing." ) ;//.printStackTrace() ;//todo Exception未定义。
			//KuilabErrorResource
		}
		////对于某些方法，虽然是公开的，但是仍然需要执行这句，
		////否则执行时会抛出IllegalAccessException错误。
		////比如嵌套类的方法。
			method.setAccessible( true ) ;
			
		WrappingMethodAccessor ex = null ;//new WrappingMethodAccessor( host, method, maOrgn ) ;
		map.put( ex, new HostToMethodPair( host, method ) ) ;
		
		RF.setMethodAccessor( method, ex ) ;
	}*/
	
//	public static boolean equalWrapper( Method m ){
//		return WRAP_body.equals( m ) ;
//	}
	
//	protected static Method WRAP_body ;
//	static{
//		try {
//			WRAP_body = WrappingMethodAccessor.class.getDeclaredMethod( "invoke", new Class<?>[]{ Object[].class } ) ;
//		} catch (NoSuchMethodException | SecurityException e) {
//		}
//	}

}
class WrappingMethodAccessor implements MethodAccessor {
		//protected static Map<Method,Object> map = MethodWrapper.map ;
		
		//TODO 改用方法。
		public Object host ;
		public Method wrappr ;
		public Method actual ;
		
		public WrappingMethodAccessor( Object host, Method actual, Method wrapper ){
			this.host = host ;
			this.wrappr = wrapper ;
			this.actual = actual ;
			//map.put( m, ma ) ;
		}
			
//		public Object ex( Object...args ){
//			return null ;
//		}

		@Override
		public Object invoke( Object arg0, Object[] args )
				throws IllegalArgumentException, InvocationTargetException {
			try {
				////如果直接调用原来的那个MethodAccessor，会报NullPointerException。所以只能这样，或者复制Method对象。
				MethodSupplementer.noteInvocation( wrappr, host, this ) ;
				//MethodWrapper.RF.setMethodAccessor( recd.wrapr , maActual );
				Object ret = actual.invoke( host, args ) ;
				//MethodWrapper.RF.setMethodAccessor( method , this );
				MethodSupplementer.removeInvocation( wrappr, host, this ) ;
				return ret ;
			} catch ( IllegalAccessException e ) {
				throw new InvocationTargetException( e ) ;
			}
		}
		
//		public boolean equals( Object o ){
//			return super.equals( o ) ;
//		}
		
		//TODO finalize()
}
