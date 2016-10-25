package ua.vzaperti.matrix;

import static ua.vzaperti.matrix.util.ImageUtils.getImage;
import static ua.vzaperti.matrix.util.MatrixLoaderEvents.*;

import java.awt.Color;
import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import java.util.Timer;
import java.util.TimerTask;

import ua.vzaperti.util.COMByteListener;
import ua.vzaperti.util.Config;
import ua.vzaperti.util.SimpleRead;
import ua.vzaperti.matrix.util.Images;
import ua.vzaperti.matrix.util.MatrixLoaderEvents;
import ua.vzaperti.matrix.util.Resolution;

public class MatrixLoader implements COMByteListener {
	
	private static final int IMAGE_TIME = 300;

	private JFrame frame;
	
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
	
	private Timer juJitsuTimer;
	private TimerTask juJitsuTask;		//1st skill timer and task
	
	private Timer defenseTimer;
	private TimerTask defenseTask;		//2nd skill timer and task
	
	private Timer spSkillsTimer;
	private TimerTask spSkillsTask;		//3rd skill timer and task
	
	private int i = 0, j = 0; 				// iterators
	private int diskProcess = 0;	// case for what disk is processing now
	private int diskIdentifier[] = {-1, -1, -1, 2, 3, 5, 7, 9, 10};		// array to identify disk order: 1st, 2nd, 3rd, neo_cn, neo_out, disconnect	
	private int previous = -1;
	
	private Color foregroundColor = new Color(43, 153, 214);     // #2b99d6
	private Color backgroundColor = new Color(7, 25, 45);		//#07192d
	
