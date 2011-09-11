package maxcsp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	public static final int LEVEL_DEFAULT=0;
	public static final int LEVEL_LESS=-1;
	
	public final int level;
	private static Logger _inst;
	
	public static Logger inst(){
		if (Logger._inst==null)
			Logger._inst=new Logger();
		return Logger._inst;
	}
	public void debug(String msg){
		out(msg);
	}
	public void normal(String string) {
		out(string);
	}
	private void out(String msg){
		System.out.println(new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(new Date()) + "| " + msg);
		System.out.flush();
	}
	private Logger(){
		String l = System.getenv("Logging");
		this.level=(l==null ? LEVEL_DEFAULT : Integer.parseInt(l));
			
	}
	
	
}
