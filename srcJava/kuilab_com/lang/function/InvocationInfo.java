package kuilab_com.lang.function;

import java.lang.reflect.Method;
import java.util.function.Function;

import sun.reflect.MethodAccessor;

/**
 * Oracle在设计JDK8的{@link Function}与{@link Method}没有使用抽象，
 * 所以。
 * 
 * @author kui.
 */
public class InvocationInfo {
	
	////getters,setters
			
			public Object getMethod() {
				return method;
			}
			public void setMethod( Object method ) throws Exception {
				this.method = method;
			}
			
//			public Object getMethodOrFunction(){
//				if( method != null )
//					return method ;
//				return function ;
//			}
			
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
	
	protected Object method ;
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
			
	////结束：构造函数。
	
	public boolean equals( Object that ){
		InvocationInfo tht = (InvocationInfo) that ;
		if( method != tht.method )
			return false ;
//		if( function != tht.function )
//			return false ;
//		if( getMethodOrFunction() != tht.getMethodOrFunction() )
//			return false ;
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
