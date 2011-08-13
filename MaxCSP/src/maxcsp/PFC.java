package maxcsp;

public class PFC extends BranchAndBoundSolver {

	public PFC(Problem problem) {
		super(problem);
	}
	
	@Override
	protected int lookahead(Assignment ass,int var, int value){
		return calcIC(ass);
	}
	
	protected int calcIC(Assignment ass){
		int ans = 0;
		for(int var : _problem._vars){
			if(!ass.isAssigned(var)){
				int varMinConflicts = Integer.MAX_VALUE;
				for(int value : _problem._domain){
					int ic = calcSingleVariableDistance(var, value, ass);//TODO: cache
					varMinConflicts = Math.min(varMinConflicts, ic);
				}
				ans+=varMinConflicts;
			}
		}
		return ans;
	}
	public String getName() {
		return "PFC";
	}

}
