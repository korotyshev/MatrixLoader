package ua.vzaperi.matrix;

import static ua.vzaperi.matrix.util.ImageUtils.getImage;
import static ua.vzaperi.matrix.util.MatrixLoaderEvents.*;

import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

import ua.vzaperi.matrix.util.MatrixLoaderEvents;

public class MatrixLoader implements KeyListener {

	private JFrame frame;

	private static final String BASE_IMAGE = "NEO_interface_0.jpg"; 
	private static final String CONNECTED_IMAGE = "connected.jpg"; 
	private JLabel backImg;
	private JLabel userStatusImg;
	
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
		
		frame.addKeyListener(this);
		
		userStatusImg = new JLabel("");
		userStatusImg.setIcon(getImage(CONNECTED_IMAGE));
		userStatusImg.setBounds(765, 564, 218, 93);
		frame.getContentPane().add(userStatusImg);
		
		backImg = new JLabel("");
		backImg.setBounds(0, 0, 1024, 768);
		
		backImg.setIcon(getImage(BASE_IMAGE));
		frame.getContentPane().add(backImg);
	}
	
	private void processEvent(MatrixLoaderEvents event) {
		switch (event) {
			case NEO_CONNECTED: 
				userStatusImg.setVisible(true);
				break;
			case NEO_DISCONNECTED: 
				userStatusImg.setVisible(false);
				break;
			default:
				// nothing
				break;
		}
	}

	////// KeyListener - only for testing!
	
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_1:
			processEvent(NEO_CONNECTED);
			break;
		case KeyEvent.VK_2:
			processEvent(NEO_DISCONNECTED);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	
}
