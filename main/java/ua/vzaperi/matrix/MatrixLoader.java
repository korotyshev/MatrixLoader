package ua.vzaperi.matrix;

import static ua.vzaperi.matrix.util.ImageUtils.getImage;
import static ua.vzaperi.matrix.util.MatrixLoaderEvents.*;

import java.awt.EventQueue;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import java.util.Timer;
import java.util.TimerTask;

import ua.vzaperi.matrix.util.MatrixLoaderEvents;

public class MatrixLoader implements KeyListener {

	private JFrame frame;

	private static final String BASE_IMAGE = "NEO_interface_0.jpg"; 
	private static final String CONNECTED_IMAGE = "connected.jpg";
	
	private static final String SKILL_UPLOAD_IMAGE = "special_skills_uploading.jpg";
	private static final String SP_SKILLS_AVATAR = "jujitsu_1.jpg"; //потом замена
	private static final String SP_SKILLS_COMPLETE = "special_skills_complete.jpg";
	
	private static final String JU_JITSU_AVATAR = "ju_jitsu_avatar.jpg";
	private static final String JU_JITSU_UPLOAD = "ju_jitsu_uploading.jpg";
	private static final String JU_JITSU_COMPLETE = "ju_jitsu_complete.jpg";
	private static final String[] JU_JITSU_SLIDES = {
			"jujitsu_1.jpg", "jujitsu_2.jpg", "jujitsu_3.jpg", "jujitsu_4.jpg", "jujitsu_5.jpg",
			"jujitsu_6.jpg", "jujitsu_7.jpg", "jujitsu_8.jpg", "jujitsu_9.jpg"
			};

	private static final String ATTACK_AVATAR = "ju_jitsu_avatar.jpg";
	private static final String ATTACK_UPLOAD = "ju_jitsu_uploading.jpg";
	private static final String ATTACK_COMPLETE = "ju_jitsu_complete.jpg";
	private static final String ATTACK_SMALL_IMAGE = "attack.jpg";
	private static final String ATTACK_IMAGE = "sp_skills_attack_pic.jpg";
	
	private JLabel backImg;
	private JLabel userStatusImg;
	
	private JLabel uploadingSkillImg;
	private JLabel spSkillsCompleteImg;
	private JLabel spSkillsImg;
	
	private JLabel juJitsuImg;
	private JLabel juJitsuUploadImg;
	private JLabel juJitsuCompleteImg;
	private JLabel juJitsuSlides;
	
	private JLabel attackUploadImg;
	private JLabel attackCompleteImg;
	private JLabel attackImg;
	private JLabel attackSlideImg;
	private JLabel attackSmallImg;
	
	private JProgressBar progressBar;	
	
	private Timer timer;
	private Timer spSkillsTimer;
	private Timer aTimer;
	
	private TimerTask timerTask;
	private TimerTask spSkillsTask;
	private TimerTask aTask;
	
	private int i = 0, j = 0, k = 0;
	private int loadFinish = 0, loadFn = 0, loadAtt = 0;	// перем. тиггеры - некоторыезаменю на флаги
	private int bleak = 0;
	
	private boolean disk1 = false;
	private boolean disk2 = false;
	private boolean disk3 = false;					//флаги для отключения кабеля и атаки
	private boolean attackReleased = false;
	private boolean freeRoadDisk3 = false;
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
		userStatusImg.setIcon(getImage(CONNECTED_IMAGE)); // NEO connected - cable
		userStatusImg.setBounds(765, 564, 218, 93);
		frame.getContentPane().add(userStatusImg);
		userStatusImg.setVisible(false);
		
		spSkillsImg = new JLabel("");
		spSkillsImg.setIcon(getImage(JU_JITSU_AVATAR)); // uploading skills right section - avatar
		spSkillsImg.setBounds(779, 114, 218, 93);
		frame.getContentPane().add(spSkillsImg);
		spSkillsImg.setVisible(false);
		
		uploadingSkillImg = new JLabel("");
		uploadingSkillImg.setIcon(getImage(SKILL_UPLOAD_IMAGE)); // uploading skills right section - upload
		uploadingSkillImg.setBounds(865, 114, 218, 93);
		frame.getContentPane().add(uploadingSkillImg);
		uploadingSkillImg.setVisible(false);
		
