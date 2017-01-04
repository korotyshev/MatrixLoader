package ua.vzaperti.matrix;

import static ua.vzaperti.matrix.util.ImageUtils.getImage;
import static ua.vzaperti.matrix.util.MatrixLoaderEvents.*;
import static ua.vzaperti.matrix.util.Resolution.*;
import static ua.vzaperti.matrix.util.Images.*;

import ua.vzaperti.matrix.util.MatrixLoaderEvents;
import ua.vzaperti.util.Config;
import ua.vzaperti.util.COMByteListener;
import ua.vzaperti.util.SimpleRead;
import ua.vzaperti.matrix.Test;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import java.util.Timer;
import java.util.TimerTask;



public class MatrixLoader implements COMByteListener {	

	private static Test video;
	private static MatrixLoader window; // CONTENT for Each monitor
	
	private SimpleRead comPort;			// COM port
	
	private JFrame frame;				// MAIN frame
		
	private JLabel backgroundImg;		// Background label
	private JLabel neoStatusImg;		// CABLE connect label
	
	private JLabel textImg;
	private JLabel slides;				// COLLECTIVE labels for Disks 
	private JLabel uploadImg;
	
	private JLabel disk1CompleteImg;
	private JLabel disk2CompleteImg;	// UNIQUE labels for each Disk
	private JLabel disk3CompleteImg;
	private JLabel disk3StoppedImg;	
	
	private JLabel attackImg;			// labels for ATTACK
	private JLabel deathImg;
	
	private JProgressBar progressBar;	// PROGRESS BAR label
		
	private Timer timer;
	private TimerTask timerTask;		// COLLECTIVE timer for DISK processing
	
	private boolean disk1Complete = false;
	private boolean disk2Complete = false;		// check if DISK PROGRAM complete
	private boolean disk3Complete = false;
	
	private boolean attack = false;				// check if ATTACK started
	private boolean empPressed = false;			// check if EMP pressed to stop the Attack
	
	private boolean diskInside = false;			// check if we have DISK inside, even when Neo cable disconnect
		
	private int[] disks = {-1, -1, -1};			// array for identified DISKS
	private int diskKnown = 0;					// how many DISK already identified
	private int hiddenDisk = -1;				// when insert DISK without CABLE
	private int currentSlide = 0;				// SLIDES counter  
	private int currentDisk = 0; 				// watch after NUMBER OF DISK connected	
			
	private Color foregroundColor = new Color(43, 153, 214);	// #2b99d6
	private Color backgroundColor = new Color(7, 25, 45);		//#07192d
	
	/*********************** Enter the program ***********************/
			/****** Launch the application. *********/
	
	public static void main(String[] args) {
		// read properties file
		Config.initConfig("matrixLoader.properties"); 

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {					
					showOnScreen(0, window);	// show Matrix frame on 1st monitor
					showOnScreen(1, video);		// show Video on 2nd monitor
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/*********************** Selecting correct monitor to display the program ***********************/
	public static void showOnScreen( int screen, MatrixLoader loader)
	{
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] gs = ge.getScreenDevices();
	    
	    if( screen == 0)
	    {        
	    	window = new MatrixLoader();
			window.frame.setBounds(gs[screen].getConfigurations()[0].getBounds());
			window.frame.setVisible(true);
	    }
	    else if( screen == 1 )
	    {          	
			video.setResizable(false);
			video.setUndecorated(true);
			video.setBounds(gs[screen].getConfigurations()[0].getBounds());		// this part is NOT REALLY necessary here
			video.setVisible(true);
			video.start();
	    }
	    else
	    {
	        throw new RuntimeException( "No Screens Found" );
	    }
	}
	
	public static void showOnScreen( int screen, JFrame frame )		// Override procedure to show the VIDEO
	{
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] gs = ge.getScreenDevices();
	    
	    if( screen == 0)
	    {        
			window.frame.setBounds(gs[screen].getConfigurations()[0].getBounds());	// this part is NOT REALLY necessary here
			window.frame.setVisible(true);
	    }
	    else if( screen == 1 )
	    {          	
	    	video = new Test();
			video.setResizable(false);
			video.setUndecorated(true);	
			video.setBounds(gs[screen].getConfigurations()[0].getBounds());
			video.setVisible(true);
			video.start();
	    }
	    else
	    {
	        throw new RuntimeException( "No Screens Found" );
	    }
	}

