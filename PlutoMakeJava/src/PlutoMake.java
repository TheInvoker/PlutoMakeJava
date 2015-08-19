import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
	
	public static void main(String[] args) throws JSONException, IOException, InterruptedException {
		
		// determine path to the class file, I will use it as current directory
		String classDirFile = PlutoMake.class.getResource("PlutoMake.class").getPath();
		classDir = classDirFile.substring(0, classDirFile.lastIndexOf("/") + 1);
		
		// get the input arguments
		final String logoPath;
		final String filename;
		if (args.length < 2) {
			 logoPath = classDir + "tests/android.png";
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
       
        // read the logo image
    	final BufferedImage logoImage = ImageIO.read(new File(logoPath));


        
        ExecutorService es = Executors.newCachedThreadPool();
        //ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        
        
        // loop through all active templates
        int len = files.length();
        for(int i=0; i<len; i+=1) {
        	final JSONObject template = files.getJSONObject(i);
            if (template.getBoolean("active")) {
            	 es.execute(new Runnable() {
					@Override
					public void run() {
		                try {
							try {
								BatchGenerateResult(
									logoImage, 
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
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
            	 });
            }
        }
        
        es.shutdown();
        boolean finshed = es.awaitTermination(2, TimeUnit.MINUTES);
        
        logoImage.flush();
	}
	
	
	
    private static void BatchGenerateResult(BufferedImage logoImage, String templatePath, String mappingPath, String metadataPath, String resultPath, String filter, String maskPath, int x, int y, int w, int h) throws IOException, JSONException
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

        

       
        int lw = logoImage.getWidth();
        int lh = logoImage.getHeight();
        int gridw = metadata.getInt("width");
        int gridh = metadata.getInt("height");
        boolean resized = false;
        if (lw != gridw || lh != gridh)
        {
        	logoImage = Exporter.ResizeImage(logoImage, gridw, gridh);
        	resized = true;
        }
        
        
        BufferedImage warpedimage = Exporter.GenerateWarpedLogo(logoImage, maskPath, mapping);
        //ImageIO.write(warpedimage, "png", new FileOutputStream(classDir + "warpedlogo.png"));
        
        Exporter.StampLogo(templatePath, resultPath, x, y, w, h, warpedimage, filterobj);

        
        warpedimage.flush();
        if (resized) {
        	logoImage.flush();
        }
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
