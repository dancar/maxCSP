package maxcsp;

public class UnorderedPair<T> extends OrderedPair<T> {

	public UnorderedPair(T _left, T _right) {
		super(_left, _right);
	}
	public UnorderedPair(OrderedPair<T> p) {
		super(p._left, p._right);
	}
	
	@Override
	public boolean equals(Object other){
		boolean ans = false;
		if (other instanceof UnorderedPair){
			@SuppressWarnings("unchecked")
			UnorderedPair<T> o = (UnorderedPair<T>)other;
			ans = o._left.equals(this._left) & o._right.equals(this._right);
			ans = ans | o._left.equals(this._right) & o._right.equals(this._left);
		}	
		return ans;
	}
	@Override
	public String toString(){
		String ans = String.format("{%d , %d}", _left,_right);
		return ans;
	}
}
