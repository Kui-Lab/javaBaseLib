package kuilab_com.lang.function;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CombinedMethodMnplt {
	public CombinedMethodMnplt( OptmzCombinedMethodAccessor target ){
		this.target = target ;
	}
	
	OptmzCombinedMethodAccessor target ;
	
	public Map<Method, Integer> getInvokeCounts( boolean copy ){
		return target.getInvokeCounts( copy ) ;
	}
	public Map<Method, Integer> getInvokeCounts( ){
		return target.getInvokeCounts( true ) ;
	}
	
	public Method[] getCombinedMethods(){
		return (Method[]) target.paramMap.keySet().toArray()  ;
	}
}
