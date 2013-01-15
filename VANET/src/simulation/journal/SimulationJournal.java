package simulation.journal;

import java.util.*;

import simulation.utils.StaticPerf;
import simulation.utils.aDate;

import simulation.events.Event;

/** 
 * allows to manages of the events
 * @author Jean-Paul Jamont
 */
public class SimulationJournal {

	/** extraction of events by raiser identifier*/
	private final static int EXTRACT_EVENTS_BY_RAISER = 0;
	/** extraction of events by raiser identifier and event class*/
	private final static int EXTRACT_EVENTS_BY_RAISER_CLASS = 1;
	/** extraction of events by raiser identifier and two event class*/
	private final static int EXTRACT_EVENTS_BY_RAISER_2CLASSES = 2;
	/** extraction of event by event class*/
	private final static int EXTRACT_EVENTS_BY_CLASS = 3;
	/** extraction of event by two event class*/
	private final static int EXTRACT_EVENTS_BY_2CLASSES = 4;
	/** extraction of all events */
	private final static int EXTRACT_ALL_EVENTS = 5;
	/** extraction of event on a time interval*/
	private final static int EXTRACT_EVENTS_BY_TIME_INTERVAL= 6;
	/** extraction of event by raisers identifiers list*/
	private final static int EXTRACT_EVENTS_BY_RAISER_LIST= 7;


	/** max size of the list in memory */
	public final static int MAX_SIZE = 50000;


	/** list of events */
	private ArrayList<Event> events;
	/** events counter */
	private int eventsCounter;
	/** reference to the ZIP journal segment manager */
	private ZIPJournalWriterManager zip;
	/** date of the last added event */
	private long msDateOfLastEvent;
	/** is an existing journal open to only read access */
	private boolean readOnly;

	/**
	 * Parametrized constructor
	 * @param folder folder where must be saved the fragments
	 */
	public SimulationJournal(String folder)
	{
		this(folder,false);
	}

