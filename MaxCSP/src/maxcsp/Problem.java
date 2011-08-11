package maxcsp;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class Problem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5650021309415437018L;
	public static final int UNKNOWN = -1;
	private int _CCs;
	public final int _varCount;
	public final int _domainSize;
	public final double _P1;
	public final double _P2;
	protected final Map<UnorderedPair<Variable>, Constraint> _constraints;
	protected final Vector<Variable>_vars;
	protected final Vector<Integer> _domain;
	
	public Problem(int varCount, int domainSize, double p1, double p2) {
		super();
		this._varCount = varCount;
		this._domainSize = domainSize;
		this._P1 = p1;
		this._P2 = p2;
		this._constraints=new HashMap<UnorderedPair<Variable>, Constraint>();
		this._vars=Variable.createVariableList(varCount);
		this._domain=Util.numlist(_domainSize);
		Iterator<OrderedPair<Variable>> varsIter = Util.differentPairsIterator(this._vars);
		while(varsIter.hasNext()){
			UnorderedPair<Variable> vars = new UnorderedPair<Variable>(varsIter.next());
			if (Util.chance(p1)){
				Collection<OrderedPair<Integer>> possibleValues = new Vector<OrderedPair<Integer>>((int)Math.pow(domainSize, 2));
				Iterator<OrderedPair<Integer>> valuesIter = Util.pairsIterator(this._domain);
				boolean addConstraint = false;
				while(valuesIter.hasNext()){
					OrderedPair<Integer> values = valuesIter.next();
					if(!Util.chance(p2)){
						possibleValues.add(values);
					}
					else{
						addConstraint = true;
					}
				}
				if(addConstraint){
					Constraint c = new Constraint(vars._left,vars._right,possibleValues);
					this._constraints.put(vars,c);
				}
			}
		}
	}
	public Problem(int varsCount, int domainSize, Collection<Constraint> constraints){
		this._varCount=varsCount;
		this._domainSize=domainSize;
		this._P1=UNKNOWN;
		this._P2=UNKNOWN;
		this._constraints=new HashMap<UnorderedPair<Variable>,Constraint>();
		this._vars=Variable.createVariableList(this._varCount);
		this._domain=Util.numlist(domainSize);
		Iterator<Constraint> itr = constraints.iterator();
		while(itr.hasNext()){
			Constraint c = itr.next();
			this._constraints.put(new UnorderedPair<Variable>(c._var1,c._var2), c);
		}
	}
	
	public boolean check(Variable assignedVar1, Variable assignedVar2){
		boolean ans = true;
		if (!_vars.contains(assignedVar1) | !_vars.contains(assignedVar2))
			Util.panic("Problem check: variable(s) inexistence");
		Constraint c = this._constraints.get(new UnorderedPair<Variable>(assignedVar1,assignedVar2));
		if(c!=null){ //Variables are constrained
//			boolean check=true;
//			check &=c._var1.equals(assignedVar1) | c._var1.equals(assignedVar2);
//			check &=c._var2.equals(assignedVar1) | c._var2.equals(assignedVar2);
//			check &=!(c._var1.equals(c._var2));
//			if (!check)
//				Util.panic("Problem check - wrong constraint taken");
			ans=c.consistent(assignedVar1,assignedVar2);
		}
		this._CCs++;
		return ans;
	}	
	public int getCCs(){
		return this._CCs;
	}
	
	
}
