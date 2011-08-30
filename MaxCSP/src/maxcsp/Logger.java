package maxcsp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
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
	
	
}
