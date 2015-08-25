import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import Filter.ColorCalculate;
import Filter.ColorFilter;

public final class Exporter {
	
    public final static BufferedImage GenerateWarpedLogo(BufferedImage logoImage, String maskPath, Map<Point, Point> mapping) throws IOException
    {
        BufferedImage flag = new BufferedImage(logoImage.getWidth(), logoImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        BufferedImage maskImage = null;
        File maskfile = new File(PlutoMake.classDir + maskPath);
        if(maskfile.exists() && !maskfile.isDirectory())
        {
            maskImage = ImageIO.read(new File(PlutoMake.classDir + maskPath));
        }

        for(Entry<Point, Point> entry : mapping.entrySet())
        {
            int x = entry.getKey().x;
            int y = entry.getKey().y;
            Point point = entry.getValue();
            Color pixel = getColor(logoImage.getRGB(point.x, point.y));

            int newA = pixel.getAlpha();
            if (maskImage != null)
            {
            	Color maskpixel = getColor(maskImage.getRGB(x, y));
                int maskA = maskpixel.getAlpha();
                if (maskA != 0)
                {
                    newA *= maskA / 255;
                } else
                {
                    continue;
                }
            }

            flag.setRGB(x, y, (new Color(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), newA)).getRGB());
        }

        if (maskImage != null)
        {
        	maskImage.flush();
        }

        logoImage.flush();
        return flag;
    }

    public final static void StampLogo(String templatePath, String outPath, int xStart, int yStart, int width, int height, BufferedImage logo, ColorFilter filter) throws IOException
    {
    	BufferedImage templateImage = ImageIO.read(new File(PlutoMake.classDir + templatePath));
    	
        int w = logo.getWidth();
        int h = logo.getHeight();

        BufferedImage newLogo = null;
        if (w != width || h != height)
        {
            newLogo = ResizeImage(logo, width, height);
            logo = newLogo;
            w = logo.getWidth();
            h = logo.getHeight();
        }

        for(int x = 0; x < w; x += 1)
        {
            for (int y = 0; y < h; y += 1)
            {
                if (x + xStart < templateImage.getWidth() && y + yStart < templateImage.getHeight())
                {
                    Color pixel = getColor(logo.getRGB(x, y));
                    if (pixel.getAlpha() != 0)
                    {
                        Color currentPixel = getColor(templateImage.getRGB(x + xStart, y + yStart));
                        Color newColor = ColorCalculate.Mix(currentPixel, pixel);
                        templateImage.setRGB(x + xStart, y + yStart, (filter.GetFilteredColor(currentPixel, newColor)).getRGB());
                    }
                }
            }
        }

        if (newLogo != null)
        {
            newLogo.flush();
        }

        ImageIO.write(templateImage, "png", new FileOutputStream(PlutoMake.classDir + outPath));
        
        
        templateImage.flush();
    }

    public final static BufferedImage ResizeImage(BufferedImage originalImage, int newWidth, int newHeight){
		
		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
		g.dispose();	
		g.setComposite(AlphaComposite.Src);
	
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
		RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
		
		return resizedImage;
    }	
    
	private final static Color getColor(int pixel) {
		int alpha = (pixel >> 24) & 0xff;
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;
		return new Color(red, green, blue, alpha);
	}
}
