package maxcsp;

public interface  MaxCSPSolver {
	public  Assignment solve();
	public  int solutionCost();
	public  int solutionCCs();
	public  int solutionAssignments();
	public  String getName() ;
}
