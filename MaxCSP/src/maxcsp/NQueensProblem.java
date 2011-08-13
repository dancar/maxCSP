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
		for(int i=0;i<n;i++){
			for (int j=i+1;j<n;j++){
				Vector<IntPair>allowedValues = new Vector<IntPair>();
				Iterator<OrderedPair<Integer>> possibleValues = Util.pairsIterator(Util.numlist(n));
				while(possibleValues.hasNext()){
					OrderedPair<Integer> ip = possibleValues.next();
					int x = ip._left;
					int y = ip._right;
					boolean ok = true;
					ok &= x!=y;
					ok &= Math.abs(j-i)!=Math.abs(y-x);
					if(ok) allowedValues.add(ip);
				}
				constraints.add(new Constraint(new Variable(i), new Variable(j), allowedValues));
			}
		}
		return constraints;
	}
	public String showSolution(Assignment sol){
		String ans="";
		for(int i=0;i<_n;i++){
			for(int j=0;j<_n;j++){
				if(j==sol.value(new Variable(i))){
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
