package maxcsp;

public class Assignment{
	public static final int UNASSIGNED=-1;
	private final int _vars[];
	
	public Assignment(int varsCount){
		_vars=new int[varsCount];
		for(int i : _vars){
			i=UNASSIGNED;
		}
	}
	
	public void assigned(int var, int value){
		_vars[var]=value;
	}
	public int value(int var){
		return _vars[var];
	}
	public boolean isAssigned(int var){
		return _vars[var]!=UNASSIGNED;
	}
}