package ua.vzaperi.matrix;

import java.awt.EventQueue;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MatrixLoader {

	private JFrame frame;
	private HashMap<String, ImageIcon> imageCache = new HashMap<>(); 

	private static final String BASE_IMAGE = "NEO_interface_0.jpg"; 
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MatrixLoader window = new MatrixLoader();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MatrixLoader() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(0, 0, 1024, 768);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setUndecorated(true);
		
		JLabel backImg = new JLabel("");
		backImg.setBounds(0, 0, 1024, 768);
		
//		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\korotyshev\\workspace\\MatrixLoader\\main\\resources\\NEO_interface_0.jpg"));
		backImg.setIcon(new ImageIcon(getClass().getClassLoader().getResource("NEO_interface_0.jpg")));
		frame.getContentPane().add(backImg);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setBounds(793, 415, 46, 14);
		frame.getContentPane().add(lblNewLabel);
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon getImage(String path) {
		ImageIcon ii = imageCache.get(path);
		if (ii == null) {
		    java.net.URL imgURL = getClass().getClassLoader().getResource(path);
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
