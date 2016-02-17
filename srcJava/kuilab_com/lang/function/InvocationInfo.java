package kuilab_com.lang.function;

import java.lang.reflect.Method;
import java.util.function.Function;

import sun.reflect.MethodAccessor;

/**
 * Oracle在设计JDK8的{@link Function}与{@link Method}没有使用抽象，
 * 所以我们也只能为他们各自写方法，就是这么难受。
 * 
 * @author kui.
 */
public class InvocationInfo {
	
	////getters,setters
			public Function<?, ?> getFunction(){
				return function ;
			}
			public void setFunction( Function<?,?> function ) throws Exception{
				if( method != null )
					throw new Exception( "Function与Method不能同时存在" ) ;//TODO 未定义Exception类型。
				this.function = function ;
			}
			
			public Method getMethod() {
				return method;
			}
			public void setMethod( Method method ) throws Exception {
				if( method != null )
					throw new Exception( "Function与Method不能同时存在" ) ;
				this.method = method;
			}
			
			public Object getMethodOrFunction(){
				if( method != null )
					return method ;
				return function ;
			}
			
			public MethodAccessor getAccssr() {
				return accss;
			}
			public void setAccssr( MethodAccessor accss ) {
				this.accss = accss;
			}
			
			public Object getHost() {
				return host;
			}
			public void setHost( Object host ) {
				this.host = host;
			}

			public Thread getThread() {
				return thread;
			}
			public void setThread(Thread thread) {
				this.thread = thread;
			}
			
			public int getIndex() {
				return index;
			}
			public void setIndex(int index) {
				this.index = index;
			}
	////end of getters,setters
	
	protected Method method ;
	protected Function<?, ?> function ;
	protected Object host ;
	protected MethodAccessor accss ;
	protected Thread thread ;
	protected int index = -1 ;
	
	////构造函数。
			public InvocationInfo( Object host, Method method, MethodAccessor accessor ){
				this.host = host ;
				this.method = method ;
				this.accss = accessor ;
			}
			
			public InvocationInfo( Function<?, ?> function, MethodAccessor accessor ){
				this.function = function ;
				this.accss = accessor ;
			}
			
			public InvocationInfo( Function<?, ?> function, Method method, MethodAccessor accessor ){
				this( function, accessor ) ;
				this.accss = accessor ;
			}
	////结束：构造函数。
	
	public boolean equals( Object that ){
		InvocationInfo tht = (InvocationInfo) that ;
//		if( method != tht.method )
//			return false ;
//		if( function != tht.function )
//			return false ;
		if( getMethodOrFunction() != tht.getMethodOrFunction() )
			return false ;
		if( host != tht.host )
			return false ;
		if( accss != tht.accss )
			return false ;
		if( thread != tht.thread )
			return false ;
		if( index != tht.index )
			return false ;
		return true ;
	}
}
