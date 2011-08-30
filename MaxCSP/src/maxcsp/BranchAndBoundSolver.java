package maxcsp;

public class BranchAndBoundSolver implements MaxCSPSolver{
	private Util.OutLine outline;
	private double maxAssignments;
	private long lastTime;
	private static final int UNKNOWN = -1;
	private static final int CONFLICT= 0;
	private static final int CONSISTENT = 1;
	public static final double MAX_ASSIGNMENTS = 100000000;
	public static final int MAX_TIME = 10000;
	public static boolean stopAfterMaxSeconds = false;
	public final Problem _problem;
	private Assignment _bestAssignment;
	private int _upperBound;
	private double _ccs;
	private double _assignments;
	private int[][][][]_checks;
	private long firstTime;
	public BranchAndBoundSolver(Problem problem){
		this._problem=problem;
		_checks=new int[problem._varCount][problem._domainSize][problem._varCount][problem._domainSize];
		for(int[][][] var1:_checks)
			for(int[][] val1: var1)
				for(int[] var2: val1)
					for(int val2=0;val2<var2.length;val2++)
						var2[val2]=UNKNOWN;
	}
	
	public Assignment solve(){
		lastTime = System.currentTimeMillis();
		firstTime = lastTime;
		outline=new Util.OutLine();
		maxAssignments=Math.pow(_problem._domainSize, _problem._varCount);
		_ccs=0;
		_assignments=0;
		_upperBound=Integer.MAX_VALUE;
		Assignment init = new Assignment(_problem._varCount);
		branch(init,0,0);
		return new Assignment(_bestAssignment);
	}
	protected void branch(Assignment partial, int var, int distance){
			
		if (_assignments>MAX_ASSIGNMENTS){
			_assignments = MAX_ASSIGNMENTS;
			return;
		}
		if (System.currentTimeMillis()-lastTime > 1000){
			double percent = ((double)_assignments) / maxAssignments;
			outline.clear();
			outline.out("" + String.format("%12E (%2.9f%%)",_assignments,percent));
			lastTime = System.currentTimeMillis();
			
		}
		if (stopAfterMaxSeconds && System.currentTimeMillis() - firstTime > MAX_TIME){
			return;
		}
		
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
		outline.clear();
	}
	
	/**
	 * lookahead calculation - should be overridden by subclasses.
	 */
	protected int lookahead(Assignment ass, int var, int value){
		return 0; //No lookahead.
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
		int cache=_checks[var1][value1][var2][value2];
		if(cache==UNKNOWN){
			cache=_problem.check(var1, value1, var2, value2)?CONSISTENT:CONFLICT;
			_ccs++;
			_checks[var1][value1][var2][value2]=cache;
		}
		return cache==CONSISTENT;
	}
	
	public int solutionCost() {
		return _upperBound;
	}
	public double solutionCCs(){
		return _ccs;
	}
	public double solutionAssignments(){
		return _assignments;
	}

	public String getName() {
		return "BranchAndBound";
	}
}
