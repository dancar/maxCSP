package maxcsp;

public  class OrderedPair<T> {
	public final T _left;
	public final T _right;
	public OrderedPair(T _left, T _right) {
		super();
		this._left = _left;
		this._right = _right;
	}
	public OrderedPair (OrderedPair<T> other){
		this(other._left,other._right);
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object other){
		boolean ans = false;
		if (other instanceof OrderedPair<?>)
			ans = _left.equals(((OrderedPair<T>) other)._left) & _right.equals(((OrderedPair<T>) other)._right);
		return ans;
	}
	@Override
	public String toString(){
		String ans = String.format("<%s , %s>", _left,_right);
		return ans;
	}
	@Override
	public int hashCode(){
		return _left.hashCode() + _right.hashCode();
	}
}
