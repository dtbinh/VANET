package simulation.solutions.custom.DSDV;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class DSDV_Manager {
	
	//delais de mise à jour du SN et envoi des table de routage
	private int Table_Update_Delay = 25000;
	
	//le niveau minimum d'energie pour qu'un agent ne puisse pas participer au routage
	public float EnergyMin = (float)3.75;
	
	
	// le numero de sequence de l'agent DSDV
	private int SequenceNumber;
	
	//variable qui indique à un agent qu'il est temps d'envoyer une mise à jour
	public Boolean RoundOfTableUpdate;
	
	//chronometre pour mise à jour periodique le sequence Number et envoyer la table de routage
	private Timer Timer_RoundOfTableUpdate;
	
	public DSDV_Manager(int Id){
		
		//le SN est initilisé à l'identité de l'agent multiplié par deux
		this.SequenceNumber = Id *2;
		
		this.RoundOfTableUpdate = false;
		this.Timer_RoundOfTableUpdate=new Timer(this.Table_Update_Delay, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SequenceNumber = SequenceNumber + 2;
				RoundOfTableUpdate = true;
			}
		});
		this.Timer_RoundOfTableUpdate.start();
	}
	
	
	public int get_SN(){
		return this.SequenceNumber;
	}
	
	public void Stop_Chrono(){
		this.SequenceNumber +=1;
		this.Timer_RoundOfTableUpdate.stop();
	}
}
