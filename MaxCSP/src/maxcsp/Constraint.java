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
	public final Variable _var1;
	public final Variable _var2;
	public final Collection<OrderedPair<Integer>> _possibleValues;
	public Constraint(Variable var1, Variable var2,
			Collection<OrderedPair<Integer>> possibleValues) {
		super();
		this._var1 = var1;
		this._var2 = var2;
		this._possibleValues = new Vector<OrderedPair<Integer>>(possibleValues.size());
		Iterator<OrderedPair<Integer>> itr = possibleValues.iterator();
		while(itr.hasNext()){
			this._possibleValues.add(itr.next());
		}
	}
	public boolean consistent(Variable var1, Variable var2){
		boolean ans = true;
		if(!(var1.isAssigned() & var2.isAssigned()))
			Util.panic("Constraint: consistency check given unassigned variable(s)");
		if(this._var1.equals(var1) & this._var2.equals(var2))
			ans=consistent(var1.value(),var2.value());
		else if(this._var2.equals(var1) & this._var1.equals(var2))
			ans=consistent(var2.value(),var1.value());
		else
			Util.panic("Constraint: consistency check given wrong variables.");
		return ans;
	}
	public boolean consistent(int value1, int value2){
		boolean ans=false;
		Iterator<OrderedPair<Integer>> itr = this._possibleValues.iterator();
		while(!ans & itr.hasNext()){
			OrderedPair<Integer> cur = itr.next();
			ans = cur._left==value1 & cur._right==value2;
		}
		return ans;
		
	}
	@Override
	public String toString(){
		String ans = String.format("Constraint between %s and %s, possibleValues: %s",_var1,_var2,_possibleValues);
		return ans;
	}
}
