package kuilab_com.lang.supplements;

/**
 * 代码编写者认为不可能发生的错误，如发生证明他（她）的设计可能出现了谬误或漏洞。
 * @author kui.
 */
public class ProgrammingMistakenException extends Exception {

	private static final long serialVersionUID = 2250209269660035332L;

	public ProgrammingMistakenException( String message ){
		super( message ) ;
	}
	
	public ProgrammingMistakenException( Throwable cause ){
		super( cause ) ;
	}
	
	public ProgrammingMistakenException( String message, Throwable cause ){
		super( message, cause ) ;
	}
	
	public ProgrammingMistakenException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrack ){
		super( message, cause, enableSuppression, writableStackTrack ) ;
	}
	
}
