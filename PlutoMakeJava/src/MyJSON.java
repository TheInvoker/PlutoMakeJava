import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public final class MyJSON {
	
    public final static BufferedImage GenerateWarpedLogo(BufferedImage logoImage, String maskPath, String mappingPath) throws IOException
    {
        BufferedImage mappingImage = ImageIO.read(new File(mappingPath));
        int x=0, y=0, width=mappingImage.getWidth(), height=mappingImage.getHeight();
        int[] xy = new int[2];
        Color color;

        // new warped logo
        BufferedImage flag = new BufferedImage(logoImage.getWidth(), logoImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        // read mask if exists
        BufferedImage maskImage = null;
        if (PlutoMake.isFile(new File(maskPath)))
        {
            maskImage = ImageIO.read(new File(maskPath));
        }
        

        
        for(x=0; x<width; x+=1) {
        	for(y=0; y<height; y+=1) {

        		color = Exporter.getColor(mappingImage.getRGB(x, y));
        		
        		if (color.getAlpha() != 0) {

	            	cantor_pair_reverse(getint(color.getRed(), color.getGreen(), color.getBlue()), xy);

	                Color pixel = Exporter.getColor(logoImage.getRGB(xy[0], xy[1]));
	
	                int newA = pixel.getAlpha();
	                if (maskImage == null) {
	                	flag.setRGB(x, y, (new Color(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), newA)).getRGB());
	                } else {
	                    int maskA = Exporter.getColor(maskImage.getRGB(x, y)).getAlpha();
	                    if (maskA != 0)
	                    {
	                        newA *= maskA / 255;
	                        flag.setRGB(x, y, (new Color(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), newA)).getRGB());
	                    }
	                }
	        	}
	    	}
	    } 
        
        if (maskImage != null)
        {
        	maskImage.flush();
        }
        
        mappingImage.flush();

    	return flag;
    }
    
    private final static int getint(int r, int g, int b)
    {
        int rl = (r << 16) & 0xFF0000;
        int gl = (g << 8) & 0x00FF00;
        int bl = b & 0x0000FF;
        return rl | gl | bl;
    }
    
    
    /**
     * Return the source integers from a cantor pair integer.
     */
    private final static void cantor_pair_reverse(int z, int[] xy)
    {
      int t = (int) Math.floor((-1 + Math.sqrt(1 + 8 * z))/2);
      int x = t * (t + 3) / 2 - z;
      int y = z - t * (t + 1) / 2;
      xy[0] = x;
      xy[1] = y;
    }
}
