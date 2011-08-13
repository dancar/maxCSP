package maxcsp;

import java.io.Serializable;
import java.util.Iterator;

public class MaxCSPSolver implements Serializable{
	private static final long serialVersionUID = -2450853734027328704L;
	public final Problem _problem;
	private Assignment _bestAssignment;
	private int _upperBound;
	private int _ccs;
	private int _assignments;
	
	public MaxCSPSolver(Problem problem){
		this._problem=problem;
	}
	protected void preprocess(){
		//for subclasses to override.
	}
	public Assignment solve(){
		_ccs=0;
		_assignments=0;
		_upperBound=Integer.MAX_VALUE;
		this.preprocess();
		Assignment init = new Assignment(_problem._varCount);
		init._distance=0;
		branch(init);
		return new Assignment(_bestAssignment);
	}
	protected void branch(Assignment partial){
		if(partial.isComplete()){ //LEAF
			if(partial._distance<_upperBound){
				_upperBound=partial._distance;
				_bestAssignment=new Assignment(partial);
			}
		}
		else{ //INNER NODE
			Variable variable = partial.pickUnassignedVariable();
			Assignment newAssignment = new Assignment(partial);
			for(Integer value : _problem._domain){	
				variable.assign(value);
				newAssignment.assign(variable);
				_assignments++;
				newAssignment._distance=
						partial._distance + 
						calcSingleVariableDistance(variable,newAssignment);
				int lowerBound=bound(newAssignment);
				if (lowerBound>_upperBound){
					// -- prune --
					//(do nothing)
				}
				else{
					branch(newAssignment);
				}
			}
		}
	}
	
	/**
	 * lower bound calculation
	 * Assumes partial assignment's _distance is properly calculated in advanced.
	 */
	protected int bound(Assignment ass){
		//simplest calculation
		return calcDistance(ass);
	}
	
	protected int calcDistance(Assignment ass){
		return ass._distance;
	}
	
	/**
	 * Calculates the distance a single variable adds to an existing partial assignment.
	 * Assumes the desired variable is assigned.
	 */
	protected int calcSingleVariableDistance(Variable assignedVar, Assignment ass){
		int ans = 0;
		for(Variable nextVar : ass.getAssignedVars()){ 
			if(!nextVar.equals(assignedVar)){
				if(!check(assignedVar, nextVar))
					ans++;
			}
		} 
		return ans;
	}
	protected boolean check(Variable assignedVar1, Variable assignedVar2) {
		boolean ans = _problem.check(assignedVar1, assignedVar2);
		_ccs++;
		return ans;
	}
	/**
	 * Same as above, but with a given value to be assigned to the variable. 
	 */
	protected int calcSingleVariableDistance(Variable var, Integer value,
			Assignment ass) {
		ass.assign(var.assign(value));
		int ans = calcSingleVariableDistance(var, ass);
		ass.unassign(var);
		return ans;
	}

	public int solutionDistance() {
		return _bestAssignment._distance;
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
