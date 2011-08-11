package maxcsp;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

public class MaxCSPSolver implements Serializable{
	private static final long serialVersionUID = -2450853734027328704L;
	public final Problem _problem;
	public MaxCSPSolver(Problem problem){
		this._problem=problem;
	}
	
	public Solution solve(){
		Logger.inst().debug("Solve: starting varscount: " + _problem._varCount);
		Assignment init = new Assignment(_problem._varCount);
		init._distance=0;
		Assignment solutionAssignment = branch(init,Integer.MAX_VALUE);
		return new Solution(solutionAssignment,_problem.getCCs());
	}
	protected Assignment branch(Assignment partial, int upperBound){
		Logger.inst().debug(String.format("branch in , assignment: %s, UB: %d", partial, upperBound));
		Assignment ans=null;
		int newUpperBound = upperBound;
		if(partial.isComplete())
			ans = partial;
		else{
			Variable variable = partial.pickUnassignedVariable();
			Assignment newAssignment = new Assignment(partial);
			Iterator<Integer> values = _problem._domain.iterator();		
			while(values.hasNext()){
				newAssignment.assign(variable.assign(values.next()));
				newAssignment._distance=partial._distance + calcSingleVariableDistance(variable,newAssignment);
				if (calcLowerBound(newAssignment)<newUpperBound){
					Assignment rest = branch(newAssignment,newUpperBound);
					if(rest!=null && rest._distance<newUpperBound){
						newUpperBound=rest._distance;
						ans=rest;
					}
				}
			}
			
		}
		Logger.inst().debug(String.format("branch out , assignment: %s, UB: %d", partial, upperBound));
		return ans;
	}
	
	protected int calcLowerBound(Assignment ass){
		return ass._distance;
	}
	
	protected int calcSingleVariableDistance(Variable assignedVar, Assignment ass){
		int ans = 0;
		Iterator<Variable> assignedVars = ass.assignedVariablesIterator();
		while(assignedVars.hasNext()){
			Variable nextVar = assignedVars.next();
			if(!nextVar.equals(assignedVar)){
				if(!_problem.check(assignedVar, nextVar))
					ans++;
			}
		}
		return ans;
	}
}
