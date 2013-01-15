package simulation.solutions.custom.ACO_MWAC;

import java.util.LinkedList;
import java.util.ListIterator;
import simulation.solutions.custom.ACO_MWAC.AntAssistant.Ant_Route_Assistant;
import simulation.solutions.custom.ACO_MWAC.Messages.ACO_Message_Backward_Ant;
import simulation.utils.aDate;

/**
 * Table of Received Acknowledgment and Route Taken by Backward Ant
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */
public class ACO_MWAC_AlreadyProcessedBackwardAnt {

	//delais maximum pour raffraichir la liste
	private static int ms_MAX_FORWARD_ANT_DELAY = 3000;
	
	//liste des acquittement reçus
	private LinkedList<AlreadyProcessedBackwardAntItem> list;

	
	public  ACO_MWAC_AlreadyProcessedBackwardAnt()
	{
		this.list=new LinkedList<AlreadyProcessedBackwardAntItem>();
	}

	//supprime les anciennes fourmis et ajouter une nouvelle
	public synchronized void isAlreadyProcessed (ACO_Message_Backward_Ant fourmi)
	{
		AlreadyProcessedBackwardAntItem item;
		ListIterator<AlreadyProcessedBackwardAntItem> iter=this.list.listIterator();
		aDate now = new aDate();
		while(iter.hasNext())
		{
			item=iter.next();
			
			//raffraichir la liste des fourmis deja passées par un agent
			if (now.differenceToMS(item.date)>ms_MAX_FORWARD_ANT_DELAY) iter.remove();
		}
		list.add(new AlreadyProcessedBackwardAntItem(fourmi));
	}
	
	
	//permet l'affichage de la liste des fourmis Backward deja reçues
	public synchronized String toHTML()
	{
		String res="<B>Table of already processed Backward Ant Ant</B> ("+this.list.size()+")<BR>";
		res+="<TABLE border=1>";
		res+="<TR><TD>Date</TD><TD>Sender</TD><TD>AntId</TD><TD>Route</TD></TR>";
		ListIterator<AlreadyProcessedBackwardAntItem> iter = this.list.listIterator();
		while(iter.hasNext()) res+=iter.next().toHTML();
		res+="</TABLE>";
		return res;
	}
	
	//classe interne
	private class AlreadyProcessedBackwardAntItem
	{
		ACO_Message_Backward_Ant fourmi ;
		public aDate date;

		public AlreadyProcessedBackwardAntItem(ACO_Message_Backward_Ant fourmi)
		{
			this.fourmi = fourmi;
			this.date = new aDate();
		}
		
		public String toHTML()
		{
			return "<TR><TD>"+aDate.msToHHMMSSCCC(this.date.getTimeInMillis())+"</TD><TD>"+fourmi.getSender()+"</TD><TD>"+fourmi.get_AntId()+"</TD><TD>"+Ant_Route_Assistant.toString(fourmi.get_Memory())+"</TD></TR>";
		}
	}
	
}
