package ua.vzaperti.matrix;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ua.vzaperti.util.Config;
import ua.vzaperti.util.gui.VideoPanel;

public class ShipVideoFrame extends JFrame {

	private static final long serialVersionUID = -6671518318930567262L;
//	public static JPanel contentPane;
	private VideoPanel vp; 

	private String matrixVideo = null;
	private String sentineilVideo = null;
	private String empVideo = null;

	private Thread videoThread = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Config.initConfig("matrixLoader.properties");
					
					final ShipVideoFrame frame = new ShipVideoFrame();
					frame.setVisible(true);
					frame.showMatrix();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	public void showSentinails() {
		vp.stop();
		vp.setVideoFile(sentineilVideo);

		videoThread =  new Thread() {@Override
			public void run() {
			vp.start(true);
		}};

		videoThread.start();
	}

	public void showEmp() {
		vp.stop();
		vp.setVideoFile(empVideo);
		vp.start(false);
	}

	public void showMatrix() {
		vp.stop();
		vp.setVideoFile(matrixVideo);
		videoThread =  new Thread() {@Override
			public void run() {
			vp.start(true);
		}};

		videoThread.start();
	}

	/**
	 * Create the frame.
	 */
	public ShipVideoFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		matrixVideo = Config.getConfig().getProperty("video.matrix");
		sentineilVideo = Config.getConfig().getProperty("video.sentinels");
		empVideo = Config.getConfig().getProperty("video.emp");
		
		vp = new ua.vzaperti.util.gui.VideoPanel();
		vp.setVideoFile(matrixVideo);
		
		contentPane.add(vp, BorderLayout.CENTER);
	}

	
	
}
