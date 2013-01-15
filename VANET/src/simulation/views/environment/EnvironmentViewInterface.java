package simulation.views.environment;

import java.awt.image.BufferedImage;

import simulation.utils.IntegerPosition;

public abstract interface EnvironmentViewInterface {
	public abstract boolean hasBeenUpdated();
	public abstract BufferedImage graphicalView(IntegerPosition origine, int width, int height, double zoom);
	public abstract int getHeight();
	public abstract int getWidth();

}
