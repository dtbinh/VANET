package simulation.solutions.custom.TrustedMWAC;

import simulation.utils.aDate;

/**
 * 
 * @author Jean-Paul Jamont
 */
public class TrustManager {

	/** teta1 is used in the neighbood trust decision algorithm - threshold */
	public final static float TETA1 = 0.6f; 
	/** alpha is used in the trust decrease algorithm (!!! 0<ALPHA<BETA<1) */
	public final static float ALPHA = 0.1f; 	
	/** béta is used in the trust decrease algorithm (!!! 0<ALPHA<BETA<1) */
	public final static float BETA = 0.2f; 	
	
	/**
	 * (Anca) value by which trust is decreased,in the case of workstation usurpation 
	 * */
	public final static float ETA = 0.3f;
	
	/**
	 * (Anca) values for trust recovery algorithm
	 */
	// evaporation rate 0 <= LAMBDA < 1
	public static final float LAMBDA = 0.01f;
	// frequency of calling the trust recovery function --> number of
	// miliseconds after which trustRecovery function is called 
	public static final int NIU = 5000; 
	
	
	
	/** link to the managed agent */
	private TrustedMWACAgent myAgent;
	/** Date of the last detected id usurpation */
	private aDate dateOfIdUsurpation = null;
	
	/**
	 * default constructor
	 * @param agent link to the managed agent
	 */
	public TrustManager(TrustedMWACAgent agent)
	{
		this.myAgent=agent;
	}
	

	/**
	 * this method is called when an id has been usurped
	 */
	public void myIdHasBeenUsurped()
	{
		this.dateOfIdUsurpation=new aDate();
	}
	
	/**
	 * returns if the id has already been usurped
	 * @return true if the id has been usurped
	 */
	public boolean isIdHasBeenUsurped()
	{
		return (this.dateOfIdUsurpation!=null);
	}
	

	/**
	 *
	 * @param str
	 * @return
	 */
	private String _toHTML_trusted(String str)
	{
		return "<FONT color=green>"+str+"<BR></FONT>";
	}
	/**
	 * 
	 * @param str
	 * @return
	 */
	private String _toHTML_distrusted(String str)
	{
		return "<FONT color=red>"+str+"<BR></FONT>";
	}
	/**
	 * returns a HTML string representation the neighboor list
	 */
	public String toHTML()
	{
				
		String res="<B>Trust management informations:</B><BR>";
		
		
		
		if(myAgent.isConfidentNeighboorhood()) 
			res+=_toHTML_trusted("Neighboorhood is trusted");
		else
			res+=_toHTML_distrusted("Neighboorhood is distrusted");
		
		
		
		if (!this.isIdHasBeenUsurped())
			res+=_toHTML_trusted("My identifier has never been usurpated");
		else
			res+=_toHTML_distrusted("My identifier has already  been usurpated at "+this.dateOfIdUsurpation.toString());			
		
		

		return res;
	}
}
