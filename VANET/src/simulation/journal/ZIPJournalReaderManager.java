package simulation.journal;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

import simulation.events.Event;

/** allows to read in the zipped journal. Manage the journal segments 
 * @author Jean-Paul Jamont
 */
public class ZIPJournalReaderManager {

	/** file name of the journal*/
	private String fileName;

	/**
	 * parametrized constructor
	 * @param fileName  file name of the journal  
	 */
	public ZIPJournalReaderManager(String fileName)
	{
		this.fileName=fileName;
	}

	/**
	 * returns the events stored in a specific segment of the journal
	 * @param segmentNumber number of the segment
	 * @return the events of the specified segment
	 */
	public LinkedList<Event> getJournalSegment(int segmentNumber)
	{
		LinkedList<Event> list = new LinkedList<Event>();
		FileInputStream fis = null;
		GZIPInputStream gs = null;
		ObjectInputStream ois = null;

		try
		{

			fis = new FileInputStream(this.fileName+"_part"+segmentNumber);
			gs = new GZIPInputStream(fis);
			ois = new ObjectInputStream(gs);
			list=(LinkedList<Event>) ois.readObject();

			ois.close();
			gs.close();
			fis.close();
		}
		catch(Exception e)
		{
			System.out.println("Erreur pendant la lecture "+e);
		}

		try
		{
			if(ois!=null) ois.close();
			if(gs!=null) gs.close();
			if(fis!=null) fis.close();
		}
		catch(Exception e)
		{
			System.out.println("Erreur "+e);
		}

		return list;
	}

	/**
	 * returns the number of existing journal fragment
	 * @return the number of existing fragment
	 */
	public int getNumberOfExistingSegment()
	{
		File f;
		int i=0;
		boolean find=true;

		while(find)
		{
			i++;
			f=new File(this.fileName+"_part"+i);
			find=f.exists();
		}

		return i;
	}

	/** getter to the file name
	 * @return the file name
	 */
	public String getFileName()
	{
		return this.fileName;
	}

}
