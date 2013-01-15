import java.io.File;
import java.util.LinkedList;

import simulation.multiagentSystem.Perf;
import simulation.utils.StaticPerf;
import simulation.utils.aDate;

/** class used to enable communication between the simulator and the HMI 
 * @author Jean-Paul Jamont (for the modification of the native code)
 */
public class WinDev {


	/** WD efficiency evaluation */
	//private static EfficiencyEvaluation efficiencyEvaluation;
	
	
	/** is the WD API busy? */
	private static boolean locked = false;


	/**
	 * Ajoute un nouveau répertoire dans le java.library.path.
	 * @param dir Le nouveau répertoire à ajouter.
	 */
	public static void addToJavaLibraryPath(File dir) {
		final String LIBRARY_PATH = "java.library.path";
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException(dir + " is not a directory.");
		}
		String javaLibraryPath = System.getProperty(LIBRARY_PATH);
		System.setProperty(LIBRARY_PATH, javaLibraryPath + File.pathSeparatorChar + dir.getAbsolutePath());
		
		//resetJavaLibraryPath();
	}


	
	
	/** lock to prevent concurrent acces (which are prohibed)*/
	private static synchronized void acquire()
	{
		//if(WinDev.efficiencyEvaluation==null) WinDev.efficiencyEvaluation  = new EfficiencyEvaluation();
		//if(WinDev.efficiencyEvaluation.numberOfCall++%1000==0) System.out.println("\n"+WinDev.efficiencyEvaluation.numberOfCall+" calls to the WD API");

		if (WinDev.locked) System.out.print("2nd ACQ");
		while(WinDev.locked);
		WinDev.locked=true;
	}

	/** release the access on the method */
	private static synchronized void release()
	{
		WinDev.locked=false;
	}
	//	private static synchronized void acquire(String s)
	//	{
	//		System.out.print("<ACQ "+s+">");
	//		while(WinDev.locked);
	//		WinDev.locked=true;
	//	}
	//	private static synchronized void release(String s)
	//	{
	//		System.out.print("<REL "+s+">\n");
	//		WinDev.locked=false;
	//	}

	/* Add by Jean-Paul Jamont */

	public static boolean getWDBoolean(String field)
	{
		return (getWDEntier(field)==1);
	}
	public static int getWDEntier(String field)
	{
		int i;
		acquire();
		//StaticPerf.init();
		APPELWD("Ecran,recupere,"+field);
		//WinDev.efficiencyEvaluation.add(new aDate(), StaticPerf.elapsed(), "Ecran,recupere,"+field);
		//StaticPerf.init();
		
		if(WDTouche().equals("*E*")) System.out.println("WD WARNING : "+field);
		i=WDEntier();
		release();
		return i;
	}
	public static String getWDChaine(String field)
	{
		String s;
		acquire();
		//StaticPerf.init();
		APPELWD("Ecran,recupere,"+field);
		//WinDev.efficiencyEvaluation.add(new aDate(), StaticPerf.elapsed(), "Ecran,recupere,"+field);
		if(WDTouche().equals("*E*")) System.out.println("WD WARNING : "+field);
		s=WDChaine();
		release();
		return s;
	}
	public static float getWDReel(String field)
	{
		float f;
		acquire();
		//StaticPerf.init();
		APPELWD("Ecran,recupere,"+field);
		//WinDev.efficiencyEvaluation.add(new aDate(), StaticPerf.elapsed(), "Ecran,recupere,"+field);
		if(WDTouche().equals("*E*")) System.out.println("WD WARNING : "+field);
		f=WDReel();
		release();
		return f;
	}
	public static long getWDLong(String field)
	{
		long l;
		acquire();
		//StaticPerf.init();
		APPELWD("Ecran,recupere,"+field);
		//WinDev.efficiencyEvaluation.add(new aDate(), StaticPerf.elapsed(), "Ecran,recupere,"+field);
		if(WDTouche().equals("*E*")) System.out.println("WD WARNING : "+field);
		l=WDLong();
		release();
		return l;
	}
	public static double getWDReelD(String field)
	{
		double d;
		acquire();
		//StaticPerf.init();
		APPELWD("Ecran,recupere,"+field);
		//WinDev.efficiencyEvaluation.add(new aDate(), StaticPerf.elapsed(), "Ecran,recupere,"+field);
		if(WDTouche().equals("*E*")) System.out.println("WD WARNING : "+field);
		d=WDReelD();
		release();
		return d;
	}
	public static String getWDTexteLong(String field)
	{
		String s;
		acquire();
		//StaticPerf.init();
		APPELWD("Ecran,recupere,"+field);
		//WinDev.efficiencyEvaluation.add(new aDate(), StaticPerf.elapsed(), "Ecran,recupere,"+field);
		if(WDTouche().equals("*E*")) System.out.println("WD WARNING : "+field);
		s=WDTexteLong();
		release();
		return s;
	}

	/* Consultation variables WinDev */
	public static native int WDEntier();
	public static native long WDLong();
	public static native double WDReelD();
	public static native float WDReel();
	public static native String WDChaine();
	public static native String WDNom();
	public static native int WDIndice();
	public static native String WDTouche();
	public static native String WDTexteLong();
	/* Commande WinDev */
	/** DANGEROUS!!! PREFER USE AppelWD */
	public  static native void APPELWD(String szCommande);

	public static synchronized void AppelWD(String szCommande)
	{
		acquire();
		//StaticPerf.init();
		APPELWD(szCommande);
		//WinDev.efficiencyEvaluation.add(new aDate(), StaticPerf.elapsed(), szCommande);
		if(WDTouche().equals("*E*")) System.out.println("WD WARNING : "+szCommande);
		release();
	}


	//	public static native int ();
	//	public static native String WDTouche();
	//	public static native String WDTexteLong();
	//	

	public static int AppelWD_WDIndice(String szCommande)
	{
		acquire();
		//StaticPerf.init();
		APPELWD(szCommande);
		//WinDev.efficiencyEvaluation.add(new aDate(), StaticPerf.elapsed(), szCommande);
		int i=WDIndice();
		release();
		return i;
	}  
	public static String AppelWD_WDTouche(String szCommande)
	{
		acquire();
		//StaticPerf.init();
		APPELWD(szCommande);
		//WinDev.efficiencyEvaluation.add(new aDate(), StaticPerf.elapsed(), szCommande);
		String s=WDTouche();
		release();
		return s;
	}  
	public static String AppelWD_WDTexteLong(String szCommande)
	{
		acquire();
		//StaticPerf.init();
		APPELWD(szCommande);
		//WinDev.efficiencyEvaluation.add(new aDate(), StaticPerf.elapsed(), szCommande);
		if(WDTouche().equals("*E*")) System.out.println("WD WARNING : "+szCommande);
		String s=WDTexteLong();
		release();
		return s;
	}   



	public static void AfficheTexteLong(String szChamp,String szTexte)
	{
		acquire();
		//StaticPerf.init();
		AfficheTexteLong(szChamp,szTexte,(short) szTexte.length());
		//WinDev.efficiencyEvaluation.add(new aDate(), StaticPerf.elapsed(), "AfficheTextLong("+szChamp+")");
		release();
	}




	public static String AppelWD_WDChaine(String szCommande)
	{
		acquire();
		//StaticPerf.init();
		APPELWD(szCommande);
		//WinDev.efficiencyEvaluation.add(new aDate(), StaticPerf.elapsed(), szCommande);
		if(WDTouche().equals("*E*")) System.out.println("WD WARNING : "+szCommande);

		String s=WDChaine();
		release();
		return s;
	}  
	public static String AppelWD_WDNom(String szCommande)
	{
		acquire();
		//StaticPerf.init();
		APPELWD(szCommande);
		//WinDev.efficiencyEvaluation.add(new aDate(), StaticPerf.elapsed(), szCommande);
		String s=WDNom();
		release();
		return s;
	}

	public static float AppelWD_WDReel(String szCommande)
	{
		acquire();
		//StaticPerf.init();
		APPELWD(szCommande);
		//WinDev.efficiencyEvaluation.add(new aDate(), StaticPerf.elapsed(), szCommande);
		if(WDTouche().equals("*E*")) System.out.println("WD WARNING : "+szCommande);

		float f=WDReel();
		release();
		return f;
	}
	public static double AppelWD_WDReelD(String szCommande)
	{
		acquire();
		//StaticPerf.init();
		APPELWD(szCommande);
		//WinDev.efficiencyEvaluation.add(new aDate(), StaticPerf.elapsed(), szCommande);
		double d=WDReelD();
		release();
		return d;
	}
	public static long AppelWD_WDLong(String szCommande)
	{
		acquire();
		//StaticPerf.init();
		APPELWD(szCommande);
		//WinDev.efficiencyEvaluation.add(new aDate(), StaticPerf.elapsed(), szCommande);
		long l=WDLong();
		release();
		return l;
	}
	public static int AppelWD_WDEntier(String szCommande)
	{
		acquire();
		//StaticPerf.init();
		APPELWD(szCommande);
		//WinDev.efficiencyEvaluation.add(new aDate(), StaticPerf.elapsed(), szCommande);
		if(WDTouche().equals("*E*")) System.out.println("WD WARNING : "+szCommande);

		int i=WDEntier();
		release();
		return i;
	}



	/* Terminaison WinDev */
	public static native void WDTermine();
	/* Affichage texte long */
	public static native void AfficheTexteLong(String szChamp,String szTexte,short nTaille);

	

    public static boolean Init(){
        //chargement librairie WinDev
	try
	{
		//addToJavaLibraryPath(new File("."));
		//addToJavaLibraryPath(new File(".\\all dlls"));

		System.out.println("We are in "+(new File(".")).getAbsolutePath());
			System.loadLibrary("WD150IJV");
		return true;
	} catch (UnsatisfiedLinkError e)
	{
		System.out.println("La librairie WD150IJV.DLL n'a pas été trouvée");
		e.printStackTrace();
		return false;
	}
    }
    static
    {
        Init();
    }
    
    
}