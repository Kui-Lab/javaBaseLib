package kuilab_com.lang.function;

import kuilab_com.KuilabErrorResource;

public class MethodNotFoundException extends Exception {

	private static final long serialVersionUID = -7298670048756533885L;
	
	MethodNotFoundException( String methodName ){
		super( KuilabErrorResource.methodNotFound( methodName ) ) ;
	}
}
