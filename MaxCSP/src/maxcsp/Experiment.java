package maxcsp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

public class Experiment implements Runnable{
	
	
	public static final int DEFAULT_PROBLEMS_COUNT = 15;
	public final static int DEFAULT_VARS_COUNT=7;
	public final static int DEFAULT_DOMAIN_SIZE=7;
	public final static double DEFAULT_P1=0.9;
	public final static double DEFAULT_P2_MIN=0.5;
	public final static double DEFAULT_P2_MAX=1.0;
	public final static double DEFAULT_P2_STEP=0.1;
	public final static String DEFAULT_OUTPUT_FOLDER="exp";
	
	
	public static final String  PROBLEMS_COUNT = "problems_count";
	public static final String  VARS_COUNT="vars_count";
	public static final String  DOMAIN_SIZE="domain_size";
	public static final String  P1="p1";
	public static final String  P2_MIN="p2_min";
	public static final String  P2_MAX="p2_max";
	public static final String  P2_STEP="p2_step";
	public static final String  OUTPUT_FOLDER="output_folder";
	private static final String OUTPUT_FILE_BASE = "%s.csv";
	private static final String RESULTS_RECORD_BASE = "%.2f,%.2f,%.2f\n";
	
	
	private int _varsCount;
	private int _domainSize;
	private double _p1;
	private double _p2start;
	private double _p2end;
	private double _p2step;
	private int _problemsCount;
	private String _outputFolder;
	
	private class ExpCounter{
		private Map<String,ExpAlgoCounter> _map;
		public final double _p1,_p2;
		public ExpCounter(double p1, double p2){
			_p1=p1;
			_p2=p2;
			_map = new HashMap<String, Experiment.ExpCounter.ExpAlgoCounter>();
			for(String solver : Util.getSolversNames())
				_map.put(solver, new ExpAlgoCounter(p1,p2));
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
		public String toString(){
			String ans = "";
			for(String solver : _map.keySet()){
				ans+=String.format("Algorithm: %s, p1: %1.2f, p2: %1.2f, Avg. CCs: %1.2f, Avg. Assignments: %1.2f.",
						solver,
						_map.get(solver)._p1,
						_map.get(solver)._p2,
						_map.get(solver).getAverageCcs(),
						_map.get(solver).getAverageAssignments());
			}
			return ans;
		}
		private class ExpAlgoCounter{
			public final double _p1;
			public final double _p2;
			
			private double _totalCcs;
			private double _totalAssignments;
			private int _count;
			private ExpAlgoCounter(double p1, double p2) {
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
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Experiment ex;
		if (args.length>0){
			ex=new Experiment(args[0]);
		}
		else{
			Properties conf = new Properties();
			conf.setProperty(Experiment.OUTPUT_FOLDER, findNewFolder());
			ex=new Experiment(conf);
		}
		ex.run();
	}
	private static String findNewFolder() {
		File pwd = new File(".");
		int count = 0;
		boolean exists;
		String newFolder=DEFAULT_OUTPUT_FOLDER + count;
		do{
			exists=false;
			for(String f : pwd.list()){
				newFolder = DEFAULT_OUTPUT_FOLDER + count;
				exists |= f.equals(newFolder);
			}			
			count++;
		}while(exists);
		new File(newFolder).mkdir();
		return newFolder;
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
		  _domainSize=Integer.parseInt(configuration.getProperty(DOMAIN_SIZE, ""+DEFAULT_DOMAIN_SIZE));
		  _p1=Double.parseDouble(configuration.getProperty(P1, DEFAULT_P1+""));
		  _p2start=Double.parseDouble(configuration.getProperty(P2_MIN, DEFAULT_P2_MIN+""));
		  _p2end=Double.parseDouble(configuration.getProperty(P2_MAX, DEFAULT_P2_MAX+""));
		  _p2step=Double.parseDouble(configuration.getProperty(P2_STEP, DEFAULT_P2_STEP+""));
		  _problemsCount=Integer.parseInt(configuration.getProperty(PROBLEMS_COUNT,""+DEFAULT_PROBLEMS_COUNT));
		  _outputFolder=configuration.getProperty(OUTPUT_FOLDER,""+DEFAULT_OUTPUT_FOLDER);
	}
	
	public void run(){
		out("Experiment start");
		out("Vars count:"  + _varsCount);
		out("Domain size:"  + _domainSize);
		out("P1:"  + _p1);
		out("P2 start:"  + _p2start);
		out("P2 end:"  + _p2step);
		out("P2 Step:"  + _p2end);
		out("Problems count:"  + _problemsCount);
		out("Output folder:"  + _outputFolder);
		
		Map<String, PrintWriter> fileOut = new HashMap<String, PrintWriter>();
		for(String solver : Util.getSolversNames()){
			String filename = String.format(OUTPUT_FILE_BASE, solver);
			try {
				File file = new File(_outputFolder,filename);
				file.createNewFile();
				fileOut.put(solver, new PrintWriter(new FileWriter(file)));
			} catch (IOException e) {
				Util.panic(e.getMessage());
			}
		}
		//TODO: assignments and ccs as longs.
		final String outFormatBase = "%-30s";
		final String outFormat = String.format("%1$s%1$s%1$s", outFormatBase);
		ExpCounter finalResultsCounter = new ExpCounter(_p1,0);
		for(double p2 = _p2start;p2<=_p2end;p2+=_p2step){
			ExpCounter curP2Results = new ExpCounter(_p1,p2);
			for(int i=0;i<_problemsCount;i++){
				Problem p = new Problem(_varsCount, _domainSize, _p1,p2);
				out("\n#" + i + ": " + p);
				out(String.format(outFormat,"Solver", "Ccs", "Assignments" ));
				out("---------------------------------------------------------------");
				Vector<MaxCSPSolver> solvers = Util.makeSolvers(p);
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
			for(String solver : fileOut.keySet()){
				fileOut.get(solver).write(
						String.format(
								RESULTS_RECORD_BASE, 
								p2,
								curP2Results.getAverageCcs(solver),
								curP2Results.getAverageAssignments(solver)));
			}
//			finalResults.add(curP2Results);
		}
		out(finalResultsCounter);
		for(PrintWriter pw : fileOut.values()){
			pw.close();
		}
	}
	private void out(Object msg){
		Logger.inst().debug(msg.toString());
	}

}
