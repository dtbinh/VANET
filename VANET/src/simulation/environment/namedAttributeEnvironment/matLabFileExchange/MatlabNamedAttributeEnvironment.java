package simulation.environment.namedAttributeEnvironment.matLabFileExchange;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import simulation.views.environment.EnvironmentViewInterface;
import simulation.environment.namedAttributeEnvironment.NamedAttribute;
import simulation.environment.namedAttributeEnvironment.NamedAttributeEnvironment;
import simulation.environment.namedAttributeEnvironment.NamedAttributeNotFoundException;
import simulation.environment.namedAttributeEnvironment.XMLDecoder;
import simulation.environment.namedAttributeEnvironment.XMLTagNotFound;
import simulation.utils.IntegerPosition;

/**
 * class to process a matlab named attribute environment (XML file exchange)
 * @author JPeG
 */
public class MatlabNamedAttributeEnvironment extends NamedAttributeEnvironment implements Runnable
{
	/** is the environment model suspended */
	private boolean isSuspended;
	/** is the environement model stoped  */
	private boolean isStoped;
	/** environment model input XML filename */
	private String environmentModelInputFileName;
	/** environment model output XML filename */
	private String environmentModelOutputFileName;


	/**
	 * parameterized constructor
	 * @param environmentModelInputFileName environment model input XML filename
	 * @param environmentModelOutputFileName environment model output XML filename
	 * @param environmentView the associated matlab environment view
	 */
	public MatlabNamedAttributeEnvironment(String environmentModelInputFileName,String environmentModelOutputFileName,EnvironmentViewInterface environmentView)
	{
		super(environmentView);
		this.environmentModelInputFileName=environmentModelInputFileName;
		this.environmentModelOutputFileName=environmentModelOutputFileName;
		this.isStoped=false;
		this.isSuspended=false;
		new Thread(this).start(); // launch the thread
	}

	/**
	 * update the inputs i.e. read the XML matlab file
	 * @return true if the XML has been successfully processed
	 */
	public boolean updateInputs()
	{
		File inputFile = new File(this.environmentModelInputFileName);
		if (!inputFile.exists()) return false;

		try {
			BufferedReader in = new BufferedReader(new FileReader(this.environmentModelInputFileName));
			String XMLContainer  = new String();
			String line = new String();

			while((line = in.readLine()) != null)
			{
				XMLContainer+=line+"\n";
			}
			in.close();

			try {
				XMLContainer=XMLDecoder.extractNext(XMLContainer, "root");

				if(XMLContainer!=null) 
				{
					int i=1;
					String data =new String();
					while(data!=null)
					{
						if((data=XMLDecoder.extractNext(XMLContainer, "data"+i))!=null)
						{
							System.out.println("\ndata"+i);
							String name="";
							try
							{
								name= XMLDecoder.extractNext(data, "name");
								String type=XMLDecoder.extractNext(data, "type");
								String value=XMLDecoder.extractNext(data, "value");
								String unit=XMLDecoder.extractNext(data, "unit");
								String location=XMLDecoder.extractNext(data, "location");
								String description=XMLDecoder.extractNext(data, "description");
								String direction=XMLDecoder.extractNext(data, "direction");
								boolean direction_in = direction.toUpperCase().equals("IN");
								// if (name.equals("Tai_01")) System.out.println("\nTai_01 va t il etre remplacé?  DIRECTION_IN="+direction_in+"/"+direction);

								if( !direction_in || (direction_in && !this.exists(name))) 
								{
									//	if (name.equals("Tai_01")) System.out.println("\nOUI!!!");

									this.setAttribute(name,new MatlabAttribute(name,type,value,unit,location,description,direction));
								}
								//  else if (name.equals("Tai_01")) System.out.println("\nNON!!!");

							}
							catch (XMLTagNotFound e) {
								if(e.getTag().equals("name")) throw e;
							}
						}
						i++;
					}
				}
			} catch (XMLTagNotFound e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("\n\n\n"+this.toString());
			// remove the input file
			inputFile.delete();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * updates the output values i.e. generate a matlab XML file
	 */
	public void updateOutputs()
	{
		int i=1;
		try{
			PrintWriter p = new PrintWriter(new FileWriter(this.environmentModelOutputFileName),true); 

			p.println("<SimulationData>");
			Iterator<NamedAttribute> iter = this.attributes.values().iterator();
			while(iter.hasNext())
				p.println( ((MatlabAttribute)iter.next()).toXML(i++) );

			p.println("</SimulationData>");
			p.close();

		} catch(IOException e) {}

	}

	/**
	 * method called when the thread starts 
	 */
	public void run() 
	{
		// Thread launched
		System.out.println("\nEnvironment manager process launched");

		while(!this.isStoped)
		{
			while (isSuspended && !isStoped) try{Thread.sleep(100);}catch(Exception e){}

			if (this.updateInputs()) 
			{
				System.out.println("Environment updated\n"+this.toString());
				this.updateOutputs();
			}

			// Little pause
			try{Thread.sleep(100);}catch(Exception e){}
		}
	}

	@Override
	public void pause() {
		this.isSuspended=true;
	}

	@Override
	public void resume() {
		this.isSuspended=false;		
	}

	@Override
	public void stop() {
		this.isStoped=true;
	}

	@Override
	public Object get(String name, IntegerPosition pos) throws NamedAttributeNotFoundException {
		// TODO Auto-generated method stub
		return ((MatlabAttribute) this.getAttribute(name)).getValue();
	}

	@Override
	public void set(String name, Object value, IntegerPosition pos) throws NamedAttributeNotFoundException {
		// TODO Auto-generated method stub
		((MatlabAttribute) this.getAttribute(name)).setValue(value);
	}

	@Override
	public Object get(String name) throws NamedAttributeNotFoundException {
		// TODO Auto-generated method stub
		return this.get(name,null);
	}

	@Override
	public void set(String name, Object value) throws NamedAttributeNotFoundException {
		this.set(name, value,null);
	}


	/**
	 * returns this object under a string representation
	 */
	public String toString()
	{
		String res ="Matlab simulated environment  [input="+this.environmentModelInputFileName+" output="+this.environmentModelOutputFileName+"]";
		res+="\n";
		Iterator<NamedAttribute> iter = this.attributes.values().iterator();
		while(iter.hasNext())
			res+=iter.next().toString()+"\n";
		return res;
	}
}
