package ua.vzaperi.matrix;

import static ua.vzaperi.matrix.util.ImageUtils.getImage;
import static ua.vzaperi.matrix.util.MatrixLoaderEvents.*;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import java.util.Timer;
import java.util.TimerTask;

import ua.vzaperi.matrix.util.MatrixLoaderEvents;
import ua.vzaperi.matrix.util.Images;
import ua.vzaperi.matrix.util.Resolution;

public class MatrixLoader implements KeyListener {

	private JFrame frame;
	
	private JLabel backgroundImg;		//background label
	private JLabel neoStatusImg;		//cable connect label
	
	private JLabel juJitsuUploadImg;
	private JLabel juJitsuCompleteImg;	//1st skill labels
	private JLabel juJitsuSlides;
	
	private JLabel defenceUploadImg;
	private JLabel defenceCompleteImg;	//2nd skills labels
	private JLabel defenceSlides;
	
	private JLabel spSkillsUploadImg;
	private JLabel spSkillsCompleteImg;
	private JLabel spSkillsStoppedImg;	//3rd skill labels
	private JLabel attackImg;
	private JLabel spSkillsSlides;
	
	private JProgressBar progressBar;	// progress bar label
	
	private Timer juJitsuTimer;
	private TimerTask juJitsuTask;		//1st skill timer and task
	
	private Timer defenceTimer;
	private TimerTask defenceTask;		//2nd skill timer and task
	
	private Timer spSkillsTimer;
	private TimerTask spSkillsTask;		//3rd skill timer and task
	
	private int i = 0; 				// iterator
	private int diskProcess = 0;	// case for what disk is processing now
	
	private Color foregroundColor = new Color(99, 172, 238);	// colors for progress bar
	private Color backgroundColor = new Color(0, 23, 48);
	
