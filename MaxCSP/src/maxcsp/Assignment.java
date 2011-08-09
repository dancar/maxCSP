package maxcsp;

import java.io.Serializable;

public class Assignment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8650677597710303310L;
	private static final int UNASSIGNED = -1;
	private final int _assigns[];
	public Assignment(int domainSize){
		_assigns=new int[domainSize];
		for(int i: this._assigns)
			this._assigns[i]=UNASSIGNED;
	}
	public Assignment(Assignment other){
		this._assigns=new int[other._assigns.length];
		for(int i: other._assigns)
			this._assigns[i]=other._assigns[i];
	}
	public void assign(int var, int value){
		this._assigns[var]=value;
	}
	public void unassign(int var){
		_assigns[var]=UNASSIGNED;
	}
	public boolean isAssigned(int var){
		return _assigns[var]!=UNASSIGNED;
	}
	public int value(int var){
		return _assigns[var];
	}
}
