package maxcsp;

public class PFC extends MaxCSPSolver {

	public PFC(Problem problem) {
		super(problem);
	}
	@Override
	protected int bound(Assignment ass){
		return calcDistance(ass) + calcIC(ass);
	}
	
	protected int calcIC(Assignment ass){
		int ans = 0;
		for(Variable var : ass.getUnassignedVars()){
			int varMinConflicts = Integer.MAX_VALUE;
			for(Integer value : _problem._domain){
				int ic = calcSingleVariableDistance(var, value, ass);
				varMinConflicts = Math.min(varMinConflicts, ic);
			}
			ans+=varMinConflicts;
		}
		return ans;
	}
	public String getName() {
		return "PFC";
	}

}
