package simulation.utils;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class Log 
{



	private static PrintWriter writer = null;
	private static boolean isHTML = false;
	private static long date_origine;

	public synchronized static void open(String fileName)
	{
		Log.open(fileName,false);
		
	}

	public synchronized static void open(String fileName,boolean isHTML)
	{
		if(Log.writer==null)
		{
			try 
			{
				Log.writer = new PrintWriter(new FileWriter(fileName),true);
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			Log.date_origine = aDate.getCurrentTimeInMS();
			Log.isHTML=isHTML;
		}
	}


	public static void println(Object obj)
	{
		if(!Log.isHTML)
			Log.print(obj.toString()+"\n");
		else
			Log.println(obj.toString(),Color.BLACK);
	}

	public static void print(Object obj)
	{
		if(Log.writer==null) return;
		
		if(!Log.isHTML)
			Log.writer.print(aDate.msToHHMMSSCCC(aDate.getCurrentTimeInMS()-Log.date_origine)+" : "+obj.toString());
		else
			Log.print(obj.toString(),Color.BLACK);
	}



	public static void println(Object obj,int indexColor)
	{
		Log.println(obj,Log.indexColorToColor(indexColor));
	}
	public static void print(Object obj,int indexColor)
	{
		Log.print(obj,Log.indexColorToColor(indexColor));
	}




	public static void println(Object obj,Color color)
	{
		if(Log.writer==null) return;

		if(Log.isHTML)
			Log.writer.println("<FONT face=\"Courier\" color=#"+Log.colorToHex(color)+"> <B>"+aDate.msToHHMMSSCCC(aDate.getCurrentTimeInMS()-Log.date_origine)+"</B> : "+obj.toString().replace("\n", "<BR>")+"<BR></FONT>");
		else
			Log.writer.println(obj.toString());

		//System.out.println(obj);
	}
	public static void print(Object obj,Color color)
	{
		if(Log.writer==null) return;

		if(Log.isHTML)
			Log.writer.println("<FONT face=\"Courier\" color=#"+Log.colorToHex(color)+"> <B>"+aDate.msToHHMMSSCCC(aDate.getCurrentTimeInMS()-Log.date_origine)+"</B> : "+obj.toString().replace("\n", "<BR>")+"</FONT>");
		else
			Log.writer.println(obj.toString());

		//System.out.print(aDate.msToHHMMSSCCC(aDate.getCurrentTimeInMS()-Log.date_origine)+" : "+obj);

	}


	private static String colorToHex(Color c)
	{
		return String.format("%02X%02X%02X",c.getRed(),c.getGreen(),c.getBlue());
	}


	public static Color indexColorToColor(int indexColor)
	{
		switch(indexColor%7)
		{
		case 0: return Color.BLACK;
		case 1: return Color.BLUE;
		case 2: return Color.GREEN;
		case 3: return Color.ORANGE;
		case 4: return Color.MAGENTA;
		case 6: return Color.PINK;
		default: return Color.DARK_GRAY;
		}
	}

	public synchronized static void close()
	{
		if(Log.writer!=null)
		{
			Log.writer.close();		
			Log.writer=null;
		}
	}


}
