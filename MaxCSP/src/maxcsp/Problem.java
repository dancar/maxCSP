package maxcsp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
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
		this(varsCount, domainSize, UNKNOWN, UNKNOWN, constraints);
	}
		
	public Problem(int varsCount, int domainSize, double p1, double p2, Collection<Constraint> constraints){
		this._varCount=varsCount;
		this._domainSize=domainSize;
		this._P1=p1;
		this._P2=p2;
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
	@Override
	public String toString(){
		String ans = String.format("CSP: Vars=%d, Domain=%d, P1=%1.2f, P2=%1.2f.", 
				_varCount,_domainSize,_P1,_P2
				);
		return ans;
	}
	public static final String P_VARIABLE_COUNT="variable_count";
	public static final String P_DOMAIN_SIZE="domain_size";
	public static final String P_P1 = "p1";
	public static final String P_P2 = "p2";
	public static final String P_CONSTRAINTS_COUNT = "constraints_count";
	public static final String P_CONSTRAINT = "constraint_%d_%s";
	public static final String P_VAR_LEFT="left";
	public static final String P_VAR_RIGHT="right";
	public static final String P_POSSIBLE_VALUES = "possible_values";
	public static final String P_PAIR_SPLIT = ":";
	public static final String P_LIST_SPLIT=",";

	
	public String print(){
		String ans = "";
		ans+=printProperty(P_VARIABLE_COUNT, _varCount);
		ans+=printProperty(P_DOMAIN_SIZE, +_domainSize);
		ans+=printProperty(P_P1, _P1);
		ans+=printProperty(P_P2, _P2);
		ans+=printProperty(P_CONSTRAINTS_COUNT, _constraints.size());
		
		Iterator<UnorderedPair<Variable>> itr = _constraints.keySet().iterator();
		int c_counter=0;
		while(itr.hasNext()){
			UnorderedPair<Variable> variables = itr.next();
			Constraint c = _constraints.get(variables);
			ans+=printProperty(String.format(P_CONSTRAINT,c_counter,P_VAR_LEFT), variables._left._id);
			ans+=printProperty(String.format(P_CONSTRAINT,c_counter,P_VAR_RIGHT), variables._right._id);
			String list = "";
			Iterator<OrderedPair<Integer>> values = c._possibleValues.iterator();
			while(values.hasNext()){
				OrderedPair<Integer> pair = values.next();
				list+=pair._left+ P_PAIR_SPLIT +pair._right 
						+ (values.hasNext() ? P_LIST_SPLIT : "");
				
			}
			ans+=printProperty(String.format(P_CONSTRAINT, c_counter,P_POSSIBLE_VALUES),list);
			c_counter++;
		}
		return ans;
	}
	private String printProperty(String key, Object value){
		return key + "=" + value.toString() + "\n"; 
	}
	public void toFile(String path)throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(path));
		out.write(print());
		out.close();
	}
	public static Problem fromFile(String path)throws Exception{
		Problem ans = null;
		double p1, p2;
		int varsCount, domainSize, constraintsCount;
		Collection<Constraint> constraints = new Vector<Constraint>();
		Properties p = new Properties();
		p.load(new FileInputStream(path));
		varsCount=Integer.parseInt(p.getProperty(P_VARIABLE_COUNT));
		domainSize = Integer.parseInt(p.getProperty(P_DOMAIN_SIZE));
		constraintsCount = Integer.parseInt(p.getProperty(P_CONSTRAINTS_COUNT));
		p1 = Double.parseDouble(p.getProperty(P_P1));
		p2 = Double.parseDouble(p.getProperty(P_P2));
		for(int i=0;i<constraintsCount;i++){
			int varLeft = Integer.parseInt(p.getProperty(String.format(P_CONSTRAINT, i,P_VAR_LEFT)));
			int varRight= Integer.parseInt(p.getProperty(String.format(P_CONSTRAINT, i,P_VAR_RIGHT)));
			Collection<OrderedPair<Integer>> possibleValues = new Vector<OrderedPair<Integer>>();
			String valuelist[] = p.getProperty(String.format(P_CONSTRAINT, i,P_POSSIBLE_VALUES)).split(P_LIST_SPLIT);
			for(String pair : valuelist){
				String pairVals[]= pair.split(P_PAIR_SPLIT);
				if(!pairVals[0].isEmpty()){
					int valLeft = Integer.parseInt(pairVals[0]);
					int valRight= Integer.parseInt(pairVals[1]);
					possibleValues.add(new OrderedPair<Integer>(valLeft,valRight));
				}
			}
			constraints.add(new Constraint(new Variable(varLeft),
					new Variable(varRight),
					possibleValues));
			
		}
		ans = new Problem(varsCount, domainSize, p1,p2,constraints);
		return ans;
	}
}
