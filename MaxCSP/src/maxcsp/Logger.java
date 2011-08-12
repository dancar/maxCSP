package maxcsp;

public class Logger {
	private static Logger _inst;
	
	public static Logger inst(){
		if (Logger._inst==null)
			Logger._inst=new Logger();
		return Logger._inst;
	}
	public void debug(String msg){
		debug(msg, true);
	}
	public void debug(String msg, boolean line){
		System.out.print(msg);
		if(line) System.out.println();
	}
	public void normal(String string) {
		System.out.print(string);
	}
	public void normaln(String string) {
		System.out.println(string);
	}
	
	
}
