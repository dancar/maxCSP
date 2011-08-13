package maxcsp;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

public class Problem {
	public static final int UNKNOWN = -1;
	public final int _varCount;
	public final int _domainSize;
	public final double _P1;
	public final double _P2;
	public final int[] _vars;
	protected final Map<IntPair, Constraint> _constraints;
	
	public Problem(int varCount, int domainSize, double p1, double p2) {
		this._varCount = varCount;
		this._domainSize = domainSize;
		this._P1 = p1;
		this._P2 = p2;
		this._constraints=new HashMap<IntPair, Constraint>();
		_vars=Util.numlist(varCount);
		int[] domain = Util.numlist(domainSize);
		for(int varLeft=0;varLeft<varCount;varLeft++){
			for(int varRight=varLeft+1;varRight<varCount;varRight++){
				if (Util.chance(p1)){
					Vector<IntPair> possibleValues = new Vector<IntPair>((int)Math.pow(domainSize,2));
					boolean addConstraint = false;
					for(int valueLeft:domain){
						for(int valueRight:domain){
							if(!Util.chance(p2)){
								possibleValues.add(new IntPair(valueLeft,valueRight));
							}
							else{
								addConstraint = true;
							}
							
						}
					}
					if(addConstraint){
						Constraint c = new Constraint(varLeft,varRight,possibleValues);
						this._constraints.put(new IntPair(varLeft,varRight),c);
					}
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
		this._constraints=new HashMap<IntPair,Constraint>();
		this._vars=Util.numlist(varsCount);
		for(Constraint c : constraints){
			this._constraints.put(new IntPair(c._leftVar,c._rightVar), c);
		}
	}
	
	public boolean check(int var1, int value1, int var2, int value2){
		boolean ans = true;
		if(Util.order(0,var1,var2,_varCount)){
			Constraint c = this._constraints.get(new IntPair(var1,var2));
			if(c!=null){ //Variables are constrained
				//Sanity check:
				if(c._leftVar!=var1 | c._rightVar!=var2)
					Util.panic("Problem check: wrong constraint chosen");
				ans=c.consistent(value1,value2);
			}
		}
		else if(Util.order(0,var2,var1,_varCount)){
			ans = check(var2,value2,var1,value1);
		}
		else{
			Util.panic("Problem check: invalud variables");
		}
		return ans;
		
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
		
		int c_counter=0;
		for(IntPair variables : _constraints.keySet()){
			Constraint c = _constraints.get(variables);
			ans+=printProperty(String.format(P_CONSTRAINT,c_counter,P_VAR_LEFT), variables._left);
			ans+=printProperty(String.format(P_CONSTRAINT,c_counter,P_VAR_RIGHT), variables._right);
			String list = "";
			for(IntPair values : _constraints.get(variables).getAllowedValues()){
				list+=values._left+ P_PAIR_SPLIT +values._right 
						+  P_LIST_SPLIT;
				
			}
			list=list.substring(0,list.length()-1);
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
		Vector<Constraint> constraints = new Vector<Constraint>();
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
			Vector<IntPair> possibleValues = new Vector<IntPair>();
			String valuelist[] = p.getProperty(String.format(P_CONSTRAINT, i,P_POSSIBLE_VALUES)).split(P_LIST_SPLIT);
			for(String pair : valuelist){
				String pairVals[]= pair.split(P_PAIR_SPLIT);
				if(!pairVals[0].isEmpty()){
					int valLeft = Integer.parseInt(pairVals[0]);
					int valRight= Integer.parseInt(pairVals[1]);
					possibleValues.add(new IntPair(valLeft,valRight));
				}
			}
			constraints.add(new Constraint(varLeft,varRight,possibleValues));
			
		}
		ans = new Problem(varsCount, domainSize, p1,p2,constraints);
		return ans;
	}
}
