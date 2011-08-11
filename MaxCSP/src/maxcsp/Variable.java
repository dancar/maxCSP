package maxcsp;

import java.io.Serializable;
import java.util.Vector;

public class Variable implements Serializable{
	public static final int UNASSIGNED=-1;
	private static final long serialVersionUID = -7164496881037159955L;
	public final int _id;
	private int _value;
	private boolean _assigned;
	public Variable (int id){
		this._id=id;
		this._assigned=false;		
	}
	public Variable(Variable other){
		this._id=other._id;
		this._assigned=other._assigned;
		this._value=other._value;
	}
	@Override
	public boolean equals(Object other){
		boolean ans = false;
		if (other instanceof Variable)
			ans = ((Variable)other)._id==this._id;
		return ans;
	}
	public boolean isAssigned(){
		return this._assigned;
	}
	public Variable unassign(){
		this._assigned=false;
		return this;
	}
	public Variable assign(int value){
		this._assigned=true;
		this._value=value;
		return this;
	}
	public int value(){
		return this._value;
	}
	public static Vector<Variable> createVariableList(int count){
		Vector<Variable>ans = new Vector<Variable>(count);
		for(int i=0;i<count;i++)
			ans.add(i,new Variable(i));
		return ans;
	}
	
	@Override
	public String toString(){
		String ans = "Var#" + this._id;
		if (this.isAssigned())
			ans+=String.format("[%d]",this.value());
		return ans; 
		
	}
	@Override
	public int hashCode(){
		return _id;
	}
}
