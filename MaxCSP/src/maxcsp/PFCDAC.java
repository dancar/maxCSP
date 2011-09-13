package maxcsp;

import java.util.HashMap;
import java.util.Map;

public class PFCDAC extends PFC {
	protected Map<IntPair,Integer>_dac;
	private int _memo[][][][];
	private final static int
		UNKNOWN=7,
		CONSISTENT=11,
		CONFLICT=-19;
	/**
	 * Calculates for each variable and value,
	 * the amount of variables later in the order with which the two are arc-inconsistent.  
	 */
	public PFCDAC(Problem problem) {
		super(problem);
		_memo = new int[problem._varCount][problem._varCount][problem._domainSize][problem._domainSize];
		for(int[][][] var1: _memo)
			for(int[][] var2: var1)
				for(int[] val1: var2)
					for(int val2=0;val2<problem._domainSize;val2++)
						val1[val2]=UNKNOWN;
		this._dac = new HashMap<IntPair, Integer>();
		for(int var1:problem._vars){
			for(int value1 : problem._domain){
				int DACcount=0;
				for(int var2=var1+1;var2<problem._varCount;var2++){
					boolean isAC=false;
					for(int value2=0;!isAC & value2<problem._domainSize;value2++){
						isAC=this.check(var1, value1, var2, value2);
					}
					if(!isAC)DACcount++;
					
				}
				_dac.put(new IntPair(var1,value1), DACcount);
			}
		}
	}
	@Override
	protected boolean check(int var1, int value1, int var2, int value2) {
		boolean ans;
		switch (_memo[var1][var2][value1][value2]){
		case UNKNOWN:
			_memo[var1][var2][value1][value2]=((ans=super.check(var1, value1, var2, value2))?CONSISTENT:CONFLICT);break;
		case CONSISTENT:
			ans=true;break;
		case CONFLICT:
			ans=false;break;
		default: Util.panic("PFCDAC check: invalid cache value");ans=false;break; 
		}
		return ans;
	}
	@Override
	protected int calcIC(Assignment ass, int var, int val) {
		int ans = calcSingleVariableDistance(var, val, ass)
				+ _dac.get(new IntPair(var,val));
		return ans;
	}
	@Override
	public String getName() {
		return "PFC_DAC";
	}
}