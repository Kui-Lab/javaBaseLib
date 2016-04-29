package kuilab_com.lang.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;

/**
 * <pre>
 * 对于某些类，他们的{@code equals()}方法对于不同实例会返回true，
 * 而且无法扩展以实现所需的细节比较。
 * 因为{@link ArrayList}使用{@link Object#equals()}方法而不是“==”来比较对象。
 * 所以{@link ArrayList}无法正常区别他们，也就不能正确的执行查找与删除等操作。
 * 这个类就是使用“==”来比较对象的{@link ArrayList}。
 * Java中的“==”是比较对象内存地址。
 * 
 * @author kui.
 * </pre>
 * **/
public class AddressIndexingList<E> extends ArrayList<E> {
	
	private static final long serialVersionUID = 3838003602282620162L;

	
	public AddressIndexingList(){
		super() ;
	}
	
	public AddressIndexingList( Collection<E> c ){
		super( c ) ;
	}
	
	public AddressIndexingList( int initCapacity ){
		super( initCapacity ) ;
	}
	
	@Override
	public int indexOf( Object o ) {
		if( o == null ){
			return super.indexOf( null ) ;
		}
		ListIterator<E> itr = listIterator() ;
			while( itr.hasNext() ){
				E item = itr.next() ;
				if( o == item )
					return itr.nextIndex() -1 ;
			}
		return -1 ;
	}
	
	@Override
	public boolean remove( Object o ) {
        if ( o == null ) {
           return super.remove( null ) ;
        } else {
           int idx = indexOf( o ) ;
           if( idx >= 0 ){
        	   super.remove( idx ) ;
        	   return true ;
           }else
        	   return false ;
        }
	}
	
}
