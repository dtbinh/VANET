// Implémenter OPR = open project
// En deduire la suppression de TAB_ITEMS_START
// Poursuivre en suppimant TAB_ITEMS


//
//
// OPTIMISER scenarioEntityAddition
// Faire un test dans lequel on créer 300 capteurs : tps = 45s 
// LE probléme vient de this.updateLinkBetweenObjects();  (97% du temps consommé)
//
//



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import simulation.views.InexistantLevelGraphicalViewException;
import simulation.views.environment.BMPFileBasedEnvironmentView;
import simulation.views.environment.BlankEnvironmentView;
import simulation.views.environment.EnvironmentViewInterface;
import simulation.battery.*;
import simulation.battery.custom.*;

import simulation.journal.SimulationJournal;
import simulation.messages.Frame;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.messages.system.SystemFrame;
import simulation.multiagentSystem.MAS;
import simulation.multiagentSystem.ObjectAndItsNeighboorhood;
import simulation.multiagentSystem.ObjectListManager;
import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.multiagentSystem.Perf;
import simulation.multiagentSystem.SimulatedAgent;
import simulation.multiagentSystem.SimulatedObject;
import simulation.multiagentSystem.MASConfigurationFile.MASInitialConfiguration;
import simulation.multiagentSystem.MASConfigurationFile.MASInitialConfigurationItem;
import simulation.embeddedObject.EmbeddedObjectsManager;
import simulation.embeddedObject.serialCommunication.FrameReceivedInterface;
import simulation.embeddedObject.serialCommunication.RS232;
import simulation.entities.Agent;
import simulation.entities.Object;
import simulation.environment.namedAttributeEnvironment.NamedAttributeEnvironment;
import simulation.environment.namedAttributeEnvironment.matLabFileExchange.MatlabNamedAttributeEnvironment;
import simulation.environment.BlankEnvironment;
import simulation.environment.Environment;
import simulation.events.*;
import simulation.events.system.ColorModificationEvent;
import simulation.events.system.EnergyModificationEvent;
import simulation.events.system.MessageNotTransmittedEvent;
import simulation.events.system.NeighboorhoodModificationEvent;
import simulation.events.system.PositionModificationEvent;
import simulation.events.system.RangeModificationEvent;
import simulation.events.system.ReceivedBytesByEmbeddedObjectEvent;
import simulation.events.system.ReceivedFrameEvent;
import simulation.events.system.ReceivedMessageEvent;
import simulation.events.system.RoleModificationEvent;
import simulation.events.system.SendedBytesByEmbeddedObjectEvent;
import simulation.events.system.SendedFrameEvent;
import simulation.events.system.SendedMessageEvent;
import simulation.statistics.AbstractDataBlock;
import simulation.statistics.StatisticManager;
import simulation.utils.*;
import simulation.scenario.FinishedScenarioException;
import simulation.scenario.InterfaceEntityAddition;
import simulation.scenario.MethodNotFoundException;
import simulation.scenario.ScenarioBadTimeFormatException;
import simulation.scenario.ScenarioManager;
import simulation.scenario.ScenarioNotUnderstandedInstructionException;
import simulation.scenario.ScenarioNotUnderstandedLineException;
import simulation.scenario.VariableNotFoundException;
import simulation.solutions.ClassNotFoundInSolution;
import simulation.solutions.Solution;
import simulation.solutions.SolutionManager;
import simulation.solutions.SolutionNotFoundException;
import simulation.solutions.custom.RecMAS.MWAC.MWACAgent;
import simulation.solutions.custom.RecMAS.RecursiveAgent.RecursiveAgent;
import simulation.utils.Dimension;

// ATTENTION! Pas d'APPELWD en dehors de wMain et pas que dans la "boucle" (pas de timer)

/**
 * the main class
 * @author Jean-Paul Jamont
 */
public class WDMainWindow implements EventMapNotificationInterface,InterfaceEntityAddition
{


	/** Default temp folder */
	public static String DEFAULT_TEMP_FOLDER = "D:\\";


	/** default temp view number */
	public static int 	 DEFAULT_TEMP_VIEW_NUMBER = 1;
	/** default temp view file name */
	public static String DEFAULT_TEMP_VIEW_FILE_NAME = "TEMP";
	/** default temp view file extension */
	public static String DEFAULT_TEMP_VIEW_FILE_EXTENSION = "BMP";
	/** default temp view file name */
	public static String DEFAULT_JOURNAL_FILE_NAME = "TEMP.JRN";

	/** default active project folder */
	public static String DEFAULT_TEMP_OPENED_PROJECT = DEFAULT_TEMP_FOLDER+"ACTIVE_PROJECT\\";

	/** Default initial amount of energy */
	public static double DEFAULT_INIT_AMOUNT_ENERGY = 1000;
	/** Identifier to the mas statistics */
	private final static int MAS_STAT_ID = 0;

	// These constants allow to inform the HMI of a representation modification

	/** pause duration after each iteration of the main function */
	private  final static int PAUSE_TIME_SLOT = 20;
	/** duration between two update of a spy window */
	private  final static int DELAY_BETWEEN_TWO_SPY_WINDOW_UPDATE = 500;
	/** duration of a pause during the suspended mode */
	private  final static int PAUSE_DURING_LOOP = 5;

	/** to define a journal view of all events */
	private  final static int journalViewALL_EVENTS = 1;
	/** to define a journal view of a specific raiser */
	private  final static int journalViewRAISERS = 2;
	/** to define a journal view of event in an specified interval */
	private  final static int journalViewTIME_INTERVAL = 3;

	/** time between to update of the MAS representation in the HMI */
	private static int DRAWING_UPDATE_FREQUENCY_IN_MS = 1000;

	/** the timer which trig the spy window update */
	private  Timer timer_taskSpyWindowUpdate = null;  
	/** the action which allow to update the spy window */
	private  ActionListener taskSpyWindowUpdate = new TaskSpyWindowUpdate();
	/** reference to the simulated multiagent system */
	private  MAS mas = null;
	/** old states of the spyied objects */
	private  HashMap<Integer,String> oldStatesOfSpyiedObjects = new HashMap<Integer,String>();
	/** is the spy windows must be updated */
	private  boolean mustUpdateSpyWindows = false;

	/** reference to the statistic manager */
	private StatisticManager statisticManager=null;
	/** solution to the statistic manager */
	private SolutionManager solutionManager=null;
	/** scenario to the statistic manager */
	private ScenarioManager scenarioManager=null;

	/** embedded agent manager */
	private EmbeddedObjectsManager embeddedObjectsManager=null;
	/** Embedded agent bytes exchanged list (used to memorize exchanges which must be signaled to the HMI)*/
	private EmbeddedAgentExchangeConcurrentTraceList embeddedAgentExchangeConcurrentTraceList=null;

	/** archived journal */
	private SimulationJournal openedArchivedJournal = null;
	/** archived statistic manager */
	private StatisticManager archivedStatisticManager=null;

	/** Nom du WDL */
	private String WDLFileName = "MASH.WDL";

	/** Opened project */
	private MASInitialConfiguration openedProject=null;

	/** IHM view */
	private IHM_MASGraphicalRepresentation view;



