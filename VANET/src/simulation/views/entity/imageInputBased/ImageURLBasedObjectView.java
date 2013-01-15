package simulation.views.entity.imageInputBased;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import simulation.views.entity.BadObjectRenderingBufferedImage;
import simulation.views.entity.EntityViewInterface;
import simulation.utils.ImageToolkit;
import simulation.utils.IntegerPosition;

public class ImageURLBasedObjectView implements ImageBasedObjectViewInterface
{
	private URL url;
	private boolean hasBeenUpdated;

	public ImageURLBasedObjectView(URL input) 
	{
		this.url=input;
		this.hasBeenUpdated=true;
	}


	
	@Override
	public BufferedImage graphicalView(double zoom) {
		return this.graphicalView(zoom,false);
	}
	public BufferedImage graphicalView(double zoom,boolean noText) {
		// TODO Auto-generated method stub
		try {
			if(zoom==1)

				return ImageIO.read(url);

			else 
				return ImageToolkit.scale(ImageIO.read(url), zoom);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return BadObjectRenderingBufferedImage.getBadAgentRenderingBufferedImage();
		}
	}
	
	public boolean hasBeenUpdated() {
		return this.hasBeenUpdated;
	}
}
