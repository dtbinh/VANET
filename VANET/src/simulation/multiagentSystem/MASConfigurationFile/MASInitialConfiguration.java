package simulation.multiagentSystem.MASConfigurationFile;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;

import javax.imageio.ImageIO;

import simulation.views.environment.BMPFileBasedEnvironmentView;
import simulation.utils.IntegerPosition;

public class MASInitialConfiguration {

	public static Font fontObject_identifier= new Font("Courier",Font.BOLD,10);
	public static Font fontObject_type= new Font("Courier",Font.BOLD,9);

	/** tag of the file */
	public static String tag = "MASH PROJECT FILE";
	/** file name of the MAS configuration */
	public String fileName;
	/** name of the project */
	public String projectName;
	/** base folder of the project */
	public String baseFolder;
	/** width of the environment */
	public int width;
	/** height of the environment */
	public int height;
	/** background picture */
	public String background;
	/** objects */
	private Vector<MASInitialConfigurationItem> objectList;


	public MASInitialConfiguration(String projectName,String folder,int envWidth,int envHeight,String backgroundPicture)
	{
		this.fileName=projectName+".prj";
		this.projectName=projectName;
		this.baseFolder=folder;
		this.width=envWidth;
		this.height=envHeight;
		this.background=backgroundPicture;
		this.objectList=new Vector<MASInitialConfigurationItem>();
	}

	public MASInitialConfiguration(String fileName) throws FileNotFoundException,IOException
	{
		this(new File(fileName));
		System.out.println("Ouverture de "+fileName);

	}
	public MASInitialConfiguration(File file) throws IOException,FileNotFoundException
	{
		this.fileName=file.getName();
		this.objectList=new Vector<MASInitialConfigurationItem>();
		System.out.println("Ouverture de "+file.getAbsolutePath()+"\\"+file.getName());
		BufferedReader in = new BufferedReader(new FileReader(file));
		String s = new String();
		int i=1;


		while((s = in.readLine()) != null)
		{
			switch(i)
			{
			case 1:		/* tag of the fil */				break;
			case 2:		this.projectName=s;					break;
			case 3:		this.baseFolder=s;					break;
			case 4:		this.width=Integer.parseInt(s);		break;
			case 5:		this.height=Integer.parseInt(s);	break;
			case 6:		this.background=s;					break;
			default:	this.objectList.add(new MASInitialConfigurationItem(s));
			}
			i++;
		}
		in.close();


	}

	public MASInitialConfigurationItem get(int i)
	{
		return this.objectList.get(i);
	}

	public int getNbOfObject()
	{
		return this.objectList.size();
	}

	public BufferedImage graphicalView(boolean displayBackground,boolean displayAgentInformations,IntegerPosition origine, int width, int height,double zoom)
	{
		//System.out.println("\nVUE DU PROJET DEMANDEE");

		BufferedImage res=null;

		if(displayBackground)
		{
			File backgroundFile = new File(this.background);

			if(backgroundFile.isFile())
			{
				BMPFileBasedEnvironmentView env;
				try 
				{
					env = new BMPFileBasedEnvironmentView(backgroundFile);
					res=env.graphicalView(origine, width, height, zoom);
				} 
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if(res==null) 
		{
			res=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
			Graphics2D gr=(Graphics2D)res.getGraphics();
			gr.setColor(Color.WHITE);
			gr.fillRect(0, 0, res.getWidth(), res.getHeight());
		}

		String txt="";

		Graphics2D gr=(Graphics2D)res.getGraphics();	
		Iterator<MASInitialConfigurationItem> iter = this.objectList.iterator();
		while(iter.hasNext()) 
		{
			MASInitialConfigurationItem item = iter.next();
			IntegerPosition p = item.coordinate.clone();
			if(p.inRectangleArea(origine, new IntegerPosition((int) (width/zoom+origine.x),(int) (height/zoom+origine.y))));
			{

				p.sub(origine);
				p.multi((float) zoom);

				// select the object color
				gr.setColor(this.getAgentColor(item.isAgent, item.objectId));

				// draw object symbole
				if(item.isAgent)
				{
					gr.drawLine(p.x-2, p.y, p.x+2, p.y);
					gr.drawLine(p.x, p.y-2, p.x, p.y+2);
					gr.drawOval(p.x-5, p.y-5, 10, 10);	
				}
				else
				{
					gr.drawLine(p.x-5, p.y, p.x+5, p.y);
					gr.drawLine(p.x, p.y-5, p.x, p.y+5);
				}

				if(displayAgentInformations)
				{
					// draw agent identifier
					if (item.isAgent)
						txt="ag #"+item.id;
					else
						txt="obj #"+item.id;
					gr.setFont(MASInitialConfiguration.fontObject_identifier);
					gr.drawString(txt,p.x-(txt.length()*3),p.y+14);

					// draw agent type
					txt=(item.objectClassName==null ? "" : item.objectClassName);
					gr.setFont(MASInitialConfiguration.fontObject_type);
					gr.drawString(txt,(int) (p.x-(txt.length()*2.5)),p.y+22);
				}
			}
		}

		return res;
	}

	private Color getAgentColor(boolean isAgent, int objectId) 
	{
		if (!isAgent) objectId=11-objectId;

		switch(1+objectId%10)
		{
		case 1: return Color.BLACK;
		case 2: return Color.BLUE;
		case 3: return Color.GREEN;
		case 4: return Color.RED;
		case 5: return Color.CYAN;
		case 6: return Color.MAGENTA;
		case 7: return Color.ORANGE;
		case 8: return Color.YELLOW;
		case 9: return Color.PINK;
		case 10:return Color.DARK_GRAY;
		default: return Color.BLACK;
		}
	}

	public synchronized void add(int x, int y, float energy,int range)
	{
		this.add(x,y,energy,range,true,"","",1);
	}
	public synchronized void add(int x, int y, float energy,int range,boolean isAgent,String solutionName,String objectType,int objectId)
	{
		this.objectList.add(new MASInitialConfigurationItem (1+this.objectList.size(),x,y,energy,range,isAgent,solutionName,objectType,objectId));
	}


	public Vector<MASInitialConfigurationItem> getObjectListCopy()
	{
		return (Vector<MASInitialConfigurationItem>) this.objectList.clone();
	}

	public boolean save()
	{
		return this.save(this.baseFolder+"\\"+this.fileName);
	}
	public boolean save(String fileName)
	{

		try{
			FileWriter fos = new FileWriter(fileName);
			PrintWriter p = new PrintWriter(fos,true); 


			p.println(MASInitialConfiguration.tag);	
			p.println(this.projectName);
			p.println(this.baseFolder);
			p.println(this.width);
			p.println(this.height);
			p.println(this.background);
			ListIterator<MASInitialConfigurationItem> iter =this.objectList.listIterator();
			while(iter.hasNext())
				p.println(iter.next().toSavedTextLine());
			fos.close();
			p.close();

		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public String toString()
	{
		String res = "*** MAS configuration file ***\nFile name:"+this.fileName+"\nProjet name:"+this.projectName+"\nBase folder:"+this.baseFolder+"\nDimension: "+width+" by "+height+"\nBackground:"+this.background;
		Iterator<MASInitialConfigurationItem> iter = this.objectList.iterator();
		while(iter.hasNext()) res+="\n"+iter.next().toString();
		return res;
	}
}
