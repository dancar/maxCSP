package maxcsp;

import java.util.Vector;

public class Constraint{
	public final int _leftVar;
	public final int _rightVar;
	private final java.util.Vector<IntPair> _allowedValues;
	
	public Constraint(int _leftVar, int _rightVar,
			Vector<IntPair> _alloedValues) {
		super();
		this._leftVar = _leftVar;
		this._rightVar = _rightVar;
		this._allowedValues = _alloedValues;
	}
	public boolean consistent(int left, int right){
		for(IntPair p : _allowedValues)
			if(p._left==left & p._right==right)
				return true;
		return false;
	}
	
	public Vector<IntPair>getAllowedValues(){
		return new Vector<IntPair>(_allowedValues);
	}
}