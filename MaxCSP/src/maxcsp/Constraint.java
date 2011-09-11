package maxcsp;

import java.util.Vector;

public class Constraint{
	public final int _leftVar;
	public final int _rightVar;
	private final boolean [][] _allowed;
	
	public Constraint(int _leftVar, int _rightVar,
			Vector<IntPair> consistentValues) {
		this._leftVar = _leftVar;
		this._rightVar = _rightVar;
		int maxValue = -1;
		for(IntPair v:consistentValues){
			maxValue = Math.max(Math.max(maxValue, v._left), v._right);
		}
		maxValue++;
		this._allowed = new boolean[maxValue][maxValue];
		for(boolean right[]: _allowed){
			for(int i=0;i<right.length;i++)
				right[i]=false;
		}
		for(IntPair p:consistentValues){
			_allowed[p._left][p._right]=true;
		}
	}
	public boolean consistent(int left, int right){
		boolean ans = false;
		int max = _allowed.length;
		if(left<0 | right<0)
			Util.panic("Constraint check: value < 0");
		if(left<max & right<max)
			ans=_allowed[left][right];
		return ans;
	}
	
	public Vector<IntPair>getAllowedValues(){
		int max=_allowed.length;
		Vector<IntPair> ans = new Vector<IntPair>();
		for(int left=0;left<max;left++)
			for(int right=0;right<max;right++)
				if(_allowed[left][right])
					ans.add(new IntPair(left,right));
		return ans;
	}
}