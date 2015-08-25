import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public final class MyJSON {
	
    public final static Map<Point, Point> ReadMapping(String mappingPath) throws IOException
    {
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

        cantor_pair_reverse(result[0], xy);
        int i, newIndex, x=0, y=0, width = xy[0];
        
        Map<Point, Point> data = new HashMap<Point, Point>();
        
        for(i=1; i<size; i+=1) {
        	if (result[i] < 0) {
                newIndex = width * y + x - result[i];
                y = newIndex / width;
                x = newIndex - (width * y);
        	} else {
            	cantor_pair_reverse(result[i], xy);
                data.put(new Point(x, y), new Point(xy[0], xy[1]));
                x += 1;
                if (x >= width)
                {
                    x = 0;
                    y += 1;
                }
        	}
        } 
        
    	return data;
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
