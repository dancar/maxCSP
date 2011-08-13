package maxcsp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

public class Experiment implements Runnable{
	public static final int DEFAULT_PROBLEMS_COUNT = 20;
	public final static int DEFAULT_VARS_COUNT=7;
	public final static int DEFAULT_DOMAIN_SIZE=7;
	public final static double DEFAULT_P1=0.9;
	public final static double DEFAULT_P2_MIN=0.5;
	public final static double DEFAULT_P2_MAX=1.0;
	public final static double DEFAULT_P2_STEP=0.25;
	
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
	
	private static class ExperimentRecord{
		public final String solverName;
		public final double averageCcs;
		public final double averageAssignments;
		public final double p1;
		public final double p2;
		
		public ExperimentRecord(String solverName, double averageCcs,
				double averageAssignments, double p1, double p2) {
			super();
			this.solverName = solverName;
			this.averageCcs = averageCcs;
			this.averageAssignments = averageAssignments;
			this.p1 = p1;
			this.p2 = p2;
		}
		public String toString(){
			return String.format("Algorithm: %s, p1: %1.2f, p2: %1.2f, Avg. CCs: %1.2f, Avg. Assignments: %1.2f.",
					solverName,p1,p2,averageCcs,averageAssignments);
		}
	}
	
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
		final String outFormatBase = "%-20s";
		final String outFormat = String.format("%1$s%1$s%1$s", outFormatBase);
		Vector<ExperimentRecord> results = new Vector<ExperimentRecord>();
		for(double p2 = _p2start;p2<=_p2end;p2+=_p2step){
			Map<String,Integer> ccs = new Hashtable<String,Integer>();
			Map<String,Integer>  assignmentsCount= new Hashtable<String,Integer>();
			for(int i=0;i<_problemsCount;i++){
				Problem p = new Problem(_varsCount, _domainSize, _p1,p2);
				out("\n#" + i + ": " + p);
				out(String.format(outFormat,"Solver", "Ccs", "Assignments" ));
				out("---------------------------------------------------------------");
				Vector<MaxCSPSolver> solvers = Util.makeSolvers(p);
				for(MaxCSPSolver solver : solvers){
					solver.solve();
					String key = solver.getName();
					int newCcs = solver.solutionCCs();
					int newAC = solver.solutionAssignments();
					out(String.format(outFormat,key,newCcs,newAC));
					
					Integer oldCcs = ccs.get(key);
					Integer oldAC=assignmentsCount.get(key);
					ccs.put(key, (oldCcs==null? 0 : oldCcs) + newCcs);
					assignmentsCount.put(key, (oldAC==null?0:oldAC)+newAC);
				}
			}
			for(String key : ccs.keySet()){
				double avCcs = (double) ccs.get(key) / _problemsCount;
				double avAssignments = (double) assignmentsCount.get(key) / _problemsCount;
				results.add(new ExperimentRecord(key, avCcs, avAssignments, _p1, p2));
			}
		}
		for(ExperimentRecord e : results){
			out(e);
		}
	}
	private void out(Object msg){
		Logger.inst().debug(msg.toString());
	}
	private void ou(Object msg){
		Logger.inst().debug(msg.toString(),false);
	}

}
