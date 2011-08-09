package maxcsp;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class Constraint implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6744599457394601133L;
	public final int _var1;
	public final int _var2;
	public final Collection<IntegerPair> _possibleValues;
	public Constraint(int _var1, int _var2,
			Collection<IntegerPair> possibleValues) {
		super();
		this._var1 = _var1;
		this._var2 = _var2;
		this._possibleValues = new Vector<IntegerPair>(possibleValues.size());
		Iterator<IntegerPair> itr = possibleValues.iterator();
		while(itr.hasNext()){
			this._possibleValues.add(itr.next());
		}
	}
	public boolean consistent(int value1, int value2){
		boolean ans=false;
		Iterator<IntegerPair> itr = this._possibleValues.iterator();
		while(!ans & itr.hasNext()){
			IntegerPair cur = itr.next();
			ans = cur._a==value1 & cur._b==value2;
		}
		return ans;
		
	}
}
