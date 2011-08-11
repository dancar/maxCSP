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
		_upperBound=Integer.MAX_VALUE;
		_ccs=0;
		_assignments=0;
		Assignment init = new Assignment(_problem._varCount);
		init._distance=0;
		branch(init);
		return new Assignment(_bestAssignment);
	}
	protected void branch(Assignment partial){
		if(partial.isComplete()){
			if(partial._distance<_upperBound){
				_upperBound=partial._distance;
				_bestAssignment=new Assignment(partial);
			}
		}
		else{
			Variable variable = partial.pickUnassignedVariable();
			Assignment newAssignment = new Assignment(partial);
			Iterator<Integer> values = _problem._domain.iterator();		
			while(values.hasNext()){
				variable.assign(values.next());
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
	 * @param assignedVar Which variable to observe
	 * @param ass partial assignment
	 * @return the distance contributed by the aforementioned variable.
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
