package maxcsp;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class NQueensProblem extends Problem {
	public final int _n;
	public NQueensProblem(int n){
		super(n,n,createConstraints(n));
		_n=n;
	}

	private static Collection<Constraint> createConstraints(int n) {
		Collection<Constraint> constraints = new Vector<Constraint>(n*n);
		for(int queenLeft=0;queenLeft<n;queenLeft++){
			for (int queenRight=queenLeft+1;queenRight<n;queenRight++){
				Vector<IntPair>allowedValues = new Vector<IntPair>();
				for(int valueLeft : Util.numlist(n))for(int valueRight:Util.numlist(n)){
					if((valueLeft!=valueRight)
						&(Math.abs(queenLeft-queenRight)!=Math.abs(valueLeft-valueRight)))						allowedValues.add(new IntPair(valueLeft,valueRight));
				}
				constraints.add(new Constraint(queenLeft, queenRight, allowedValues));
			}
		}
		return constraints;
	}
	public String showSolution(Assignment sol){
		String ans="";
		for(int i=0;i<_n;i++){
			for(int j=0;j<_n;j++){
				if(j==sol.value(i)){
					ans+="X";
				}
				else{
					ans+="O";
				}
			}
			ans+="\n";
		}
		return ans;
	}
}
