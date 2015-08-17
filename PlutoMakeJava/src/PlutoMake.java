import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import Filter.ColorFilter;
import Filter.Darken;
import Filter.NoFilter;
import Filter.VividLight;

public class PlutoMake {
	
	public static String classDir;
	
	public static void main(String[] args) throws JSONException, IOException {
		// determine path to the class file, I will use it as current directory
		String classDirFile = PlutoMake.class.getResource("PlutoMake.class").getPath();
		classDir = classDirFile.substring(0, classDirFile.lastIndexOf("/") + 1);
		
		// get the input arguments
		String logoPath, filename;
		if (args.length < 2) {
			 logoPath = classDir + "test.jpg";
			 filename = "result.png";
		} else {
			 logoPath = args[0];
			 filename = args[1];
		}
        
		// make sure the logo image exists
        File logofile = new File(logoPath);
        if(!logofile.exists() || logofile.isDirectory()) {
            System.exit(1);
        }
	        
        // get the master.js file
        String text = readFile(classDir + "master.js");
        JSONArray files = new JSONArray(text);
        
        // loop through all active templates
        int len = files.length();
        for(int i=0; i<len; i+=1)
        {
        	JSONObject template = files.getJSONObject(i);
            if (template.getBoolean("active"))
            {
                BatchGenerateResult(
            		logoPath, 
            		template.getString("template"), 
            		template.getString("mapping"), 
            		template.getString("metadata"), 
    				template.getString("result") + filename, 
					template.getString("filter"), 
					template.getString("mask"),
            		template.getInt("x"), 
            		template.getInt("y"), 
            		template.getInt("w"),  
            		template.getInt("h")
            	);
            }
        }
	}
	
	
	
    private static void BatchGenerateResult(String logoPath, String templatePath, String mappingPath, String metadataPath, String resultPath, String filter, String maskPath, int x, int y, int w, int h) throws IOException, JSONException
    {
        ColorFilter filterobj = null;
        if (filter.equals("none")) {
        	filterobj = new NoFilter();
        } else if (filter.equals("darken")) {
        	filterobj = new Darken();
        } else if (filter.equals("vividlight")) {
        	filterobj = new VividLight();
        } else {
            System.exit(1);
        }

        String text = readFile(classDir + metadataPath);
        JSONObject metadata = new JSONObject(text);
        
        Map<Point, Point> mapping = MyJSON.ReadMapping(classDir + mappingPath);

        BufferedImage warpedimage = Exporter.GenerateWarpedLogo(logoPath, maskPath, mapping, metadata.getInt("width"), metadata.getInt("height"));
        //ImageIO.write(warpedimage, "png", new FileOutputStream(classDir + "warpedlogo.png"));
        
        Exporter.StampLogo(templatePath, resultPath, x, y, w, h, warpedimage, filterobj);

        warpedimage.flush();
    }
    
    
    private static String readFile(String path) throws IOException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String text = new String(data, "UTF-8");
        return text;
    }
}