	private boolean disk1 = false; 
	private boolean disk2 = false; 				//flags for cable output
	private boolean disk3 = false; 					 
	private boolean attackReleased = false;		//flags for attack releasing
	private boolean cureDisk3 = false;
	
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
		try {
			comPort = new SimpleRead(this);
			initialize();		
		} catch (IOException e) {
			e.printStackTrace();
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
				progressBar.setValue(progressBar.getValue() + 3);
				juJitsuSlides.setIcon(getImage(Images.FIRST_SKILL_SLIDES[i]));			//upload disk
				i++;
				if(i == 28){
					disk1 = true;
					juJitsuUploadImg.setVisible(false);
					juJitsuCompleteImg.setVisible(true);				//upload complete
					return;
				}
			}
			else{				
				progressBar.setValue(84);
				juJitsuSlides.setIcon(getImage(Images.FIRST_SKILL_SLIDES[27]));		//insert into complete program				
			}
		}
	}
	
	private void juJitsuStop(){
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
				progressBar.setValue(progressBar.getValue() + 4);
				defenseSlides.setIcon(getImage(Images.SECOND_SKILL_SLIDES[i]));			//upload disk
				i++;
				if(i == 23){
					disk2 = true;
					defenseUploadImg.setVisible(false);
					defenseCompleteImg.setVisible(true);				//upload complete
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
			if(attackReleased && !cureDisk3){
				spSkillsSlides.setIcon(getImage(Images.THIRD_SKILL_SLIDES[18]));
				if(attackImg.isVisible()){
					attackImg.setVisible(false);			//attack released
				}
				else{
					attackImg.setVisible(true);
				}				
			}else if(!spSkillsCompleteImg.isVisible() && (i != 19 || cureDisk3))
			{
				spSkillsUploadImg.setVisible(true);
				progressBar.setValue(progressBar.getValue() + 4);
				spSkillsSlides.setIcon(getImage(Images.THIRD_SKILL_SLIDES[i]));			//upload disk
				i++;				
				if(i == 25){
					disk3 = true;
					spSkillsUploadImg.setVisible(false);
					spSkillsCompleteImg.setVisible(true);		//upload complete
					return;
				}
			}
			else{
				if(i == 19 && !cureDisk3){
					spSkillsUploadImg.setVisible(false);
					spSkillsStoppedImg.setVisible(true);
					attackReleased = true;
					if(attackImg.isVisible()){
						attackImg.setVisible(false);			//attack released
					}
					else{
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
	
	private void cableDisconnect(){
		if(neoStatusImg.isVisible()){
			switch(diskProcess){
			case 1:
				juJitsuTask.cancel();
				juJitsuTimer.cancel();
				break;
			case 2:
				defenseTask.cancel();
				defenseTimer.cancel();			//closing still open threads
				break;
			case 3:
				spSkillsTask.cancel();
				spSkillsTimer.cancel();
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
	
	private void processEvent(MatrixLoaderEvents event) {
		switch (event) {
			case NEO_CONNECTED: 
				neoStatusImg.setVisible(true);
				if(disk1) {					
					juJitsuCompleteImg.setVisible(true);					
				}
				else if(diskProcess == 1) {
					processEvent(DISK1_CONNECTED);					
				}
				
				if(disk2){					
					defenseCompleteImg.setVisible(true);					
				}else if(diskProcess == 2) {
					processEvent(DISK2_CONNECTED);
					break;
				}
				
				if(disk3){					
					spSkillsCompleteImg.setVisible(true);					
				}else if(diskProcess == 3){
					processEvent(DISK3_CONNECTED);
					break;
				}				
				break;
			case NEO_DISCONNECTED:				
				cableDisconnect();
				break;
			case DISK1_CONNECTED:
				if(attackReleased && !cureDisk3){
					processEvent(DISK3_CONNECTED);					
					break;
				}
				diskProcess = 1;				
				if(neoStatusImg.isVisible()){
					progressBar.setMaximum(84);
					if(!juJitsuCompleteImg.isVisible())
					{
						progressBar.setValue(0);		//clear when insert after another disk
					}
					juJitsuTimer = new Timer();
					juJitsuTask = new JuJitsuTask();
					juJitsuTextImg.setVisible(true);
					juJitsuSlides.setVisible(true);
					progressBar.setVisible(true);
					juJitsuTimer.schedule(juJitsuTask, 0, IMAGE_TIME);
				}							
				break;
			case DISK1_DISCONNECTED:
				if(attackReleased && !cureDisk3){
					processEvent(DISK3_DISCONNECTED);
					break;
				}
				diskProcess = 0;
				if(neoStatusImg.isVisible()){					
					juJitsuTask.cancel();		//stopping open threads
					juJitsuTimer.cancel();				
					juJitsuStop();					
				}				
				break;
			case DISK2_CONNECTED:
				if(attackReleased && !cureDisk3){
					processEvent(DISK3_CONNECTED);					
					break;					
				}
				diskProcess = 2;
				if(neoStatusImg.isVisible()){
					progressBar.setMaximum(92);
					if(!defenseCompleteImg.isVisible())
					{
						progressBar.setValue(0);		//clear when insert after another disk
					}					
					defenseTimer = new Timer();
					defenseTask = new DefenseTask();
					defenseTextImg.setVisible(true);
					defenseSlides.setVisible(true);
					progressBar.setVisible(true);
					defenseTimer.schedule(defenseTask, 0, IMAGE_TIME);	
				}							
				break;
			case DISK2_DISCONNECTED:
				if(attackReleased && !cureDisk3){
					processEvent(DISK3_DISCONNECTED);
					break;
				}
				diskProcess = 0;
				if(neoStatusImg.isVisible()){
					defenseTask.cancel();		//stopping open threads
					defenseTimer.cancel();
					defenseStop();
				}				
				break;
			case DISK3_CONNECTED:				
				diskProcess = 3;
				if(neoStatusImg.isVisible()){
					progressBar.setMaximum(100);
					if(!spSkillsCompleteImg.isVisible())
					{
						progressBar.setValue(0);		//clear when insert after another disk
					}
					spSkillsTimer = new Timer();
					spSkillsTask = new SpSkillsTask();
					spSkillsTextImg.setVisible(true);
					spSkillsSlides.setVisible(true);
					progressBar.setVisible(true);
					spSkillsTimer.schedule(spSkillsTask, 0, IMAGE_TIME);			
				}					
				break;
			case DISK3_DISCONNECTED:
				diskProcess = 0;
				if(neoStatusImg.isVisible()){
					spSkillsTask.cancel();		//stopping open threads
					spSkillsTimer.cancel();
					spSkillsStop();
				}				
				break;
			case EMP_PRESSED:
				if(attackReleased && !neoStatusImg.isVisible())
				{
					cureDisk3 = true;
				}
				else if(attackReleased && neoStatusImg.isVisible())
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
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				break;
			default:
				// nothing
				break;
		}
	}

	private int diskIdentify(int value){		
		for(int k = 0; k < 9; k++)
		{
			if(value == diskIdentifier[k]){		// key already exist
				if(value == 5 && value == 6 && value == 7){
					previous = -1;
				}
				return k;
			}				
		}
		if(j < 3 && neoStatusImg.isVisible()){
			diskIdentifier[j] = value;			// add new disk
			j++;
			previous = -1;
			return j-1;				
		}
		if(previous != value){
			previous = value;
		}				
		return -1;
	}
	@Override
	public void comByteProcess(int i) {
		switch (diskIdentify(i)) {
		case 0:
			processEvent(DISK1_CONNECTED);
			break;
		case 1:
			processEvent(DISK2_CONNECTED);
			break;		
		case 2:
			processEvent(DISK3_CONNECTED);
			break;	
		case 3:
			processEvent(NEO_CONNECTED);
			if(previous != -1){				
				switch(diskIdentify(previous)) {
				case 0:
					processEvent(DISK1_CONNECTED);
					break;
				case 1:
					processEvent(DISK2_CONNECTED);
					break;		
				case 2:
					processEvent(DISK3_CONNECTED);
					break;	
				}
			}
			break;
		case 4:
			processEvent(NEO_DISCONNECTED);
			break;		
		case 5:	
			switch (diskProcess){
			case 1:
				processEvent(DISK1_DISCONNECTED);
				break;	
			case 2:
				processEvent(DISK2_DISCONNECTED);		// output the disk
				break;
			case 3:
				processEvent(DISK3_DISCONNECTED);
				break;
			default:
					break;
			}
			break;	
		case 6:
			switch (diskProcess){
			case 1:
				processEvent(DISK1_DISCONNECTED);
				break;	
			case 2:
				processEvent(DISK2_DISCONNECTED);		// output the disk
				break;
			case 3:
				processEvent(DISK3_DISCONNECTED);
				break;
			default:
					break;
			}
			break;
		case 7:
			switch (diskProcess){
			case 1:
				processEvent(DISK1_DISCONNECTED);
				break;	
			case 2:
				processEvent(DISK2_DISCONNECTED);		// output the disk
				break;
			case 3:
				processEvent(DISK3_DISCONNECTED);
				break;
			default:
					break;
			}
			break;
		case 8:
			processEvent(EMP_PRESSED);
			break;						
		default:
			break;
		}
		//System.out.println("Received" + i);		
	}	
}

