package maxcsp;

public interface  MaxCSPSolver {
	public  Assignment solve();
	public  int solutionCost();
	public  double solutionCCs();
	public  double solutionAssignments();
	public  String getName() ;
}
