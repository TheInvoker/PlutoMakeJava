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
    	BufferedReader br = new BufferedReader(new FileReader(mappingPath));
    	try {
    	    String finalstring = br.readLine();
    	    
    	    Pattern rx = Pattern.compile("(x?\\d+)");
            Matcher m = rx.matcher(finalstring);
            Map<Point, Point> data = new HashMap<Point, Point>();
            Point newPoint = new Point(0,0);

            int state = 0;
            int width = 0;
            int x = 0, y = 0;
            
            
            while (m.find()) {
                String val = m.group(0).toString();

                if (state == 0)
                {
                    width = Integer.parseInt(val);
                    state += 1;
                } else if (state == 1)
                {
                    state += 1;
                } else if (val.contains("x")) {
                    int commalength = Integer.parseInt(val.substring(1))/2;
                    int curentIndex = width * y + x;
                    int newIndex = curentIndex + commalength;
                    y = newIndex / width;
                    x = newIndex - (width * y);
                } else
                {
                    if (state == 2)
                    {
                        newPoint.x = Integer.parseInt(val);
                        state += 1;
                    } else
                    {
                        newPoint.y = Integer.parseInt(val);
                        data.put(new Point(x, y), newPoint);
                        newPoint = new Point(0, 0);
                        state = 2;
                    
                        x += 1;
                        if (x >= width)
                        {
                            x = 0;
                            y += 1;
                        }
                    }
                }
            }

            return data;
    	    
    	    
    	    
    	    
    	    
    	} finally {
    	    br.close();
    	}
    }
}
