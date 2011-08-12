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
	
	public Assignment solve(){
		_ccs=0;
		_assignments=0;
		_upperBound=Integer.MAX_VALUE;
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
	 * bound search
	 * Assumes partial assignment's _distance is properly calculated in advanced.
	 * @param ass partial assignment
	 * @return new lower bound with respect to the given partial assignment
	 */
	protected int bound(Assignment ass){
		//simplest calculation
		return ass._distance;
	}
	
	/**
	 * Calculates the distance a single variable adds to an existing partial assignment.
	 * Assumes the desired variable is assigned.
	 */
	protected int calcSingleVariableDistance(Variable assignedVar, Assignment ass){
		int ans = 0;
		Iterator<Variable> assignedVars = ass.assignedVariablesIterator();
		while(assignedVars.hasNext()){
			Variable nextVar = assignedVars.next();
			if(!nextVar.equals(assignedVar)){
				_ccs++;
				if(!_problem.check(assignedVar, nextVar))
					ans++;
			}
		} 
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
}
