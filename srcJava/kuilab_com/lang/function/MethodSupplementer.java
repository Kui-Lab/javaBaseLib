package kuilab_com.lang.function;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

import sun.reflect.MethodAccessor;
import kuilab_com.KuilabErrorResource;


public class MethodSupplementer {
	
	protected static Map<Thread,List<InvocationInfo>> map = 
		new WeakHashMap<Thread,List<InvocationInfo>>( new HashMap<Thread, List<InvocationInfo>>() ) ;
	
	public static InvocationInfo getTopInvocation(){
		return getTopInvocation( Thread.currentThread() ) ;
	}
	public static InvocationInfo getTopInvocation( Thread thread ){
		List<InvocationInfo> stk = getStack( thread ) ;
		if( stk == null )
			return null ;
		if( stk.size() > 1 ){
			//throw 
		}
		return stk.get( stk.size()-1 ) ;
	}
	
	/**<pre>
	 * 只有通过{@link kuilab_com.lang.function }中的工具类
	 * （{@link MethodWrapper}或{@link OverloadedMethod}）
	 * 造出的Method实例可以获取方法对象本身的引用。
	 * 这个方法的调用，必须在函数体中的第一句，
	 * 否则不能保证获取的方法就是当前调用的方法。
	 * 在被传出本身所在函数体后调用的Lambda中，也不能获取。
	 * 除非是 
	 * </pre>
	 * @return
	 */
	public static Method getCurrentMethod(){
		//TODO 文档
		return getCurrentMethod( Thread.currentThread() ) ;
	}
	
	public static Method getCurrentMethod( Thread thread ){
		InvocationInfo top = getTopInvocation( thread ) ;
		if( top != null )
			return top.getMethod() ;
		return null ;
	}
	
		protected static int[] chkHash( StackTraceElement[] stk ){
			int[] ret = new int[stk.length] ;
			for( int i=0; i<stk.length; i++ ){
				ret[i]= stk[i].hashCode() ;
			}
			return ret ;
		}
	
	protected static int noteInvocation( Method method, Object host, MethodAccessor accessor ){
		return noteInvocation( new InvocationInfo( host, method, accessor ) ) ;
	}
	
	protected static int noteInvocation( Function<?,?> function, Method method, MethodAccessor accessor ){
		return noteInvocation( new InvocationInfo( function, method, accessor ) ) ;
	}
		
	protected static int noteInvocation( InvocationInfo ii ){
		Thread curThd = Thread.currentThread() ;
		List<InvocationInfo> infs ;
		if( ! map.containsKey( curThd ) ){
			infs = new ArrayList<InvocationInfo>(1) ;
			infs.add( ii ) ;
			map.put( curThd, infs ) ;
			return 1 ;
		}
		infs =  map.get( curThd ) ;
		infs.add( ii ) ;
		return infs.size() ;
	}
	
	@SuppressWarnings("unchecked")
	public static List<InvocationInfo> getStack( Thread thread ){
		List<InvocationInfo> stack = map.get( thread ) ;
		if( stack instanceof ArrayList<?> )
			return (List<InvocationInfo>)((ArrayList<?>)stack).clone() ;
		return stack ;
	}

	public static Object removeInvocation( Function<?, ?> function ){
		InvocationInfo info = new InvocationInfo( function, null ) ;
		return removeInvocation( info, Thread.currentThread() );
	}
	public static Object removeInvocation( Function<?, ?> function, Thread thread ){
		InvocationInfo info = new InvocationInfo( function, null ) ;
		return removeInvocation( info, thread );
	}
	 
	public static Object removeInvocation( Method method ){//TODO remove(),改名为rmStack
		return removeInvocation( method , null, null ) ;
	}
	public static Object removeInvocation( Method method, Object host, MethodAccessor accessor ){
		return removeInvocation( method, host, accessor, Thread.currentThread() ) ;
	}//TODO 返回值类型未确定。
	public static boolean removeInvocation( Method method, Object host, MethodAccessor accessor, Thread thread ){
		InvocationInfo info = new InvocationInfo( host, method, accessor ) ;
		return removeInvocation( info, thread ) ;
	}
	public static boolean removeInvocation( InvocationInfo info, Thread thread ){
		List<InvocationInfo> stack = getStack( thread ) ;
		for( InvocationInfo each : stack ){
			if( each.equals( info ) ){
				stack.remove( each ) ;
				return true ;
			}
		}
		return false ;
	}
	
	public static void cleanThread( Thread thread ){
		map.get( thread ).clear();
		//TODO clean()
	}
	
	public static List<Method> getMethod( Object host, String methodName ){
		Method[] ms = host.getClass().getDeclaredMethods() ;
		List<Method> ret = new ArrayList<Method>() ;
		for( Method em : ms ){
			if( em.getName().equals( methodName ) ){
				ret.add( em ) ;
			}
		}
		return ret ;
	}

	public static boolean equlas( Method m1, Method m2 ){//TODO
		if( m1.equals( m2 ) ){
			return true ;
		}else
			return false ;
	}
}
