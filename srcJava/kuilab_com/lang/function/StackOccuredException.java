package kuilab_com.lang.function;

import kuilab_com.KuilabErrorResource;
import kuilab_com.lang.supplements.ReturningException;

public class StackOccuredException extends ReturningException {
	

	public StackOccuredException( Object stack ) {
		super( stack ) ;
		if( stack instanceof InvocationInfo ){
		} else 
			throw new IllegalArgumentException(  ) ;
		
	}

//	public StackOccuredException(Object returningValue, String message) {
//		super(returningValue, message);
//		// TODO Auto-generated constructor stub
//	}

//	public StackOccuredException(Object returningValue, Throwable cause) {
//		super(returningValue, cause);
//		// TODO Auto-generated constructor stub
//	}
	
//	public StackOccuredException(Object returningValue, String message,
//			Throwable cause) {
//		super(returningValue, message, cause);
//		// TODO Auto-generated constructor stub
//	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7462814659617055256L;

}