		spSkillsCompleteImg = new JLabel("");
		spSkillsCompleteImg.setIcon(getImage(SP_SKILLS_COMPLETE)); // uploading skills right section - complete
		spSkillsCompleteImg.setBounds(865, 114, 218, 93);
		frame.getContentPane().add(spSkillsCompleteImg);
		spSkillsCompleteImg.setVisible(false);
		
		juJitsuImg = new JLabel("");
		juJitsuImg.setIcon(getImage(JU_JITSU_AVATAR)); //ju jitsu right section - avatar
		juJitsuImg.setBounds(779, 199, 218, 93);
		frame.getContentPane().add(juJitsuImg);
		juJitsuImg.setVisible(false);
		
		juJitsuUploadImg = new JLabel("");
		juJitsuUploadImg.setIcon(getImage(JU_JITSU_UPLOAD)); // ju jistsu right section - upload
		juJitsuUploadImg.setBounds(865, 199, 218, 93);
		frame.getContentPane().add(juJitsuUploadImg);
		juJitsuUploadImg.setVisible(false);
		
		juJitsuCompleteImg = new JLabel("");
		juJitsuCompleteImg.setIcon(getImage(JU_JITSU_COMPLETE)); // ju jistsu right section - complete
		juJitsuCompleteImg.setBounds(865, 199, 218, 93);
		frame.getContentPane().add(juJitsuCompleteImg);
		juJitsuCompleteImg.setVisible(false);
		
		attackImg = new JLabel("");
		attackImg.setIcon(getImage(ATTACK_AVATAR)); //attack right section - avatar
		attackImg.setBounds(779, 284, 218, 93);
		frame.getContentPane().add(attackImg);
		attackImg.setVisible(false);
		
		attackUploadImg = new JLabel("");
		attackUploadImg.setIcon(getImage(ATTACK_UPLOAD)); // attack right section - upload
		attackUploadImg.setBounds(865, 284, 218, 93);
		frame.getContentPane().add(attackUploadImg);
		attackUploadImg.setVisible(false);
		
		attackCompleteImg = new JLabel("");
		attackCompleteImg.setIcon(getImage(ATTACK_COMPLETE)); // attack right section - complete
		attackCompleteImg.setBounds(865, 284, 218, 93);
		frame.getContentPane().add(attackCompleteImg);
		attackCompleteImg.setVisible(false);
		
		attackSmallImg = new JLabel("");
		attackSmallImg.setIcon(getImage(ATTACK_SMALL_IMAGE)); //attack - red image
		attackSmallImg.setBounds(138, 384, 566, 58);
		frame.getContentPane().add(attackSmallImg);
		attackSmallImg.setVisible(false);
		
		attackSlideImg = new JLabel("");
		attackSlideImg.setIcon(getImage(ATTACK_IMAGE)); //attack - slide
		attackSlideImg.setBounds(76, 30, 690, 490);
		frame.getContentPane().add(attackSlideImg);
		attackSlideImg.setVisible(false);
		
		juJitsuSlides = new JLabel("");				//ju jitsu slides
		juJitsuSlides.setBounds(76, 30, 690, 490);
		frame.getContentPane().add(juJitsuSlides);
		
		progressBar = new JProgressBar();			//progress bar
		progressBar.setIndeterminate(false);
		progressBar.setStringPainted(false);
		progressBar.setBounds(345, 510, 401, 35);
        progressBar.setMinimum(0);
        progressBar.setMaximum(90);
        frame.getContentPane().add(progressBar);        
        progressBar.setVisible(false);
        
        spSkillsTimer = new Timer();		
		spSkillsTask = new SpSkillsTimerTask();
		
		timer = new Timer();
		timerTask = new JuJitsuTimerTask();
		
		aTimer = new Timer();
		aTask = new AttackTimerTask();
		