	private boolean disk1 = false; 
	private boolean disk2 = false; 				//flags for cable output
	private boolean disk3 = false; 					 
	private boolean attackReleased = false;		//flags for attack releasing
	private boolean cureDisk3 = false;
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
		frame.setBounds(Resolution.SCREEN_X, Resolution.SCREEN_Y, Resolution.SCREEN_WIDTH, Resolution.SCREEN_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//frame
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setUndecorated(true);
		
		frame.addKeyListener(this);
				
		neoStatusImg = new JLabel("");
		neoStatusImg.setBounds(Resolution.NEO_X, Resolution.NEO_Y, Resolution.NEO_WIDTH, Resolution.NEO_HEIGHT);
		neoStatusImg.setIcon(getImage(Images.NEO_CONNECTED)); 							// NEO connected - cable
		frame.getContentPane().add(neoStatusImg);
		neoStatusImg.setVisible(false);		
				
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
		
		defenceUploadImg = new JLabel("");
		defenceUploadImg.setBounds(Resolution.SECOND_SKILL_X, Resolution.SECOND_SKILL_Y, Resolution.SECOND_SKILL_WIDTH, Resolution.SECOND_SKILL_HEIGHT);
		defenceUploadImg.setIcon(getImage(Images.SECOND_SKILL_UPLOAD)); 				// defense right section - upload
		frame.getContentPane().add(defenceUploadImg);
		defenceUploadImg.setVisible(false);
		
		defenceCompleteImg = new JLabel("");
		defenceCompleteImg.setBounds(Resolution.SECOND_SKILL_X, Resolution.SECOND_SKILL_Y, Resolution.SECOND_SKILL_WIDTH, Resolution.SECOND_SKILL_HEIGHT);
		defenceCompleteImg.setIcon(getImage(Images.SECOND_SKILL_COMPLETE)); 			// defense right section - complete
		frame.getContentPane().add(defenceCompleteImg);
		defenceCompleteImg.setVisible(false);
		
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
		
		juJitsuSlides = new JLabel("");				
		juJitsuSlides.setBounds(Resolution.SKILL_SCREEN_X, Resolution.SKILL_SCREEN_Y, Resolution.SKILL_SCREEN_WIDTH, Resolution.SKILL_SCREEN_HEIGHT);
		frame.getContentPane().add(juJitsuSlides); 										//ju jitsu slides
		
		defenceSlides = new JLabel("");				
		defenceSlides.setBounds(Resolution.SKILL_SCREEN_X, Resolution.SKILL_SCREEN_Y, Resolution.SKILL_SCREEN_WIDTH, Resolution.SKILL_SCREEN_HEIGHT);
		frame.getContentPane().add(defenceSlides); 										//defense slides		
		
		spSkillsSlides = new JLabel("");				
		spSkillsSlides.setBounds(Resolution.SKILL_SCREEN_X, Resolution.SKILL_SCREEN_Y, Resolution.SKILL_SCREEN_WIDTH, Resolution.SKILL_SCREEN_HEIGHT);
		frame.getContentPane().add(spSkillsSlides); 									//sp skills slides
		
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
				progressBar.setValue(progressBar.getValue() + 10);
				juJitsuSlides.setIcon(getImage(Images.FIRST_SKILL_SLIDES[i]));			//upload disk
				i++;
				if(i == 9){
					disk1 = true;
					juJitsuUploadImg.setVisible(false);
					juJitsuCompleteImg.setVisible(true);				//upload complete
					return;
				}
			}
			else{
				if(i < 9){
					juJitsuSlides.setIcon(getImage(Images.FIRST_SKILL_SLIDES[i]));		//insert into complete program
					i++;
				}
			}
		}
	}
	
	private void juJitsuStop(){
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
	
	class DefenceTask extends TimerTask{
		public void run(){			
			if(!defenceCompleteImg.isVisible())
			{
				defenceUploadImg.setVisible(true);
				progressBar.setValue(progressBar.getValue() + 10);
				defenceSlides.setIcon(getImage(Images.FIRST_SKILL_SLIDES[i]));			//upload disk
				i++;
				if(i == 9){
					disk2 = true;
					defenceUploadImg.setVisible(false);
					defenceCompleteImg.setVisible(true);				//upload complete
					return;
				}
			}
			else{
				if(i < 9){
					defenceSlides.setIcon(getImage(Images.FIRST_SKILL_SLIDES[i]));		//insert into complete program
					i++;
				}
			}
		}
	}
	
	private void defenceStop(){
		progressBar.setVisible(false);
		defenceSlides.setVisible(false);
		defenceSlides.setIcon(getImage(Images.FIRST_SKILL_SLIDES[0]));		//output from complete program
		i = 0;
		if(!defenceCompleteImg.isVisible())
		{
			defenceUploadImg.setVisible(false);			//output from uploading program
			progressBar.setValue(0);
		}						
	}
	
	class SpSkillsTask extends TimerTask{
		public void run(){			
			if(!spSkillsCompleteImg.isVisible() && (i != 19 || cureDisk3))
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
				if(i == 19){
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
				if(i < 25 && cureDisk3){
					spSkillsSlides.setIcon(getImage(Images.THIRD_SKILL_SLIDES[i]));			//insert into complete program
					i++;
				}
			}
		}
	}
	
	private void spSkillsStop(){
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
		switch(diskProcess){
		case 1:
			juJitsuTask.cancel();
			juJitsuTimer.cancel();
			break;
		case 2:
			defenceTask.cancel();
			defenceTimer.cancel();			//closing still open threads
			break;
		case 3:
			spSkillsTask.cancel();
			spSkillsTimer.cancel();
			break;
		}
		
		i = 0;
		
		neoStatusImg.setVisible(false);
		progressBar.setVisible(false);
		
		juJitsuUploadImg.setVisible(false);
		juJitsuCompleteImg.setVisible(false);
		juJitsuSlides.setVisible(false);
		
		defenceUploadImg.setVisible(false);
		defenceCompleteImg.setVisible(false);
		defenceSlides.setVisible(false);
		
		spSkillsUploadImg.setVisible(false);
		spSkillsCompleteImg.setVisible(false);
		spSkillsStoppedImg.setVisible(false);
		spSkillsSlides.setVisible(false);
		attackImg.setVisible(false);
		
		if(attackReleased)
		{
			cureDisk3 = true;		//disk 3 can be upload to full
		}	
	}
	
	private void processEvent(MatrixLoaderEvents event) {
		switch (event) {
			case NEO_CONNECTED: 
				neoStatusImg.setVisible(true);
				if(disk1){
					juJitsuCompleteImg.setVisible(true);					
				}
				if(disk2){					
					defenceCompleteImg.setVisible(true);
				}
				if(disk3){
					spSkillsCompleteImg.setVisible(true);
				}
				break;
			case NEO_DISCONNECTED:				
				cableDisconnect();
				break;
			case DISK1_CONNECTED:
				if(neoStatusImg.isVisible()){
					diskProcess = 1;
					progressBar.setMaximum(90);
					if(!juJitsuCompleteImg.isVisible())
					{
						progressBar.setValue(0);		//clear when insert after another disk
					}
					juJitsuTimer = new Timer();
					juJitsuTask = new JuJitsuTask();
					juJitsuSlides.setVisible(true);
					progressBar.setVisible(true);
					juJitsuTimer.schedule(juJitsuTask, 0, 2000);
				}							
				break;
			case DISK1_DISCONNECTED:
				if(neoStatusImg.isVisible()){
					diskProcess = 0;
					juJitsuTask.cancel();		//stopping open threads
					juJitsuTimer.cancel();				
					juJitsuStop();					
				}				
				break;
			case DISK2_CONNECTED:
				if(neoStatusImg.isVisible()){
					diskProcess = 2;
					progressBar.setMaximum(90);
					if(!defenceCompleteImg.isVisible())
					{
						progressBar.setValue(0);		//clear when insert after another disk
					}
					defenceTimer = new Timer();
					defenceTask = new DefenceTask();
					defenceSlides.setVisible(true);
					progressBar.setVisible(true);
					defenceTimer.schedule(defenceTask, 0, 2000);	
				}							
				break;
			case DISK2_DISCONNECTED:
				if(neoStatusImg.isVisible()){
					diskProcess = 0;
					defenceTask.cancel();		//stopping open threads
					defenceTimer.cancel();
					defenceStop();
				}				
				break;
			case DISK3_CONNECTED:
				if(neoStatusImg.isVisible()){
					diskProcess = 3;
					progressBar.setMaximum(100);
					if(!spSkillsCompleteImg.isVisible())
					{
						progressBar.setValue(0);		//clear when insert after another disk
					}
					spSkillsTimer = new Timer();
					spSkillsTask = new SpSkillsTask();
					spSkillsSlides.setVisible(true);
					progressBar.setVisible(true);
					spSkillsTimer.schedule(spSkillsTask, 0, 2000);			
				}					
				break;
			case DISK3_DISCONNECTED:
				if(neoStatusImg.isVisible()){
					diskProcess = 0;
					spSkillsTask.cancel();		//stopping open threads
					spSkillsTimer.cancel();
					spSkillsStop();
				}				
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
		case KeyEvent.VK_3:
			processEvent(DISK1_CONNECTED);
			break;
		case KeyEvent.VK_4:
			processEvent(DISK1_DISCONNECTED);
			break;				
		case KeyEvent.VK_5:
			processEvent(DISK2_CONNECTED);
			break;
		case KeyEvent.VK_6:
			processEvent(DISK2_DISCONNECTED);
			break;
		case KeyEvent.VK_7:
			processEvent(DISK3_CONNECTED);
			break;
		case KeyEvent.VK_8:
			processEvent(DISK3_DISCONNECTED);
			break;	
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}	
}
