package simulation.solutions.custom.ACO_MWAC;

public class Configuration {
	
	public static float initial_amount_of_energy 	= (float) 25;
	public static int base_station = 1;
	
	public static float Hop_Redution = 2;  
	
	public static float alpha 	= (float) 0.75;      		//0.75   	//0.50
	public static float beta 	= (float) 0.50;		 		//0.50   	//0.75
	public static float Q 		= (float) 2;  	    		//0.50	  	//0.50	
	public static float roe 	= (float) 0.01;		 		//0.01	  	//0.01
	
	public static int Evaporation_Delay = 20000;           
	public static int Energy_Update_Delay = 30000;
	
	public static int NetWork_Size = 200;
	public static int Waiting_Time_for_creating_All_Agents = 55;                 //10,20,55 secondes
	
	public static int come_back = 0;
}
