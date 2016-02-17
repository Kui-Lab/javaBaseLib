package kuilab_com.lang.function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import sun.reflect.MethodAccessor;
import sun.reflect.ReflectionFactory;

public class MethodCopy {//TODO 加更多参数。
	public static void setReflectionFactory( ReflectionFactory reflectionFactory ){
		rf = reflectionFactory ;
	}
	protected static ReflectionFactory rf = ReflectionFactory.getReflectionFactory() ;
	
//	public static Method copyMethod( Object host, String methodName ){
//		Method method = MethodSupplementer.getMethod( host, methodName ).get(0) ;
//		MethodAccessor macs =new WrappMethodAccessor(host, method) ;
//		return copyMethod( method, macs ) ;
//	}
	
	/**<pre>
	 * Java的反射机制有一个特性是：对于某个类的某个方法，
	 * 反射得出的所有Method实例共享一个{@link MethodAccessor}，
	 * 这样使得我们难以实现对每个Method对象绑定“this”。
	 * （{@link MethodAccessor}在执行时也无法获取自己是被哪个{@link Method}调用的。）
	 * 只有制作新的方法体实例才可以给每个{@link Method}实例绑定对应的数据对象。
	 * 另外{@link ReflectionFactory#copyMethod()}不能直接使用。
	 * 所以只能使用{@link ReflectionFactory#newMethod()}制作新的方法体。
	 * </pre>
	 */
	public static Method copyMethod( Method source ){
		int slot = 0 ;
        String signature = null ;
        byte[] annotations = null ;
        byte[] parameterAnnotations = null ;
        byte[] annotationDefault = null ;
		Method ret = rf.newMethod( Object.class, source.getName(), 
				source.getParameterTypes(), source.getReturnType(), source.getExceptionTypes(), 
				Modifier.PUBLIC, slot, signature, 
				annotations, parameterAnnotations, annotationDefault ) ;
		//source.setAccessible( true ) ;
		return ret ;
	}
	
	public static Method copyMethod( Method source, MethodAccessor accss ){
		Method ret = copyMethod( source ) ;
		rf.setMethodAccessor( ret, accss );
		return ret ;
	}
	
	public static class WrappMethodAccessor implements MethodAccessor{
		public WrappMethodAccessor( Object host, Method method ){
			this.actual = method ;
			this.host = host ;
		}
		protected Method actual ;
		protected Object host ;
		@Override
		public Object invoke(Object arg0, Object[] args )
				throws IllegalArgumentException, InvocationTargetException {
			try {
				return actual.invoke( host, args ) ;
			} catch ( IllegalAccessException e ) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
}
