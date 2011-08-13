package maxcsp;

public class Assignment{
	public static final int UNASSIGNED=-1;
	private final int _vars[];
	private int _assignedCount;
	public Assignment(int varsCount){
		_vars=new int[varsCount];
		_assignedCount=0;
		for(int i : _vars){
			i=UNASSIGNED;
		}
	}
	public void assign(int var, int value){
		if(!isAssigned(var))_assignedCount++;
		_vars[var]=value;
	}
	public boolean isComplete(){
		return _assignedCount==_vars.length;
	}
	public Assignment(Assignment other) {
		_vars = new int[other._vars.length];
		for(int i=0;i<other._vars.length;i++){
			_vars[i]=other._vars[i];
			if(_vars[i]!=UNASSIGNED)_assignedCount++;
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