package ua.vzaperti.matrix;

import static ua.vzaperti.matrix.util.MatrixLoaderEvents.DISC1_CONNECTED;
import static ua.vzaperti.matrix.util.MatrixLoaderEvents.DISC1_DISCONNECTED;
import static ua.vzaperti.matrix.util.MatrixLoaderEvents.DISC2_CONNECTED;
import static ua.vzaperti.matrix.util.MatrixLoaderEvents.DISC2_DISCONNECTED;
import static ua.vzaperti.matrix.util.MatrixLoaderEvents.DISC3_CONNECTED;
import static ua.vzaperti.matrix.util.MatrixLoaderEvents.DISC3_DISCONNECTED;
import static ua.vzaperti.util.ImageUtils.getImage;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.vzaperti.matrix.net.MatrixEvent;
import ua.vzaperti.matrix.util.Images;
import ua.vzaperti.matrix.util.MatrixLoaderEvents;
import ua.vzaperti.matrix.util.Resolution;
import ua.vzaperti.net.LanCommunication;
import ua.vzaperti.net.LanCommunication.MessageListener;
import ua.vzaperti.util.COMByteListener;
import ua.vzaperti.util.Config;
import ua.vzaperti.util.SimpleRead;

public class MatrixLoader implements COMByteListener, MessageListener<MatrixEvent> {

	private  LanCommunication<MatrixEvent> quest; 

	static Logger log = LoggerFactory.getLogger(MatrixLoader.class);
	private static final int IMAGE_TIME = 300;

	private JFrame frame;
	private static ShipVideoFrame videoFrame;

	private JLabel backgroundImg;		//background label
	private JLabel neoStatusImg;		//cable connect label	
	private JLabel deathImg;

	private JLabel juJitsuTextImg;
	private JLabel juJitsuUploadImg;
	private JLabel juJitsuCompleteImg;	//1st skill labels
	private JLabel juJitsuSlides;

	private JLabel defenseTextImg;
	private JLabel defenseUploadImg;
	private JLabel defenseCompleteImg;	//2nd skills labels
	private JLabel defenseSlides;

	private JLabel spSkillsTextImg;
	private JLabel spSkillsUploadImg;
	private JLabel spSkillsCompleteImg;
	private JLabel spSkillsStoppedImg;	//3rd skill labels
	private JLabel attackImg;
	private JLabel spSkillsSlides;

	private JProgressBar progressBar;	// progress bar label

	private Timer juJitsuTimer = new Timer();
	private TimerTask juJitsuTask;		//1st skill timer and task

	private Timer defenseTimer = new Timer();
	private TimerTask defenseTask;		//2nd skill timer and task

	private Timer spSkillsTimer = new Timer();
	private TimerTask spSkillsTask;		//3rd skill timer and task

	private Timer attackTimer = new Timer();
	private TimerTask attackTimerTask;

	private MatrixLoaderEvents diskMap[] = new MatrixLoaderEvents[3];

	private int i = 0;
	//private int j = 0; 				// iterators
	//private int diskProcess = 0;	// case for what disk is processing now
	//private int diskIdentifier[] = {-1, -1, -1, 2, 3, 5, 7, 9, 10};		// array to identify disk order: 1st, 2nd, 3rd, neo_cn, neo_out, disconnect	
	//private int previous = -1;
	private MatrixEvent currentDisk = MatrixEvent.NONE; 

	private Color foregroundColor = new Color(43, 153, 214);     // #2b99d6
	private Color backgroundColor = new Color(7, 25, 45);		//#07192d

	private boolean disk1 = false; 
	private boolean disk2 = false; 				//flags for cable output
	private boolean disk3 = false; 					 
	private boolean attackActive = false;		//flags for attack releasing
	private boolean cureDisk3 = false;
	//	private boolean proceed = false;