		backImg = new JLabel("");					//background
		backImg.setBounds(0, 0, 1024, 768);
		backImg.setIcon(getImage(BASE_IMAGE));
		frame.getContentPane().add(backImg);				
	}
	
	class AttackTimerTask extends TimerTask{
		public void run(){			
			if(progressBar.getValue() < 90 && progressBar.isVisible() && !attackCompleteImg.isVisible() && (k != 4 || freeRoadDisk3 == true)){
				attackImg.setVisible(true);
				attackUploadImg.setVisible(true);
				progressBar.setValue(progressBar.getValue()+10);
				juJitsuSlides.setIcon(getImage(JU_JITSU_SLIDES[k]));		// НЕ загружена
				k++;				
				if(k == 9){
					disk3 = true;
					attackUploadImg.setVisible(false);
					attackCompleteImg.setVisible(true);
					k = 0;
					loadAtt = 1;
				}
			}
			else{
				if(attackCompleteImg.isVisible())
				{
					attackImg.setVisible(true);
					if(progressBar.isVisible() && loadAtt != 1){
						juJitsuSlides.setIcon(getImage(JU_JITSU_SLIDES[k]));	//вставили в загруженную
						k++;
						loadAtt = 10 - k;
					}
					else{					
						k = 0;
						if(loadAtt !=1){
							juJitsuSlides.setIcon(null);				// вытащили из загруженной
						}								
					}
					attackImg.setVisible(true);							
				}
				else{
					if(progressBar.isVisible() && k == 4)
					{
						attackReleased = true;
						juJitsuSlides.setVisible(false);
						attackSlideImg.setVisible(true);						
						if(bleak == 0){
							attackSmallImg.setVisible(true);			//атака
							bleak = 1;
						}
						else{
							attackSmallImg.setVisible(false);
							bleak = 0;
						}
					}
					else
					{
						k = 0;
						bleak = 0;
						attackSlideImg.setVisible(false);
						attackSmallImg.setVisible(false);						
						attackUploadImg.setVisible(false);
						attackImg.setVisible(false);				// вытащили из НЕ загруженной
						progressBar.setValue(0);
						juJitsuSlides.setIcon(null);
						juJitsuSlides.setVisible(true);
					}		
					
				}							
			}			
		}
	}		
	
	class JuJitsuTimerTask extends TimerTask{
		public void run(){			
			if(progressBar.getValue() < 90 && progressBar.isVisible() && !juJitsuCompleteImg.isVisible()){
				juJitsuImg.setVisible(true);
				juJitsuUploadImg.setVisible(true);
				progressBar.setValue(progressBar.getValue()+10);
				juJitsuSlides.setIcon(getImage(JU_JITSU_SLIDES[i]));		// НЕ загружена
				i++;
				if(i == 9){
					disk2 = true;
					juJitsuUploadImg.setVisible(false);
					juJitsuCompleteImg.setVisible(true);
					i = 0;
					loadFinish = 1;
				}
			}
			else{
				if(juJitsuCompleteImg.isVisible())
				{
					juJitsuImg.setVisible(true);
					if(progressBar.isVisible() && loadFinish != 1){
						juJitsuSlides.setIcon(getImage(JU_JITSU_SLIDES[i]));	//вставили в загруженную
						i++;
						loadFinish = 10 - i;
					}
					else{					
						i = 0;
						if(loadFinish !=1){
							juJitsuSlides.setIcon(null);				// вытащили из загруженной
						}								
					}
					juJitsuImg.setVisible(true);							
				}
				else
				{
					i = 0;
					juJitsuUploadImg.setVisible(false);
					juJitsuImg.setVisible(false);				// вытащили из НЕ загруженной
					progressBar.setValue(0);
					juJitsuSlides.setIcon(null);
				}				
			}			
		}
	}		
	
	class SpSkillsTimerTask extends TimerTask{
		public void run(){			
			if(progressBar.getValue() < 90 && progressBar.isVisible() && !spSkillsCompleteImg.isVisible()){
				spSkillsImg.setVisible(true);
				uploadingSkillImg.setVisible(true);
				progressBar.setValue(progressBar.getValue()+10);
				juJitsuSlides.setIcon(getImage(JU_JITSU_SLIDES[j]));		// НЕ загружена
				j++;
				if(j == 9){
					disk1 = true;
					uploadingSkillImg.setVisible(false);
					spSkillsCompleteImg.setVisible(true);
					j = 0;
					loadFn = 1;
				}
			}
			else{
				if(spSkillsCompleteImg.isVisible())
				{
					spSkillsImg.setVisible(true);
					if(progressBar.isVisible() && loadFn != 1){
						juJitsuSlides.setIcon(getImage(JU_JITSU_SLIDES[j]));	//вставили в загруженную
						j++;
						loadFn = 10 - j;
					}
					else{					
						j = 0;
						if(loadFn !=1){
							juJitsuSlides.setIcon(null);				// вытащили из загруженной
						}								
					}
					spSkillsImg.setVisible(true);							
				}
				else
				{
					j = 0;
					uploadingSkillImg.setVisible(false);
					spSkillsImg.setVisible(false);				// вытащили из НЕ загруженной
					progressBar.setValue(0);
					juJitsuSlides.setIcon(null);
				}				
			}			
		}
	}		
	
	private void processEvent(MatrixLoaderEvents event) {
		switch (event) {
			case NEO_CONNECTED: 
				userStatusImg.setVisible(true);
				if(disk1){
					spSkillsImg.setVisible(true);
					spSkillsCompleteImg.setVisible(true);
				}
				if(disk2){
					juJitsuImg.setVisible(true);
					juJitsuCompleteImg.setVisible(true);
				}
				if(disk3){
					attackImg.setVisible(true);
					attackCompleteImg.setVisible(true);
				}
				break;
			case NEO_DISCONNECTED:				
				userStatusImg.setVisible(false);
				spSkillsImg.setVisible(false);
				uploadingSkillImg.setVisible(false);
				spSkillsCompleteImg.setVisible(false);
				juJitsuImg.setVisible(false);
				juJitsuUploadImg.setVisible(false);
				juJitsuCompleteImg.setVisible(false);
				attackImg.setVisible(false);
				attackUploadImg.setVisible(false);
				attackCompleteImg.setVisible(false);
				attackSmallImg.setVisible(false);
				attackSlideImg.setVisible(false);
				progressBar.setVisible(false);
				juJitsuSlides.setVisible(false);
				if(attackReleased)
				{
					freeRoadDisk3 = true;
				}
				break;
			case DISK1_CONNECTED:
				if(userStatusImg.isVisible())
				{
					juJitsuSlides.setVisible(true);
					progressBar.setVisible(true);
					spSkillsTimer.schedule(spSkillsTask, 0, 2000);
				}							
				break;
			case DISK1_DISCONNECTED:
				if(userStatusImg.isVisible()){
					loadFn = 0;
					progressBar.setVisible(false);
					spSkillsTimer.schedule(spSkillsTask, 0);
				}				
				break;
			case DISK2_CONNECTED:
				if(userStatusImg.isVisible()){
					juJitsuSlides.setVisible(true);
					progressBar.setVisible(true);
					timer.schedule(timerTask, 0, 2000);	
				}							
				break;
			case DISK2_DISCONNECTED:
				if(userStatusImg.isVisible()){
					loadFinish = 0;
					progressBar.setVisible(false);
					timer.schedule(timerTask, 0);
				}				
				break;
			case DISK3_CONNECTED:
				if(userStatusImg.isVisible()){
					juJitsuSlides.setVisible(true);
					progressBar.setVisible(true);
					aTimer.schedule(aTask, 0, 2000);			
				}					
				break;
			case DISK3_DISCONNECTED:
				if(userStatusImg.isVisible()){
					loadAtt = 0;
					progressBar.setVisible(false);
					aTimer.schedule(aTask, 0);
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
			processEvent(DISK2_CONNECTED);
			break;
		case KeyEvent.VK_5:
			processEvent(DISK3_CONNECTED);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()){
		case KeyEvent.VK_3:
			processEvent(DISK1_DISCONNECTED);
			break;
		case KeyEvent.VK_4:
			processEvent(DISK2_DISCONNECTED);
			break;
		case KeyEvent.VK_5:
			processEvent(DISK3_DISCONNECTED);
			break;
		}
	}
	
}
