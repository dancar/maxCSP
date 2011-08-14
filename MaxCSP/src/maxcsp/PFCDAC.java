package maxcsp;

import java.util.HashMap;

public class PFCDAC extends PFC {
	protected java.util.HashMap<IntPair,Integer>_dac;
	/**
	 * Calculates for each variable and value,
	 * the amount of variables later in the order with which the two are arc-inconsistent.  
	 */
	public PFCDAC(Problem problem) {
		super(problem);
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
	protected int calcIC(Assignment ass, int var, int val) {
		int ans = calcSingleVariableDistance(var, val, ass)
				+ _dac.get(new IntPair(var,val));
		return ans;
	}
	
	@Override
	public String getName() {
		return "PFC-DAC";
	}
	

}
//private class IntTrio{
//	public final int _x,_y,_z;
//	public IntTrio(int _x, int _y, int _z) {
//		this._x = _x;
//		this._y = _y;
//		this._z = _z;
//	}
//	@Override
//	public int hashCode(){return _x+_y+_z;}
//	@Override
//	public boolean equals(Object other){
//		boolean ans = false;
//		if(other instanceof IntTrio){
//			IntTrio o = (IntTrio) other;
//			ans=o._x==_x & o._y==_y & o._z==_z;
//		}
//		return ans;
//	}
//}
