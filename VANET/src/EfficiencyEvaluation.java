import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;

import simulation.utils.aDate;


    /**
     * To inspect WD framework efficienty
     * @author JPeG
     */
    public class EfficiencyEvaluation
    {
    	
    	/** number of call of the WD API  by java classes*/
    	public int numberOfCall = 0;
    	
    	public LinkedList<String> traceList;
    	
    	public aDate origine;
    	
    	public EfficiencyEvaluation()
    	{
    		this.traceList=new LinkedList<String>();
    		this.origine=new aDate();
    	}
    	
    	public void add(aDate date,long delta,String WDmsg)
    	{
    		this.traceList.add(date.differenceToHHMMSS(date)+"\t"+delta+"\t"+WDmsg);
    		if(this.traceList.size()%2000==0) this.toFile("D:\\journal.txt");
    	}
    	
    	public void toFile(String fileName)
    	{
    		PrintWriter pw =null; 
    		try {
				 pw = new PrintWriter(new File(fileName));
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Iterator<String> iter = this.traceList.iterator();
			while(iter.hasNext()) pw.print(iter.next()+"\n");
			pw.close();
			
    		
    		
    	}
    }
    
    
 