	/*********************** Create the application ***********************/
	public MatrixLoader() {
		try {			
			initialize();
			comPort = new SimpleRead(this);								
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/***********************************************
	 * USER PART OF PROGRAM - operates what he see *
	 **********************************************/	
	
	/*********************** Initialize the contents of the frame ***********************/
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(SCREEN_X, SCREEN_Y, SCREEN_WIDTH, SCREEN_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);					// main frame
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setUndecorated(true);
				
		neoStatusImg = new JLabel("");
		neoStatusImg.setBounds(NEO_X, NEO_Y, NEO_WIDTH, NEO_HEIGHT);
		neoStatusImg.setIcon(getImage(NEO_CONNECT)); 							// NEO connected - cable
		frame.getContentPane().add(neoStatusImg);
		neoStatusImg.setVisible(false);
		
		textImg = new JLabel("");
		textImg.setBounds(SKILL_TITLE_X, SKILL_TITLE_Y, SKILL_TITLE_WIDTH, SKILL_TITLE_HEIGHT);
		frame.getContentPane().add(textImg);									// DISK program name
		textImg.setVisible(false);
				
		uploadImg = new JLabel("");		
		frame.getContentPane().add(uploadImg);									// DISK right section - upload
		uploadImg.setVisible(false);
		
		disk1CompleteImg = new JLabel("");
		disk1CompleteImg.setBounds(FIRST_SKILL_X, FIRST_SKILL_Y, FIRST_SKILL_WIDTH, FIRST_SKILL_HEIGHT);
		disk1CompleteImg.setIcon(getImage(FIRST_SKILL_COMPLETE)); 				// 1st DISK section - complete
		frame.getContentPane().add(disk1CompleteImg);
		disk1CompleteImg.setVisible(false);		
		
		disk2CompleteImg = new JLabel("");
		disk2CompleteImg.setBounds(SECOND_SKILL_X, SECOND_SKILL_Y, SECOND_SKILL_WIDTH, SECOND_SKILL_HEIGHT);
		disk2CompleteImg.setIcon(getImage(SECOND_SKILL_COMPLETE)); 				// 2nd DISK right section - complete
		frame.getContentPane().add(disk2CompleteImg);
		disk2CompleteImg.setVisible(false);
		
		disk3CompleteImg = new JLabel("");
		disk3CompleteImg.setBounds(THIRD_SKILL_X, THIRD_SKILL_Y, THIRD_SKILL_WIDTH, THIRD_SKILL_HEIGHT);
		disk3CompleteImg.setIcon(getImage(THIRD_SKILL_COMPLETE));				 // 3rd DISK right section - complete
		frame.getContentPane().add(disk3CompleteImg);
		disk3CompleteImg.setVisible(false);
		
		disk3StoppedImg = new JLabel("");
		disk3StoppedImg.setBounds(THIRD_SKILL_X, THIRD_SKILL_Y, THIRD_SKILL_WIDTH, THIRD_SKILL_HEIGHT);
		disk3StoppedImg.setIcon(getImage(THIRD_SKILL_STOPPED)); 				// 3rd DISK right section - stopped
		frame.getContentPane().add(disk3StoppedImg);
		disk3StoppedImg.setVisible(false);
		
		attackImg = new JLabel("");
		attackImg.setBounds(ATTACK_MSG_X, ATTACK_MSG_Y, ATTACK_MSG_WIDTH, ATTACK_MSG_HEIGHT);
		attackImg.setIcon(getImage(ATTACK_MESSAGE)); 							// ATTACK - red image
		frame.getContentPane().add(attackImg);
		attackImg.setVisible(false);		
		
		deathImg = new JLabel("");
		deathImg.setBounds(DEATH_X, DEATH_Y, DEATH_WIDTH, DEATH_HEIGHT);
		deathImg.setIcon(getImage(DEATH)); 										// NEO potential death image
		frame.getContentPane().add(deathImg);
		deathImg.setVisible(false);
		
		slides = new JLabel("");				
		slides.setBounds(SKILL_SCREEN_X, SKILL_SCREEN_Y, SKILL_SCREEN_WIDTH, SKILL_SCREEN_HEIGHT);
		frame.getContentPane().add(slides); 									// SLIDES
				
		progressBar = new JProgressBar();
		progressBar.setBackground(backgroundColor);
		progressBar.setForeground(foregroundColor);		
		progressBar.setBounds(PROGRESS_BAR_X, PROGRESS_BAR_Y, PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);
		progressBar.setIndeterminate(false);									// PROGRESS BAR
		progressBar.setStringPainted(false);			        
        frame.getContentPane().add(progressBar);
        progressBar.setVisible(false);   
		
		backgroundImg = new JLabel("");					
		backgroundImg.setBounds(SCREEN_X, SCREEN_Y, SCREEN_WIDTH, SCREEN_HEIGHT);
		backgroundImg.setIcon(getImage(BASE));									// BACKGROUD
		frame.getContentPane().add(backgroundImg);				
	}	
	
	/*********************** Reset program to initial state ***********************/
	private void programReset()
	{
		if(currentDisk != 0)
		{
			timerTask.cancel();
			timer.cancel();
		}
		
		for(int i = 0; i < disks.length; i++)
		{
			disks[i] = -1;	
		}
		
		disk1Complete = false;
		disk2Complete = false;
		disk3Complete = false;
		
		attack = false;
		empPressed = false;
		
		diskInside = false;
				
		diskKnown = 0;
		hiddenDisk = -1;			
		currentSlide = 0;
		currentDisk = 0; 	
		
		neoStatusImg.setVisible(false);
		textImg.setVisible(false);
		uploadImg.setVisible(false);
		disk1CompleteImg.setVisible(false);
		disk2CompleteImg.setVisible(false);	
		disk3CompleteImg.setVisible(false);
		disk3StoppedImg.setVisible(false);
		attackImg.setVisible(false);		
		deathImg.setVisible(false);
		
		progressBar.setVisible(false);
		progressBar.setValue(0);
		
		slides.setVisible(false);
		slides.setIcon(null);
	}
	
	/*********************** Matches correct pictures to appropriate Disk ***********************/
	private int diskImageProcessing(int number)
	{
		switch(number)
		{
		case 1:
			uploadImg.setBounds(FIRST_SKILL_X, FIRST_SKILL_Y, FIRST_SKILL_WIDTH, FIRST_SKILL_HEIGHT);
			uploadImg.setIcon(getImage(FIRST_SKILL_UPLOAD));
			textImg.setIcon(getImage(FIRST_SKILL_TEXT));
			return 1;			
		case 2:
			uploadImg.setBounds(SECOND_SKILL_X, SECOND_SKILL_Y, SECOND_SKILL_WIDTH, SECOND_SKILL_HEIGHT);
			uploadImg.setIcon(getImage(SECOND_SKILL_UPLOAD));
			textImg.setIcon(getImage(SECOND_SKILL_TEXT));
			return 2;
		case 3:
			uploadImg.setBounds(THIRD_SKILL_X, THIRD_SKILL_Y, THIRD_SKILL_WIDTH, THIRD_SKILL_HEIGHT);
			uploadImg.setIcon(getImage(THIRD_SKILL_UPLOAD));
			textImg.setIcon(getImage(THIRD_SKILL_TEXT));
			return 3;
		default:
			return 0;				
		}
	}	
	
	/*********************** Disk processing task ***********************/
	class DiskTimerTask extends TimerTask
	{
		public void run()
		{
			switch(diskImageProcessing(currentDisk))
			{
				case 1:
					if(!disk1CompleteImg.isVisible())
					{
						uploadImg.setVisible(true);
						progressBar.setValue(progressBar.getValue() + 1);
						slides.setIcon(getImage(FIRST_SKILL_SLIDES[currentSlide]));		//upload
						currentSlide++;
						if(currentSlide == (FIRST_SKILL_SLIDES.length))
						{
							disk1Complete = true;
							uploadImg.setVisible(false);								// complete
							disk1CompleteImg.setVisible(true);
							return;
						}
					}
					else
					{
						progressBar.setValue(progressBar.getMaximum());
						slides.setIcon(getImage(FIRST_SKILL_SLIDES[FIRST_SKILL_SLIDES.length - 1])); //insert in complete
					}
					break;
				case 2:
					if(!disk2CompleteImg.isVisible())
					{
						uploadImg.setVisible(true);
						progressBar.setValue(progressBar.getValue() + 1);
						slides.setIcon(getImage(SECOND_SKILL_SLIDES[currentSlide]));		//upload
						currentSlide++;
						if(currentSlide == (SECOND_SKILL_SLIDES.length))
						{
							disk2Complete = true;
							uploadImg.setVisible(false);									// complete
							disk2CompleteImg.setVisible(true);
							return;
						}
					}
					else
					{
						progressBar.setValue(progressBar.getMaximum());
						slides.setIcon(getImage(SECOND_SKILL_SLIDES[SECOND_SKILL_SLIDES.length - 1])); //insert in complete
					}
					break;
				case 3:					
					if(!disk3CompleteImg.isVisible() && (empPressed || currentSlide != ATTACK_SLIDE))
					{
						uploadImg.setVisible(true); 
						progressBar.setValue(progressBar.getValue() + 1);
						slides.setIcon(getImage(THIRD_SKILL_SLIDES[currentSlide]));		//upload
						currentSlide++;
						if(currentSlide == (THIRD_SKILL_SLIDES.length))
						{
							disk3Complete = true;
							uploadImg.setVisible(false);								// complete
							disk3CompleteImg.setVisible(true);
							return;
						}
					}
					else
					{
						if(currentSlide == ATTACK_SLIDE && !empPressed){
							uploadImg.setVisible(false);
							if(neoStatusImg.isVisible())
							{
								disk3StoppedImg.setVisible(true);
							}							
							attack = true;
							if(attackImg.isVisible()){
								attackImg.setVisible(false);							//attack released
							}
							else{
								attackImg.setVisible(true);
							}
						}
						if(empPressed){					
							progressBar.setValue(progressBar.getMaximum());
							slides.setIcon(getImage(THIRD_SKILL_SLIDES[THIRD_SKILL_SLIDES.length - 1])); //insert in complete					
						}
						
					}
					break;
			}
		}
	}
	
	/*********************** Disk removing procedure ***********************/
	private void removeDisk()
	{
		textImg.setVisible(false);		
		progressBar.setVisible(false);
		uploadImg.setVisible(false);
		
		if(!attack || empPressed)
		{
			currentSlide = 0;
			slides.setVisible(false);		// "IF" need to display Attack slide when attack happened 
			slides.setIcon(null);
		}	
		
		progressBar.setValue(0);		
	}

	/*********************** Cable disconnecting procedure ***********************/
	private void cableDisconnect()
	{
		if(neoStatusImg.isVisible() && (!attack || empPressed))		// If attack did not happened
		{					
			if(currentDisk != 0)
			{
				timerTask.cancel();		//stopping open threads
				timer.cancel();
			}							
			
			removeDisk();
			neoStatusImg.setVisible(false);
			disk1CompleteImg.setVisible(false);
			disk2CompleteImg.setVisible(false);
			disk3CompleteImg.setVisible(false);			
		} 
		else														// If attack happened
		{
			removeDisk();
			neoStatusImg.setVisible(false);
			disk1CompleteImg.setVisible(false);
			disk2CompleteImg.setVisible(false);
			disk3StoppedImg.setVisible(false);
		}
			
	}
	
	/*********************** Processing event taken from LOGIC part of program ***********************/
	private void processEvent(MatrixLoaderEvents event)
	{
		switch(event)
		{
			case RESET:
				programReset();
				break;
			case NEO_CONNECTED:
				neoStatusImg.setVisible(true);
				if(disk1Complete) {					
					disk1CompleteImg.setVisible(true);					
				}
				if(disk2Complete) {					
					disk2CompleteImg.setVisible(true);					
				}
				if(disk3Complete) {					
					disk3CompleteImg.setVisible(true);					
				}
				break;
			case NEO_DISCONNECTED:
				cableDisconnect();
				break;
			case DISK1_CONNECTED:				
				if(neoStatusImg.isVisible() && (!attack || empPressed))	// Cable connect and no Attack
				{
					progressBar.setMaximum(FIRST_SKILL_SLIDES.length);
					if(!disk1CompleteImg.isVisible())
					{
						progressBar.setValue(0);		//clear when insert after another disk
					}
					
					currentDisk = 1;
					timer = new Timer();
					timerTask = new DiskTimerTask();
					
					textImg.setVisible(true);
					slides.setVisible(true);
					progressBar.setVisible(true);
					
					timer.schedule(timerTask, 0, IMAGE_TIME);
				}
				break;
			case DISK1_DISCONNECTED:
				if(neoStatusImg.isVisible() && (!attack || empPressed))		// Cable connect and no Attack
				{	
					currentDisk = 0;
					timerTask.cancel();					//stopping open threads
					timer.cancel();				
					removeDisk();					
				}
				break;
			case DISK2_CONNECTED:
				if(neoStatusImg.isVisible() && (!attack || empPressed))		// Cable connect and no Attack
				{
					progressBar.setMaximum(SECOND_SKILL_SLIDES.length);
					if(!disk2CompleteImg.isVisible())
					{
						progressBar.setValue(0);		//clear when insert after another disk
					}
					
					currentDisk = 2;
					timer = new Timer();
					timerTask = new DiskTimerTask();
					
					textImg.setVisible(true);
					slides.setVisible(true);
					progressBar.setVisible(true);
					
					timer.schedule(timerTask, 0, IMAGE_TIME);
				}
				break;
			case DISK2_DISCONNECTED:
				if(neoStatusImg.isVisible() && (!attack || empPressed))		// Cable connect and no Attack
				{
					currentDisk = 0;
					timerTask.cancel();					//stopping open threads
					timer.cancel();				
					removeDisk();					
				}
				break;
			case DISK3_CONNECTED:
				if(neoStatusImg.isVisible() && (!attack || empPressed))		// Cable connect and no Attack
				{
					progressBar.setMaximum(THIRD_SKILL_SLIDES.length);
					if(!disk3CompleteImg.isVisible())
					{
						progressBar.setValue(0);		//clear when insert after another disk
					}
					
					currentDisk = 3;
					timer = new Timer();
					timerTask = new DiskTimerTask();
					
					textImg.setVisible(true);
					slides.setVisible(true);
					progressBar.setVisible(true);
					
					timer.schedule(timerTask, 0, IMAGE_TIME);
				}
				break;
			case DISK3_DISCONNECTED:
				if(neoStatusImg.isVisible() && (!attack || empPressed))		// Cable connect and no Attack
				{
					currentDisk = 0;
					timerTask.cancel();					//stopping open threads
					timer.cancel();				
					removeDisk();					
				}
				break;
			case EMP_PRESSED:				
				if(attack && !neoStatusImg.isVisible() && !empPressed)
				{
					currentDisk = 0;
					timerTask.cancel();					//stopping open threads
					timer.cancel();		
					empPressed = true;
					removeDisk();
					attackImg.setVisible(false);
					disk3StoppedImg.setVisible(false);					
				}
				else if(attack && neoStatusImg.isVisible() && !empPressed)
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
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}				
				break;
			default:
				break;					
		}
	}
	
