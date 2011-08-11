package maxcsp;

public class Solution extends Assignment{
	private static final long serialVersionUID = 1524510753465948381L;
	public final int CCs;
	
	public Solution(Assignment assignment, int cCs) {
		super(assignment);
		this.CCs = cCs;
	}
}
