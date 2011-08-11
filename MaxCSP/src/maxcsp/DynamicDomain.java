package maxcsp;

import java.io.Serializable;
import java.util.Vector;

public class DynamicDomain implements Serializable{
	public static final int EMPTY = -1;
	private static final long serialVersionUID = 297182141884991450L;
	public final int _domainSize;
	private final Vector<Integer> _vec;
	public DynamicDomain(int domainSize) {
		this._domainSize = domainSize;
		this._vec = new Vector<Integer>(domainSize);
		this.reset();
	}
	public void reset(){
		this._vec.clear();
		for(int i = 0;i<this._domainSize;i++)
			this._vec.add(new Integer(i));
	}
	public int pop(){
		if (this._vec.size()==0)
			return DynamicDomain.EMPTY;
		else
			return this._vec.elementAt(0);
	}

}