	/****************************************************************************
	 * LOGIC PART OF PROGRAM - behind the scenes between COM port and USER part *
	 ***************************************************************************/
			
	/*********************** Check number of DISK inserted or removed ***********************/
	private int diskNumber(int value)
	{
		for(int i = 0; i < disks.length; i++)			
		{
			if(value == disks[i])		// If disk already known
			{
				return i;
			}
		}
		
		if(neoStatusImg.isVisible() && (value == 4 || value == 6 || value == 8))
		{
			disks[diskKnown] = value;	// find and record position of new disk
			diskKnown++;
			return (diskKnown - 1);
		}
		return -1;						// If COM process ID is not about disks
	}	

	/*********************** Controls insert or remove DISK process ***********************/
	private void diskIdentify(int value){		
		if(value == 4 || value == 6 || value == 8)  			// DISK inserted		
		{	
			diskInside = true;
			hiddenDisk = value;			
			switch (diskNumber(value)) {						
			case 0:									
				processEvent(DISK1_CONNECTED);
				break;
			case 1:									
				processEvent(DISK2_CONNECTED);
				break;		
			case 2:									
				processEvent(DISK3_CONNECTED);
				break;					
			default:
				break;
			}
		} else if(value == 5 || value == 7 || value == 9)		// DISK removed		
		{		
			diskInside = false;
			hiddenDisk = 0;
			switch (diskNumber(value - 1)) {					// COM process ID of disconnection always			
			case 0:												// +1 to connection ID	of the same disk			
				processEvent(DISK1_DISCONNECTED);
				break;
			case 1:				
				processEvent(DISK2_DISCONNECTED);
				break;		
			case 2:					
				processEvent(DISK3_DISCONNECTED);
				break;					
			default:
				break;
			}
		}
	}
	
	/*********************** Read value from COM port ***********************/
	@Override
	public void comByteProcess(int comByteProcessId) {		
		switch(comByteProcessId){
			case 1:				
				processEvent(RESET);
				break;
			case 2:								
				processEvent(NEO_CONNECTED);
				if(diskInside)
				{
					diskIdentify(hiddenDisk);		// if we have DISK inside  before connect the CABLE
				}
				break;
			case 3:							
				processEvent(NEO_DISCONNECTED);
				break;
			case 4:
				diskIdentify(comByteProcessId);
				break;
			case 5:					
				diskIdentify(comByteProcessId);
				break;
			case 6:					
				diskIdentify(comByteProcessId);
				break;
			case 7:					
				diskIdentify(comByteProcessId);
				break;
			case 8:					
				diskIdentify(comByteProcessId);
				break;
			case 9:					
				diskIdentify(comByteProcessId);
				break;
			case 10:				
				processEvent(EMP_PRESSED);
				break;
			default:
					break;
		}			
	}	
}

