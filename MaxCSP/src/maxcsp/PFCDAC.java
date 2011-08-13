package maxcsp;

public class PFCDAC extends PFC {

	public PFCDAC(Problem problem) {
		super(problem);
	}
	
	@Override 
	protected int bound(Assignment ass){
		return calcDistance(ass) + calcIC(ass) + calcDAC(ass);
	}

	private int calcDAC(Assignment ass) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	protected void proprocess(){
		//init DAC databases.
	}

}
