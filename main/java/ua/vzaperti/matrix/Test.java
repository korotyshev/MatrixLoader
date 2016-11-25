package ua.vzaperti.matrix;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ua.vzaperti.matrix.util.ImageUtils;
import ua.vzaperti.util.Config;
import ua.vzaperti.util.VideoPanel;

public class Test extends JFrame {

	private static final long serialVersionUID = -6671518318930567262L;
//	public static JPanel contentPane;
	private ua.vzaperti.util.VideoPanel vp; 

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Test frame = new Test();
					frame.setVisible(true);
					frame.start();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void start() {
		vp.start();		
	}

	/**
	 * Create the frame.
	 */
	public Test() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		java.net.URL url = ImageUtils.class.getClassLoader().getResource(Config.getConfig().getProperty("video"));
		File f;
		try {
		  f = new File(url.toURI());
		} catch(URISyntaxException e) {
		  f = new File(url.getPath());
		}
		vp = new VideoPanel(f);

		contentPane.add(vp, BorderLayout.CENTER);
	}

}
