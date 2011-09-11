package maxcsp;

import java.util.Vector;

public class Util {
	private final static boolean work = Logger.inst().level>Logger.LEVEL_LESS;
	public static class OutLine{
		public int _count=0;
		public void out(Object o){
			if(work){
				String str = o.toString();
				_count+=str.length();
				System.out.print(str);
			}
		}
		public void clear(){
			if(work){
				String clean="";
				do{
					clean+="\b";
				}			while(_count-->0);
				System.out.print(clean);
			}
		}
	}
	public static Vector<MaxCSPSolver> makeSolvers(Problem p){
		Vector<MaxCSPSolver> ans = new Vector<MaxCSPSolver>();
//		ans.add(new BranchAndBoundSolver(p));
//		ans.add(new PFC(p));
		ans.add(new PFCDAC(p));
		return ans;
	}
	
	public static Vector<String> getSolversNames(){
		Vector<String>ans = new Vector<String>();
		for(MaxCSPSolver solver:  makeSolvers(new Problem(1,1,1,1)))
			ans.add(solver.getName());
		return ans;
	}
	public static boolean chance(double probability){
		return (Math.random()<probability);
	}
	public static void panic(String reason){
		new Exception("PANIC: " + reason).printStackTrace();
		System.exit(-1);
	}
	public static void panic(String reason,Exception e){
		new Exception("PANIC: " + reason);
		e.printStackTrace();
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
	public static Vector<IntPair> quickPV(int[][] values) {
		Vector<IntPair> ans = new Vector<IntPair>();
		for(int[] pair : values){
			ans.add(new IntPair(pair[0],pair[1]));
		}
		return ans;
	}
	public static int[] intrio(int x, int y, int z){
		int[] ans = {x,y,z};
		return ans;
		
	}
	
}
