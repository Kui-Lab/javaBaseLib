package kuilab_com.lang.supplements;

import kuilab_com.KuilabErrorResource;

/**
 * 特殊用法，有些特殊情况不能正常返回值，则通过抛出意外的形式返回。<br/>
 * 比如有的函数可能返回不止一种类型的对象，但是又不想使用Object来定义。
 * **/
public class ReturningException extends Exception {
	
	private static final long serialVersionUID = 7424425211781508593L;
	
	public ReturningException( Object returningValue ){
		super( KuilabErrorResource.deviantReturning() ) ;
		this.returningValue = returningValue ;
	}

	public ReturningException( Object returningValue, String message ){
		super( message ) ;
		this.returningValue = returningValue ;
	}
	
	public ReturningException( Object returningValue, Throwable cause ){
		super( cause ) ;
		this.returningValue = returningValue ;
	}
	
	public ReturningException( Object returningValue, String message, Throwable cause ){
		super( message, cause ) ;
		this.returningValue = returningValue ;
	}
	
	protected Object returningValue ;

	public Object getReturningValue() {
		return returningValue ;
	}

	public void setReturningValue( Object returningValue ) {
		this.returningValue = returningValue ;
	}
}