	/**
	 * Parametrized constructor
	 * @param folder folder where must be saved the fragments
	 */
	public SimulationJournal(String fileName,boolean reader)
	{
		String paramFileName = fileName;
		if(!reader) 
		{
			
			GregorianCalendar calendar= new GregorianCalendar();
			System.out.println(calendar);
			paramFileName+=("\\"+calendar.get(Calendar.YEAR)+"y"+String.format("%02d",1+calendar.get(Calendar.MONTH))+"m"+String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH))+"d__"+String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY))+"h"+String.format("%02d",calendar.get(Calendar.MINUTE))+"m"+String.format("%02d",calendar.get(Calendar.SECOND))+"s__journal.zip");
			System.out.println("\n Création du journal "+paramFileName);
		}

		this.zip=new ZIPJournalWriterManager(paramFileName,reader);
		this.events = new ArrayList<Event>(MAX_SIZE);
		this.eventsCounter=0;
		this.msDateOfLastEvent = 0;
		this.readOnly=reader;

		StaticPerf.init();
		// Update some attributes
		LinkedList<Event> lst=null;
		ZIPJournalReaderManager zip = new ZIPJournalReaderManager(this.zip.getFileName());
		for (int i=1;i<=this.zip.getNumberOfSegments();i++)
		{
			lst=zip.getJournalSegment(i);
			this.eventsCounter+=lst.size();
			try{if(lst!=null) this.msDateOfLastEvent=lst.getLast().getDateInMs();}
			catch(NoSuchElementException e){System.out.println("Problème dans le chaînage de la sauvegarde"); this.zip.ignoreLastFragment();}
		}
		System.out.println("SimJournal de "+this.eventsCounter+" évenements reconstruit en "+StaticPerf.elapsedToString()+" Dernier evt:"+this.msDateOfLastEvent+"ms");
		if (reader)
			System.out.println("Ouverture du journal '"+this.zip.getFileName()+"' en lecture");
		else
			System.out.println("Création et ouverture du journal '"+this.zip.getFileName()+"' en écriture");

	}

	/**
	 * date of the last event added in the journal
	 * @return the date
	 */
	public aDate getLastEventDate()
	{
		System.out.println("LAST DATE:"+msDateOfLastEvent);
		return new aDate(msDateOfLastEvent);
	}

	/**
	 * Add a new event
	 * @param event the added event
	 */
	public synchronized void add(Event event) 
	{
		this.eventsCounter++;
		this.events.add(event);
		this.msDateOfLastEvent=event.getDate().getTimeInMillis();
		// On vérifie que pas locked car il est possible qu'on soit en train de faire une extraction d'evenement
		if (events.size()>=MAX_SIZE) 
		{
			zip.addJournalSegment(events);
			events.clear();
		}
		if (this.eventsCounter%MAX_SIZE==0) System.out.println(this.eventsCounter+" events in memory");
	}

	/**
	 * Saves the events in memory in a fragment
	 */
	public synchronized void flushInFile()
	{
		ArrayList<Event> lst = this.events;
		this.events=new ArrayList<Event>(MAX_SIZE);
		zip.addJournalSegment(lst);
	}

	/**
	 * getter to the number of events in memory
	 * @return the number of event in memory
	 */
	public synchronized long nbEventsInMemory()
	{
		return this.events.size();
	}

	/**
	 * the number of event added during the whole simulation
	 * @return the number of events
	 */
	public long nbEvents()
	{
		return this.eventsCounter;
	}




	/**
	 * Event extraction : all events
	 * @return the events list
	 */
	public LinkedList<Event> extractAllEvents()
	{
		return extractAllEvents(SimulationJournal.EXTRACT_ALL_EVENTS,-1,null,null,null);
	}
	/**
	 * Event extraction : events concerning a subset of entities (union)
	 * @param raisers the raisers identifier
	 * @param indifferent RESERVED
	 * @return a subset of events
	 */
	public LinkedList<Event> extractAllEvents(ArrayList<Integer> raisers,boolean indifferent)
	{
		return extractAllEvents(SimulationJournal.EXTRACT_EVENTS_BY_RAISER_LIST,-1,null,null,raisers);
	}
	/**
	 * Event extraction : extraction between two dates
	 * @param date1 first date
	 * @param date2 second date
	 * @return a subset of events
	 */
	public LinkedList<Event> extractAllEvents(int date1,int date2)
	{
		ArrayList<Integer> lst = new ArrayList<Integer>(2);
		lst.add(date1); lst.add(date2);
		return extractAllEvents(SimulationJournal.EXTRACT_EVENTS_BY_TIME_INTERVAL,-1,null,null,lst);
	}
	/**
	 * Event extraction : events concerning one entity
	 * @param raiser identifier of the raiser
	 * @return a subset of events
	 */
	public LinkedList<Event> extractAllEvents(int raiser)
	{
		return extractAllEvents(SimulationJournal.EXTRACT_EVENTS_BY_RAISER,raiser,null,null,null);
	}
	/**
	 * Event extraction : a class of event
	 * @param cl class of the searched events
	 * @return a subset of events
	 */
	public LinkedList<Event> extractAllEvents(Class cl)
	{
		return extractAllEvents(SimulationJournal.EXTRACT_EVENTS_BY_CLASS,-1,cl,null,null);
	}
	/**
	 * Event extraction : two classes optimized search (union)
	 * @param cl1 class of the searched events
	 * @param cl2 class of the searched events
	 * @return a subset of events
	 */
	public LinkedList<Event> extractAllEvents(Class cl1,Class cl2)
	{
		return extractAllEvents(SimulationJournal.EXTRACT_EVENTS_BY_2CLASSES,-1,cl1,cl2,null);
	}
	/**
	 * Event extraction : a class of event raised by a specified raiser (optimized search)
	 * @param raiser raiser identifier
	 * @param cl class of searched events
	 * @return a subset of events
	 */
	public LinkedList<Event> extractAllEvents(int raiser,Class cl)
	{
		return extractAllEvents(SimulationJournal.EXTRACT_EVENTS_BY_RAISER_CLASS,raiser,cl,null,null);
	}

	/**
	 * Event extraction : two classes of events (union) for a specific raiser
	 * @param raiser identifier of the raiser
	 * @param cl1 class of searched events
	 * @param cl2 class of searched events
	 * @return a subset of events
	 */
	public LinkedList<Event> extractAllEvents(int raiser,Class cl1,Class cl2)
	{
		return extractAllEvents(SimulationJournal.EXTRACT_EVENTS_BY_RAISER_2CLASSES,raiser,cl1,cl2,null);
	}

	/**
	 * Event extraction : generic search (sumarize all search) 
	 * @param type type of search
	 * @param raiser raiser identifier
	 * @param cl1 a class of event
	 * @param cl2 a class of event
	 * @param raisers raisers identifier list
	 * @return a subset of events
	 */
	private LinkedList<Event> extractAllEvents(int type,int raiser,Class cl1,Class cl2,ArrayList<Integer> raisers)
	{
		StaticPerf.init();
		LinkedList<Event> res = new LinkedList<Event>();

		ZIPJournalReaderManager zip = new ZIPJournalReaderManager(this.zip.getFileName());

		System.out.println("EXTRACT "+this.zip.getFileName()+" ("+this.zip.getNumberOfSegments()+"fragments)");

		for (int i=1;i<=this.zip.getNumberOfSegments();i++)
		{
			switch(type)
			{
			case EXTRACT_EVENTS_BY_RAISER:			res.addAll(SimulationJournal.extractAllEvents(zip.getJournalSegment(i), raiser));			break;
			case EXTRACT_EVENTS_BY_CLASS:			res.addAll(SimulationJournal.extractAllEvents(zip.getJournalSegment(i), cl1));				break;
			case EXTRACT_EVENTS_BY_2CLASSES:		res.addAll(SimulationJournal.extractAllEvents(zip.getJournalSegment(i), cl1, cl2));			break;
			case EXTRACT_EVENTS_BY_RAISER_CLASS:	res.addAll(SimulationJournal.extractAllEvents(zip.getJournalSegment(i), raiser, cl1));		break;
			case EXTRACT_EVENTS_BY_RAISER_2CLASSES:	res.addAll(SimulationJournal.extractAllEvents(zip.getJournalSegment(i), raiser, cl1, cl2));	break;
			case EXTRACT_ALL_EVENTS:				res.addAll(SimulationJournal.extractAllEvents(zip.getJournalSegment(i)));					break;
			case EXTRACT_EVENTS_BY_RAISER_LIST:		res.addAll(SimulationJournal.extractAllEvents(zip.getJournalSegment(i),raisers));			break;
			case EXTRACT_EVENTS_BY_TIME_INTERVAL:	res.addAll(SimulationJournal.extractAllEvents(zip.getJournalSegment(i),raisers.get(0),raisers.get(1)));	break;	
			default: System.out.println("FATAL ERROR!!! Extraction by an unknown type"); System.exit(-1);
			}	
		}

		if(this.events!=null)
			switch(type)
			{
			case EXTRACT_EVENTS_BY_RAISER:			res.addAll(SimulationJournal.extractAllEvents(new LinkedList<Event>((ArrayList<Event>)this.events.clone()), raiser));				break;
			case EXTRACT_EVENTS_BY_CLASS:			res.addAll(SimulationJournal.extractAllEvents(new LinkedList<Event>((ArrayList<Event>)this.events.clone()), cl1));				break;
			case EXTRACT_EVENTS_BY_2CLASSES:		res.addAll(SimulationJournal.extractAllEvents(new LinkedList<Event>((ArrayList<Event>)this.events.clone()), cl1, cl2));			break;
			case EXTRACT_EVENTS_BY_RAISER_CLASS:	res.addAll(SimulationJournal.extractAllEvents(new LinkedList<Event>((ArrayList<Event>)this.events.clone()), raiser, cl1));		break;
			case EXTRACT_EVENTS_BY_RAISER_2CLASSES:	res.addAll(SimulationJournal.extractAllEvents(new LinkedList<Event>((ArrayList<Event>)this.events.clone()), raiser, cl1, cl2));	break;
			case EXTRACT_ALL_EVENTS:				res.addAll(SimulationJournal.extractAllEvents(new LinkedList<Event>((ArrayList<Event>)this.events.clone())));						break;	
			case EXTRACT_EVENTS_BY_RAISER_LIST:		res.addAll(SimulationJournal.extractAllEvents(new LinkedList<Event>((ArrayList<Event>)this.events.clone()),raisers));				break;
			case EXTRACT_EVENTS_BY_TIME_INTERVAL:	res.addAll(SimulationJournal.extractAllEvents(new LinkedList<Event>((ArrayList<Event>)this.events.clone()),raisers.get(0),raisers.get(1)));break;	
			default: System.out.println("FATAL ERROR!!! Extraction by an unknown type"); System.exit(-1);
			}	

		System.out.println("Le resultat d'extraction contient "+res.size()+"   Temps d'extraction:"+StaticPerf.elapsedToString());

		return res;
	}


	/**
	 * RESERVED ! specific optimized extraction
	 * @param events
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private static LinkedList<Event> extractAllEvents(LinkedList<Event> events,int startTime,int endTime)
	{
		long time;
		boolean ok=true;

		LinkedList<Event> list = new LinkedList<Event>();
		ListIterator<Event> iter = events.listIterator();
		Event evt=null;
		while(ok && iter.hasNext()) 
		{
			evt=iter.next();
			time=evt.getDateInMs();
			if (time>=startTime)
			{
				if(time<=endTime) list.add(evt); 
				ok=(time<=endTime);
			}

		}

		return list;
	}
	/**
	 * RESERVED ! specific optimized extraction
	 * @param events
	 * @param raisers
	 * @return
	 */
	private static LinkedList<Event> extractAllEvents(LinkedList<Event>events,ArrayList<Integer> raisers)
	{
		LinkedList<Event> list = new LinkedList<Event>();
		ListIterator<Event> iter = events.listIterator();
		Event evt;
		while(iter.hasNext()) 
		{
			evt=iter.next();
			if (raisers.contains(evt.getRaiser().getId())) list.add(evt);
		}

		return list;
	}

	/**
	 * RESERVED ! specific optimized extraction
	 * @param events
	 * @return
	 */
	private static LinkedList<Event> extractAllEvents(LinkedList<Event> events)
	{
		LinkedList<Event> list = new LinkedList<Event>();
		ListIterator<Event> iter = events.listIterator();
		Event evt;
		while(iter.hasNext()) 
		{
			evt=iter.next();
			list.add(evt);
		}

		return list;
	}

	/**
	 * RESERVED ! specific optimized extraction
	 * @param events
	 * @param raiser
	 * @return
	 */
	private static LinkedList<Event> extractAllEvents(LinkedList<Event> events,int raiser)
	{
		LinkedList<Event> list = new LinkedList<Event>();
		ListIterator<Event> iter = events.listIterator();
		Event evt;
		while(iter.hasNext()) 
		{
			evt=iter.next();
			if (evt.getRaiser().equivalent(raiser)) list.add(evt);
		}

		return list;
	}

	/**
	 * RESERVED ! specific optimized extraction
	 * @param events
	 * @param cl
	 * @return
	 */
	private static  LinkedList<Event> extractAllEvents(LinkedList<Event> events,Class cl)
	{
		LinkedList<Event> list = new LinkedList<Event>();
		ListIterator<Event> iter = events.listIterator();
		Event evt;
		while(iter.hasNext()) 
		{
			evt=iter.next();
			if (evt.getClass()==cl) list.add(evt);
		}
		return list;
	}

	/**
	 * RESERVED ! specific optimized extraction
	 * @param events
	 * @param raiser
	 * @param cl
	 * @return
	 */
	private static  LinkedList<Event> extractAllEvents(LinkedList<Event> events,int raiser,Class cl)
	{
		LinkedList<Event> list = new LinkedList<Event>();
		ListIterator<Event> iter = events.listIterator();
		Event evt;
		while(iter.hasNext()) 
		{
			evt=iter.next();
			if ((evt.getRaiser().equivalent(raiser)) && (evt.getClass()==cl)) list.add(evt);
		}

		return list;
	}

	/**
	 * RESERVED ! specific optimized extraction
	 * @param events
	 * @param raiser
	 * @param cl1
	 * @param cl2
	 * @return
	 */
	private static  LinkedList<Event> extractAllEvents(LinkedList<Event> events,int raiser,Class cl1,Class cl2)
	{
		LinkedList<Event> list = new LinkedList<Event>();
		ListIterator<Event> iter = events.listIterator();
		Event evt;
		while(iter.hasNext()) 
		{
			evt=iter.next();
			if ((evt.getRaiser().equivalent(raiser)) && ((evt.getClass()==cl1) || (evt.getClass()==cl2))) list.add(evt);
		}

		return list;
	}

	/**
	 * RESERVED ! specific optimized extraction
	 * @param events
	 * @param cl1
	 * @param cl2
	 * @return
	 */
	private static  LinkedList<Event> extractAllEvents(LinkedList<Event> events,Class cl1,Class cl2)
	{
		LinkedList<Event> list = new LinkedList<Event>();
		ListIterator<Event> iter = events.listIterator();
		Event evt;
		while(iter.hasNext()) 
		{
			evt=iter.next();
			if (((evt.getClass()==cl1) || (evt.getClass()==cl2))) list.add(evt);
		}

		return list;
	}

}
