package ua.vzaperti.util;

import java.io.FileReader;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
	
	Logger log = LoggerFactory.getLogger(Config.class);
	private Map<String, String> env = System.getenv();
    
	private Properties props = new Properties();
	
	private static Config instance;
	
	public static void initConfig(String filename) {
		instance = new Config(filename);
	}
	
	public static Config getConfig() {
		return instance;
	}
	
	private Config(String filename) {
		try {
			props.load(new FileReader(filename));
		} catch (Exception e) {
			log.error("Error", e);
		}
	}

	/**
	 * @return COM port ID
	 */
	public String getCOM() {
		return props.getProperty("com.port"); 
	}
	
	/**
	 * Screen ID can be configured if multiple monitores attached 
	 * @return
	 */
    public int getScreenId() {
        return Integer.parseInt(props.getProperty("screen.id")); 
    }

    /**
     * This method can check if parameter has Windows style macros referencing some environment paths and replace them with actual values   
     * @param value input parameter to be check. Path to a file most likely
     * @return the same path, but macros replaced with actual values 
     */
	@SuppressWarnings("unused")
	private String checkEnv(String value) {
    	if (value==null) return null;
    	String []str = value.split("%");
    	String result = ""; 
    	
    	for (int i=0; i<str.length; i++ ) {
    		if (i%2 == 0) {
        		result+=str[i];
    		} else {
        		result+=env.get(str[i]);
    		}
    	}
    	
    	return result;
    }

    
    
}
