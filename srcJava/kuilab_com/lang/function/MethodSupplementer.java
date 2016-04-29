package kuilab_com.lang.function;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Function;

import kuilab_com.lang.util.AddressIndexingList;
import sun.reflect.MethodAccessor;


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
	
	public static void setDaemon( Daemon daemon_ ){
		if( Objects.isNull( daemon_ ) )
			daemon_ = idleDaemon ;
		daemon = daemon_ ;
	}
	/**监控可能出错的运行情况。**/
	public interface Daemon{
		/**发生了移除不在顶部的条目的行为。不在顶部，即不是最后一个被调用。逻辑错误的可能性比较大。**/
		Object removeNotAtTop( InvocationInfo removing, List<InvocationInfo> stack, Thread thread ) ;
		/**发生了移除不存在的条目的行为。**/
		Object removeNonexistent( InvocationInfo removing, List<InvocationInfo> stack, Thread thread ) ;
		/**发生了递归调用（包括非直接的）。仅当attentionRecurrence()返回true时会通知。**/
		Object recurrence( InvocationInfo invoking, List<InvocationInfo> stack, Thread thread ) ;
		/**递归调用是否通知**/
		boolean attentionRecurrence() ;
	}
	protected static Daemon idleDaemon = new Daemon(){
		public Object removeNotAtTop( InvocationInfo removing, List<InvocationInfo> stack, Thread thread ){ return null; } ;
		public Object removeNonexistent( InvocationInfo removing, List<InvocationInfo> stack, Thread thread ){ return null; } ;
		public Object recurrence( InvocationInfo invoking, List<InvocationInfo> stack, Thread thread ){ return null; } ;
		public boolean attentionRecurrence(){ return false ; } ;
	} ;
	protected static Daemon daemon = idleDaemon ;
	/**<pre>
	 * 正在执行的函数可以获取它本身的对象引用。
	 * 但只有通过{@link kuilab_com.lang.function }中的工具类
	 * （{@link MethodWrapper}或{@link OverloadedMethod}）
	 * 造出的Method实例可以获取方法对象本身的引用。
	 * 这个方法的调用，必须在函数体中的第一句，
	 * 否则不能保证获取的方法就是当前调用的方法。
	 * 因为如果执行其它语句，引发新的包装方法执行，
	 * getCurrentMethod()将会得到最后执行的那个方法引用。
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
			return (Method) top.getMethod() ;
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
		List<InvocationInfo> stack ;
		synchronized ( map ) {
			if( ! map.containsKey( curThd ) ){
				stack = new AddressIndexingList<InvocationInfo>( 0 ) ;
				map.put( curThd, stack ) ;
			}else
				stack = map.get( curThd ) ;
		}
		if( stack.indexOf( ii ) != -1 ){//发生递归，包括非直接的递归。
			daemon.recurrence( ii, stack, curThd ) ;
		}
		synchronized( stack ){
			stack.add( ii ) ;
		}
		return stack.size() ;
	}
	
	@SuppressWarnings("unchecked")
	public static List<InvocationInfo> getStack( Thread thread ){
		if( map.containsKey( thread ) ){
			List<InvocationInfo> stack = map.get( thread ) ;
			if( stack instanceof ArrayList<?> )
				return (List<InvocationInfo>)((ArrayList<?>)stack).clone() ;
			return stack ;
		}else
			return null ;
	}

	public static Object removeInvocation( Method method ){
		return removeInvocation( method , null, null ) ;
	}
	public static Object removeInvocation( Method method, Object host, MethodAccessor accessor ){
		return removeInvocation( method, host, accessor, Thread.currentThread() ) ;
	}//TODO 返回值类型未确定。
	public static boolean removeInvocation( Method method, Object host, MethodAccessor accessor, Thread thread ){
		InvocationInfo info = new InvocationInfo( host, method, accessor ) ;
		return removeInvocation( info, thread ) ;
	}
	public static boolean removeInvocation( InvocationInfo cur, Thread thread ){//TODO 制作InvocationInfo实例开销大。
		List<InvocationInfo> stack = getStack( thread ) ;
		synchronized( stack ){
			if( Objects.isNull( stack) ){
				daemon.removeNonexistent( cur, stack, thread ) ;
				return false ;
			}
			int p = stack.lastIndexOf( cur ) ;
			if( p == -1 ){
				daemon.removeNonexistent( cur, stack, thread ) ;
				return false ;
			}
			if( p != stack.size()-1 ){
				daemon.removeNotAtTop( cur, stack, thread ) ;
			}
			stack.remove( p ) ;
			return true ;
		}
	}
	
	public static void cleanThread( Thread thread ){
		if( map.containsKey( thread ) )
			map.get( thread ).clear() ;
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
