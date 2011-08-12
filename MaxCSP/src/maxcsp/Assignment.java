package maxcsp;

import java.io.Serializable;
import java.util.Collection;
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
	
	public Assignment(int varsCount){
		this._distance=0;
		this._assignsCount=0;
		this._vars=new Vector<Variable>(varsCount);
		for(int i=0;i<varsCount;i++)
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
		sanityCheck();
	}
	protected void sanityCheck(){
		Iterator<Variable> itr = _vars.iterator();
		int counter=0;
		while (itr.hasNext())
			if(itr.next().isAssigned())
				counter++;
		if(counter!=_assignsCount)
			Util.panic("Assignment sanity check");
			
	}
	public void unassign(Variable var){
		Variable myvar=findVar(var);
		if(myvar.isAssigned()){
			this._assignsCount--;
			myvar.unassign();
		}
		sanityCheck();
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
		return variablesIterator(true);
	}
	public Iterator<Variable> unassignedVariablesIterator(){
		return variablesIterator(false);
	}
	private Iterator<Variable> variablesIterator(final boolean assignedVars){
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
					if(next.isAssigned()==assignedVars)
						cur = next;
				}
			}
			@Override
			public boolean hasNext() {
				return cur!=null;
			}

			@Override
			public Variable next() {
				Variable ans = new Variable(cur);
				goNext();
				return ans;
			}

			@Override
			public void remove() {
				Util.panic("Assignment Iterator Remove");
			}
			
		};
	}
	public void assign(Variable var) {
		if(!var.isAssigned())
			Util.panic("Assignment - variable given is not assigned a value");
		this.assign(var,var.value());
	}
	public Variable pickUnassignedVariable() {
		sanityCheck();
		if(this.isComplete())
			Util.panic("Assignment - cannot pick an unassigned variable from a complete assignment");
		
		Variable ans;
		Iterator<Variable> itr = _vars.iterator();
		do{
			
			if(!itr.hasNext()){
				Util.panic("Assignment - Not complete but no unassigneds");
			}
			ans=itr.next();
		}while(ans.isAssigned());
		return new Variable(ans);
	}
	@Override
	public String toString(){
		String ans = "";
		Iterator<Variable> itr = _vars.iterator();
		while(itr.hasNext()){
			Variable v = itr.next();
			ans+=String.format("<%d,%d>%s",v._id,v.value(),(itr.hasNext() ? ", " : "."));
		}
		return ans;
	}
	
	public Collection<Variable>getUnassignedVars(){
		Vector<Variable> ans = new Vector<Variable>(_vars.size());
		Iterator<Variable> itr = unassignedVariablesIterator();
		while(itr.hasNext())ans.add(itr.next());
		return ans;
	}
}