	/** go */
	public void go() 
	{

		//		SystemFrame frame1=new SystemFrame((byte)0x00,(byte)0x00);
		//		SystemFrame frame2=new SystemFrame((byte)0x01,(byte)0x02);
		//		SystemFrame frame3=new SystemFrame((byte)0xFF,(byte)0xFF);
		//		
		//		System.out.println("Frame1 : "+frame1+"      "+BytesArray.displayByteArray(frame1.toBytes()));
		//		System.out.println("Frame1 : "+frame2+"      "+BytesArray.displayByteArray(frame2.toBytes()));
		//		System.out.println("Frame1 : "+frame3+"      "+BytesArray.displayByteArray(frame3.toBytes()));
		//		
		String WDTouche="---";
		boolean bSaisie = true;
		if (WinDev.Init())
		{
			/* Load the library */

			System.out.println("MASH Simulator v"+MASHSimulator.VERSION+"             (c) Jean-Paul Jamont et Michel Occello");
			System.out.println("---------------------------------------------------------------------");
			WinDev.APPELWD("BIBLI,Disque,"+this.WDLFileName+" /CR");
			File f= new File("");
			System.out.println(f.getAbsolutePath());
			if (WinDev.WDEntier() == 0)
			{	
				/* Splash screen */
				System.out.println("Splach screen...");
				WinDev.APPELWD("Ouvre,Splach.wdw");
				WinDev.AfficheTexteLong("splach.LIB_version","version "+MASHSimulator.VERSION);
				// Attends un clic sur la fenetre
				while (!WDTouche.equals("ESC")) 
				{
					WinDev.APPELWD("Ecran,Saisie");	
					WDTouche=WinDev.WDTouche();
				}
				WinDev.APPELWD("Ferme");
				WDTouche="";

				System.out.println("Fin splach screen...");

				// Main window
				WinDev.APPELWD("ouvre,wMain.wdw");

				System.out.println("Ouverture de wMain...");

				/* add the solutions in the statistic manager */
				statisticManager = new StatisticManager();
				statisticManager.addUserSolutions(WinDev.getWDTexteLong("wMain.S_CRITERES_DE_MESURES"));
				archivedStatisticManager = new StatisticManager();
				archivedStatisticManager.addUserSolutions(WinDev.getWDTexteLong("wMain.S_CRITERES_DE_MESURES"));


				/* add the solutions in the solution manager */
				solutionManager = new SolutionManager();
				solutionManager.addSolutions(WinDev.getWDTexteLong("wMain.S_SOLUTIONS_EXISTANTES"));

				/* the embedded agent bytes exchanged list*/
				this.embeddedAgentExchangeConcurrentTraceList=new EmbeddedAgentExchangeConcurrentTraceList();

				/* update the solution list in the graphic interface */	
				//WinDev.AppelWD("wMain._java_temp_chaine1=\""+solutionManager.toString()+"\"");		
				WinDev.AfficheTexteLong("wMain._java_temp_chaine1", solutionManager.toString());
				WinDev.AppelWD("wMain._java_UpdateSolutionList()");
				String[] solution = solutionManager.toString().split("\n");
				try{for(int i=0;i<solution.length;i++) System.out.println("*"+solution[i].split("\t")[5]+"*");}catch(Exception e){}

				/* creation of the serial/parallel ports list */
				System.out.println("Port enumeration:"+WDMainWindow.stringArrayToString(RS232.getPortList()));
				WinDev.AppelWD("wMain._java_temp_chaine1=\""+WDMainWindow.stringArrayToString(RS232.getPortList())+"\"");
				WinDev.AppelWD("wMain._java_UpdatePortList()");

				//this.concurrentEmbeddedAgentDispatcherList=new ConcurrentEmbeddedAgentDispatcherList();

				// Update les listes combo
				this.updateArchivedJournalCriteria();

				WDMainWindow.DEFAULT_TEMP_FOLDER = WinDev.getWDChaine("wMain.Sai_temp_file");


				//WinDev.APPELWD("Ferme");

				aDate updateEnvironmentAttributesView = null;
				/* Go go go... */
				while (bSaisie)
				{
					try{Thread.sleep(PAUSE_TIME_SLOT);}catch(Exception e){}

					// on effectue la saisie du menu 
					//WinDev.AppelWD(")
					WinDev.AppelWD("Ecran,Saisie/NONBLOQUANT");
					//WDTouche=WinDev.getWDChaine("wMain._java_temp_WDTouche");
					//WinDev.AppelWD("wMain._java_temp_WDTouche=\"\"");
					//System.out.println("<"+WDTouche+">");

					/* if a WD error is detected */
					WDTouche=WinDev.WDTouche();
					if (!WDTouche.equals("*N*"))
						System.out.print("\n\""+WDTouche+"\"");
					else
						System.out.print(".");

					/* Analyze of old performances */
					if (WDTouche.equals("AAL")) 
					{
						String journalFileName = WinDev.getWDChaine("wMain._java_temp_chaine1");
						System.out.println("Archive:"+journalFileName);
						this.openedArchivedJournal = new SimulationJournal(journalFileName,true);
						this.archivedStatisticManager.setJournal(this.openedArchivedJournal);
						WinDev.AppelWD("lib_stat_traitement_en_cours..Visible=Faux");
					}

					/* Start of simulation*/    
					if (WDTouche.equals("SoS")) 
					{
						this.startSimulation();
						WinDev.AppelWD("wMain.MASViewableArea:modifiedView=true");
					}

					if (WDTouche.equals("IOP")) WinDev.AfficheTexteLong("wMain.SAI_GABARIT",this.objectPositionToScenario());

					/* Suspend the simulation*/    
					if (WDTouche.equals("StS")) this.suspendSimulation();

					/* End of simulation*/    
					if (WDTouche.equals("EoP")) 
					{
						this.stopSimulation();
						this.openedProject=null;
						this.view=null;
						WinDev.AppelWD("wMain.initEnvironment()");
					}

					/* End of simulation*/    
					if (WDTouche.equals("EoS")) this.stopSimulation();

					/* Save project*/    
					if (WDTouche.equals("SSP")) this.openedProject.save();

					/* Save project as*/    
					if (WDTouche.equals("SSA")) this.openedProject.save(WinDev.getWDChaine("wMain._java_temp_chaine1"));


					/* End of simulation*/    
					if (WDTouche.equals("TSF")) 
					{
						ObjectSystemIdentifier sys_id = new ObjectSystemIdentifier(WinDev.AppelWD_WDEntier("wMain._java_temp_int1"));
						String message = WinDev.AppelWD_WDChaine("wMain._java_temp_chaine1");

						String[] strbytes=message.split(" ");
						byte[] bytes=new byte[strbytes.length];

						int temp;
						for(int i=0;i<strbytes.length;i++) 
						{
							temp=Integer.valueOf(strbytes[i],16);
							bytes[i]= Byte.parseByte(""+(temp>127 ? temp-256 : temp));
						}

						mas.sendBytes(sys_id, bytes);
					}

					/* Add of a new object (the simulation is running) */    
					if (WDTouche.equals("COE")) 
					{
						//System.out.println("Clic sur l'environement");
						int x=WinDev.AppelWD_WDEntier("wMain._java_temp_int1");
						int y=WinDev.AppelWD_WDEntier("wMain._java_temp_int2");
						int objectId = WinDev.AppelWD_WDEntier("wMain._java_temp_int3");
						String solutionName = WinDev.AppelWD_WDChaine("wMain._java_temp_chaine1");
						boolean isAgent = WinDev.AppelWD_WDChaine("wMain._java_temp_chaine2").equals("AGENT");
						String objectType = WinDev.AppelWD_WDChaine("wMain._java_temp_chaine3");

						//System.out.println("Solution name="+solutionName+"\nIt is an "+(isAgent ? "agent" : "object")+"#"+objectId+" :"+objectType);


						if(this.mas==null && this.openedProject==null) 
							WinDev.AppelWD("Erreur,No opened project");
						else
						{
							if(this.mas==null)
								this.openedProject.add(x,y,1,Object.DEFAULT_INITIAL_RANGE,isAgent,solutionName,objectType,objectId);
							else
							{
								SimulatedObject sObj = this.mas.getSimulatedObject(new IntegerPosition(x,y));
								if(sObj==null)
									this.processObjectAdditionDuringSimulation(isAgent,solutionName,objectType,objectId,x,y);
								else
								{
									WinDev.AppelWD("wMain._java_spy_object("+sObj.getObject().getSystemId()+","+sObj.getObject().getUserId()+")");
									this.verifySpyWindows();
								}
							}
							this.setViewableAreaRepresentation();
						}
					}
					/* Update the MAS graphical representation */    
					if (WDTouche.equals("DRM")) if(this.view!=null) 
					{
						//System.out.println("\nDRM");
						this.setViewableAreaRepresentation();
					}

					/* Need to update a statistic (multiagent system) */
					if (WDTouche.equals("UST") || WDTouche.equals("STA")) this.updateStatistics(WinDev.getWDChaine("_java_temp_chaine1"),WinDev.getWDChaine("wMain._java_temp_chaine2"),statisticManager);

					/* Need to update a statistic (for a specified agent) */
					if (WDTouche.equals("UAS")) this.updateStatistics(WinDev.getWDChaine("_java_temp_chaine1"),WinDev.getWDChaine("wMain._java_temp_chaine2"),statisticManager,WinDev.getWDEntier("wMain._java_temp_int1"));

					/* Need to update a statistic (for a specified agent) */
					if (WDTouche.equals("NPR")) this.newProject();

					/* Need to update a statistic (for a specified agent) */
					if (WDTouche.equals("OPR")) this.openProject(WinDev.getWDChaine("_java_temp_chaine1"));

					/* Need to update an archived statistic  (multiagent system) */
					if (WDTouche.equals("ASA")) 
					{
						this.updateStatistics(WinDev.getWDChaine("_java_temp_chaine1"),WinDev.getWDChaine("wMain._java_temp_chaine2"),this.archivedStatisticManager);
						WinDev.AppelWD("lib_stat_traitement_en_cours..Visible=Faux");
					}

					/* Need to update a statistic (for a specified agent) */
					if (WDTouche.equals("ASB")) 
					{
						this.updateStatistics(WinDev.getWDChaine("_java_temp_chaine1"),WinDev.getWDChaine("wMain._java_temp_chaine2"),this.archivedStatisticManager,WinDev.getWDEntier("wMain.SAI_agent_id"));
						WinDev.AppelWD("lib_stat_traitement_en_cours..Visible=Faux");
					}

					/* Journal extraction */
					if  (WDTouche.equals("JR1") || WDTouche.equals("JR2") || WDTouche.equals("JR3")) 
					{
						String journalFileName = WDMainWindow.DEFAULT_TEMP_FOLDER+WDMainWindow.DEFAULT_JOURNAL_FILE_NAME;
						this.updateJournalView(WDTouche.charAt(2)-'0',new File(journalFileName));
						WinDev.AppelWD("wMain._java_update_journal_view(\""+journalFileName+"\")");
					}

					/* Journal extraction */
					if  (WDTouche.equals("VEN") || (updateEnvironmentAttributesView!= null && updateEnvironmentAttributesView.differenceToMS(new aDate())>500) )
					{
						if(mas!=null)
						{
							String environmentStringRepresentation=this.mas.getEnvironmentStringRepresentation();
							if(!WinDev.getWDTexteLong("wMain.SAI_EnvironmentView").equals(environmentStringRepresentation))
								WinDev.AfficheTexteLong("wMain.SAI_EnvironmentView",environmentStringRepresentation);
							if (WinDev.getWDBoolean("wMain.isInEnvironementAttributeView"))
								updateEnvironmentAttributesView=new aDate(); 
							else
								updateEnvironmentAttributesView=null;
						}
					}

					/* Archived journal extraction */
					if  (WDTouche.equals("AR1") || WDTouche.equals("AR2") || WDTouche.equals("AR3")) 
					{
						String journalFileName = WDMainWindow.DEFAULT_TEMP_FOLDER+WDMainWindow.DEFAULT_JOURNAL_FILE_NAME;
						this.updateArchivedJournalView(WDTouche.charAt(2)-'0',new File(journalFileName));
						WinDev.AppelWD("wMain._java_update_archived_journal_view(\""+journalFileName+"\")");
						WinDev.AppelWD("lib_stat_traitement_en_cours..Visible=Faux");
					}

					/* Need to spy an agent/object */
					if (WDTouche.equals("SPY") || WDTouche.equals("Spy") || !WinDev.getWDChaine("wMain.SAI_aliasWindowEvent").isEmpty()) 
					{
						System.out.println("SPY?");
						WinDev.AppelWD("wMain.SAI_aliasWindowEvent=\"\"");
						this.verifySpyWindows();
					}


					/* An agent must send a message */
					if (WDTouche.equals("SAM"))  this.executeCallOfMethods(WinDev.getWDEntier("wMain._java_temp_int1"),WinDev.getWDChaine("wMain._java_temp_chaine1"));

					/* An agent must send a message */
					if (WDTouche.equals("TST"))  
					{

						LinkedList<LinkedList<IntegerPosition>> lst = new LinkedList<LinkedList<IntegerPosition>>();

						System.out.println("Densité/ecart type");

						boolean exit=false;
						int level=0;
						while(!exit && level<10)
						{
							lst.add(new LinkedList<IntegerPosition>());

							exit=true;
							if(this.mas.nbItem()>1)
								for(int i=1;i<=this.mas.nbItem();i++)
								{
									RecursiveAgent ag=(RecursiveAgent)this.mas.getSimulatedObject(new ObjectSystemIdentifier(i)).getObject();
									if(ag!=null)
									{
										RecursiveAgent recAg=ag.getRecurssiveAgent(level);
										if(recAg!=null)
										{

											int duree = -1;
											try
											{
												duree=recAg.getAbstractionConstructionDuration();
											}
											catch(NullPointerException e)
											{
											}

											MWACAgent appAg=recAg.getApplicativeAgent();

											if(ag.getUserId()==recAg.getUserId()) lst.get(level).add(new IntegerPosition(appAg.getNeighboorlist().size(),duree));
											exit=false;
										}
									}
								}
							else
								exit=true;
							level++;
						}

						String res="";
						for(int i=0;i<lst.size();i++)
						{
							res+="LEVEL "+i;
							res+="\n**************";
							res+="\n"+lst.get(i).size()+"\tagents";
							res+="\nNb voisins\t\t\t\tTemps creation abstraction(ms)";
							for(int j=0;j<lst.get(i).size();j++)
								res+="\n"+lst.get(i).get(j).x+"\t\t\t\t"+(lst.get(i).get(j).y==-1 ? " " : lst.get(i).get(j).y);

							res+="\n\n\n";
						}

						try {
							PrintWriter print=new PrintWriter(new FileWriter("D:\\_recRES"+lst.get(0).size()+".txt",false));
							print.println(res);
							print.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					/* Snapshoots*/    
					if (WDTouche.equals("SNA")) 
					{
						if (this.mas!=null) this.snapshoot();
					}

					/* Exit required*/    
					if (WDTouche.equals("ESC")) 
					{
						if (this.mas!=null) this.mas.stopSimulation();
						bSaisie = false;
					}


					/* Update the IHM view */    
					if (WinDev.getWDBoolean("wMain.MASViewableArea:modifiedArea")||WinDev.getWDBoolean("wMain.MASViewableArea:modifiedView")) 
					{
						//System.out.println("\nwMain.MASViewableArea:modifiedArea="+WinDev.getWDBoolean("wMain.MASViewableArea:modifiedArea")+"  wMain.MASViewableArea:modifiedView="+WinDev.getWDBoolean("wMain.MASViewableArea:modifiedView"));
						this.updateViewableAreaInformation();
						this.setViewableAreaRepresentation();
						WinDev.AppelWD("wMain.MASViewableArea:modifiedArea=false");
						WinDev.AppelWD("wMain.MASViewableArea:modifiedView=false");

					}

					if((this.view!=null)&&(this.view.updatedMASGraphicalRepresentation()||this.view.mustUpdateAgentsViewModel())) 
					{
						this._setViewableAreaRepresentation(this.view.mustUpdateAgentsViewModel());
						this.view.agentsViewModelUpdated();
					}


					/* update trace of bytes exchanged simulator<=>agents */
					//String previousExchange = WinDev.getWDChaine("wMain.SAI_terminal");
					while(!this.embeddedAgentExchangeConcurrentTraceList.isEmpty())
					{
						EmbeddedAgentExchangeTrace trace=this.embeddedAgentExchangeConcurrentTraceList.pop();
						System.out.println("Tableajouteligne(\"TABLE_JOURNAL_EMB\",\""+aDate.msToHHMMSSCCC(trace.date)+"\",\""+trace.id+"\",\""+(trace.simulatorToEmbeddedAgent ? "S->A":"A->S" )+"\",\""+RS232.debugByteArray(trace.msg)+"\",\""+trace.signification+"\")");
						System.out.flush();
						//previousExchange=RS232.debugByteArray(trace.msg)+previousExchange;
						WinDev.AfficheTexteLong("_java_temp_chaine3",aDate.msToHHMMSSCCC(trace.date)+"\t"+trace.id+"\t"+(trace.simulatorToEmbeddedAgent ? "S->A":"A->S" )+"\t"+RS232.debugByteArray(trace.msg)+"\t"+trace.signification);
						WinDev.AppelWD("wMain._java_update_embedded_trace()");
						//WinDev.AppelWD("Table,ajoute,TABLE_JOURNAL_EMB,"+aDate.msToHHMMSSCCC(trace.date)+"\t"+trace.id+"\t"+(trace.simulatorToEmbeddedAgent ? "S->A":"A->S" )+"\t"+RS232.debugByteArray(trace.msg)+"\t"+"[role:ROLE_REPRESENTATIVE,group:30]");
						//WinDev.AppelWD("Table,ajoute,TABLE_JOURNAL_EMB,"+aDate.msToHHMMSSCCC(trace.date)+"\t"+trace.id+"\t"+(trace.simulatorToEmbeddedAgent ? "S->A":"A->S" )+"\t"+RS232.debugByteArray(trace.msg)+"\t"+trace.signification);
						//WinDev.AppelWD("TableModifieColonne(\"TAB_EMB_signification\","+1+",\"COUOCU\")");
						//WinDev.AppelWD("Table,modifie,TABLE_JOURNAL_EMB.TAB_EMB_signification,1=\"coucou\"");
						//WinDev.AppelWD("Tableajouteligne(\"TABLE_JOURNAL_EMB\",\""+aDate.msToHHMMSSCCC(trace.date)+"\",\""+trace.id+"\",\""+(trace.simulatorToEmbeddedAgent ? "S->A":"A->S" )+"\",\""+RS232.debugByteArray(trace.msg)+"\",\""+trace.signification+"\")");
						//System.out.println("PASSED");
						//System.out.flush();
					}
					//WinDev.AfficheTexteLong("wMain.SAI_terminal",previousExchange);


					/* compute a scenario step*/
					if (this.scenarioManager!=null) this.updateScenario();

					/* An update of the representation data is required */
					if (mas!=null && mas.startedSimulation() && mas.eventListAge()>WDMainWindow.DRAWING_UPDATE_FREQUENCY_IN_MS) 
					{
						mas.processEventList();
						System.gc();
					}

					/* An update of the agents spy windows is required */
					if (mustUpdateSpyWindows) this.updateSpyWindows();

					try{Thread.sleep(PAUSE_DURING_LOOP);}catch(Exception e){}


				}   
			}
			else
			{
				/* Bibliothèque non trouvée */
				System.out.println("Erreur : le Fichier \""+WDLFileName+"\" introuvable");
			}


			System.out.println("End of program\r\n------------------------------------------------------------------");
			if(this.timer_taskSpyWindowUpdate!=null) this.timer_taskSpyWindowUpdate.stop();
			/* Terminer... */
			WinDev.WDTermine();
		}
		else
		{
			System.out.println("Erreur : initialisation de l'IHM impossible");
		}
	}



	private String objectPositionToScenario() {
		// TODO Auto-generated method stub

		String variables="\t// Variables declaration\n";
		String code = "\t// Initialization of agents\n";

		int agentID = 1;
		int objectID =1;
		for(int i=0;i<this.openedProject.getNbOfObject();i++)
		{
			MASInitialConfigurationItem item=this.openedProject.get(i);

			variables += ("\t"+item.objectClassName+(item.isAgent ? " agent_"+agentID : " object_"+objectID)+";\n");
			code+=("\t"+(item.isAgent ? " agent_"+(agentID++) : " object_"+(objectID++))+"= new "+item.objectClassName+"("+item.coordinate.x+","+item.coordinate.y+","+item.energy+","+item.range+");\n");
		}
		return ("variables\n"+variables+"\n\nbegin\n"+code+"\nend\n").replace("\n", "\r\n");
	}



	private void newProject() {
		String params = WinDev.getWDChaine("wMain._java_temp_chaine1");
		System.out.println("Paramétres du nouveau projet:"+params);
		String[] tParams = params.split("\t");
		try
		{

			String projectName = tParams[0];
			String folder = tParams[1];
			int envWidth = Integer.parseInt(tParams[2]);
			int envHeight = Integer.parseInt(tParams[3]);
			String backgroundPicture ="";
			if(tParams.length>4) backgroundPicture=tParams[4];
			this.openedProject=new MASInitialConfiguration(projectName,folder,envWidth,envHeight,backgroundPicture);
			System.out.println(this.openedProject);
			this.updateWDProjectInformations(projectName,folder,envWidth,envHeight, true, false, false);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			System.out.println("Paramétres du nouveau projet:"+params);
		}
	}

	private void _updateWDProjectInformations_name(String name)
	{
		WinDev.AppelWD("wMain.project:name=\""+ name+"\"");
	}
	private void _updateWDProjectInformations_baseFolder(String baseFolder)
	{
		WinDev.AppelWD("wMain.project:baseFolder=\""+ baseFolder+"\"");
	}
	private void _updateWDProjectInformations_openProject(boolean openProject)
	{
		WinDev.AppelWD("wMain.project:openProject="+ (openProject ? "True" : " False"));
	}
	private void _updateWDProjectInformations_simulation(boolean inSimulation)
	{
		WinDev.AppelWD("wMain.project:simulation="+( inSimulation ? "True" : " False"));
	}
	private void _updateWDProjectInformations_modifiedProject(boolean modifiedProject)
	{
		WinDev.AppelWD("wMain.project:modifiedProject="+(modifiedProject ? "True" : " False"));
	}
	private void _updateWDProjectInformations_dimensions(Dimension d)
	{
		this._updateWDProjectInformations_dimensions(d.x,d.y);
	}
	private void _updateWDProjectInformations_dimensions(int x,int y)
	{
		WinDev.AppelWD("wMain.project:x="+x);
		WinDev.AppelWD("wMain.project:y="+y);
		WinDev.AppelWD("wMain.updateEnvironmentElevator()");
	}

	private void updateWDProjectInformations(String name, String baseFolder,int environmentWidth, int environmentHeight,boolean openProject,boolean inSimulation,boolean modifiedProject )
	{
		this._updateWDProjectInformations_baseFolder(baseFolder);
		this._updateWDProjectInformations_dimensions(environmentWidth, environmentHeight);
		this._updateWDProjectInformations_modifiedProject(modifiedProject);
		this._updateWDProjectInformations_name(name);
		this._updateWDProjectInformations_openProject(openProject);
		this._updateWDProjectInformations_simulation(inSimulation);
	}



	private void openProject(String fileName) {

		if(!fileName.equals(""))
		{
			System.out.println("Ouverture du projet "+fileName);
			this.openedProject=null;

			try {
				this.openedProject=new MASInitialConfiguration(fileName);

				this.updateWDProjectInformations(this.openedProject.fileName, this.openedProject.baseFolder, this.openedProject.width, this.openedProject.height, true, false, false);
				//System.out.println(this.openedProject.toString());
				System.out.println("Opened project: "+fileName);
				this.updateViewableAreaInformation(true);
				this.setViewableAreaRepresentation();

			} catch (Exception e) {
				WinDev.AppelWD("Erreur,Impossible to open the specified project");
				//e.printStackTrace();
			}
		}

	}
	private void updateViewableAreaInformation() 
	{
		this.updateViewableAreaInformation(false);
	}
	private void updateViewableAreaInformation(boolean modifiedView) 
	{
		IntegerPosition p = new IntegerPosition(WinDev.getWDEntier("wMain.MASViewableArea:offset:x"),WinDev.getWDEntier("wMain.MASViewableArea:offset:y"));
		double scale =  WinDev.getWDReel("wMain.MASViewableArea:scale");
		Dimension viewArea = new Dimension(WinDev.getWDEntier("wMain.MASViewableArea:dim:x"),WinDev.getWDEntier("wMain.MASViewableArea:dim:y"));
		int layer = WinDev.getWDEntier("wMain.MASViewableArea:layer");
		if (this.view==null)
			this.view=new IHM_MASGraphicalRepresentation(p,viewArea,layer,scale);
		else
			this.view.setParameters(p,viewArea,layer,scale);

		if(modifiedView  || WinDev.getWDBoolean("wMain.MASViewableArea:modifiedView"))
		{
			//System.out.println("\nUPDATE LAYER:::::::");
			WinDev.AppelWD("wMain.actuInterfaceGraphiqueRepresentationSMA()");
			this.view.setParameters(WinDev.getWDChaine("wMain._java_temp_chaine1"),WinDev.getWDChaine("wMain._java_temp_chaine2"),WinDev.getWDEntier("wMain._java_temp_int1"));
			WinDev.AppelWD("wMain.MASViewableArea:modifiedView=False");
		}
	}


	/** verifies which are the spyied objects */
	public void verifySpyWindows()
	{

		int listOccurrence=WinDev.AppelWD_WDEntier("Liste,occurrence,wMain.LISTE_spyView");
		HashSet<Integer> set= new HashSet<Integer>();
		for (int i=1;i<=listOccurrence;i++) set.add(new Integer(WinDev.AppelWD_WDChaine("Liste,recupere,wMain.LISTE_spyView,"+i)));

		// Update the spyied attribute of spyied objects
		if (mas!=null) this.mas.updateSpyiedObjectIdentifiersSet(set);
	}

	/** updates the journal view */
	public void updateJournalView(int type)
	{
		this.updateJournalView(type,null);
	}


	/** updates the journal view */
	// si tempFile est a null alors on fait les appels WinDev
	public void updateJournalView(int type,File tempFile)
	{
		FileWriter fos = null;
		PrintWriter p = null;

		boolean useTempFile = (tempFile!=null);
		if(useTempFile) 
		{
			try
			{
				fos = new FileWriter(tempFile);
				p = new PrintWriter(fos,true); 
			} 
			catch(IOException e) 
			{
				e.printStackTrace();
				return;
			}

		}

		LinkedList<Event> lst=null;
		boolean viewOnlysSendedFrame = (WinDev.getWDEntier("wMain._java_temp_int3")==1);
		switch(type)
		{
		/** view of all events */
		case WDMainWindow.journalViewALL_EVENTS:	
			lst=this.mas.getJournal().extractAllEvents();
			break;
			/** view of a specific raisers subset */
		case WDMainWindow.journalViewRAISERS: 
			String[] tab=WinDev.getWDChaine("wMain._java_temp_chaine1").split(" ");
			ArrayList<Integer> tabInt=new ArrayList<Integer>(tab.length);
			for(int i=0;i<tab.length;i++)	
			{
				System.out.println("Traitement de '"+tab[i]+"'");
				tabInt.add(Integer.parseInt(tab[i]));
			}
			lst=this.mas.getJournal().extractAllEvents(tabInt,true);
			break;
			/** View of event occured in a specific laps of time */
		case WDMainWindow.journalViewTIME_INTERVAL: // Vue des evenements d'un intervalle de temps
			int debut=WinDev.getWDEntier("wMain._java_temp_int1");
			int fin=WinDev.getWDEntier("wMain._java_temp_int2");
			System.out.println("Traitement de "+debut+" à "+fin);
			lst=this.mas.getJournal().extractAllEvents(debut,fin);
			break;
		}


		StaticPerf.init();

		if(lst!=null)
		{
			WinDev.AppelWD("table,supprime,TABLE_JOURNAL,*");
			System.out.println("IL Y A "+lst.size()+" EVENEMENTS!!!!!!!!!!!!!!!");
			String param;
			Event evt;
			ListIterator<Event> iter=lst.listIterator();
			while(iter.hasNext()) 
			{
				evt=iter.next();



				if((!viewOnlysSendedFrame)||(viewOnlysSendedFrame && evt instanceof SendedFrameEvent)) 
					if (useTempFile)
						p.println(aDate.msToHHMMSSCCC(evt.getDateInMs())+"\t"+evt.getRaiser()+"\t"+evt.toString());
					else
					{
						param = aDate.msToHHMMSSCCC(evt.getDateInMs())+"\",\""+evt.getRaiser()+"\",\""+evt.toString();
						if (param.length()>200) param=param.substring(0, 200);
						WinDev.AppelWD("Tableajouteligne(\"TABLE_JOURNAL\",\""+param+"\")");
					}
				iter.remove();
			}

			System.out.println("Elapsed time : "+StaticPerf.elapsedToString());
		}
		else
		{
			System.out.println("ERREUR DANS LE RESULTAT DE LA RECHERCHE DU JOURNAL");
		}

		if(useTempFile)
		{
			p.close();
			try 
			{
				fos.close();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	/** updates the journal view */
	public void updateArchivedJournalView(int type)
	{
		this.updateArchivedJournalView(type,null);
	}

	/** updates the journal view */
	public void updateArchivedJournalView(int type,File tempFile)
	{
		FileWriter fos = null;
		PrintWriter p = null;

		boolean useTempFile = (tempFile!=null);
		if(useTempFile) 
		{
			try
			{
				fos = new FileWriter(tempFile);
				p = new PrintWriter(fos,true); 
			} 
			catch(IOException e) 
			{
				e.printStackTrace();
				return;
			}

		}

		if(this.openedArchivedJournal==null)
		{
			System.out.println("\nATTENTION: Aucun journal archivé ouvert");
		}
		else
		{
			LinkedList<Event> lst=null;
			switch(type)
			{
			/** view of all events */
			case WDMainWindow.journalViewALL_EVENTS:	
				lst=this.openedArchivedJournal.extractAllEvents();
				break;
				/** view of a specific raisers subset */
			case WDMainWindow.journalViewRAISERS: 
				String[] tab=WinDev.getWDChaine("wMain._java_temp_chaine1").split(" ");
				ArrayList<Integer> tabInt=new ArrayList<Integer>(tab.length);
				for(int i=0;i<tab.length;i++)	
				{
					System.out.println("Traitement de '"+tab[i]+"'");
					tabInt.add(Integer.parseInt(tab[i]));
				}
				lst=this.openedArchivedJournal.extractAllEvents(tabInt,true);
				break;
				/** View of event occured in a specific laps of time */
			case WDMainWindow.journalViewTIME_INTERVAL: // Vue des evenements d'un intervalle de temps
				int debut=WinDev.getWDEntier("wMain._java_temp_int1");
				int fin=WinDev.getWDEntier("wMain._java_temp_int2");
				System.out.println("Traitement de "+debut+" à "+fin);
				lst=this.openedArchivedJournal.extractAllEvents(debut,fin);
				break;
			}


			if(lst!=null)
			{
				Perf perf = new Perf();
				perf.init();
				int i=0;
				float pourcent=0;
				int lastPourcent=0;
				int size = lst.size();
				WinDev.AppelWD("table,supprime,TABLE_ARCHIVED_JOURNAL,*");
				System.out.println("IL Y A "+lst.size()+" EVENEMENTS!!!!!!!!!!!!!!!");
				Event evt;
				ListIterator<Event> iter=lst.listIterator();

				String sto= new String();
				String param;
				while(iter.hasNext()) 
				{
					i++;
					evt=iter.next();
					if (useTempFile)
						p.println(aDate.msToHHMMSSCCC(evt.getDateInMs())+"\t"+evt.getRaiser()+"\t"+evt.toString());
					else
					{
						param = aDate.msToHHMMSSCCC(evt.getDateInMs())+"\",\""+evt.getRaiser()+"\",\""+evt.toString();
						if (param.length()>200) param=param.substring(0, 200);
						WinDev.AppelWD("Tableajouteligne(\"TABLE_ARCHIVED_JOURNAL\",\""+param+"\")");
					}
					//sto+=(aDate.msToHHMMSSCCC(evt.getDateInMs())+"\t"+evt.getRaiser()+"\t"+evt.toString()+"\n");

					if (evt.getDateInMs()<1000) System.out.println(evt.getDateInMs()+" "+aDate.msToHHMMSSCCC(evt.getDateInMs()));

					iter.remove();
					pourcent=((100*i)/size);
					if(pourcent>=(5.0+lastPourcent))
					{
						System.out.print(pourcent+"% ");
						lastPourcent+=5;
					}
				}

				System.out.println("Fin traitement: "+perf.elapsedToString());
			}
			else
			{
				//				System.out.println("ERREUR DANS LE RESULTAT DE LA RECHERCHE DU JOURNAL");
			}

			if(useTempFile)
			{
				p.close();
				try 
				{
					fos.close();
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/** starts the simulation */
	public void startSimulation()
	{
		if (!(this.mas==null || !this.mas.startedSimulation()))
		{
			// Une simulation est déjà en cours
			System.out.println("Simulation déjà en cours");
		}
		else
		{
			System.out.println("Start the simulation...");
			this._updateWDProjectInformations_simulation(true);

			/* create an embedded objects manager (the reference to the mas will be set after) */
			this.embeddedObjectsManager= new EmbeddedObjectsManager(); 


			/* get parameters of simulation*/
			String scenario=WinDev.getWDTexteLong("wMain._java_temp_chaine1");
			String solution=WinDev.getWDChaine("wMain._java_temp_chaine2");
			String scenarioName=WinDev.getWDChaine("wMain._java_temp_chaine3");

			/* get the embedded object list*/
			WinDev.AppelWD("TableOccurrence(\"wMain.TABLE_EMB_Agent\")");
			int size=WinDev.WDEntier();

			System.out.println("\nThere is "+size+" linked real world agent");

			for(int i=1;i<=size;i++)
			{
				int id=WinDev.AppelWD_WDEntier("Table,Recupere,wMain.TAB_EMB_Id,"+i);
				String type=WinDev.AppelWD_WDChaine("Table,Recupere,wMain.TAB_EMB_Type,"+i);
				int protocol=WinDev.AppelWD_WDEntier("Table,Recupere,wMain.TAB_EMB_Protocole,"+i);
				String config=WinDev.AppelWD_WDChaine("Table,Recupere,wMain.TAB_EMB_conf,"+i);
				//Mis en commentaire au passage au system identifier
				this.embeddedObjectsManager.add(new ObjectSystemIdentifier(id),type,protocol,config);
			}
			this.embeddedObjectsManager.openConnections();

			if(scenario.isEmpty() || solution.isEmpty())
			{
				startSimulation_manual();
				WinDev.AppelWD("Message(\"Simulation manuelle\")");
			}
			else
			{
				WinDev.AppelWD("Message(\"Simulation avec scénario\")");
				String res="";
				try
				{
					startSimulation_scenario(scenarioName,scenario,solution);
				}
				catch(SolutionNotFoundException e){res=e.toString();}
				catch(ScenarioNotUnderstandedLineException e) {res=e.toString();}
				catch (ScenarioBadTimeFormatException e) {res=e.toString();}
				if (!res.isEmpty())
				{
					WinDev.AppelWD("wMain._java_stop_simulation()");
					WinDev.AppelWD("Erreur,"+res);
				}
			}
			this._setViewableAreaRepresentation(true);
		}
	}


	/* starts the simulation using a scenario 
	 * called by  startSimulation()
	 */
	public void startSimulation_scenario(String scenarioName,String scenario,String solution) throws SolutionNotFoundException, ScenarioBadTimeFormatException, ScenarioNotUnderstandedLineException
	{

		String[] tSolution = solution.split("\t");

		if (tSolution.length>2) throw new SolutionNotFoundException();
		updateContextualStatisticMenu();
		Solution solutionItem=solutionManager.getSolution(tSolution[0],tSolution[1]);

		MAS.NOTIFY_FRAME_RECEIVED_EVENT=WinDev.getWDBoolean("wMain.INT_NotifyReceivedFrameEvent,1");


		/* create the MAS */
		Environment environment=null;

		EnvironmentViewInterface environmentViewInterface = null;
		System.out.println("\nENVIRONNEMENT");
		if(new File(this.openedProject.background).isFile())
		{
			System.out.println("\nL'image "+this.openedProject.background+" existe");
			try 
			{
				environmentViewInterface=new BMPFileBasedEnvironmentView(this.openedProject.background);
			} 
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		else
		{
			System.out.println("\nL'image "+this.openedProject.background+" n'existe pas");
			environmentViewInterface=new BlankEnvironmentView(this.openedProject.width,this.openedProject.height);
		}

		if(WinDev.getWDBoolean("wMain.INT_MatlabEnvironment"))
			environment=new MatlabNamedAttributeEnvironment(WinDev.getWDChaine("wMain.Sai_env_input_file"),WinDev.getWDChaine("wMain.Sai_env_output_file"),environmentViewInterface);
		else
			environment=new BlankEnvironment(this.openedProject.width,this.openedProject.height,environmentViewInterface);
		System.out.println(environment.toString());
		this.mas=new MAS(this,environment,this.embeddedObjectsManager,this.statisticManager,WinDev.getWDChaine("wMain.project.performancesFolder"),solutionItem.getBaseFrame());

		/* set the link to the MAS in the embedded object manager */
		this.embeddedObjectsManager.setMAS(this.mas);
		/* set the link to the MAS in the scenario manager */
		this.scenarioManager= new ScenarioManager(this,mas,scenario,solutionItem);



		this.setViewableAreaRepresentation();


		System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
		this.mas.startSimulation();

		// On démarre la tache de maj des fenetres d'espionnage (si nécéssaire)
		if(this.timer_taskSpyWindowUpdate==null)
		{
			this.timer_taskSpyWindowUpdate = new Timer(DELAY_BETWEEN_TWO_SPY_WINDOW_UPDATE,taskSpyWindowUpdate);
			this.timer_taskSpyWindowUpdate.start();
		}
		else
			this.timer_taskSpyWindowUpdate.restart();


		// On met à jour la fenetre de gestion du scenario
		WinDev.AppelWD("wMain._java_scenario_ok()");

		this.setViewableAreaRepresentation();

		WinDev.AppelWD("wMain._java_update_scenario_view(\""+scenarioName+"\",0,0,0,0)");
	}

	/** updates the scenario player view */
	private void updateScenario()
	{
		long time=mas.elapsedSimulationTime();
		try 
		{
			this.scenarioManager.exec(time);
		} 
		catch (FinishedScenarioException e) 
		{
			//System.out.println("Plus d'instruction pour le scénario!!!!!");
			//e.printStackTrace();
		} 
		catch (ScenarioNotUnderstandedInstructionException e) 
		{
			WinDev.AppelWD("wMain._java_update_scenario_view(\"\",0,0,0,0,\""+e.toString()+"\")");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			WinDev.AppelWD("wMain._java_update_scenario_view(\"\",0,0,0,0,\""+e.toString()+"\")");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			WinDev.AppelWD("wMain._java_update_scenario_view(\"\",0,0,0,0,\""+e.toString()+"\")");
			e.printStackTrace();
		} catch (InstantiationException e) {
			WinDev.AppelWD("wMain._java_update_scenario_view(\"\",0,0,0,0,\""+e.toString()+"\")");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			WinDev.AppelWD("wMain._java_update_scenario_view(\"\",0,0,0,0,\""+e.toString()+"\")");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			WinDev.AppelWD("wMain._java_update_scenario_view(\"\",0,0,0,0,\""+e.toString()+"\")");
			e.printStackTrace();
		} catch (VariableNotFoundException e) {
			WinDev.AppelWD("wMain._java_update_scenario_view(\"\",0,0,0,0,\""+e.toString()+"\")");
			e.printStackTrace();
		} catch (MethodNotFoundException e) {
			WinDev.AppelWD("wMain._java_update_scenario_view(\"\",0,0,0,0,\""+e.toString()+"\")");
			e.printStackTrace();
		} catch (ClassNotFoundInSolution e) {
			// TODO Auto-generated catch block
			WinDev.AppelWD("wMain._java_update_scenario_view(\"\",0,0,0,0,\""+e.toString()+"\")");
			e.printStackTrace();
		}
		int[] interval = scenarioManager.getLastLineNumberIntervalProcessed();
		WinDev.AppelWD("wMain._java_update_scenario_view(\"\","+time+","+scenarioManager.getProgression(time)+","+interval[0]+","+interval[1]+")");

	}

	/* enable the user to call methods on a object/agent */
	void executeCallOfMethods(int int_system_identifier,String cmd)
	{
		mas.executeCallOfMethods(mas.int_to_ObjectSystemIdentifier(int_system_identifier),cmd);
	}

	/* starts the simulation (manual mode : no scenario)
	 * called by  startSimulation()
	 */
	public void startSimulation_manual()
	{
		System.out.println("Start of the manual simulation");

		if (this.mas==null || !this.mas.startedSimulation())
		{
			int nbItems;
			int id,x,y,range;
			float energy;
			String solutionName;
			String version;
			String agentClassName;
			int agentClassIdentifier;
			boolean isAgent;

			Solution solution = null;

			// Quel solution faut-il tester?
			WinDev.AppelWD("wMain._java_get_manual_selected_solution()");
			try 
			{
				solution=this.solutionManager.getSolution(WinDev.getWDChaine("wMain._java_temp_chaine1"), WinDev.getWDChaine("wMain._java_temp_chaine2"));
			} 
			catch (SolutionNotFoundException e) {
				WinDev.AppelWD("wMain._java_stop_simulation()");
				e.printStackTrace();
			}

			if(solution!=null)
			{
				updateContextualStatisticMenu();

				MAS.NOTIFY_FRAME_RECEIVED_EVENT=WinDev.getWDBoolean("wMain.INT_NotifyReceivedFrameEvent,1");

				/* create the MAS */
				Environment environment=null;
				EnvironmentViewInterface environmentViewInterface = null;
				if(new File(this.openedProject.background).isFile())
				{
					try 
					{
						environmentViewInterface=new BMPFileBasedEnvironmentView(this.openedProject.background);
					} 
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
				else
					environmentViewInterface=new BlankEnvironmentView(this.openedProject.width,this.openedProject.height);

				if(WinDev.getWDBoolean("wMain.INT_MatlabEnvironment"))
					environment=new MatlabNamedAttributeEnvironment(WinDev.getWDChaine("wMain.Sai_env_input_file"),WinDev.getWDChaine("wMain.Sai_env_output_file"),environmentViewInterface);
				else
					environment=new BlankEnvironment(this.openedProject.width,this.openedProject.height,environmentViewInterface);
				System.out.println(environment.toString());
				this.mas=new MAS(this,environment,this.embeddedObjectsManager,this.statisticManager,WinDev.getWDChaine("wMain.project.performancesFolder"),solution.getBaseFrame());

				/* set the link to the MAS in the embedded object manager */
				this.embeddedObjectsManager.setMAS(this.mas);
				nbItems = this.openedProject.getNbOfObject();


				//System.out.println("Start");
				this.mas.startSimulation();
				System.out.println("Start of simulation");




				System.out.println("\nAdd "+nbItems+" new items");
				boolean finded;
				for(int i=0;i<nbItems;i++)
				{
					MASInitialConfigurationItem item = this.openedProject.get(i);
					System.out.println(item);
					id=item.id;
					x=item.coordinate.x;
					y=item.coordinate.y;
					energy=item.energy;
					range=item.range;
					solutionName=item.solutionName;
					version=item.version;
					agentClassName=item.objectClassName;
					agentClassIdentifier=item.objectId;
					isAgent=item.isAgent;

					finded = false;
					// Search the class of agent to create
					Class theClass = null;
					try {
						theClass=(isAgent ? solution.getTheAgentClass(agentClassName) : solution.getTheObjectClass(agentClassName));
						finded=true;
					} catch (ClassNotFoundInSolution e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (!finded)
					{
						try {
							theClass=(isAgent ? solution.getTheAgentClass(item.objectId) : solution.getTheObjectClass(item.objectId));
							finded=true;
							WinDev.AppelWD("Trace(\"Class "+agentClassName+" replaced by "+theClass.getSimpleName()+"\")");
						} catch (ClassNotFoundInSolution e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(!finded)
					{
						try {
							theClass=(isAgent ? solution.getTheAgentClass(1) : solution.getTheObjectClass(1));
							finded=true;
						} catch (ClassNotFoundInSolution e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							WinDev.AppelWD("Trace,Class "+agentClassName+" replaced by "+theClass.getSimpleName());
						}
					}
					if(finded)
					{
						java.lang.Object[] param = new java.lang.Object[4];
						Agent ag;
						Constructor[] tabConstructors = theClass.getConstructors();
						int index = -1;
						finded=false;
						while(!finded && index<tabConstructors.length)
						{
							index++;
							Class[] cl = tabConstructors[index].getParameterTypes();
							finded=(cl.length==4) && (cl[0]==MAS.class) && (cl[1]==Integer.class) && (cl[2]==Float.class) && (cl[3]==Integer.class);
						}

						ag = null;

						param[0]=this.mas;
						param[1]=new Integer(id);
						param[2]=new Float(energy);
						param[3]=new Integer(range);

						try 
						{
							ag=(Agent)tabConstructors[index].newInstance(param);
						}
						catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 				
						if (!WinDev.getWDBoolean("wMain.INT_InfiniteBattery,1")) ag.setBattery(new LinearBatteryBasicModel(this.mas,this.mas.int_to_ObjectSystemIdentifier(id),WDMainWindow.DEFAULT_INIT_AMOUNT_ENERGY,WDMainWindow.DEFAULT_INIT_AMOUNT_ENERGY));
						this.mas.addNewItem(new SimulatedAgent(ag,x,y));

						//this.mas.addNewItem(new SimulatedAgent(new MWACAgent(this.mas,id,(float)energy,range),x,y));
					}
					//					System.out.println("Update link between objects");
					//					this.updateLinkBetweenObjects();
					System.out.println("Draw");
					this.setViewableAreaRepresentation();




					// On démarre la tache de maj des fenetres d'espionnage (si nécéssaire)
					if(this.timer_taskSpyWindowUpdate==null)
					{
						this.timer_taskSpyWindowUpdate = new Timer(DELAY_BETWEEN_TWO_SPY_WINDOW_UPDATE,taskSpyWindowUpdate);
						this.timer_taskSpyWindowUpdate.start();
					}
					else
						this.timer_taskSpyWindowUpdate.restart();

				}

			}

			else
			{
				WinDev.AppelWD("Erreur,Choosen solution not found");

			}
		}

	}


	private void setViewableAreaRepresentation()
	{
		this.view.requireGraphicalUpdate();
	}

	private void _setViewableAreaRepresentation() 
	{
		this._setViewableAreaRepresentation(false);
	}
	private synchronized void _setViewableAreaRepresentation(boolean mustUpdateAgentsViewModel) 
	{
		Perf perf=new Perf();
		perf.init();

		String fileName = WDMainWindow.DEFAULT_TEMP_FOLDER+"\\"+WDMainWindow.DEFAULT_TEMP_VIEW_FILE_NAME+(WDMainWindow.DEFAULT_TEMP_VIEW_NUMBER++)+"."+WDMainWindow.DEFAULT_TEMP_VIEW_FILE_EXTENSION;
		BufferedImage img = null;

		if(mustUpdateAgentsViewModel && this.mas!=null) this.mas.updateAgentsViewModel(this.view.getTypeOfAgentViews(),this.view.getViewLength());
		if(mas!=null)
		{
			try 
			{
				img = this.mas.getMASView(this.view);
			} 
			catch (InexistantLevelGraphicalViewException e) 
			{
				// TODO Auto-generated catch block
				img=e.getImage();
			}
		}
		else if(this.openedProject!=null)
			img = this.openedProject.graphicalView(this.view.isVisibleEnvironmentLayerView(),this.view.isVisibleTextLayerView(),this.view.getOffset(), this.view.getViewAreaDimension().x, this.view.getViewAreaDimension().y, this.view.getScale());
		else 
			return;

		long interm = perf.elapsed();

		try 
		{
			ImageIO.write(img, "BMP", new File(fileName));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}

		WinDev.AppelWD("wMain._java_update_MAS_view(\""+fileName+"\")");

		//System.out.println("\nVUE DU SMA DEMANDEE   dt="+perf.elapsed()+"ms => "+(1000.0/perf.elapsed())+" img/s   (dt_img="+interm+"ms => "+(1000.0/interm)+"img/s)");
		this.view.set_TIME_BETWEEN_TWO_GRAPHICAL_VIEW(2*perf.elapsed());
	}


	public  void snapshoot() 
	{
		String baseFileName="\\Snapshoots\\snap_at_"+DateFormat.getInstance().format(new Date()).replace('\\', '_').replace('/', '_').replace(':','h')+"_level";
		this.snapshoot(this.openedProject.baseFolder+baseFileName);
	}
	public  void snapshoot(String baseFileName) 
	{
		this.snapshoot(baseFileName,"BMP");
	}
	public synchronized void snapshoot(String baseFileName,String format) 
	{

		int level=0;
		boolean exit=false;
		BufferedImage img = null;




		while(!exit)
		{
			if(mas!=null)
			{

				try 
				{
					img = this.mas.getMASView(this.view,level++);
				} 
				catch (InexistantLevelGraphicalViewException e) 
				{
					// TODO Auto-generated catch block
					img=e.getImage();
					exit=true;
				}
			}
			else if(this.openedProject!=null)
			{
				img = this.openedProject.graphicalView(this.view.isVisibleEnvironmentLayerView(),this.view.isVisibleTextLayerView(),this.view.getOffset(), this.view.getViewAreaDimension().x, this.view.getViewAreaDimension().y, this.view.getScale());
				exit=true;
			}
			else 
				return;


			try 
			{
				System.out.println("Snapshoot level "+level+" => file '"+baseFileName+level+"."+format+"'");
				ImageIO.write(img, format, new File(baseFileName+level+"."+format));
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	/** suspends the simulation */
	public void suspendSimulation()
	{
		System.out.println("Suspend!!!");
		if (this.mas.startedSimulation())
			if (!this.mas.suspendedSimulation())
			{
				System.out.println("Suspend the simulation");
				this.mas.suspendSimulation();
			}
			else
			{
				System.out.println("Resume the simulation");
				this.mas.resumeSimulation();
			}
	}

	/** stops the simulation */
	public void stopSimulation()
	{
		if ((this.mas!=null) && (this.mas.startedSimulation()) )
		{
			if(this.timer_taskSpyWindowUpdate!=null)
			{
				this.timer_taskSpyWindowUpdate.stop();
				this.timer_taskSpyWindowUpdate=null;
			}

			//this.view=null;
			this.mas.stopSimulation();	
			this.mas=null;
			this.scenarioManager=null;
			this.embeddedObjectsManager.closeConnections();
			this.embeddedObjectsManager=null;
			this._updateWDProjectInformations_simulation(false);
			System.out.println("End of simulation");
			System.gc();
		}
	}

	/** add a new object during the simulation */
	public void processObjectAdditionDuringSimulation(boolean isAgent,String solutionName,String agentClassName,int classId,int x,int y)
	{
		System.out.println("\nprocessObjectAdditionDuringSimulation");


		if (this.mas.startedSimulation()) 
		{


			Solution solution = null;

			// Quel solution faut-il tester?
			WinDev.AppelWD("wMain._java_get_manual_selected_solution()");
			try 
			{
				solution=this.solutionManager.getSolution(WinDev.getWDChaine("wMain._java_temp_chaine1"), WinDev.getWDChaine("wMain._java_temp_chaine2"));
			} 
			catch (SolutionNotFoundException e) {
				e.printStackTrace();
			}

			if(solution!=null)
			{
				int id=1+this.mas.nbObjects();
				double energy=Object.DEFAULT_INITIAL_ENERGY_VALUE;
				int range=Object.DEFAULT_INITIAL_RANGE;;

				//System.out.println("On va créer l'agent "+numItem+" en ("+x+","+y+") de range="+range);

				boolean finded = false;
				// Search the class of agent to create
				Class theClass = null;
				try {
					theClass=(isAgent ? solution.getTheAgentClass(agentClassName) : solution.getTheObjectClass(agentClassName));
					finded=true;
				} catch (ClassNotFoundInSolution e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (!finded)
				{
					try {
						theClass=(isAgent ? solution.getTheAgentClass(classId) : solution.getTheObjectClass(classId));
						finded=true;
						WinDev.AppelWD("Trace(\"Class "+agentClassName+" replaced by "+theClass.getSimpleName()+"\")");
					} catch (ClassNotFoundInSolution e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if(!finded)
				{
					try {
						theClass=(isAgent ? solution.getTheAgentClass(1) : solution.getTheObjectClass(1));
						finded=true;
					} catch (ClassNotFoundInSolution e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						WinDev.AppelWD("Trace,Class "+agentClassName+" replaced by "+theClass.getSimpleName());
					}
				}
				// Search the class of agent to create

				java.lang.Object[] param = new java.lang.Object[4];
				Agent ag;
				Constructor[] tabConstructors = theClass.getConstructors();
				int index = -1;
				finded = false;
				while(!finded && index<tabConstructors.length)
				{
					index++;
					Class[] cl = tabConstructors[index].getParameterTypes();
					finded=(cl.length==4) && (cl[0]==MAS.class) && (cl[1]==Integer.class) && (cl[2]==Float.class) && (cl[3]==Integer.class);
				}

				if (finded)
				{
					ag = null;

					param[0]=this.mas;
					param[1]=new Integer(id);
					param[2]=new Float(energy);
					param[3]=new Integer(range);

					try 
					{
						ag=(Agent)tabConstructors[index].newInstance(param);
					}
					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 				
					if (!WinDev.getWDBoolean("wMain.INT_InfiniteBattery,1")) ag.setBattery(new LinearBatteryBasicModel(this.mas,this.mas.int_to_ObjectSystemIdentifier(id),WDMainWindow.DEFAULT_INIT_AMOUNT_ENERGY,WDMainWindow.DEFAULT_INIT_AMOUNT_ENERGY));
					this.mas.addNewItem(new SimulatedAgent(ag,x,y));

					//this.mas.addNewItem(new SimulatedAgent(new MWACAgent(this.mas,id,(float)energy,range),x,y));

					//this.updateLinkBetweenObjects();

					this.setViewableAreaRepresentation();
				}
				else
					WinDev.AppelWD("Erreur,Construtor not found");
			}
			else
				WinDev.AppelWD("Erreur,Solution not found");

		}
	}


	/** adds an entity (agent or object) in the simulation
	 * @param id identifier of the entity
	 * @param x absisse of the entity
	 * @param y ordinate of the entity
	 * @param ag the entity (an Object or an Agent)
	 */
	public void scenarioEntityAddition(int uid,int x,int y,Object ag)
	{
		int sid=ag.getSystemId().getId();
		//		String add1=uid+"\t"+x+"\t"+y+"\t"+1.00 /*energy ag.getEnergy()*/ +"\t"+ag.getRange()+"\t"+255;
		//		WinDev.AppelWD("Table,ajoute,wMain.TAB_ITEMS_START,"+add1);
		//
		//		if(WinDev.WDTouche().equals("*E*")) WinDev.AppelWD("Table,ajoute,wMain.TAB_ITEMS_START,"+add1);
		//		//System.out.println(WinDev.WDTouche()+"\n"+add1);
		//		String add2=sid+"\t"+uid+"\t"+x+"\t"+y+"\t"+1.00 /*energy - ag.getEnergy()*/ +"\t"+""+"\t"+mCREATION;
		//		WinDev.AppelWD("Table,ajoute,TAB_ITEMS,"+add2);

		if (!WinDev.getWDBoolean("wMain.INT_InfiniteBattery,1")) ag.setBattery(new LinearBatteryBasicModel(this.mas,this.mas.int_to_ObjectSystemIdentifier(sid),WDMainWindow.DEFAULT_INIT_AMOUNT_ENERGY,WDMainWindow.DEFAULT_INIT_AMOUNT_ENERGY));
		this.mas.addNewItem(new SimulatedAgent((Agent)ag,x,y));
		//this.updateLinkBetweenObjects();
		this.setViewableAreaRepresentation();


	}

	/** updates the spy windows */
	public void updateSpyWindows()
	{

		/* Update if necessary the spy windows*/
		if (mas!=null && mas.startedSimulation() && !mas.suspendedSimulation())
		{
			Vector<Object> presentStateVector = mas.spyiedObjectsState();
			Iterator<Object> iter = presentStateVector.iterator();
			HashMap<Integer,String> futurMap = new HashMap<Integer,String>();
			Object item;
			while (iter.hasNext())
			{
				item=iter.next();

				int id = item.getSystemId().getId();
				String spyString = item.toSpyWindows();
				futurMap.put(id,spyString);

				if (!oldStatesOfSpyiedObjects.containsKey(id) || !spyString.equals(oldStatesOfSpyiedObjects.get(id)))
				{
					// On actualise l'affichage
					WinDev.AppelWD("wMain._java_temp_int1="+id);
					WinDev.AfficheTexteLong("wMain._java_temp_chaine1",spyString);

					String strEvents ="";
					if (!oldStatesOfSpyiedObjects.containsKey(id))
					{
						Perf perf=new Perf();
						LinkedList<Event> lst = mas.getJournal().extractAllEvents(id);

						Iterator<Event> iterLst = lst.iterator();
						while(iterLst.hasNext()) strEvents+=eventToTable(iterLst.next());
					}

					//WinDev.AppelWD("_java_temp_chaine2=\""+strEvents+"\"");
					WinDev.AfficheTexteLong("wMain._java_temp_chaine2",strEvents);

					String criteria="___SYSTEM___\n";
					ListIterator<String> iterCriteria = this.statisticManager.AGENTS_system_criteria_name().listIterator();
					while(iterCriteria.hasNext()) criteria+=(iterCriteria.next().split("\t")[0]+"\n");
					iterCriteria = this.statisticManager.AGENTS_user_criteria_name().listIterator();
					criteria+="\n___USER___\n";
					while(iterCriteria.hasNext()) criteria+=(iterCriteria.next().split("\t")[0]+"\n");
					//System.out.println("CRITERAS="+criteria);
					WinDev.AfficheTexteLong("wMain._java_temp_chaine3",criteria);


					WinDev.AppelWD("wMain._java_update_spy_window()");

					if (WinDev.getWDEntier("wMain._java_temp_int1")!=1) verifySpyWindows();
				}

			}
			oldStatesOfSpyiedObjects=futurMap;
		}
	}

	//	/** updates the physical links set between objects*/
	//	public void updateLinkBetweenObjects()
	//	{
	//		int index=0;
	//		int modify;
	//		String s;
	//
	//		Iterator<ObjectAndItsNeighboorhood> iterList = mas.acquointances().getList().listIterator();	
	//
	//		//WinDev.AppelWD("wMain.TAB_ITEMS..AffichageActif=Faux");
	//
	//
	//		while (iterList.hasNext()) 
	//		{
	//			index++;
	//			s=iterList.next().getNeighboorhood().toString();
	//			//System.out.println("NEIGHBOORHOOD_String("+index+")="+s);
	//			s=s.substring(1,s.length()-1);
	//
	//			if (!s.equals(WinDev.AppelWD_WDChaine("Table,recupere,wMain.COL_NEIGHBOORHOOD,"+index)))
	//			{
	//				WinDev.AppelWD("tablemodifie(wMain.COL_NEIGHBOORHOOD,\""+s+"\","+index+")");
	//				modify=WinDev.AppelWD_WDEntier("Table,recupere,wMain.COL_MODIFIED,"+index);
	//				WinDev.AppelWD("tablemodifie(wMain.COL_MODIFIED,\""+(modify|this.mNEIGBOORHOOD)+"\","+index+")");
	//			}
	//		}
	//
	//		//WinDev.AppelWD("TAB_ITEMS..AffichageActif=Vrai");
	//
	//	}


	/** notify a set of events
	 * @param events the events set
	 */
	public synchronized void notifyEvent(HashMap<ObjectSystemIdentifier,ArrayList<Event>> events)
	{
		ObjectSystemIdentifier item;

		ArrayList<Event> lst;
		Iterator<ObjectSystemIdentifier> iterId = events.keySet().iterator();
		Iterator<Event> iterEvent;

		PositionModificationEvent lastPositionModificationEvent;
		EnergyModificationEvent lastEnergyModificationEvent;
		ColorModificationEvent lastColorModificationEvent;

		ArrayList<Event> spyiedItemEvents;

		Event evt;

		while(iterId.hasNext())
		{

			item=iterId.next();
			lst=events.get(item);
			iterEvent=lst.iterator();

			lastPositionModificationEvent=null;
			lastEnergyModificationEvent=null;
			lastColorModificationEvent=null;
			spyiedItemEvents = new ArrayList<Event>(10);

			while(iterEvent.hasNext()) 
			{
				evt=iterEvent.next();

				if (evt instanceof PositionModificationEvent) 
					lastPositionModificationEvent=(PositionModificationEvent)evt;
				else if (evt instanceof EnergyModificationEvent) 
					lastEnergyModificationEvent=(EnergyModificationEvent)evt;
				else if (evt instanceof ColorModificationEvent) 
					lastColorModificationEvent=(ColorModificationEvent)evt;
				else if (evt instanceof ReceivedBytesByEmbeddedObjectEvent)
					this.embeddedAgentExchangeConcurrentTraceList.push(new EmbeddedAgentExchangeTrace(evt.getRaiser(),true,evt.getDateInMs(),((ReceivedBytesByEmbeddedObjectEvent) evt).getBytes(),((ReceivedBytesByEmbeddedObjectEvent) evt).getSignification()));
				else if (evt instanceof SendedBytesByEmbeddedObjectEvent)
					this.embeddedAgentExchangeConcurrentTraceList.push(new EmbeddedAgentExchangeTrace(evt.getRaiser(),false,evt.getDateInMs(),((SendedBytesByEmbeddedObjectEvent) evt).getBytes(),((SendedBytesByEmbeddedObjectEvent) evt).getSignification()));
				if (mas.isSpyied(item)) spyiedItemEvents.add(evt);

			}

			if ((lastPositionModificationEvent!=null) || (lastEnergyModificationEvent!=null) || (lastColorModificationEvent!=null)) this.setViewableAreaRepresentation();

			if (spyiedItemEvents.size()>0)
			{
				// On actualise l'affichage
				WinDev.AppelWD("wMain._java_temp_int1="+item);
				WinDev.AppelWD("wMain._java_temp_chaine1=\"\"");
				String strEvents ="";
				Iterator<Event> iterLst = spyiedItemEvents.iterator();
				while(iterLst.hasNext()) strEvents+=eventToTable(iterLst.next());
				WinDev.AfficheTexteLong("wMain._java_temp_chaine2",strEvents);
				WinDev.AppelWD("wMain._java_update_spy_window()");
			}


		}
		//updateLinkBetweenObjects();

	}

	/** update the statistic representation
	 * @param aliasName name of the windows
	 * @param criteria name of the criteria
	 * @param statManager 
	 */
	private void updateStatistics(String aliasName,String criteria, StatisticManager statManager)
	{
		this.updateStatistics(aliasName, criteria, statManager,WDMainWindow.MAS_STAT_ID);
	}
	/** update the statistic representation
	 * @param aliasName name of the windows
	 * @param criteria name of the criteria
	 * @param statManager reference to the statistic manager
	 * @param objectId id of the entity concerned by this statistic
	 */
	private synchronized void updateStatistics(String aliasName,String criteria, StatisticManager statManager,int objectId)
	{
		System.out.println("On s'intérésse à "+objectId+" alias="+aliasName);
		AbstractDataBlock block;
		if (objectId==WDMainWindow.MAS_STAT_ID)
			block=statManager.getDataBlock(criteria);
		else
			block=statManager.getDataBlock(criteria,objectId);

		System.out.println("STAT DRESSEE - "+block.getTitle()+" - "+block.getNbPoints()+"pts") ;
		if (block==null)
			System.out.println("ERREUR: Critère '"+criteria+"' non trouvé");
		else
		{
			System.out.print("\n1");
			WinDev.AppelWD(aliasName+".statistic_graphTitre=\""+block.getTitle()+"\"");
			System.out.print("2");
			WinDev.AppelWD(aliasName+".statistic_graphLibX=\""+block.getLabelX()+"\"");
			System.out.print("3");
			WinDev.AppelWD(aliasName+".statistic_graphLibY=\""+block.getLabelY()+"\"");
			System.out.print("4");
			WinDev.AppelWD(aliasName+".statistic_graphTypeX="+block.getTypeX());
			System.out.print("5");
			WinDev.AppelWD(aliasName+".statistic_graphTypeY="+block.getTypeY());
			System.out.print("6");

			WinDev.AppelWD("Tablesupprimetout("+aliasName+".statistic_TableCoord)");
			System.out.print("7");

			ListIterator<Point2D.Double> iter=block.listIterator();
			Point2D.Double pt;
			while(iter.hasNext())
			{
				pt=iter.next();
				WinDev.AppelWD("TableAjoute("+aliasName+".statistic_TableCoord,"+pt.x+"+TAB+"+pt.y+")");
				System.out.print("x");
			}
			WinDev.AppelWD(aliasName+".statistic_drawGraph()");
			System.out.print("8");

		}
	}




	/** convert an event to a string represetnation understandable by the HMI
	 * @param evt an event
	 * @return the string representation of the event 
	 */
	private String eventToTable(Event evt)
	{
		int endDelimiter=3;
		int typeDelimiter = 2;


		if (evt instanceof ReceivedFrameEvent)
			return "RF"+(char) typeDelimiter+aDate.msToHHMMSSCCC(evt.getDateInMs())+(char) typeDelimiter+((ReceivedFrameEvent) evt).toString()+(char) endDelimiter;
		else   if (evt instanceof SendedFrameEvent)
			return "SF"+(char) typeDelimiter+aDate.msToHHMMSSCCC(evt.getDateInMs())+(char) typeDelimiter+((SendedFrameEvent) evt).toString()+(char) endDelimiter;
		else   if (evt instanceof ReceivedMessageEvent)
			return "RM"+(char) typeDelimiter+aDate.msToHHMMSSCCC(evt.getDateInMs())+(char) typeDelimiter+((ReceivedMessageEvent) evt).toString()+(char) endDelimiter;
		else   if (evt instanceof SendedMessageEvent)
			return "SM"+(char) typeDelimiter+aDate.msToHHMMSSCCC(evt.getDateInMs())+(char) typeDelimiter+((SendedMessageEvent) evt).toString()+(char) endDelimiter;
		else if (evt instanceof MessageNotTransmittedEvent)
			return "M"+(char) typeDelimiter+aDate.msToHHMMSSCCC(evt.getDateInMs())+(char) typeDelimiter+((MessageNotTransmittedEvent) evt).toString()+(char) endDelimiter;
		else   if (evt instanceof RoleModificationEvent)
			return "Rm"+(char) typeDelimiter+aDate.msToHHMMSSCCC(evt.getDateInMs())+(char) typeDelimiter+((RoleModificationEvent) evt).toString()+(char) endDelimiter;
		else   if (evt instanceof EnergyModificationEvent)
			return "E"+(char) typeDelimiter+aDate.msToHHMMSSCCC(evt.getDateInMs())+(char) typeDelimiter+((EnergyModificationEvent) evt).toString()+(char) endDelimiter;
		else   if (evt instanceof ColorModificationEvent)
			return "C"+(char) typeDelimiter+aDate.msToHHMMSSCCC(evt.getDateInMs())+(char) typeDelimiter+((ColorModificationEvent) evt).toString()+(char) endDelimiter;
		else   if (evt instanceof PositionModificationEvent)
			return "P"+(char) typeDelimiter+aDate.msToHHMMSSCCC(evt.getDateInMs())+(char) typeDelimiter+((PositionModificationEvent) evt).toString()+(char) endDelimiter;
		else   if (evt instanceof NeighboorhoodModificationEvent)
			return "N"+(char) typeDelimiter+aDate.msToHHMMSSCCC(evt.getDateInMs())+(char) typeDelimiter+((NeighboorhoodModificationEvent) evt).toString()+(char) endDelimiter;
		else   if (evt instanceof UserDefinedEvent)
			return "U"+(char) typeDelimiter+aDate.msToHHMMSSCCC(evt.getDateInMs())+(char) typeDelimiter+((UserDefinedEvent) evt).toString()+(char) endDelimiter;
		else  if (evt instanceof RangeModificationEvent)
			return "Ra"+(char) typeDelimiter+aDate.msToHHMMSSCCC(evt.getDateInMs())+(char) typeDelimiter+((RangeModificationEvent) evt).toString()+(char) endDelimiter;
		else if (evt instanceof ReceivedBytesByEmbeddedObjectEvent)
			return "Er"+(char) typeDelimiter+aDate.msToHHMMSSCCC(evt.getDateInMs())+(char) typeDelimiter+((ReceivedBytesByEmbeddedObjectEvent) evt).toString()+(char) endDelimiter;
		else if (evt instanceof SendedBytesByEmbeddedObjectEvent)
			return "Es"+(char) typeDelimiter+aDate.msToHHMMSSCCC(evt.getDateInMs())+(char) typeDelimiter+((SendedBytesByEmbeddedObjectEvent) evt).toString()+(char) endDelimiter;


		return "Uk"+(char) typeDelimiter+aDate.msToHHMMSSCCC(evt.getDateInMs())+(char) typeDelimiter+"Unknown event "+evt.toString()+(char) endDelimiter;
	}

	//	/** notify an event
	//	 * @param event an event which must be notified
	//	 */
	//	private void notifyEvent(Event event)
	//	{
	//		this.notifyEvent(event,true);
	//	}
	//
	//	/** notify an event
	//	 * @param event an event which must be notified
	//	 * @param updateNeighboorhood must the neighboorhood be verified (optimization)
	//	 */
	//	private synchronized void notifyEvent(Event event,boolean updateNeighboorhood)
	//	{
	//
	//		if(event instanceof ColorModificationEvent)
	//		{
	//
	//			int index = 1+this.mas.getIndexOfAnObjectSystemIdentifier(event.getRaiser());
	//
	//			int modify=WinDev.AppelWD_WDEntier("Table,recupere,wMain.COL_MODIFIED,"+index);
	//			//WinDev.AppelWD("tablemodifie(wMain.COL_COLOR,\""+((ColorModificationEvent)event).getRVBColor()+"\","+index+")");
	//			//WinDev.AppelWD("tablemodifie(wMain.COL_MODIFIED,\""+(modify|this.mCOLOR)+"\","+index+")");
	//		}
	//		else if(event instanceof EnergyModificationEvent)
	//		{
	//			int index = 1+this.mas.getIndexOfAnObjectSystemIdentifier(event.getRaiser());
	//
	//			int modify=WinDev.AppelWD_WDEntier("Table,recupere,wMain.COL_MODIFIED,"+index);
	//			//WinDev.AppelWD("tablemodifie(wMain.COL_ENERGY,\""+((EnergyModificationEvent)event).getAvailableEnergyLevel()+"\","+index+")");
	//			//WinDev.AppelWD("tablemodifie(wMain.COL_MODIFIED,\""+(modify|this.mENERGY)+"\","+index+")");
	//		}
	//		else if(event instanceof PositionModificationEvent)
	//		{
	//			int index = 1+this.mas.getIndexOfAnObjectSystemIdentifier(event.getRaiser());
	//
	//			Position p = ((PositionModificationEvent) event).getNewPosition();
	//
	//			int modify=WinDev.AppelWD_WDEntier("Table,recupere,wMain.COL_MODIFIED,"+index);
	//			//WinDev.AppelWD("tablemodifie(wMain.COL_X,\""+p.x+"\","+index+")");
	//			//WinDev.AppelWD("tablemodifie(wMain.COL_Y,\""+p.y+"\","+index+")");
	//			//WinDev.AppelWD("tablemodifie(wMain.COL_MODIFIED,\""+(modify|this.mPOSITION)+"\","+index+")");
	//		}
	//	}

	/** updates the contextual statistic menu */
	private void updateContextualStatisticMenu()
	{
		System.out.println("MENU!!!");
		int i=1;
		ListIterator<String> iter = this.statisticManager.MAS_user_criteria_name().listIterator();
		while(iter.hasNext())
		{	
			String s = iter.next();
			System.out.println("USER:"+s);			
			// Update le critère utilisateur
			WinDev.AppelWD("StatistiqueUser_"+i+"..libelle=\""+s.split("\t")[0]+"\"");
			WinDev.AppelWD("StatistiqueUser_"+i+"..visible=vrai");

			i++;
		}

		i=1;
		iter = this.statisticManager.MAS_system_criteria_name().listIterator();
		while(iter.hasNext())
		{	
			String s = iter.next();
			System.out.println("SYSTEM:"+s);
			WinDev.AppelWD("Statistique_"+i+"..libelle=\""+s.split("\t")[0]+"\"");
			WinDev.AppelWD("Statistique_"+i+"..visible=vrai");

			i++;
		}

	}

	/** updates the contextual statistic menu */
	private void updateArchivedJournalCriteria()
	{
		// Vide les listes combo
		WinDev.AppelWD("Listesupprimetout(wMain.Combo_agent_archived_criteria)");
		WinDev.AppelWD("Listesupprimetout(wMain.Combo_mas_archived_criteria)");

		// Update la liste combo Combo_user_defined_archived_criteria
		int i=1;
		ListIterator<String> iter = this.statisticManager.AGENTS_user_criteria_name().listIterator();
		while(iter.hasNext())
		{	
			String s = iter.next();
			WinDev.AppelWD("Listeajoute(wMain.Combo_agent_archived_criteria,\""+s.split("\t")[0]+"\")");
			i++;
		}

		// Update la liste combo Combo_system_archived_criteria
		i=1;
		iter = this.statisticManager.MAS_user_criteria_name().listIterator();
		while(iter.hasNext())
		{	
			String s = iter.next();
			WinDev.AppelWD("Listeajoute(wMain.Combo_mas_archived_criteria,\""+s.split("\t")[0]+"\")");
			i++;
		}

	}

	/**
	 * Convert an array of String in a tabular String
	 * @param array the array of string
	 * @return the string representation of the array of string
	 */
	public static String stringArrayToString(String[] array)
	{
		String res="";
		if((array==null) || (array.length==0)) return "";
		res+=array[0];
		for(int i=1;i<array.length;i++) res+=("\t"+array[i]);
		return res;
	}



	/** Timer to launch an update of all windows */
	private class TaskSpyWindowUpdate implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			mustUpdateSpyWindows=true;
		}
	}

	/** allows to manage a set of frame which must be dispatched*/ 
	private class EmbeddedAgentExchangeConcurrentTraceList
	{
		/** the list of frame (id of receiver/bytes to send) */
		private LinkedList<EmbeddedAgentExchangeTrace> lst;
		/** to protect the linked list */
		private boolean locked;

		/** basic constructor */
		public EmbeddedAgentExchangeConcurrentTraceList()
		{
			this.lst=new LinkedList<EmbeddedAgentExchangeTrace>();
			this.locked=false;
		}

		/** lock the linked list*/
		public synchronized void acquire()
		{
			while(this.locked);
			this.locked=true;
		}

		/** release the linked list */
		public synchronized void release()
		{
			this.locked=false;
		}

		/** add a frame */
		public synchronized void push(EmbeddedAgentExchangeTrace e)
		{
			acquire();
			lst.add(e);
			release();
		}
		/** returns the next frame to send 
		 * @return a frame to dispatch
		 */
		public synchronized EmbeddedAgentExchangeTrace pop()
		{
			acquire();
			EmbeddedAgentExchangeTrace e =lst.getFirst();
			lst.removeFirst();
			release();
			return e;
		}

		/** returns the number of frames in the linked list
		 * @return the number of frame which wait to be computed
		 */
		public int size()
		{
			return lst.size();
		}

		/** is the list empty?
		 * @return true if there is no message to compute
		 */
		public boolean isEmpty()
		{
			return this.lst.isEmpty();
		}
	}

	/** a couple of receiver/frame view as a byte array */
	private class EmbeddedAgentExchangeTrace 
	{
		/** the receiver */
		public ObjectSystemIdentifier id;
		/** date of event */
		public long date;
		/** is the byte exchanged Simulator=>EmbeddedAgent or EmbeddedAgent=>Simulator*/
		public boolean simulatorToEmbeddedAgent;
		/** the message view as a bytes array */
		public byte[] msg;
		/** signification of the received/sended bytes */
		public String signification;


		/**
		 * construct the embedded exchanged trace 
		 * @param id idenfier of the agent
		 * @param simulatorToEmbeddedAgent (true is the object receives a frame from the simulator, else if it is the simulator which has received a frame from the embedded object)
		 * @param date date of transmission
		 * @param msg the received/sended bytes
		 * @param signification significaiton of the received/sended bytes
		 */
		public EmbeddedAgentExchangeTrace(ObjectSystemIdentifier id,boolean simulatorToEmbeddedAgent,long date,byte[] msg,String signification)
		{
			this.id=id;
			this.msg=msg;
			this.date=date;
			this.simulatorToEmbeddedAgent=simulatorToEmbeddedAgent;
			this.signification=signification;
			System.out.println(this.simulatorToEmbeddedAgent);
		}
	}



}

