package simulation.views.entity;

import java.awt.image.BufferedImage;


public interface EntityViewInterface {
	// Si l'user a demander une autre vue que la native
	public abstract BufferedImage graphicalView(double zoom,boolean noText);
	public abstract BufferedImage graphicalView(double zoom);
	public abstract boolean hasBeenUpdated();
}
