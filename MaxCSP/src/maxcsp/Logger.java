package maxcsp;

public class Logger {
	private static Logger _inst;
	public static Logger inst(){
		if (Logger._inst==null)
			Logger._inst=new Logger();
		return Logger._inst;
	}
	public void debug(String msg){
		System.out.println(msg);
	}
}
