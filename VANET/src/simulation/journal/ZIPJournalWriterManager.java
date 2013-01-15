package simulation.journal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import simulation.events.Event;
import simulation.utils.StaticPerf;


/** allows to write in the zipped journal. Manage the journal segments 
 * @author Jean-Paul Jamont
 */
public class ZIPJournalWriterManager {

	/** file name of the journal*/
	private String fileName;
	/** number of the future segment */
	private int numSegment = 1;

	/**
	 * parametrized constructor
	 * @param fileName  file name of the journal  
	 */
	public ZIPJournalWriterManager(String fileName)
	{
		this(fileName,false);
	}

	/**
	 * parametrized constructor
	 * @param fileName  file name of the journal  
	 * @param reader is the journal open just for reading
	 */
	public ZIPJournalWriterManager(String fileName,boolean reader)
	{
		System.out.println("ZIPWriter");
		this.fileName=fileName;
		if (!reader) 
			(new File(fileName)).delete();
		else
		{
			// Update the number of existing segments
			File f;
			this.numSegment=0;
			boolean find=true;

			while(find)
			{
				this.numSegment++;
				f=new File(this.fileName+"_part"+this.numSegment);
				find=f.exists();
			}
		}
	}

	/**
	 * creates a segment with the specified events
	 * @param events events which must be stored in the fragment
	 */
	public void addJournalSegment(ArrayList<Event> events)
	{
		StaticPerf.init();
		if (events.size()>0)
			System.out.println("SAUVEGARDE DU ZIP ("+events.size()+")  "+events.get(0).getDateInMs()+"   "+events.get(events.size()-1).getDateInMs());
		else
			System.out.println("SAUVE DE ZIP... VIde");
		System.out.flush();
		try{
			FileOutputStream fos = new FileOutputStream(this.fileName+"_part"+this.numSegment++);
			GZIPOutputStream gz = new GZIPOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(gz);
			oos.writeObject(new LinkedList<Event>(events));
			oos.flush();
			oos.close();
			fos.close();
		}
		catch(Exception e)
		{
			System.out.println("Erreur pendant l'écriture "+e);
		}
		StaticPerf.printElapsed();
	}


	/** getter to the file name
	 * @return the file name
	 */
	public String getFileName()
	{
		return this.fileName;
	}

	/**
	 * returns the number of existing journal fragment
	 * @return the number of existing fragment
	 */
	public int getNumberOfSegments()
	{
		return this.numSegment-1;
	}
	
	/**
	 * remove the last fragment
	 */
	public void ignoreLastFragment()
	{
		this.numSegment--;
	}

}
