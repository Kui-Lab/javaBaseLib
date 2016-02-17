package kuilab_com.lang.function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kuilab_com.KuilabErrorResource;
import kuilab_com.lang.function.OverloadedMethod.OptimizeStrategy;
import kuilab_com.lang.function.OverloadedMethod.Options;
import sun.reflect.MethodAccessor;

public class OptmzCombinedMethodAccessor implements MethodAccessor {

	protected static final Object[] EMPTY_arr = new Object[0] ;
	
	protected Object host ;
	protected Method wrappingMethod ;
	protected List<Method> noVarArg ;//= new Method[]{} ;
	protected List<Method> varArg ;
	protected Map<Method,Class<?>[]> paramMap = new HashMap<Method, Class<?>[]>() ;
	
	protected Map<Method,Integer> invokeCounter ;
	protected Method lastInvoke ;
	protected int lastCount = 0 ;
	protected Method mostInvoke ;
	protected int mostCount = 0 ;
	
	protected boolean allowApplyHost = false ;
	protected boolean attemptAppendNull = false ;
	protected boolean throwErrIfRecycled = false ;
	protected OverloadedMethod.OptimizeStrategy optimizeStrtg = OverloadedMethod.OptimizeStrategy.NO ;
	
	public OptmzCombinedMethodAccessor( Object host, List<Method> methods, Method wrapping ){
		//this( host, methods, null, false ) ;
		this.host = host ;
		this.wrappingMethod = wrapping ;
		scan( methods ) ;
		lastInvoke = this.getClass().getMethods()[0] ;
	}
	/**
	 * @param refType 可以使用弱引用或软引用
	 * **/
	public OptmzCombinedMethodAccessor( Object host, List<Method> methods, Method wrapping, Options opt ){
		//ms = methods ;Class<? extends Reference<Object>> ref = null ;
		//this.host = new WeakReference<Object>( host ) ;
		this( host, methods, wrapping ) ;
		installOptions( opt ) ;
	}
	
