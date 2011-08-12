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
	public static  <T> Iterator<OrderedPair<T>> differentPairsIterator(final Collection<T> col){
		return selfPairs(col, true);
	}
	public static <T> Iterator<OrderedPair<T>> pairsIterator(final Collection<T> col){
		return selfPairs(col, false);
	}
	private static  <T> Iterator<OrderedPair<T>> selfPairs(final Collection<T> col, final boolean difference){
		return new Iterator<OrderedPair<T>>(){
			int skips;
			Iterator<T> out;
			Iterator<T> in;
			T curOut;
			OrderedPair<T>next;
			{
				out = col.iterator();
				in=col.iterator();
				next=null;
				skips=1;
				if(out.hasNext()){
					curOut = out.next();
					goNext();
				}
			}
			private void goNext(){
				next=null;
				if(!in.hasNext())
					if(out.hasNext()){
						in=col.iterator();
						if(difference)
							for(int i=0;i<skips;i++)in.next();
						curOut=out.next();
						skips++;
					}
				if(in.hasNext())
					next=new OrderedPair<T>(curOut,in.next());
				if(difference && next!=null && next._left.equals(next._right))
					goNext();
			}
			@Override
			public boolean hasNext() {
				return next!=null;
			}
			@Override
			public OrderedPair<T> next() {
				OrderedPair<T> ans=next;
				goNext();
				return ans;
			}
			@Override
			public void remove() {
				Util.panic("selfDifferentPairs Iterator remove");
			}
			
		};
		
	}
	public static Vector<Integer> numlist(int size){
		Vector<Integer> ans = new Vector<Integer>(size);
		for(int i=0;i<size;i++)
			ans.add(i,new Integer(i));
		return ans;
	}
	public static <T> Vector<OrderedPair<T>> quickPV(T vals[][]){
		Vector<OrderedPair<T>>ans = new Vector<OrderedPair<T>>(vals.length);
		for(T v[]:vals)
			ans.add(new OrderedPair<T>(v[0],v[1]));
		return ans;
	}
	
	public static int randBetween(int min, int max){
		return (int) (min + Math.round(((Math.random()*(max-min)))));
	}
	public static int overTwo(int n){
		return (int)((n*(n-1))/2);
	}
	
}
