package ua.vzaperti.matrix.util;

import java.util.HashMap;

import javax.swing.ImageIcon;

public class ImageUtils {
	private static HashMap<String, ImageIcon> imageCache = new HashMap<>(); 

	/** Returns an ImageIcon, or null if the path was invalid. */
	public static final ImageIcon getImage(String path) {
//		if (imageCache == null) imageCache 
		ImageIcon ii = imageCache.get(path);
		if (ii == null) {
		    java.net.URL imgURL = ImageUtils.class.getClassLoader().getResource(path);
		    if (imgURL != null) {
		    	ii = new ImageIcon(imgURL, "");
		    	imageCache.put(path, ii);
		    } else {
		        System.err.println("Couldn't find file: " + path);
		    }
		}
		return ii;
	}
	
}
