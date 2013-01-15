package simulation.views.entity;

import java.awt.image.BufferedImage;

import simulation.views.entity.basic.BasicView;

	public interface BasicViewEntityViewInterface {
		// Si l'user a demander une autre vue que la native
		public abstract BufferedImage graphicalView(double zoom, BasicView view, boolean noText);
		public abstract BufferedImage graphicalView(double zoom, BasicView view);
}