	protected void installOptions( Options opt ){
		attemptAppendNull = opt.autoAppendNull ;
		allowApplyHost = opt.allowApplyHost ;
		setStrategy( opt.optimizeStrategy ) ;
	}
	protected void setupInvokeCounter(){
		invokeCounter = new HashMap<Method, Integer>( paramMap.size() ) ;
		for( Method em : paramMap.keySet() ){
			invokeCounter.put( em, 0 ) ;
		}
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
			paramMap.put( m, ptp ) ;
		}
	}
	
    public Object invoke( Object host, Object[] args ) throws IllegalArgumentException, InvocationTargetException{

		MethodSupplementer.noteInvocation( wrappingMethod, host, this ) ;
    	//所属对象相关逻辑。
	    	if( host == null ){
	    		host = this.host ;
	    	}else if( this.host == null ){
	    		if( allowApplyHost ){
	    		}else
	    			host = this.host ;
	    	}
	    	if( host == null ){
	    		MethodSupplementer.removeInvocation( wrappingMethod, host, this ) ;
	    		throw new IllegalArgumentException( KuilabErrorResource.methodHostMissing( getName() ) ) ;
	    	}
	    	
//			if( host instanceof Reference<?> ){
//				host = ( ( Reference<?> ) this.host ).get() ;
//				if( host == null ){
//					if( throwErrIfRecycled ){
//						Exception exc = new MethodRecycledException( KuilabErrorResource.methodRecycled( getName() ) ) ;
//						throw new InvocationTargetException( exc ) ;
//					}else{} //TODO 静态方法不需要host.
//				}
//			}
		
		if( optimizeStrtg.equals( OverloadedMethod.OptimizeStrategy.LAST )){
			try { 
				Object ret = actualInvoke( lastInvoke, host, args ) ;
//				lastCount ++ ;
//				if( lastInvoke.equals( mostInvoke ) )
//					mostCount ++ ;
				MethodSupplementer.removeInvocation( wrappingMethod, host, this ) ;
				return ret ;
									} catch ( IllegalAccessException e ) {
										
									} catch( IllegalArgumentException | InvocationTargetException e ){
										
									}
		}
		else if( optimizeStrtg.equals( OverloadedMethod.OptimizeStrategy.MOST ) ){
			try{	
				Object ret = actualInvoke( mostInvoke, host, args ) ;
//				mostCount ++ ;
//				if( mostInvoke.equals( lastInvoke ) )
//					lastCount ++ ;
				MethodSupplementer.removeInvocation( wrappingMethod, host, this ) ;
				return ret ;
									} catch ( IllegalAccessException e ) {
									} catch( IllegalArgumentException | InvocationTargetException e ){
									}
		}
			
        if( args == null )
        	args = EMPTY_arr ;
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
		    				lastNoNull = ii ;//这是最后一个非空。lastNoNull用来判断是否已找到最后一个非空。
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
	    		Class<?>[] empt = paramMap.get( em ) ; //em.getParameterTypes() ;
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
					} catch ( Exception e ) {
						//e.printStackTrace();
					}
	    		}else{ //参数个数不符。
	    			
	    		}
	    	}
    	//System.out.println( "匹配的方法个数:"+match.size() ) ; 调试输出。
		//执行匹配到的方法。
	    	if( match.size() > 0 ){
	    		for( Method em : match ){
	    			try {
	    				Object ret = actualInvoke( em, host, args ) ;
	    				MethodSupplementer.removeInvocation( wrappingMethod, host, this ) ;
	    				return ret ;
	    			}catch( IllegalArgumentException e ){
    				}catch( IllegalAccessException e ){
    				}catch( InvocationTargetException e ) {
	    				//System.out.println( "modif:"+mi.getModifiers() ) ;
	    			}
	    		}
	    	}
    	//尝试可能是可变参数的重载体。
	    	for( Method em : varArg ){
	    		Class<?>[] tp = paramMap.get( em ) ;
	    		if( tp.length <= ARG_n ){
	    			if( OverloadedMethod.matchVarArg( tp, argC ) ){
	    				try{
	    					Object r = actualInvoke( em, host, args ) ;
	    					//validVarArg.add( em ) ;
	    					MethodSupplementer.removeInvocation( wrappingMethod, host, this ) ;
							return r ;
						}catch( IllegalArgumentException e ){
							continue ;
	    				}catch( IllegalAccessException e ){
	    					continue ;
	    				}catch( InvocationTargetException e ){
							continue ;
						}catch( Exception e ){
							break ;
						}
	    			}
	    		}
	    	}
    	//System.out.println( match.size()+"个方法体全部执行失败。" );
    	//尝试在实参后加null。
		    	if( attemptAppendNull ){
			    	for( Method em : noVarArg ){
			    		Class<?>[] empt = paramMap.get( em ) ;
			    		//List<Class<?>> emptCp = Arrays.asList( empt ) ;
			    		if( match.indexOf( em ) > 0 )
			    			continue ; //尝试过的匹配，忽略。
			    		if( empt.length > ARG_n ){//IF 形参个数比实参多 THEN 尝试填充null。
							if( OverloadedMethod.matchWithNull( empt, argC ) ){
								try{
									Object[] argFill = Arrays.copyOf( args , empt.length ) ;
									Object ret = actualInvoke( em, host, argFill ) ;
									MethodSupplementer.removeInvocation( wrappingMethod, host, this ) ;
									return ret ;
								}catch( IllegalArgumentException e ){
			    				}catch( IllegalAccessException e ){
			    				}catch( InvocationTargetException e ){
								}
							}
			    		}else{
			    			
			    		}
			    	}
		    	}
    	
		//调用次数最多统计更新。
		MethodSupplementer.removeInvocation( wrappingMethod, host, this ) ;
    	String serr = KuilabErrorResource.methodArgMismatch( getName(), args );
    	throw new InvocationTargetException( null, serr ) ;
    }
    
    public Object invoke( Object...args ) throws IllegalArgumentException, InvocationTargetException, IllegalAccessException{
    	return invoke( null, args ) ;
    }
	public Object invoke( List<?> args )throws IllegalArgumentException, InvocationTargetException, IllegalAccessException{
		return invoke( null, args ) ;
	}
	
	protected Object actualInvoke( Method m, Object host, Object[] args ) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		if( this.optimizeStrtg.equals( OverloadedMethod.OptimizeStrategy.LAST ) ){
			lastInvoke = m ;//TODO 效率可优化。
		}
		Object ret = null ;
		ret = m.invoke( host, args ) ;
		counting( m ) ;
		return ret ;
	}
	
			protected void counting( Method curIvk ){
				if( optimizeStrtg.equals( OptimizeStrategy.NO ) )
					return ;
				if( lastInvoke.equals( curIvk ) ){//MOST策略下仍旧使用last。
					lastCount ++ ; //调用了lastInvoke。
				}else{
					lastCount = invokeCounter.get( lastInvoke ) ;
					lastCount ++ ; //这句可以从if，else取出。
					lastInvoke = curIvk ;
				}
		    	if( invokeCounter != null ){//使用过最多计数策略即开启统计，因为以后可能会再使用。
					if( mostInvoke.equals( curIvk ) )
						mostCount ++ ;
		    		if( lastCount > mostCount ){//最多调用被超越。此时last即是current。
		    			//这个判断的正确性依赖于？lastCount计数总是开启的。//TODO
		    			invokeCounter.replace( mostInvoke, mostCount ) ;
		    			mostInvoke = lastInvoke ;
		    			mostCount = lastCount ;
		    		}
		    	}
		    	if( lastInvoke.equals( mostInvoke ) ){
		    		
		    	}
			}
	
	///////////////////////////////////////////////////////////////////////////////////////
	public void setStrategy( OverloadedMethod.OptimizeStrategy v ){
		optimizeStrtg = v ;
		switch( v ){
			case NO :
				break ;
			case LAST :
				break ;
			case MOST :
				if( invokeCounter == null )
					setupInvokeCounter() ;
				break ;
		}
	}
	
	public OverloadedMethod.OptimizeStrategy getStrategy(){
		return optimizeStrtg ;
	}
	
	@SuppressWarnings("unchecked")
	public Map<Method, Integer> getInvokeCounts( boolean copy ){
		if( copy ){
			return (Map<Method, Integer>) 
					( (HashMap<Method, Integer>)this.invokeCounter ).clone() ;
		}
		return invokeCounter ;
	}
	
	@SuppressWarnings("unchecked")
	public Map<Method,Class<?>[]> getParamMap(){
		Object ret = ( ( HashMap<Method, Class<?>[]> ) paramMap ).clone() ;
		return ( Map<Method, Class<?>[]> ) ret ;
	}
	
	public Object getHost(){
		return host ;
	}

	public Object getManipulator( ){//TODO 接口没定好。
		return getManipulator( null ) ;
	}
	public Object getManipulator( Object securityKey ){
		CombinedMethodMnplt mnplt = new CombinedMethodMnplt( this ) ;
		return mnplt ;
	}
	
    /////////////////////////////////////////////////////////////////////////////////////////////
    
    public int getCombinedNum(){
    	return paramMap.size() ;
    }
    
    public String getName(){
    	if( noVarArg.size() > 0 )
    		return noVarArg.get( 0 ).getName() ;
    	return varArg.get( 0 ).getName() ;
    }
    
//	public boolean isRecycled() {
//		if( host instanceof Reference<?> )
//			return ((Reference<?>)host).get().equals( null ) ;
//		return false;
//	}
	
	public String toString(){
    	return super.toString() +" with:"+host.toString() +"::"+getName()+
    			" ( "+ getCombinedNum()+ " override bodys combined in.)" ;
    }
	
	protected void finalize(){
		paramMap.clear() ; paramMap = null ;
		noVarArg.clear() ; noVarArg = null ;
		varArg.clear() ;   varArg = null ;
		lastInvoke = null ;
		mostInvoke = null ;
		host = null ;
	}
}