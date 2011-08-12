package maxcsp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Experiment implements Runnable{
	public static final int DEFAULT_PROBLEMS_COUNT = 20;
	public final static int DEFAULT_VARS_COUNT=7;
	public final static int DEFAULT_DOMAIN_SIZE=7;
	public final static double DEFAULT_P1=0.5;
	public final static double DEFAULT_P2_MIN=0.0;
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
	
	
	private static void main(String[] args) {
		

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
		for(int i=0;i<_problemsCount;i++){
			//Problem p = Util.
		}
	}
	private void out(Object msg){
		Logger.inst().debug(msg.toString());
	}

}
