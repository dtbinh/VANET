package simulation.statistics.system;

/** item used to enable a quicker performance measure than the journal based statistics 
 * @author Jean-Paul Jamont
 */
public class SystemDataPerformanceItem {
		/** number of event */
		public int nbEvent;
		/** number of sended messages */
		public int nbSendedMessage;
		/** number of received messages */
		public int nbReceivedMessage;
		/** number of sended frames */
		public int nbSendedFrame;
		/** number of received frames */
		public int nbReceivedFrame;
		/** cumuled volume of received frames */
		public long volumeReceivedFrame;
		/** cumuled volume of sended frames  */
		public long volumeSendedFrame;
		/** cumuled volume of received messages  */
		public long volumeReceivedMessage;
		/** cumuled volume of sended messages */
		public long volumeSendedMessage;
		/** energy */
		public float energy;
		/**role */
		public int role;
		/** absisse of the position */
		public int xPosition;
		/** ordinate of the position */
		public int yPosition;
		/** range */
		public int range;
		
		/** allows to clone a SystemDataPerformanceItem
		 * @return a new SystemDataPerformanceItem which is a clone of this one
		 */
		public SystemDataPerformanceItem clone()
		{
			SystemDataPerformanceItem n = new SystemDataPerformanceItem();
			
			n.nbEvent=this.nbEvent;
			n.nbSendedMessage=this.nbSendedMessage;
			n.nbReceivedMessage=this.nbReceivedMessage;
			n.nbSendedFrame=this.nbSendedFrame;
			n.nbReceivedFrame=this.nbReceivedFrame;
			n.volumeReceivedFrame=this.volumeReceivedFrame;
			n.volumeSendedFrame=this.volumeSendedFrame;
			n.volumeReceivedMessage=this.volumeReceivedMessage;
			n.volumeSendedMessage=this.volumeSendedMessage;
			n.energy=this.energy;
			n.role=this.role;
			n.xPosition=this.xPosition;
			n.yPosition=this.yPosition;
			n.range=this.range;
			
			return n;
		}
}
