package simulation.solutions.custom.ACO_MWAC;

import java.util.LinkedList;
import java.util.ListIterator;

import simulation.solutions.custom.ACO_MWAC.AntAssistant.Ant_Route_Assistant;
import simulation.solutions.custom.ACO_MWAC.Messages.ACO_Message_Forward_Ant;
import simulation.utils.aDate;

/**
 * Already processed Forwarded Ant Manager
 * @author Yacine LATTAB & Ouerdia SLAOUTI
 */
public class ACO_MWAC_AlreadyProcessedForwardAnt {

	//delais maximum pour raffraichir la liste
	private static int ms_MAX_FORWARD_ANT_DELAY = 3000;

	//liste des fourmis deja passer par un agent
	private LinkedList<AlreadyProcessedForwardAntItem> list;
	
	
	public ACO_MWAC_AlreadyProcessedForwardAnt()
	{
		this.list = new LinkedList<AlreadyProcessedForwardAntItem>();
	}
	
	//revoie vrai ou faux selon que la fourmi soit passée par un agent ou non
	public synchronized void isAlreadyProcessed(ACO_Message_Forward_Ant fourmi)
	{
		AlreadyProcessedForwardAntItem item;
		ListIterator<AlreadyProcessedForwardAntItem> iter=this.list.listIterator();
		aDate now = new aDate();
		boolean finded = false;
		
		while(iter.hasNext())
		{
			item=iter.next();
			if( !finded && item.fourmi.get_msg().equals(fourmi.get_msg())) finded=true;
			
			//raffraichir la liste des fourmis deja passées par un agent
			if (now.differenceToMS(item.date)>ms_MAX_FORWARD_ANT_DELAY) iter.remove();
		}

		if (!finded) list.add(new AlreadyProcessedForwardAntItem(fourmi));
	}
	
	//permet l'affichage de la liste des fourmis deja passées par un agent durant un certain laps de temps
	public synchronized String toHTML()
	{
		String res="<B>Table of already produce and/or processed Forward Ant</B> ("+this.list.size()+")<BR>";
		res+="<TABLE border=1>";
		res+="<TR><TD>Date</TD><TD>Sender</TD><TD>AntId</TD><TD>Route</TD><TD>Data</TD></TR>";
		ListIterator<AlreadyProcessedForwardAntItem> iter = this.list.listIterator();
		while(iter.hasNext()) res+=iter.next().toHTML();
		res+="</TABLE>";
		return res;
	}
	
	//classe interne
	private class AlreadyProcessedForwardAntItem
	{
		ACO_Message_Forward_Ant fourmi ;
		public aDate date;

		public  AlreadyProcessedForwardAntItem (ACO_Message_Forward_Ant fourmi)
		{
			this.fourmi= fourmi;
			this.date=new aDate();
		}
		
		public String toHTML()
		{
			return "<TR><TD>"+aDate.msToHHMMSSCCC(this.date.getTimeInMillis())+"</TD><TD>"+fourmi.getSender()+"</TD><TD>"+fourmi.get_AntId()+"</TD><TD>"+Ant_Route_Assistant.toString(fourmi.get_Memory())+"</TD><TD>"+fourmi.get_msg()+"</TD></TR>";
		}
	}
	

}
