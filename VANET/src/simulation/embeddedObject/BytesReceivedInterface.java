package simulation.embeddedObject;

import simulation.multiagentSystem.ObjectSystemIdentifier;

/**
 * interface of a class which is able to receive bytes
 * @author JPeG
 */
public interface BytesReceivedInterface {
	/**
	 * method which enable a class to receive and process received bytes 
	 * @param bytes
	 */
	public void receivedBytes(byte[] bytes);
}
