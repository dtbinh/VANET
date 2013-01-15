package simulation.solutions.custom.AntMWAC.Ant;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;
 

public class AntManager {
	
	private short AntId;
	public boolean RoundOfEvaporation;
	public boolean RoundOfEnergyUpdate;
	public boolean RoundOfPrevent;
	public boolean RoundOfTauxOccupation;
	public Timer Timer_RoundOfPrevent;
	public Timer Timer_RoundOfEnergyUpdate; //chronometre pour qu'un capteur envoie son niveau d'energie a ses voisin 
    public Timer Timer_RoundOfEvaporation;
    public Timer Timer_RoundOfTauxOccupation;

  /***************************************************************************************************/
	public AntManager(){

		this.AntId = 0;
		this.RoundOfEvaporation = false;
		this.RoundOfEnergyUpdate = false;
		this.RoundOfPrevent =false ;
		this.RoundOfTauxOccupation =false ;
		Timer_RoundOfEnergyUpdate = new Timer(Parametres.Energy_Update_Delay, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				RoundOfEnergyUpdate = true;
			}
		});

		Timer_RoundOfEvaporation = new Timer(Parametres.Evaporation_Delay, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				RoundOfEvaporation = true;
			}
		});
	
		
		Timer_RoundOfPrevent = new Timer(Parametres.Prevent_Delay, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				RoundOfPrevent = true;
			}
		});
		
		
		Timer_RoundOfTauxOccupation = new Timer(Parametres.Taux_Delay, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				RoundOfTauxOccupation = true;
			}
		});
		
	}

  /****************************************************************************************/
	public  short get_new_AntId(){
		this.AntId ++;
		return this.AntId;
	}

  /****************************************************************************************/
	public boolean IsRoundOfEnergyUpdate(){
		if (this.RoundOfEnergyUpdate) {
			this.RoundOfEnergyUpdate = false;
			return true;
		}
		return false;
	}
	/****************************************************************************************/

	public boolean IsRoundOfEvaporation(){
		if (this.RoundOfEvaporation) {
			this.RoundOfEvaporation = false;
			return true;
		}
		return false;
	}
	
	
	/****************************************************************************************/
	public boolean IsRoundOfPrevent(){
		if (this.RoundOfPrevent) {
			this.RoundOfPrevent= false;
			return true;
		}
		return false;
	}
	/****************************************************************************************/
	public boolean IsRoundOfTauxOccupation(){
		if (this.RoundOfTauxOccupation){
			this.RoundOfTauxOccupation =false;
			return true;
		}
		return false;
	}
	public static  int []copyRoute(int []route)
	{ int []routeCopy=new int [route.length+1];
	routeCopy[0]=route.length;
	for (int i=1;i< route.length+1;i++ ) { routeCopy[i]=route[i-1]; 
	}
		
		return routeCopy;
		
	}
}
