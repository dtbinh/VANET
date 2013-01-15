package simulation.solutions.custom.ACO_MWAC;

import javax.swing.Timer;

import simulation.solutions.custom.ACO_MWAC.AntAssistant.Ant_Route_Assistant;
import simulation.solutions.custom.ACO_MWAC.Messages.ACO_Message_Backward_Ant;
import simulation.solutions.custom.ACO_MWAC.Messages.ACO_Message_Forward_Ant;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Ant Manager
 * @author yacine LATTAB & Ouerdia SLAOUTI
 */
public class ACO_MWAC_AntManager {

	//identité de la prochaine fourmi Forward
	private short AntId;

	//premier message d'initilisation de la pheromone
	int Best_Hop;

	//faut-t-il faire evaporer la pheromone ?
	public boolean RoundOfEvaporation;

	//faut-t-il mettre à jour les niveaux d'energie residuelles ?
	public boolean RoundOfEnergyUpdate;

	//chronometre pour les mise à jour des energies residuelles
	public Timer Timer_RoundOfEnergyUpdate;

	//chronometre pour les mise à jour des energies residuelles
	public Timer Timer_RoundOfEvaporation;
	
	//faut il envoyer une donnée ..... envoi de donnée ?
	public boolean RoundOfSendMessage;
	
	//chronometre pour l'envoi des messages
	public Timer Timer_RoundOfSendMessage;
	


	//constructeur pour initialiser la classe
	public ACO_MWAC_AntManager(){

		this.AntId = 0;
		this.Best_Hop = 100;
		this.RoundOfEvaporation = false;
		this.RoundOfEnergyUpdate = false;
		//this.RoundOfSendMessage = false;

		Timer_RoundOfEnergyUpdate = new Timer( Configuration.Energy_Update_Delay, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				RoundOfEnergyUpdate = true;
			}
		});

		Timer_RoundOfEvaporation = new Timer(Configuration.Evaporation_Delay, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				RoundOfEvaporation = true;
			}
		});
	
		/*Timer_RoundOfSendMessage = new Timer( Configuration.Send_Message_Delay, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				RoundOfSendMessage = true;
			}
		});*/
	}


	private short get_new_AntId(){
		this.AntId ++;
		return this.AntId;
	}


	public boolean IsRoundOfEnergyUpdate(){
		if (this.RoundOfEnergyUpdate) {
			this.RoundOfEnergyUpdate = false;
			return true;
		}
		return false;
	}

	public boolean IsRoundOfEvaporation(){
		if (this.RoundOfEvaporation) {
			this.RoundOfEvaporation = false;
			return true;
		}
		return false;
	}
	
	public boolean IsRoundOfSendMessage(){
		if (this.RoundOfSendMessage) {
			this.RoundOfSendMessage = false;
			return true;
		}
		return false;
	}


	//instancie une nuvelle fourmi à envoyer
	public ACO_Message_Forward_Ant new_Forward_Ant(int Sender, int Receiver, String Data){
		return (new ACO_Message_Forward_Ant(Sender, Receiver, this.get_new_AntId(), new int [0], Data));
	}

	//permet d'ajouter un relais à la memoire une fourmi forward
	public ACO_Message_Forward_Ant Add_Relay (ACO_Message_Forward_Ant fourmi, int agent){
		return new ACO_Message_Forward_Ant(fourmi.getSender(), fourmi.getReceiver(), fourmi.get_AntId(), Ant_Route_Assistant.add(fourmi.get_Memory(), agent), fourmi.get_msg());
	}

	//permet de supprimer un relais de la memoire de la fourmi forward
	public ACO_Message_Forward_Ant Delete_Last_Relay (ACO_Message_Forward_Ant fourmi){
			return new ACO_Message_Forward_Ant(fourmi.getSender(), fourmi.getReceiver(), fourmi.get_AntId(), Ant_Route_Assistant.removeLastId(fourmi.get_Memory()), fourmi.get_msg());
	}

	//permet de supprimer un relais de la memoire de la fourmi backward
	public ACO_Message_Backward_Ant Delete_Last_Relay (ACO_Message_Backward_Ant fourmi){
			return new ACO_Message_Backward_Ant(fourmi.getSender(), fourmi.getReceiver(), fourmi.get_AntId(), fourmi.get_length(), Ant_Route_Assistant.removeLastId(fourmi.get_Memory()));
	}

}
