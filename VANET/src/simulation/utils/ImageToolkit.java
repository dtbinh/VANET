package simulation.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageToolkit {
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

	public static BufferedImage cloneBufferedImage(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public static BufferedImage cloneBufferedImage(BufferedImage bi, IntegerPosition origine,int width,int height,double scale)
	{

		boolean isAlphaPremultiplied = bi.getColorModel().isAlphaPremultiplied();

		int dimExtractX=0; 	int  dimExtractY=0;

		dimExtractX= Math.min((int)(width/scale),bi.getWidth()-origine.x);	
		dimExtractY= Math.min((int)(height/scale),bi.getHeight()-origine.y);


		WritableRaster raster = bi.getData().createCompatibleWritableRaster(dimExtractX, dimExtractY);
		Object pixels = null;

		//System.out.println("DimResVoulu="+width+"x"+height+"   origine="+origine.toString()+"  dimEnv=("+bi.getWidth()+","+bi.getHeight()+")  scale="+scale+"   dimExtract=("+dimExtractX+","+dimExtractY+")    raster=("+raster.getWidth()+","+raster.getHeight()+")");


		//pixels=bi.getRaster().getDataElements(origine.x, origine.y, (int) (width*scale), (int) (height*scale),pixels);
		//raster.setDataElements(0, 0, pixels);
		int i=0;int j=0;	
		try
		{

			for(i=0;i<dimExtractX;i++)
				for(j=0;j<dimExtractY;j++)
				{
					pixels = bi.getRaster().getDataElements(origine.x+i, origine.y+j, pixels);
					raster.setDataElements(i, j, pixels);
				}
			//System.out.println("\n(i,j)=("+i+","+j+")  position="+origine.toString()+"  dimEnv=("+bi.getWidth()+","+bi.getHeight()+")  scale="+scale+"   dimExtract=("+dimExtractX+","+dimExtractY+")    raster=("+raster.getWidth()+","+raster.getHeight()+")");

		}
		catch(java.lang.ArrayIndexOutOfBoundsException e)
		{
			System.out.println("");
			e.printStackTrace();

		}
		//		BufferedImage trv = new BufferedImage(bi.getColorModel(), raster, isAlphaPremultiplied, null);
		//		try {
		//			ImageIO.write(trv, "BMP", new File("D:\\RESAVANTSCALE.BMP"));
		//			ImageIO.write(bi, "BMP", new File("D:\\ENVAVANTSCALE.BMP"));
		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		return ImageToolkit.scale(new BufferedImage(bi.getColorModel(), raster, isAlphaPremultiplied, null),scale);
	}


	public static BufferedImage CreateTransparencyBufferedImage(int width,int height)
	{
	
			
		BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
			ImageFilter filter = new RGBImageFilter()
			{
				public final int filterRGB(int x, int y, int rgb)
				{
					 return rgb & 0xFFFFFF;
				}
			};

			ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
			Image im= Toolkit.getDefaultToolkit().createImage(ip);
			BufferedImage dest = new BufferedImage(image.getWidth(),image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = dest.createGraphics();
			g2.drawImage(image, 0, 0, null);
			return dest;
		
	}
	
	
	public static BufferedImage TransformColorToTransparency(BufferedImage image)
	{
		return ImageToolkit.TransformColorToTransparency(image,new Color(image.getRGB(0, 0)));
	}
	
	public static BufferedImage TransformColorToTransparency(BufferedImage image, Color transparentColor)
	{

		// Primitive test, just an example
		final int rt = transparentColor.getRed();
		final int gt = transparentColor.getGreen();
		final int bt = transparentColor.getBlue();

		ImageFilter filter = new RGBImageFilter()
		{
			public final int filterRGB(int x, int y, int rgb)
			{
				int r = (rgb & 0xFF0000) >> 16;
				int g = (rgb & 0xFF00) >> 8;
				int b = rgb & 0xFF;
				if (r==rt && g==gt && b==bt) 
					{
					return rgb & 0xFFFFFF;
					}
				return rgb;
			}
		};

		ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
		Image im= Toolkit.getDefaultToolkit().createImage(ip);
		BufferedImage dest = new BufferedImage(image.getWidth(),image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = dest.createGraphics();
		g2.drawImage(im, 0, 0, null);
		return dest;
	}

}
