package maxcsp;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Problem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5650021309415437018L;
	private int _CCs;
	public final int _varCount;
	public final int _domainSize;
	public final double _P1;
	public final double _P2;
	private final Map<IntegerPair, Constraint> _constraints;
	
	public Problem(int varCount, int domainSize, double p1, double p2) {
		super();
		this._varCount = varCount;
		this._domainSize = domainSize;
		this._P1 = p1;
		this._P2 = p2;
		this._constraints=new HashMap<IntegerPair, Constraint>();
		for(int i=0;i<varCount;i++){
			for (int j=i+1;j<varCount;j++){
				if(Util.chance(_P1)){
					Collection<IntegerPair> possibleValues = new Vector<IntegerPair>();
					for(int a=0;a<_domainSize;a++)
						for(int b=0;b<domainSize;b++){
							if(!Util.chance(p2)){
								possibleValues.add(new IntegerPair(a,b));
							}
						}
					if(possibleValues.size()>0){
						Constraint c = new Constraint(i,j,possibleValues);
						this._constraints.put(new IntegerPair(i,j), c);
					}
				}
			}
		}
	}
	
	public boolean check(int var1, int value1, int var2, int value2){
		boolean ans = true;
		this._CCs++;
		int varLeft, varRight, valueLeft,valueRight;
		if (var1<var2){
			varLeft=var1;
			varRight=var2;
			valueLeft=value1;
			valueRight=value2;
		}
		else{
			varLeft=var2;
			varRight=var1;
			valueLeft=value2;
			valueRight=value1;
		}
		if(!(0<=varLeft & varLeft<varRight & varRight < this._domainSize))
			Util.panic(String.format("Invalid variables: varLeft=%d, varRight=%d, domain=%d",varLeft,varRight,this._domainSize));
		Constraint c = this._constraints.get(new IntegerPair(varLeft,varRight));
		if(c!=null){ //Variables are constrained
			if(!(c._var1==varLeft & c._var2==varRight))
				Util.panic("Wrong constraint in map");
			ans=c.consistent(valueLeft, valueRight);
		}
		return ans;
	}	
	public int getCCs(){
		return this._CCs;
	}
	
	
	
	
	
	
}
