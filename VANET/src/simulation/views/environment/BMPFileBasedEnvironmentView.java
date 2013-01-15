package simulation.views.environment;

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

import simulation.utils.ImageToolkit;
import simulation.utils.IntegerPosition;

public class BMPFileBasedEnvironmentView implements EnvironmentViewInterface
{
	private BufferedImage environmentView;
	private boolean hasBeenUpdated;

	public BMPFileBasedEnvironmentView(String fileName) throws IOException
	{
		this(new File(fileName));
	}
	public BMPFileBasedEnvironmentView(File input) throws IOException
	{
		this.update(input);
	}
	public BMPFileBasedEnvironmentView(ImageInputStream stream) throws IOException
	{
		this.update(stream);
	}
	public BMPFileBasedEnvironmentView(InputStream input) throws IOException
	{
		this.update(input);
	}
	public BMPFileBasedEnvironmentView(URL input) throws IOException
	{
		this.update(input);
	}

	/** Effectue une homothétie de l'image.
	 * 
	 * @param bi l'image.
	 * @param scaleValue la valeur de l'homothétie.
	 * @return une image réduite ou agrandie.
	 * 
	 */
	public static BufferedImage scale(BufferedImage bi, double scaleValue) {
		 AffineTransform tx = new AffineTransform();
		 tx.scale(scaleValue, scaleValue);
		 AffineTransformOp op = new AffineTransformOp(tx,AffineTransformOp.TYPE_BILINEAR);
		 BufferedImage biNew = new BufferedImage( (int) (bi.getWidth() * scaleValue),
				 (int) (bi.getHeight() * scaleValue),
				 bi.getType());
		 return op.filter(bi, biNew);

	 } 

	 public void  update(String fileName) throws IOException
	 {
		 this.update(new File(fileName));
		 this.hasBeenUpdated=true;
	 }
	 public void update(File input) throws IOException
	 {
		 this.environmentView=ImageIO.read(input);
		 this.hasBeenUpdated=true;
	 }
	 public void update(ImageInputStream stream) throws IOException
	 {
		 this.environmentView=ImageIO.read(stream);
		 this.hasBeenUpdated=true;
	 }
	 public void update(InputStream input) throws IOException
	 {
		 this.environmentView=ImageIO.read(input);
		 this.hasBeenUpdated=true;
	 }
	 public void update(URL input) throws IOException
	 {
		 this.environmentView=ImageIO.read(input);
		 this.hasBeenUpdated=true;
	 }

	 public boolean hasBeenUpdated()
	 {
		 return this.hasBeenUpdated;
	 }

	 public void setUpdatedAttribute(boolean updated)
	 {
		 this.hasBeenUpdated=updated;
	 }

	 public BufferedImage getOriginalEnvironmentViewCopy()
	 {
		 return this.getOriginalEnvironmentViewCopy(new IntegerPosition(0,0), this.environmentView.getWidth(),this.environmentView.getHeight());
	 }
	 public BufferedImage getOriginalEnvironmentViewCopy(IntegerPosition origine, int width, int height)
	 {
		return this.getOriginalEnvironmentViewCopy(origine, width, height, 1.0); 
	 }
	 public BufferedImage getOriginalEnvironmentViewCopy(IntegerPosition origine, int width, int height,double scale)
	 {
		 return ImageToolkit.cloneBufferedImage(this.environmentView,origine,width,height,scale); 
	 }
	 
	 @Override
	 public BufferedImage graphicalView(IntegerPosition origine, int width, int height,double zoom) {
		 // TODO Auto-generated method stub
		 BufferedImage newImg = this.getOriginalEnvironmentViewCopy(origine, width, height,zoom);

		 //BufferedImage newImg = this.environmentView;
		 //if(zoom!=1) newImg = BMPFileBasedEnvironmentView.scale(this.environmentView, zoom);

		 
		 try
		 {
			 //return newImg.getSubimage(origine.x, origine.y,  (origine.x+width)>newImg.getWidth() ? newImg.getWidth()-origine.x : width, (origine.y+height)>newImg.getHeight() ? newImg.getHeight()-origine.y : height);
			 return newImg;
			 
		 }
		 catch(RasterFormatException e)
		 {
			 System.out.println("public BufferedImage graphicalView(Position origine="+origine.toString()+", int width="+width+", int height="+height+",double zoom="+zoom+")");
			 System.out.println("Raster: "+newImg.getWidth()+","+newImg.getHeight());
				 e.printStackTrace();
			 return BadEnvironnementRenderingBufferedImage.getBadEnvironnementRenderingBufferedImage();
		 }
	 }
	@Override
	public int getHeight() {
		return this.environmentView.getHeight();
	}
	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return this.environmentView.getWidth();
	}

}

