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

import simulation.views.entity.EntityViewInterface;
import simulation.utils.ImageToolkit;
import simulation.utils.IntegerPosition;

public class ImageFileBasedObjectView implements ImageBasedObjectViewInterface
{
	private BufferedImage environmentView;
	private boolean hasBeenUpdated;

	public ImageFileBasedObjectView(String fileName) throws IOException
	{
		this(new File(fileName));
	}
	public ImageFileBasedObjectView(File input) throws IOException
	{
		this.environmentView=ImageIO.read(input);
		this.hasBeenUpdated=true;
	}


	@Override
	public BufferedImage graphicalView(double zoom) {
		return this.graphicalView(zoom,false);
	}
	public BufferedImage graphicalView(double zoom,boolean noText) {
		// TODO Auto-generated method stub
		BufferedImage newImg = this.environmentView;
		if(zoom!=1) newImg = ImageToolkit.scale(this.environmentView, zoom);
		
		return ImageToolkit.TransformColorToTransparency(newImg);


	}
	@Override
	public boolean hasBeenUpdated() {
		return this.hasBeenUpdated;
	}

}
