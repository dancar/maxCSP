package maxcsp;

public class PFC extends MaxCSPSolver {

	public PFC(Problem problem) {
		super(problem);
	}
	@Override
	protected int bound(Assignment ass){
		int lowerBound = ass._distance;
		for(Variable var : ass.getUnassignedVars()){
			int varMinConflicts = Integer.MAX_VALUE;
			for(Integer value : _problem._domain){
				int ic = calcSingleVariableDistance(var, value, ass);
				varMinConflicts = Math.min(varMinConflicts, ic);
			}
			lowerBound+=varMinConflicts;
		}
		
		return lowerBound;
	}
	

}
