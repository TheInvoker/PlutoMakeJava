import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import javax.imageio.ImageIO;

public final class MyJSON {
	
    public final static BufferedImage GenerateWarpedLogo(BufferedImage logoImage, String maskPath, String mappingPath) throws IOException
    {
    	// reads the binary data into in[]
    	FileInputStream stream = new FileInputStream(mappingPath);
        File f = new File(mappingPath);
        
        FileChannel inChannel = stream.getChannel();
        ByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        
        inChannel.close();
        stream.close();
        
        int size = (int) f.length() >> 2;  // divide by 4
        int[] xy = new int[2], result = new int[size];

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        IntBuffer intBuffer = buffer.asIntBuffer();
        intBuffer.get(result);

        
        // read width, height
        cantor_pair_reverse(result[0], xy);
        int i, newIndex, x=0, y=0, width = xy[0];
        

        // new warped logo
        BufferedImage flag = new BufferedImage(logoImage.getWidth(), logoImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        // read mask if exists
        BufferedImage maskImage = null;
        if (PlutoMake.isFile(new File(maskPath)))
        {
            maskImage = ImageIO.read(new File(maskPath));
        }
        

        for(i=1; i<size; i+=1) {
        	if (result[i] < 0) {
                newIndex = width * y + x - result[i];
                y = newIndex / width;
                x = newIndex - (width * y);
        	} else {
            	cantor_pair_reverse(result[i], xy);

                
                
                
                
               
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

                
                
                
                
                
                x += 1;
                if (x >= width)
                {
                    x = 0;
                    y += 1;
                }
        	}
        }  
        
        if (maskImage != null)
        {
        	maskImage.flush();
        }

    	return flag;
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
