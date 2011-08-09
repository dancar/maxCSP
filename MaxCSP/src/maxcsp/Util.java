package maxcsp;
//Nothing 2
public class Util {
	public static boolean chance(double probability){
		return (Math.random()>probability);
	}
	public static void panic(String reason){
		System.out.println("PANIC: " + reason);
		System.exit(-1);
	}
	
}
