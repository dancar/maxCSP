package maxcsp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

public class Experiment implements Runnable{
	public static final int DEFAULT_PROBLEMS_COUNT = 35;
	public final static int DEFAULT_VARS_COUNT=10;
	public final static int DEFAULT_DOMAIN_SIZE=15;
	public final static double DEFAULT_P1=0.9;
	public final static double DEFAULT_P2_MIN=0.5;
	public final static double DEFAULT_P2_MAX=1.0;
	public final static double DEFAULT_P2_STEP=0.1;
	
	public static final String  PROBLEMS_COUNT = "problems_count";
	public static final String  VARS_COUNT="vars_count";
	public static final String  DOMAIN_SIZE="domain_size";
	public static final String  P1="p1";
	public static final String  P2_MIN="p2_min";
	public static final String  P2_MAX="p2_max";
	public static final String  P2_STEP="p2_step";
	
	
	private int _varsCount;
	private int _domainSize;
	private double _p1;
	private double _p2start;
	private double _p2end;
	private double _p2step;
	private int _problemsCount;
	
	private class ExpCounter{
		private Map<String,ExpAlgoCounter> _map;
		public final double _p1,_p2;
		public ExpCounter(double p1, double p2){
			_p1=p1;
			_p2=p2;
			_map = new HashMap<String, Experiment.ExpCounter.ExpAlgoCounter>();
			for(BranchAndBoundSolver solver : Util.makeSolvers(new Problem(1,1,1,1)))
				_map.put(solver.getName(), new ExpAlgoCounter(solver.getName(),p1,p2));
		}
		public void add(ExpCounter counter){
			for(String solver : _map.keySet()){
				add(solver,counter.getAverageCcs(solver),counter.getAverageAssignments(solver));
			}
		}
		public void add(String solver, double ccs, double assignments){
			_map.get(solver).add(ccs, assignments);
		}
		public double getAverageCcs(String solver){
			return _map.get(solver).getAverageCcs();
		}
		public double getAverageAssignments(String solver){
			return _map.get(solver).getAverageAssignments();
		}
		private class ExpAlgoCounter{
			public final String _solverName;
			public final double _p1;
			public final double _p2;
			
			private double _totalCcs;
			private double _totalAssignments;
			private int _count;
			private ExpAlgoCounter(String solverName, double p1,
					double p2) {
				this._solverName = solverName;
				this._p1 = p1;
				this._p2 = p2;
				_totalAssignments=0;
				_totalCcs=0;
				_count=0;
			}
			public void add(double Ccs, double assignments){
				_totalCcs+=Ccs;
				_totalAssignments+=assignments;
				_count++;
			}
			public double getAverageCcs(){
				return _totalCcs / _count;
			}
			public double getAverageAssignments(){
				return _totalAssignments / _count;
			}
		}
	}
//	private static class ExperimentRecord{
//		public final String solverName;
//		public final double averageCcs;
//		public final double averageAssignments;
//		public final double p1;
//		public final double p2;
//		
//		public ExperimentRecord(String solverName, double averageCcs,
//				double averageAssignments, double p1, double p2) {
//			super();
//			this.solverName = solverName;
//			this.averageCcs = averageCcs;
//			this.averageAssignments = averageAssignments;
//			this.p1 = p1;
//			this.p2 = p2;
//		}
//		public String toString(){
//			return String.format("Algorithm: %s, p1: %1.2f, p2: %1.2f, Avg. CCs: %1.2f, Avg. Assignments: %1.2f.",
//					solverName,p1,p2,averageCcs,averageAssignments);
//		}
//	}
	
	public static void main(String[] args) {
		new Experiment().run();
	}
	public Experiment (){
		this(new Properties());
	}
	public Experiment(String confPath) throws FileNotFoundException, IOException{
		Properties conf = new Properties();
		conf.load(new FileInputStream(confPath));
	}
	public Experiment(Properties configuration) {
		  _varsCount=Integer.parseInt(configuration.getProperty(VARS_COUNT,""+DEFAULT_VARS_COUNT));
		  _domainSize=Integer.parseInt(configuration.getProperty(DOMAIN_SIZE, ""+DEFAULT_DOMAIN_SIZE));;
		  _p1=Double.parseDouble(configuration.getProperty(P1, DEFAULT_P1+""));
		  _p2start=Double.parseDouble(configuration.getProperty(P2_MIN, DEFAULT_P2_MIN+""));;
		  _p2end=Double.parseDouble(configuration.getProperty(P2_MAX, DEFAULT_P2_MAX+""));;;
		  _p2step=Double.parseDouble(configuration.getProperty(P2_STEP, DEFAULT_P2_STEP+""));;;
		  _problemsCount=Integer.parseInt(configuration.getProperty(PROBLEMS_COUNT,""+DEFAULT_PROBLEMS_COUNT));;
	}
	
	public void run(){
		out("Experiment start");
		final String outFormatBase = "%-30s";
		final String outFormat = String.format("%1$s%1$s%1$s", outFormatBase);
		ExpCounter finalResultsCounter = new ExpCounter(_p1,0);
		Vector<ExpCounter> finalResults = new Vector<Experiment.ExpCounter>();
		for(double p2 = _p2start;p2<=_p2end;p2+=_p2step){
			ExpCounter curP2Results = new ExpCounter(_p1,p2);
			for(int i=0;i<_problemsCount;i++){
				Problem p = new Problem(_varsCount, _domainSize, _p1,p2);
				out("\n#" + i + ": " + p);
				out(String.format(outFormat,"Solver", "Ccs", "Assignments" ));
				out("---------------------------------------------------------------");
				Vector<BranchAndBoundSolver> solvers = Util.makeSolvers(p);
				for(int j=0;j<solvers.size();j++){
					MaxCSPSolver solver = solvers.elementAt(j);
					solver.solve();
					if(j>0)
						if(solver.solutionCost()!=solvers.elementAt(j-1).solutionCost())
							Util.panic(String.format("Inequal cost:%d(%s),%d(%s)",
									solvers.elementAt(j-1).solutionCost(),
									solvers.elementAt(j-1).getName(),
									solver.solutionCost(),
									solver.getName()));
					curP2Results.add(solver.getName(),solver.solutionCCs(),solver.solutionAssignments());
				}
			}
			finalResultsCounter.add(curP2Results);
			finalResults.add(curP2Results);
		}
		
	}
	private void out(Object msg){
		Logger.inst().debug(msg.toString());
	}
	private void ou(Object msg){
		Logger.inst().debug(msg.toString(),false);
	}
	

}
