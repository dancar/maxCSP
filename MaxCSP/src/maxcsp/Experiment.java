package maxcsp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Experiment implements Runnable{
	
	
	public static final int DEFAULT_PROBLEMS_COUNT = 50;
	public final static int DEFAULT_VARS_COUNT=15;
	public final static int DEFAULT_DOMAIN_SIZE=10;
	public final static double DEFAULT_P1_MIN=0.2;
	public final static double DEFAULT_P1_STEP=0.6;
	public final static double DEFAULT_P1_MAX=0.8;
	public final static double DEFAULT_P2_MIN=0.1 ;
	public final static double DEFAULT_P2_STEP=0.1;
	public final static double DEFAULT_P2_MAX=0.9;
	public final static String DEFAULT_OUTPUT_FOLDER="exp";
	
	
	public static final String  PROBLEMS_COUNT = "problems_count";
	public static final String  VARS_COUNT="vars_count";
	public static final String  DOMAIN_SIZE="domain_size";
	public static final String  P1_MIN="p1_min";
	public static final String  P1_STEP="p1_step";
	public static final String  P1_MAX="p1_max";
	public static final String  P2_MIN="p2_min";
	public static final String  P2_MAX="p2_max";
	public static final String  P2_STEP="p2_step";
	public static final String  OUTPUT_FOLDER="output_folder";
	private static final String OUTPUT_FILE_BASE = "%s_%1.2f.csv";
	private static final String RESULTS_RECORD_BASE = "%.2f\t%.2f\t%.2f\n";
	
	private int _varsCount;
	private int _domainSize;
	private double _p1min;
	private double _p1step;
	private double _p1max;
	private double _p2min;
	private double _p2step;
	private double _p2max;
	private int _problemsCount;
	private String _outputFolder;
	
	private class ExpCounter{
		private Map<String,ExpAlgoCounter> _map;
		public ExpCounter(double p1, double p2){
			_map = new HashMap<String, Experiment.ExpCounter.ExpAlgoCounter>();
			for(String solver : Util.getSolversNames())
				_map.put(solver, new ExpAlgoCounter(p1,p2));
		}
		public synchronized void add(ExpCounter counter){
			for(String solver : _map.keySet()){
				add(solver,counter.getAverageCcs(solver),counter.getAverageAssignments(solver));
			}
		}
		public synchronized void add(String solver, double ccs, double assignments){
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
				ans+=String.format("Algorithm: %s, p1: %1.2f, p2: %1.2f, Avg. CCs: %1.2f, Avg. Assignments: %1.2f.\n",
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
			ex=new Experiment();
		}
		ex.run();
	}
	private String findNewFolder() {
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
		init(conf);
	}
	public Experiment(Properties configuration) {
		init(configuration);
	}
	private void init(Properties configuration){
		  _varsCount=Integer.parseInt(configuration.getProperty(VARS_COUNT,""+DEFAULT_VARS_COUNT));
		  _domainSize=Integer.parseInt(configuration.getProperty(DOMAIN_SIZE, ""+DEFAULT_DOMAIN_SIZE));
		  _p1min=Double.parseDouble(configuration.getProperty(P1_MIN, DEFAULT_P1_MIN+""));
		  _p1step=Double.parseDouble(configuration.getProperty(P1_STEP, DEFAULT_P1_STEP+""));
		  _p1max=Double.parseDouble(configuration.getProperty(P1_MAX, DEFAULT_P1_MAX+""));
		  _p2min=Double.parseDouble(configuration.getProperty(P2_MIN, DEFAULT_P2_MIN+""));
		  _p2max=Double.parseDouble(configuration.getProperty(P2_MAX, DEFAULT_P2_MAX+""));
		  _p2step=Double.parseDouble(configuration.getProperty(P2_STEP, DEFAULT_P2_STEP+""));
		  _problemsCount=Integer.parseInt(configuration.getProperty(PROBLEMS_COUNT,""+DEFAULT_PROBLEMS_COUNT));
		  _outputFolder=configuration.getProperty(OUTPUT_FOLDER);
		  if(_outputFolder==null)
			  _outputFolder=findNewFolder();
	}
	
	public void run(){
		String desc = description();
		out(desc);
		PrintWriter descFile=null;
		try {
			descFile = new PrintWriter(new FileOutputStream(new File(_outputFolder,"description.txt")));
		} catch (FileNotFoundException e1) {
			Util.panic("Description file error", e1);
		}
		descFile.write(desc);
		descFile.close();
		
		int counter=0;
		ExpCounter finalResultsCounter = new ExpCounter(-1,-1);
		for(double p1 = _p1min;p1<=_p1max;p1+=_p1step){
			Map<String, File> files = new HashMap<String, File>();
			for(String solver : Util.getSolversNames()){
				String filename = String.format(OUTPUT_FILE_BASE, solver,p1);
				try {
					File file = new File(_outputFolder,filename);
					file.createNewFile();
					files.put(solver,file);
				} catch (IOException e) {
					Util.panic(e.getMessage());
				}
			}
			for(double p2 = _p2min;p2<=_p2max;p2+=_p2step){
				final double p1f=p1;
				final double p2f=p2;
				final ExpCounter curP2Results = new ExpCounter(p1,p2);
				ExecutorService tpool = Executors.newFixedThreadPool(_problemsCount*Util.getSolversNames().size());
				for(int i=0;i<_problemsCount;i++){
					final Problem p = new Problem(_varsCount, _domainSize, p1f,p2f);
					out("Problem #" + (counter++) + ": " + p);
					final int problemNum= counter;
//					out(String.format(outFormat,"Solver", "Ccs", "Assignments" ));
//					String line="";for(@SuppressWarnings("unused") int nothing : new int[71])line+="-"; out(line);
					final Vector<MaxCSPSolver> solvers = Util.makeSolvers(p);
					for(int j=0;j<solvers.size();j++){
						final MaxCSPSolver solver = solvers.elementAt(j);
						tpool.execute(new Runnable(){
							@Override
							public void run() {
								out(String.format(
										"(start) %d, %s",
										problemNum,
										solver.getName()
										));
								solver.solve();
								curP2Results.add(solver.getName(),solver.solutionCCs(),solver.solutionAssignments());
								out(String.format(
										"(done) #%d, %s, CCs: %8E, Assignments: %8E",
										problemNum,
										solver.getName(),
										solver.solutionCCs(),
										solver.solutionAssignments()));
							}
						});
					}
				}
				out("waiting for " + counter);
				tpool.shutdown();
				try {
					tpool.awaitTermination(1, TimeUnit.HOURS);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				out(counter + " done.");
				finalResultsCounter.add(curP2Results);
				for(String solver : files.keySet()){
					PrintWriter pw=null;
					try {
						pw = new PrintWriter(new FileOutputStream(files.get(solver),true));
					} catch (FileNotFoundException e) {
						
						Util.panic("Experiment run: error writing to results file for " + solver, e);
					}
					pw.write(
							String.format(
									RESULTS_RECORD_BASE, 
									p2,
									curP2Results.getAverageCcs(solver),
									curP2Results.getAverageAssignments(solver)));
					out("Written to " + files.get(solver).getAbsolutePath());
					pw.close();
				}
			}
		}
		out("Done.");
	
	}
	private String description() {
		String ans = "";
		ans+="Experiment Description:";
		ans+="\nVars count:"  + _varsCount;
		ans+="\nDomain size:"  + _domainSize;
		ans+="\nP1 start:"  + _p1min;
		ans+="\nP1 step:"  + _p1step;
		ans+="\nP1 end:"  + _p1max;
		ans+="\nP2 start:"  + _p2min;
		ans+="\nP2 step:"  + _p2step;
		ans+="\nP2 end:"  + _p2max;
		ans+="\nProblems count:"  + _problemsCount;
		ans+="\nMaximum assignments: " + BranchAndBoundSolver.MAX_ASSIGNMENTS;
		ans+="\nTimout: " + (BranchAndBoundSolver.stopAfterMaxSeconds ?BranchAndBoundSolver.MAX_TIME + " millis ": " none");
		ans+="\nOutput folder:"  + _outputFolder;
		return ans;
	}
	private void out(Object msg){
		Logger.inst().debug(msg.toString());
	}

}
