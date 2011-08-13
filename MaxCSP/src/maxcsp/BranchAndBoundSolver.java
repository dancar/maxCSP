package maxcsp;

public class BranchAndBoundSolver implements MaxCSPSolver{
	public final Problem _problem;
	private Assignment _bestAssignment;
	private int _upperBound;
	private int _ccs;
	private int _assignments;
	
	public BranchAndBoundSolver(Problem problem){
		this._problem=problem;
	}
	
	public Assignment solve(){
		_ccs=0;
		_assignments=0;
		_upperBound=Integer.MAX_VALUE;
		Assignment init = new Assignment(_problem._varCount);
		branch(init,0,0);
		return new Assignment(_bestAssignment);
	}
	protected void branch(Assignment partial, int var, int distance){
		if(var==_problem._varCount){ //LEAF
			if(distance<_upperBound){
				_upperBound=distance;
				_bestAssignment=new Assignment(partial);
			}
		}
		else{ //INNER NODE
			Assignment newAssignment = new Assignment(partial);
			for(int value : _problem._domain){	
				newAssignment.assign(var, value);
				_assignments++;
				int newDistance = distance + calcSingleVariableDistance(var, value, newAssignment);
				int lowerBound=newDistance + lookahead(newAssignment,var,value);
				if (lowerBound>=_upperBound){
					// -- prune --
					//(do nothing)
				}
				else{
					branch(newAssignment,var+1,newDistance);
				}
			}
		}
	}
	
	/**
	 * lookahead calculation - should be overridden by subclasses.
	 */
	protected int lookahead(Assignment ass, int var, int value){
		return 0;
	}
	
	/**
	 * Calculates the distance a single variable adds to an existing partial assignment.
	 * Assumes the desired variable is assigned.
	 */
	protected int calcSingleVariableDistance(int var, int value, Assignment ass){
		int ans = 0;
		for(int assignedVar : _problem._vars){
			if(ass.isAssigned(assignedVar)){
				if(assignedVar!=var){
					if(!check(assignedVar,ass.value(assignedVar),var,value))
						ans++;
				}
			}
		} 
		return ans;
	}
	protected boolean check(int var1, int value1, int var2, int value2) {
		boolean ans = _problem.check(var1, value1, var2, value2);
		_ccs++;
		return ans;
	}
	
	public int solutionCost() {
		return _upperBound;
	}
	public int solutionCCs(){
		return _ccs;
	}
	public int solutionAssignments(){
		return _assignments;
	}

	public String getName() {
		return "B&B";
	}
}
