package simulation.views.user.layer;

import java.awt.image.BufferedImage;

import simulation.utils.IntegerPosition;

public  interface UserLayerInterface {
	
	/** is the user layer updated ? */
	public abstract boolean hasBeenUpdated();
	
	/** return the a part of the user layer graphical view 
	 * @param origine upper left corner of the begin of the part
	 * @param width the width of the extracted part
	 * @param height the height of the extracted part
	 * @param zoom the scale of the part
	 * @return the extracted part of the user layer
	 */
	public abstract BufferedImage graphicalView(IntegerPosition origine, int width, int height, double zoom);
	
	/** return the height of the view
	 * @return the height
	 */
	public abstract int getHeight();
	
	/** return the width of the view 
	 * @return the width 
	 */
	public abstract int getWidth();
	//public abstract MAS setMAS();
}
