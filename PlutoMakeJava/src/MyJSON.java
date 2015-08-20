import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyJSON {
	
    public static Map<Point, Point> ReadMapping(String mappingPath) throws IOException
    {
    	Map<Point, Point> data = new HashMap<Point, Point>();
    	BufferedReader br = new BufferedReader(new FileReader(mappingPath));

    	try {
    	    String finalstring = br.readLine();
    	    Pattern rx = Pattern.compile("x?\\d+");
            Matcher m = rx.matcher(finalstring);
            
            int x = 0, y = 0;
            int[] xy;
            String val;
            
            m.find();
            val = m.group(0).toString();
            int width = cantor_pair_reverse(Integer.parseInt(val))[0];
            
            while (m.find()) {
                val = m.group(0).toString();
                
                if (val.charAt(0)=='x') {
                    int curentIndex = width * y + x;
                    int newIndex = curentIndex + Integer.parseInt(val.substring(1));
                    y = newIndex / width;
                    x = newIndex - (width * y);
                } else
                {
                	xy = cantor_pair_reverse(Integer.parseInt(val));
                    data.put(new Point(x, y), new Point(xy[0], xy[1]));
                    x += 1;
                    if (x >= width)
                    {
                        x = 0;
                        y += 1;
                    }
                }
            }
    	} finally {
    	    br.close();
    	}

    	return data;
    }
    
    /**
     * Return the source integers from a cantor pair integer.
     */
    private static int[] cantor_pair_reverse(int z)
    {
      int t = (int) Math.floor((-1 + Math.sqrt(1 + 8 * z))/2);
      int x = t * (t + 3) / 2 - z;
      int y = z - t * (t + 1) / 2;
      return new int[] {x, y};
    }
}
