package maxcsp;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

public class Assignment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8650677597710303310L;
	private final Vector<Variable> _vars;
	private int _assignsCount;
	public int _distance;
	
	
	public Assignment(int domainSize){
		this._distance=0;
		this._assignsCount=0;
		this._vars=new Vector<Variable>(domainSize);
		for(int i=0;i<domainSize;i++)
			this._vars.add(i, new Variable(i));
	}
	public Assignment(Assignment other){
		this._distance=other._distance;
		this._assignsCount=other._assignsCount;
		this._vars=new Vector<Variable>(other._vars.size());
		for(int i=0;i<other._vars.size();i++){
			this._vars.add(i, new Variable(other._vars.elementAt(i)));
		}
	}
	private Variable findVar(Variable request){
		Variable ans =this._vars.elementAt(request._id); 
		if(!ans.equals(request)){
			Iterator<Variable>itr = this._vars.iterator();
			while(!ans.equals(request) & itr.hasNext()){
				ans=itr.next();
			}
		}
		if (!ans.equals(request))
			Util.panic("Assignment: variable " + request._id + " Does not exist." );
		return ans;
	}
	public void assign(Variable var, int value){
		Variable myvar=findVar(var);
		if(!myvar.isAssigned())
			this._assignsCount++;
		myvar.assign(value);
	}
	public void unassign(Variable var){
		Variable myvar=findVar(var);
		if(myvar.isAssigned()){
			this._assignsCount--;
			myvar.unassign();
		}
	}
	public boolean isAssigned(Variable var){
		Variable myvar = this.findVar(var);
		return myvar.isAssigned();
	}
	public int value(Variable var){
		return findVar(var).value();
	}
	public boolean isComplete(){
		return this._assignsCount==this._vars.size();
	}
	
	public Iterator<Variable> assignedVariablesIterator(){
		return new Iterator<Variable>(){
			Variable cur;
			Iterator<Variable> itr ;
			{
				itr = _vars.iterator();
				goNext();
			}
			private void goNext(){
				cur=null;
				while(cur==null & itr.hasNext()){
					Variable next = itr.next();
					if(next.isAssigned())
						cur = next;
				}
			}
			@Override
			public boolean hasNext() {
				return cur!=null;
			}

			@Override
			public Variable next() {
				Variable ans = cur;
				goNext();
				return ans;
			}

			@Override
			public void remove() {
				Util.panic("Assignment Iterator Remove");
			}
			
		};
	}
}
