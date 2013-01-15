package simulation.statistics.criteria.custom;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.ListIterator;
import simulation.statistics.AbstractDataBlock;
import simulation.statistics.DataBlock;
import simulation.journal.SimulationJournal;
import simulation.multiagentSystem.MAS;
import simulation.statistics.*;
import simulation.statistics.criteria.UserCriterion;
import simulation.events.Event;
import simulation.events.system.EnvoiFourmisEvent;
import simulation.events.system.EnvoiMWACEvent;
import simulation.events.system.PositionEvent;
import simulation.events.system.SendedFrameEvent;
public class UsedRouteUserCriteria  extends UserCriterion {



	private long []vectVolume ;

	public UsedRouteUserCriteria()
	{
		this.vectVolume=new long[1001];
		for(int i=0;i<=1000;i++) this.vectVolume[i]=0;
	}


	public boolean concernsAgent() {
		return true;
	}

	/**
	 * is this criteria concerning the whole system?
	 * @return yes if this criteria can be applied to the whole mas
	 */
	public boolean concernsMAS() {
		return true;
	}

	/**
	 * is this criteria concerning objects?
	 * @return yes if this criteria can be applied to an object
	 */
	public boolean concernsObject() {
		return true;
	}
	public String getDescription() {
		return "permet de voir les routes utilisees pour le routage ";
	}

	/**
	 * returns the name of the criteria
	 * @return the name of the criteria
	 */
	public String getName() {
		return "Route Used";
	}

	/**
	 * returns the author name
	 * @return the name of the author(s)
	 */
	public String getAuthor() {
		return "JP Jamont";
	}

	public AbstractDataBlock  getDataBlock(SimulationJournal journal) {
		return getDataBlock(journal,-1);
	}


	public AbstractDataBlock getDataBlock(SimulationJournal journal, int id) {


	

		DataBlock data = new DataBlock("Number of sended frames","Time",DataBlock.TIME_IN_MS,"Number of frames",DataBlock.SIMPLE_VALUE_INT);
		data.addPoint(0,0);
		
		//System.out.println("LAST EVENT:"+journal.getLastEventDate().getTimeInMillis()+"    SLOT TIME:"+SLOT_TIME);
		LinkedList<Event> eventList = journal.extractAllEvents();
		//System.out.println("ON DONNE A NOEuser "+eventList.size()+" events");
		ListIterator<Event> iter =eventList.listIterator();
		int sender;
		int receiver;
		long volume;
		long cumul=0;
		Event evt=null;
		String res="";
		while(iter.hasNext())
		{
			evt=iter.next();
			
				if (evt instanceof SendedFrameEvent) 
					{
					sender=((SendedFrameEvent) evt).getFrame().getSender();
					receiver=((SendedFrameEvent) evt).getFrame().getReceiver();
					volume=((SendedFrameEvent) evt).getFrame().getVolume();
					vectVolume[sender]+=volume;
					cumul+=volume;
					}
				
				

				
		}
		
		DecimalFormat decimalFormat = (DecimalFormat)DecimalFormat.getInstance();
        decimalFormat.applyPattern("##.###");
		
        res+="Volume\n";
        for(int i=1;i<=1000;i++) res+=vectVolume[i]+"%\t";
		res+="\n\n";
	        
		
		res+="Ratio\n";
        for(int i=1;i<=1000;i++) res+=decimalFormat.format(((double)(100*this.vectVolume[i]))/cumul)+"%\t";
    	res+="\n";
			
		
		
		eventList.clear();
		
		try {
			PrintWriter print=new PrintWriter(new FileWriter("D:\\MATRICE.TXT",false));
			print.println(res);
			print.close();
			System.out.println("Fichier D:\\MATRICE.TXT créé");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Impossible d'écrire dans D:\\MATRICE.TXT ");
		}
		return data;
	}


}
