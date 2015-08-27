import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import Filter.ColorFilter;
import Filter.Darken;
import Filter.NoFilter;
import Filter.VividLight;

public final class PlutoMake {
	
	public static String classDir;
	
	public static void main(String[] args) throws JSONException, IOException {
		
		// determine path to the class file, I will use it as current directory
		String classDirFile = PlutoMake.class.getResource("PlutoMake.class").getPath();
		classDir = classDirFile.substring(0, classDirFile.lastIndexOf("/") + 1);
		
		// get the input arguments
		String logoPath;
		String filename;
		String templateName;
		if (args.length < 3) {
			 logoPath = classDir + "tests/android.png";
			 filename = "result.png";
			 templateName = "coaster1";
		} else {
			 logoPath = args[0];
			 filename = args[1];
			 templateName = args[2];
		}
        
		// make sure the logo image exists
        if(!isFile(new File(logoPath))) {
            System.exit(1);
        }
        
        // get the master.js file
        String text = readFile(classDir + "master.js");
        JSONArray files = new JSONArray(text);
       
        // read the logo image
    	BufferedImage logoImage = ImageIO.read(new File(logoPath));
    	boolean worked = Process(files, templateName, filename, logoImage);
        logoImage.flush();
        
        if (!worked) {
        	System.exit(1);
        }
	}
	
	private final static boolean Process(JSONArray files, String templateName, String filename, BufferedImage logoImage) throws JSONException {
        
		// loop through all active templates
		JSONObject templatelistobj;
		JSONArray templatelist;
		JSONObject template;
		String curTemplateName;
		
        int len = files.length();
        for(int i=0; i<len; i+=1) {
        	templatelistobj = files.getJSONObject(i);
        	curTemplateName = templatelistobj.getString("id");
        	
        	if (curTemplateName.equals(templateName)) {
        		templatelist = templatelistobj.getJSONArray("angles");
        		
	        	int len2 = templatelist.length();
	        	for(int j=0; j<len2; j+=1) {
		        	template = templatelist.getJSONObject(j);
		        	
		            if (template.getBoolean("active")) {
		            	
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
							System.out.println(e.getMessage());
							return false;
						} catch (IOException e) {
							e.printStackTrace();
							System.out.println(e.getMessage());
							return false;
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println(e.getMessage());
							return false;	
						}
		                return true;
		            }
	        	}
        	}
        }
        return false;
	}
	
	
    private final static void BatchGenerateResult(BufferedImage logoImage, String templatePath, String mappingPath, String metadataPath, String resultPath, String filter, String maskPath, int x, int y, int w, int h) throws IOException, JSONException
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
        
  

        

       
        int lw = logoImage.getWidth();
        int lh = logoImage.getHeight();
        int gridw = metadata.getInt("width");
        int gridh = metadata.getInt("height");

        BufferedImage warpedimage;
        
        if (lw != gridw || lh != gridh)
        {
        	BufferedImage newlogoImage = Exporter.ResizeImage(logoImage, gridw, gridh);
        	logoImage.flush();

        	warpedimage = MyJSON.GenerateWarpedLogo(newlogoImage, classDir + maskPath, classDir + mappingPath);
        	newlogoImage.flush();
        } else {
        	
        	warpedimage = MyJSON.GenerateWarpedLogo(logoImage, classDir + maskPath, classDir + mappingPath);
        	logoImage.flush();
        }
        
     
        //ImageIO.write(warpedimage, "png", new FileOutputStream(classDir + "warpedlogo.png"));
        
        Exporter.StampLogo(templatePath, resultPath, x, y, w, h, warpedimage, filterobj);

        
        warpedimage.flush();
    }
    
    public final static Boolean isFile(File file) {
    	return file.exists() && !file.isDirectory();
    }
    
    private final static String readFile(String path) throws IOException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String text = new String(data, "UTF-8");
        return text;
    }
}
