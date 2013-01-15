import java.util.Iterator;
import java.util.TreeSet;

import simulation.embeddedObject.serialCommunication.*;
import simulation.solutions.custom.MWAC.MWACRouteAssistant;
import simulation.solutions.custom.RecMAS.MWAC.Messages.MWACMessage_Presentation;
import simulation.solutions.custom.RecMAS.RecursiveAgent.ReceivedRecMASMessageFIFOStack;
import simulation.solutions.custom.RecMAS.RecursiveAgent.Messages.Label;
import simulation.solutions.custom.RecMAS.RecursiveAgent.Messages.RecMASMessage;
import simulation.solutions.custom.RecMAS.RecursiveAgent.Messages.RecMASMessage_msgApplicativeMessage;
import simulation.solutions.custom.RecMAS.RecursiveAgent.Messages.RecMASMessage_msgExternSystemMessageTransport;

/** the main class 
 * @author Jean-Paul Jamont
 */
public class MASHSimulator {

	
	public static String VERSION = "1.02.m";
	
//	
	/** main */
	public static void main(String args[]) 
	{
		
				
//		int[] tab = new int[1];
//		tab[0]=115;//tab[1]=222;tab[2]=333;tab[3]=444;
//		RecMASMessage_msgExternSystemMessageTransport msg1=new RecMASMessage_msgExternSystemMessageTransport(115,98,1,new Label(1),  new RecMASMessage_msgApplicativeMessage(115,-1,1,new MWACMessage_Presentation(115,(byte)1,tab)));
//		RecMASMessage_msgExternSystemMessageTransport msg2=new RecMASMessage_msgExternSystemMessageTransport(112,98,1,new Label(1),  new RecMASMessage_msgApplicativeMessage(115,-1,1,new MWACMessage_Presentation(115,(byte)1,tab)));
//		RecMASMessage_msgExternSystemMessageTransport msg3=new RecMASMessage_msgExternSystemMessageTransport(322,98,1,new Label(1),  new RecMASMessage_msgApplicativeMessage(115,-1,1,new MWACMessage_Presentation(115,(byte)1,tab)));
//		
//		RecMASMessageFIFOStack stack=new RecMASMessageFIFOStack();
//		
//		stack.push(msg2);
//		stack.push(msg3);
//		stack.push(msg2);
//		stack.push(msg3);
//		stack.push(msg1);
//		stack.push(msg2);
//		stack.push(msg3);
//		stack.push(msg2);
//		stack.push(msg3);
//		stack.push(msg1);
//		
//		
//		System.out.println(stack);
//		
		

		
		
		WDMainWindow main = new WDMainWindow();
		main.go();
		System.out.println("Bye!");
	}
}
