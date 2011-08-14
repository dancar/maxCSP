package maxcsp;

public class PFC extends BranchAndBoundSolver {

	public PFC(Problem problem) {
		super(problem);
	}
	
	@Override
	protected int lookahead(Assignment ass,int var, int val){
		int ans = 0;
		for(int unassignedVar : _problem._vars){
			if(!ass.isAssigned(unassignedVar)){
				int varMinConflicts = Integer.MAX_VALUE;
				for(int value : _problem._domain){
					int ic = calcIC(ass,unassignedVar, value);//TODO: cache
					varMinConflicts = Math.min(varMinConflicts, ic);
				}
				ans+=varMinConflicts;
			}
		}
		return ans;
	}
	
	protected int calcIC(Assignment ass,int var, int val){
		return calcSingleVariableDistance(var, val, ass);		
	}
	public String getName() {
		return "PFC";
	}

}