	private SimpleRead comPort;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// read properties file
		Config.initConfig("matrixLoader.properties"); 

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {			
					log.info("Start");

					GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
					GraphicsDevice[] gs = ge.getScreenDevices();

					int main = Integer.parseInt(Config.getConfig().getProperty("screen.main"));
					int secondary = Integer.parseInt(Config.getConfig().getProperty("screen.video"));

					log.info("Main window");

					MatrixLoader window = new MatrixLoader();
					window.frame.setBounds(gs[main].getConfigurations()[0].getBounds());
					window.frame.setVisible(true);

					log.info("Video window");

					videoFrame = new ShipVideoFrame();
					//frame1 = new JFrame();
					videoFrame.setResizable(false);
					videoFrame.setUndecorated(true);	
					videoFrame.setBounds(gs[secondary].getConfigurations()[0].getBounds());
					videoFrame.setVisible(true);
					videoFrame.start();
					//					showOnScreen(0, frame);
					//					showOnScreen(1, videoFrame);					
				} catch (Exception e) {
					log.error("", e);
				}
			}
		});
	}


	//	public static void showOnScreen( int screen, JFrame frame )
	//	{
	//	    if( screen == 0)
	//	    {	    	
	//	    }
	//	    else if( screen == 1 )
	//	    {
	//	    }
	//	    else
	//	    {
	//	        throw new RuntimeException( "No Screens Found" );
	//	    }
	//	}
	//
	//		public static void showOnScreen( int screen, JFrame frame )
	//	{
	//	    GraphicsEnvironment ge = GraphicsEnvironment
	//	        .getLocalGraphicsEnvironment();
	//	    GraphicsDevice[] gs = ge.getScreenDevices();
	//	    if( screen == 0)
	//	    {	    	
	//	        gs[screen].setFullScreenWindow( frame );
	//	        MatrixLoader window = new MatrixLoader();
	//			window.frame.setVisible(true);
	//	    }
	//	    else if( screen == 1 )
	//	    {
	//	    	frame1 = new JFrame();
	//			frame1.setBounds(Resolution.SCREEN_X, Resolution.SCREEN_Y, Resolution.SCREEN_WIDTH, Resolution.SCREEN_HEIGHT);
	//			frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//			frame1.getContentPane().add(Test.contentPane);
	//			frame1.setResizable(false);
	//			frame1.setUndecorated(true);
	//	        gs[screen].setFullScreenWindow(frame1);      
	//	    }
	//	    else
	//	    {
	//	        throw new RuntimeException( "No Screens Found" );
	//	    }
	//	}	

	/**
	 * Create the application.
	 */
	public MatrixLoader() {
		initialize();

		quest = new LanCommunication<>("matrixQuest"); 
		quest.addMessageListemer(this);

		try {
			comPort = new SimpleRead(this);		
		} catch (Exception e) {
			log.error("", e);
		}
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(Resolution.SCREEN_X, Resolution.SCREEN_Y, Resolution.SCREEN_WIDTH, Resolution.SCREEN_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//frame
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setUndecorated(true);

		neoStatusImg = new JLabel("");
		neoStatusImg.setBounds(Resolution.NEO_X, Resolution.NEO_Y, Resolution.NEO_WIDTH, Resolution.NEO_HEIGHT);
		neoStatusImg.setIcon(getImage(Images.NEO_CONNECTED)); 							// NEO connected - cable
		frame.getContentPane().add(neoStatusImg);
		neoStatusImg.setVisible(false);

		juJitsuTextImg = new JLabel("");
		juJitsuTextImg.setBounds(Resolution.SKILL_TITLE_X, Resolution.SKILL_TITLE_Y, Resolution.SKILL_TITLE_WIDTH, Resolution.SKILL_TITLE_HEIGHT);
		juJitsuTextImg.setIcon(getImage(Images.FIRST_SKILL_TEXT));						// ju jitsu title
		frame.getContentPane().add(juJitsuTextImg);
		juJitsuTextImg.setVisible(false);

		juJitsuUploadImg = new JLabel("");
		juJitsuUploadImg.setBounds(Resolution.FIRST_SKILL_X, Resolution.FIRST_SKILL_Y, Resolution.FIRST_SKILL_WIDTH, Resolution.FIRST_SKILL_HEIGHT);
		juJitsuUploadImg.setIcon(getImage(Images.FIRST_SKILL_UPLOAD)); 					// ju jitsu right section - upload
		frame.getContentPane().add(juJitsuUploadImg);
		juJitsuUploadImg.setVisible(false);

		juJitsuCompleteImg = new JLabel("");
		juJitsuCompleteImg.setBounds(Resolution.FIRST_SKILL_X, Resolution.FIRST_SKILL_Y, Resolution.FIRST_SKILL_WIDTH, Resolution.FIRST_SKILL_HEIGHT);
		juJitsuCompleteImg.setIcon(getImage(Images.FIRST_SKILL_COMPLETE)); 				// ju jistsu right section - complete
		frame.getContentPane().add(juJitsuCompleteImg);
		juJitsuCompleteImg.setVisible(false);		

		defenseTextImg = new JLabel("");
		defenseTextImg.setBounds(Resolution.SKILL_TITLE_X, Resolution.SKILL_TITLE_Y, Resolution.SKILL_TITLE_WIDTH, Resolution.SKILL_TITLE_HEIGHT);
		defenseTextImg.setIcon(getImage(Images.SECOND_SKILL_TEXT));						// defense title
		frame.getContentPane().add(defenseTextImg);
		defenseTextImg.setVisible(false);

		defenseUploadImg = new JLabel("");
		defenseUploadImg.setBounds(Resolution.SECOND_SKILL_X, Resolution.SECOND_SKILL_Y, Resolution.SECOND_SKILL_WIDTH, Resolution.SECOND_SKILL_HEIGHT);
		defenseUploadImg.setIcon(getImage(Images.SECOND_SKILL_UPLOAD)); 				// defense right section - upload
		frame.getContentPane().add(defenseUploadImg);
		defenseUploadImg.setVisible(false);

		defenseCompleteImg = new JLabel("");
		defenseCompleteImg.setBounds(Resolution.SECOND_SKILL_X, Resolution.SECOND_SKILL_Y, Resolution.SECOND_SKILL_WIDTH, Resolution.SECOND_SKILL_HEIGHT);
		defenseCompleteImg.setIcon(getImage(Images.SECOND_SKILL_COMPLETE)); 			// defense right section - complete
		frame.getContentPane().add(defenseCompleteImg);
		defenseCompleteImg.setVisible(false);

		spSkillsTextImg = new JLabel("");
		spSkillsTextImg.setBounds(Resolution.SKILL_TITLE_X, Resolution.SKILL_TITLE_Y, Resolution.SKILL_TITLE_WIDTH, Resolution.SKILL_TITLE_HEIGHT);
		spSkillsTextImg.setIcon(getImage(Images.THIRD_SKILL_TEXT));						// sp skills title
		frame.getContentPane().add(spSkillsTextImg);
		spSkillsTextImg.setVisible(false);

		spSkillsUploadImg = new JLabel("");
		spSkillsUploadImg.setBounds(Resolution.THIRD_SKILL_X, Resolution.THIRD_SKILL_Y, Resolution.THIRD_SKILL_WIDTH, Resolution.THIRD_SKILL_HEIGHT);
		spSkillsUploadImg.setIcon(getImage(Images.THIRD_SKILL_UPLOAD)); 				// sp skills right section - upload
		frame.getContentPane().add(spSkillsUploadImg);
		spSkillsUploadImg.setVisible(false);

		spSkillsCompleteImg = new JLabel("");
		spSkillsCompleteImg.setBounds(Resolution.THIRD_SKILL_X, Resolution.THIRD_SKILL_Y, Resolution.THIRD_SKILL_WIDTH, Resolution.THIRD_SKILL_HEIGHT);
		spSkillsCompleteImg.setIcon(getImage(Images.THIRD_SKILL_COMPLETE));				 // sp skills right section - complete
		frame.getContentPane().add(spSkillsCompleteImg);
		spSkillsCompleteImg.setVisible(false);

		spSkillsStoppedImg = new JLabel("");
		spSkillsStoppedImg.setBounds(Resolution.THIRD_SKILL_X, Resolution.THIRD_SKILL_Y, Resolution.THIRD_SKILL_WIDTH, Resolution.THIRD_SKILL_HEIGHT);
		spSkillsStoppedImg.setIcon(getImage(Images.THIRD_SKILL_STOPPED)); 				// sp skills right section - stopped
		frame.getContentPane().add(spSkillsStoppedImg);
		spSkillsStoppedImg.setVisible(false);

		attackImg = new JLabel("");
		attackImg.setBounds(Resolution.ATTACK_MSG_X, Resolution.ATTACK_MSG_Y, Resolution.ATTACK_MSG_WIDTH, Resolution.ATTACK_MSG_HEIGHT);
		attackImg.setIcon(getImage(Images.ATTACK_MESSAGE)); 							//attack - red image
		frame.getContentPane().add(attackImg);
		attackImg.setVisible(false);		

		deathImg = new JLabel("");
		deathImg.setBounds(Resolution.DEATH_X, Resolution.DEATH_Y, Resolution.DEATH_WIDTH, Resolution.DEATH_HEIGHT);
		deathImg.setIcon(getImage(Images.DEATH)); 							//death image
		frame.getContentPane().add(deathImg);
		deathImg.setVisible(false);

		juJitsuSlides = new JLabel("");				
		juJitsuSlides.setBounds(Resolution.SKILL_SCREEN_X, Resolution.SKILL_SCREEN_Y, Resolution.SKILL_SCREEN_WIDTH, Resolution.SKILL_SCREEN_HEIGHT);
		frame.getContentPane().add(juJitsuSlides); 										//ju jitsu slides

		defenseSlides = new JLabel("");				
		defenseSlides.setBounds(Resolution.SKILL_SCREEN_X, Resolution.SKILL_SCREEN_Y, Resolution.SKILL_SCREEN_WIDTH, Resolution.SKILL_SCREEN_HEIGHT);
		frame.getContentPane().add(defenseSlides); 										//defense slides		

		spSkillsSlides = new JLabel("");				
		spSkillsSlides.setBounds(Resolution.SKILL_SCREEN_X, Resolution.SKILL_SCREEN_Y, Resolution.SKILL_SCREEN_WIDTH, Resolution.SKILL_SCREEN_HEIGHT);
		frame.getContentPane().add(spSkillsSlides);									//sp skills slides		

		progressBar = new JProgressBar();
		progressBar.setBackground(backgroundColor);
		progressBar.setForeground(foregroundColor);		
		progressBar.setBounds(Resolution.PROGRESS_BAR_X, Resolution.PROGRESS_BAR_Y, Resolution.PROGRESS_BAR_WIDTH, Resolution.PROGRESS_BAR_HEIGHT);
		progressBar.setIndeterminate(false);											//progress bar 
		progressBar.setStringPainted(false);			
		progressBar.setMaximum(100);
		frame.getContentPane().add(progressBar);
		progressBar.setVisible(false);   

		backgroundImg = new JLabel("");					
		backgroundImg.setBounds(Resolution.SCREEN_X, Resolution.SCREEN_Y, Resolution.SCREEN_WIDTH, Resolution.SCREEN_HEIGHT);
		backgroundImg.setIcon(getImage(Images.BASE));									//background
		frame.getContentPane().add(backgroundImg);				
	}

	class JuJitsuTask extends TimerTask{
		public void run(){			
			if(!juJitsuCompleteImg.isVisible())
			{
				juJitsuUploadImg.setVisible(true);
				//				progressBar.setValue(progressBar.getValue() + 3);
				progressBar.setValue(100*(i+1)/Images.FIRST_SKILL_SLIDES.length);
				juJitsuSlides.setIcon(getImage(Images.FIRST_SKILL_SLIDES[i]));			//upload disk
				i++;
				if(i == 28){
					disk1 = true;
					juJitsuUploadImg.setVisible(false);
					juJitsuCompleteImg.setVisible(true);				//upload complete
					finishLoadingDisk();
					return;
				}
			}
			else{				
				progressBar.setValue(100);
				//				progressBar.setValue(84);
				juJitsuSlides.setIcon(getImage(Images.FIRST_SKILL_SLIDES[27]));		//insert into complete program				
			}
		}
	}

	private void juJitsuStop(){
		if (juJitsuTask!=null)
			juJitsuTask.cancel();		//stopping open threads
		juJitsuTask = null;

		juJitsuTextImg.setVisible(false);
		progressBar.setVisible(false);
		juJitsuSlides.setVisible(false);
		juJitsuSlides.setIcon(getImage(Images.FIRST_SKILL_SLIDES[0]));	//output from complete program
		i = 0;
		if(!juJitsuCompleteImg.isVisible())
		{
			juJitsuUploadImg.setVisible(false);			//output from uploading program
			progressBar.setValue(0);
		}						
	}

	class DefenseTask extends TimerTask{
		public void run(){			
			if(!defenseCompleteImg.isVisible())
			{
				defenseUploadImg.setVisible(true);
				//				progressBar.setValue(progressBar.getValue() + 4);
				progressBar.setValue(100*(i+1)/Images.SECOND_SKILL_SLIDES.length);
				defenseSlides.setIcon(getImage(Images.SECOND_SKILL_SLIDES[i]));			//upload disk
				i++;
				if(i == 23){
					disk2 = true;
					defenseUploadImg.setVisible(false);
					defenseCompleteImg.setVisible(true);				//upload complete
					finishLoadingDisk();
					return;
				}
			}
			else{		
				progressBar.setValue(100);
				defenseSlides.setIcon(getImage(Images.SECOND_SKILL_SLIDES[22]));		//insert into complete program							
			}
		}
	}

	private void defenseStop(){
		if (defenseTask != null) {
			defenseTask.cancel();		
			defenseTask = null;
		}

		defenseTextImg.setVisible(false);
		progressBar.setVisible(false);
		defenseSlides.setVisible(false);
		defenseSlides.setIcon(getImage(Images.SECOND_SKILL_SLIDES[0]));		//output from complete program
		i = 0;
		if(!defenseCompleteImg.isVisible())
		{
			defenseUploadImg.setVisible(false);			//output from uploading program
			progressBar.setValue(0);
		}						
	}

	class SpSkillsTask extends TimerTask{
		public void run(){
			if(!spSkillsCompleteImg.isVisible() && (i != 19 || cureDisk3))
			{
				spSkillsUploadImg.setVisible(true);
				//				progressBar.setValue(progressBar.getValue() + 4);
				progressBar.setValue(100*(i+1)/Images.THIRD_SKILL_SLIDES.length);
				spSkillsSlides.setIcon(getImage(Images.THIRD_SKILL_SLIDES[i]));			//upload disk
				i++;				
				if(i == 25){
					disk3 = true;
					spSkillsUploadImg.setVisible(false);
					spSkillsCompleteImg.setVisible(true);		//upload complete
					finishLoadingDisk();
					return;
				}
			}
			else{
				if(i == 19 && !cureDisk3){
					spSkillsUploadImg.setVisible(false);
					spSkillsStoppedImg.setVisible(true);
					attackActive = true;
					try {
						comPort.write((byte)MatrixLoaderEvents.EMP_ACTIVE.ordinal());
					} catch (Exception e) {
						log.error("", e);
					}
					attackTimerTask = new AttackTask();
					attackTimer.schedule(attackTimerTask, 0, IMAGE_TIME);

					spSkillsTask.cancel();
					spSkillsTask = null;

					if(attackImg.isVisible()){
						attackImg.setVisible(false);
					} else {
						attackImg.setVisible(true);
					}
				}
				if(cureDisk3){					
					progressBar.setValue(100);
					spSkillsSlides.setIcon(getImage(Images.THIRD_SKILL_SLIDES[24]));		//insert into complete program					
				}
			}
		}
	}

	private void spSkillsStop(){
		if (spSkillsTask != null) {
			spSkillsTask.cancel();
			spSkillsTask = null;
		}

		spSkillsTextImg.setVisible(false);
		progressBar.setVisible(false);
		spSkillsSlides.setVisible(false);
		spSkillsSlides.setIcon(getImage(Images.THIRD_SKILL_SLIDES[0]));		//output from complete program
		i = 0;
		if(!spSkillsCompleteImg.isVisible())
		{
			attackImg.setVisible(false);
			spSkillsStoppedImg.setVisible(false);		//output from uploading program
			spSkillsUploadImg.setVisible(false);
			progressBar.setValue(0);
		}						
	}	

	class AttackTask extends TimerTask{
		public void run(){
			spSkillsSlides.setVisible(true);
			spSkillsSlides.setIcon(getImage(Images.THIRD_SKILL_SLIDES[18]));
			if(attackImg.isVisible()){
				attackImg.setVisible(false);			
			} else {
				attackImg.setVisible(true);
			}
			//			proceed = true;
		}
	}

	void finishLoadingDisk() {
		if (disk1 && disk2 && disk3) {
			quest.send(MatrixEvent.OPEN_METAL_CHAIR);
			try {
				comPort.write((byte)MatrixLoaderEvents.OPEN_CAHIR_LOCK.ordinal());
			} catch (Exception e) {
				log.error("", e);
			}

		}
	}

	private void cableDisconnect(){
		//		if(neoStatusImg.isVisible()){
		if(!attackActive){
			switch(currentDisk){
			case DISK1_CONNECTED:
				juJitsuTask.cancel();
				break;
			case DISK2_CONNECTED:
				defenseTask.cancel();
				break;
			case DISK3_CONNECTED:
				spSkillsTask.cancel();
				break;
			default:
				break;
			}
		}	

		i = 0;

		neoStatusImg.setVisible(false);
		progressBar.setVisible(false);

		juJitsuTextImg.setVisible(false);
		juJitsuUploadImg.setVisible(false);
		juJitsuCompleteImg.setVisible(false);
		juJitsuSlides.setVisible(false);

		defenseTextImg.setVisible(false);
		defenseUploadImg.setVisible(false);
		defenseCompleteImg.setVisible(false);
		defenseSlides.setVisible(false);

		spSkillsTextImg.setVisible(false);
		spSkillsUploadImg.setVisible(false);
		spSkillsCompleteImg.setVisible(false);
		spSkillsStoppedImg.setVisible(false);
		spSkillsSlides.setVisible(false);
		attackImg.setVisible(false);
	}

	@Override
	public synchronized void recieve(MatrixEvent event) {
		try {
			log.info("> {}", event);

			switch (event) {
			case DISK1_CONNECTED:
			case DISK2_CONNECTED:
			case DISK3_CONNECTED:
				if (currentDisk != MatrixEvent.NONE) {
					log.warn("Dick connected: {} while another one is already active: {}", event, currentDisk);
					return;
				} else {
					currentDisk = event;
				}
				break;

			case DISK1_DISCONNECTED:
			case DISK2_DISCONNECTED:
			case DISK3_DISCONNECTED:
				currentDisk = MatrixEvent.NONE;			//processEvent(e);
				break;

			default:
				break;
			}

			switch(event) {
			case RESET:
				reset();
				break;
			case OPEN_EVERYTHING:
			case OPEN_METAL_CHAIR:
				comPort.write((byte)MatrixLoaderEvents.OPEN_CAHIR_LOCK.ordinal());
				break;

			case EMP_ENABLED:
				comPort.write((byte)MatrixLoaderEvents.EMP_ACTIVE.ordinal());
				break;
			case EMP_DISABLED:
				comPort.write((byte)MatrixLoaderEvents.EMP_PASSIVE.ordinal());
				break;

			case NEO_CONNECTED: 
				neoStatusImg.setVisible(true);

				if (!attackActive) {
					if(disk1) {					
						juJitsuCompleteImg.setVisible(true);					
					}
					if(disk2){					
						defenseCompleteImg.setVisible(true);					
					}					
					if(disk3){					
						spSkillsCompleteImg.setVisible(true);					
					}
				}
				if (currentDisk != MatrixEvent.NONE) {
					MatrixEvent e = currentDisk;
					currentDisk = MatrixEvent.NONE;
					quest.send(e);
				}
				break;
			case NEO_DISCONNECTED:
				cableDisconnect();
				break;
			case DISK1_CONNECTED:				
				if(attackActive && !cureDisk3){
					break;
				}
				if(neoStatusImg.isVisible()){
					if (juJitsuTask == null) {
						progressBar.setValue(0);		//clear when insert after another disk
						juJitsuTask = new JuJitsuTask();
						juJitsuTimer.schedule(juJitsuTask, 0, IMAGE_TIME);
						juJitsuTextImg.setVisible(true);
						juJitsuSlides.setVisible(true);
						progressBar.setVisible(true);
					}

				}							
				break;
			case DISK1_DISCONNECTED:
				if(attackActive && !cureDisk3){
					break;
				}
				if(neoStatusImg.isVisible()){
					juJitsuStop();					
				}				
				break;
			case DISK2_CONNECTED:				
				if(attackActive && !cureDisk3){
					break;
				}
				if(neoStatusImg.isVisible()){
					if (defenseTask == null) {
						progressBar.setValue(0);		//clear when insert after another disk
						defenseTask = new DefenseTask();
						defenseTimer.schedule(defenseTask, 0, IMAGE_TIME);
						defenseTextImg.setVisible(true);
						defenseSlides.setVisible(true);
						progressBar.setVisible(true);
					}

				}							
				break;
			case DISK2_DISCONNECTED:		
				if(attackActive && !cureDisk3){
					break;
				}
				if(neoStatusImg.isVisible()){
					defenseStop();
				}				
				break;
			case DISK3_CONNECTED:			
				if(attackActive && !cureDisk3){
					break;
				}
				if(neoStatusImg.isVisible()){
					if (spSkillsTask == null) {
						progressBar.setValue(0);		//clear when insert after another disk
						spSkillsTask = new SpSkillsTask();
						spSkillsTimer.schedule(spSkillsTask, 0, IMAGE_TIME);			
						spSkillsTextImg.setVisible(true);
						spSkillsSlides.setVisible(true);
						progressBar.setVisible(true);
					}
				}					
				break;
			case DISK3_DISCONNECTED:
				if(attackActive && !cureDisk3){
					break;
				}
				if(neoStatusImg.isVisible()){
					spSkillsStop();
				}				
				break;
			case EMP_FIRED:
				if(attackActive && !neoStatusImg.isVisible())
				{
					cureDisk3 = true;
					attackActive = false;
					attackTimerTask.cancel();
					spSkillsSlides.setVisible(false);
					attackImg.setVisible(false);
					try {
						comPort.write((byte)MatrixLoaderEvents.EMP_PASSIVE.ordinal());
					} catch (Exception e) {
						log.error("", e);
					}
				}
				else if(attackActive && neoStatusImg.isVisible())
				{
					for(int i = 0; i<10; i++){
						if(!deathImg.isVisible())
						{
							deathImg.setVisible(true);
						}
						else
						{
							deathImg.setVisible(false);
						}
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
							log.error("", e);
						}
					}
				}
				break;

			case INTERCOM_PRESSED:
				// TODO intercom
				break;

			default:
				// nothing
				break;
			}

		} catch (Exception e) {
			log.error("Error", e);
		}

	}

	private MatrixLoaderEvents mapDisk(MatrixLoaderEvents event){
		switch (event) {
		case DISC1_CONNECTED:
		case DISC2_CONNECTED:
		case DISC3_CONNECTED:
			if (diskMap[0] == null) {
				diskMap[0] = event;
			}
			if (diskMap[0] == event) {
				return DISC1_CONNECTED;
			}

			if (diskMap[1] == null) {
				diskMap[1] = event;
			}
			if (diskMap[1] == event) {
				return DISC2_CONNECTED;
			}

			if (diskMap[2] == null) {
				diskMap[2] = event;
			}
			if (diskMap[2] == event) {
				return DISC3_CONNECTED;
			}

			break;

		case DISC1_DISCONNECTED:
		case DISC2_DISCONNECTED:
		case DISC3_DISCONNECTED:
			switch (currentDisk){
			case DISK1_CONNECTED:
				return DISC1_DISCONNECTED;
			case DISK2_CONNECTED:
				return DISC2_DISCONNECTED;
			case DISK3_CONNECTED:
				return DISC3_DISCONNECTED;
			default:
				break;
			}
			break;

		default:
			break;
		}
		return event;
	}

	@Override
	public void comByteProcess(int i) {
		MatrixLoaderEvents e = mapDisk(getEnumValue(i));
		log.info(">"+e);

		switch (e) {

		case NEO_CONNECTED:
			quest.send(MatrixEvent.NEO_CONNECTED);
			if(currentDisk != MatrixEvent.NONE){
				quest.send(currentDisk);
			}
			break;
		case NEO_DISCONNECTED: 
			quest.send(MatrixEvent.NEO_DISCONNECTED);
			break;

		case DISC1_CONNECTED:
			quest.send(MatrixEvent.DISK1_CONNECTED);
			break;
		case DISC2_CONNECTED:
			quest.send(MatrixEvent.DISK2_CONNECTED);
			break;
		case DISC3_CONNECTED:
			quest.send(MatrixEvent.DISK3_CONNECTED);
			break;

		case DISC1_DISCONNECTED:	
			quest.send(MatrixEvent.DISK1_DISCONNECTED);
			break;
		case DISC2_DISCONNECTED:
			quest.send(MatrixEvent.DISK2_DISCONNECTED);
			break;
		case DISC3_DISCONNECTED:
			quest.send(MatrixEvent.DISK3_DISCONNECTED);
			break;

		case EMP_PRESSED: 
			quest.send(MatrixEvent.EMP_FIRED);
			break;
		case INTERCOM_BUTTON_PRESSED: 
			quest.send(MatrixEvent.INTERCOM_PRESSED);
			break;

		default:
			break;
		}
	}

	/**
	 * Convert integer value into enum value 
	 * @param i byte received from COM port
	 * @return corresponding enum value
	 */
	private MatrixLoaderEvents getEnumValue(int i) {
		for (MatrixLoaderEvents v : MatrixLoaderEvents.values()) {
			if (v.ordinal() == i) 
				return v; 
		}
		return MatrixLoaderEvents.NOP;
	}


	private void reset() {
		cureDisk3 = false;
		disk1=false;
		disk2=false;
		disk3=false;
		diskMap[0]=null;
		diskMap[1]=null;
		diskMap[2]=null;
	}
}

