package maxcsp;

import java.util.Iterator;

public class PFC extends MaxCSPSolver {

	public PFC(Problem problem) {
		super(problem);
	}
	@Override
	protected int bound(Assignment ass){
		int lowerBound = ass._distance;
		Iterator<Variable>unassignedVars = ass.unassignedVariablesIterator();
		while(unassignedVars.hasNext()){
			Variable var = unassignedVars.next();
			int varMinConflicts = Integer.MAX_VALUE;
			Iterator<Integer> values = _problem._domain.iterator();
			while(values.hasNext()){
				int ic = calcSingleVariableDistance(var, values.next(), ass);
				varMinConflicts = Math.min(varMinConflicts, ic);
			}
			lowerBound+=varMinConflicts;
		}
		
		return lowerBound;
	}
	

}
