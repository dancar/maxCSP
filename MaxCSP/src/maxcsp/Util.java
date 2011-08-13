package maxcsp;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class Util {
	@SuppressWarnings("rawtypes")
	private static Class[] _solvers={MaxCSPSolver.class,PFC.class};;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Vector<MaxCSPSolver> makeSolvers(Problem p){
		Vector<MaxCSPSolver> ans = new Vector<MaxCSPSolver>(_solvers.length);
		Class[] arg = {Problem.class};
		for(int i=0;i<_solvers.length;i++){
			MaxCSPSolver solver = null;
			try {
				solver=(MaxCSPSolver) _solvers[i].getConstructor(arg).newInstance(p);
			} catch (Exception e) {
				e.printStackTrace();
				Util.panic("Solvers Sanity check");
			}
			ans.add(solver);
		}
		return ans;
	}
	public static boolean chance(double probability){
		return (Math.random()<probability);
	}
	public static void panic(String reason){
		System.out.println("PANIC: " + reason);
		System.exit(-1);
	}
	
	public static int[] numlist(int size){
		int[] ans = new int[size];
		for(int i=0;i<size;i++)
			ans[i]=i;
		return ans;
	}
	
	public static boolean order(int...nums){
		boolean ans = true;
		int previous = Integer.MIN_VALUE;
		for(int n : nums){
			ans&=previous<n;
			previous=n;
		}
		return ans;
	}
	public static int randBetween(int min, int max){
		return (int) (min + Math.round(((Math.random()*(max-min)))));
	}
	public static int overTwo(int n){
		return (int)((n*(n-1))/2);
	}
	
}
