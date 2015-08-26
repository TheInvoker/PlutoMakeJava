import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import Filter.ColorCalculate;
import Filter.ColorFilter;

public final class Exporter {
	
    public final static void StampLogo(String templatePath, String outPath, int xStart, int yStart, int width, int height, BufferedImage logo, ColorFilter filter) throws IOException
    {
    	BufferedImage templateImage = ImageIO.read(new File(PlutoMake.classDir + templatePath));
    	
        int w = logo.getWidth(), h = logo.getHeight(), xs, ys;

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
                Color pixel = getColor(logo.getRGB(x, y));
                if (pixel.getAlpha() != 0)
                {
                	xs = xStart + x;
                	ys = yStart + y;
                    Color currentPixel = getColor(templateImage.getRGB(xs, ys));
                    Color newColor = ColorCalculate.Mix(currentPixel, pixel);
                    templateImage.setRGB(xs, ys, (filter.GetFilteredColor(currentPixel, newColor)).getRGB());
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
    
	public final static Color getColor(int pixel) {
		int alpha = (pixel >> 24) & 0xff;
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;
		return new Color(red, green, blue, alpha);
	}
}
