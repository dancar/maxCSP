package maxcsp;

import java.io.Serializable;
import java.util.Vector;

public class MaxCSPSolver implements Serializable{
	private static final long serialVersionUID = -2450853734027328704L;
	public final Problem _problem;
	public MaxCSPSolver(Problem problem){
		this._problem=problem;
	}
	
	public int solve(Problem problem){
		int distance=0;
		Assignment _ass = new Assignment(problem._domainSize);
		int lowerBound = Integer.MAX_VALUE;
		int upperBound=0;
		
		//return null;
		return -1;
	}
	private Assignment branch(Assignment partial, Variable variable, int upperBound){
		if(partial.isAssigned(variable))
			Util.panic("Trying to assign alreayd-assigned variable: " + variable);
		Assignment newAssignment = new Assignment(partial);
		DynamicDomain domain = new DynamicDomain(this._problem._domainSize);
		int lowerBound=partial._distance;
		int value;
		while(lowerBound<upperBound & (value=domain.pop())!=DynamicDomain.EMPTY){
			newAssignment.assign(variable, value);
			
//			Assignment rest = 
		}
		
		return null;//distance;
	}
	private int calcSingleVariableDistance(int var, Assignment ass){
		int ans = 0;
//		Iterator<Integer>
		return ans;
	}
}
