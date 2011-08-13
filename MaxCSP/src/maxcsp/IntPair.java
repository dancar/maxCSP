package maxcsp;

public class IntPair{
	public final int _left;
	public final int _right;
	public IntPair(int left, int right){
		_left=left;
		_right=right;
	}
	@Override
	public String toString(){
		return String.format("(%d,%d)", _left,_right);
	}
	@Override
	public int hashCode(){
		return _left+_right;
	}
	@Override 
	public boolean equals(Object other){
		boolean ans = false;
		if(other instanceof IntPair)
			ans=((IntPair)other)._left ==_left & ((IntPair)other)._right==_right;
		return ans;
	}
	
}