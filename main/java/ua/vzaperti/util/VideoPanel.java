package ua.vzaperti.util;

import java.awt.Dimension;
import java.io.File;

//import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

/**
 * Use this panel to show looped video:
 * 1. Pass video file into constructor  
 * 2. Call start() 
 */
public class VideoPanel extends CanvasImage{

	private static final long serialVersionUID = -7036188704461810558L;
	private FFmpegFrameGrabber video;
	private static boolean useVideo = true;
	private boolean stop = false;
	private long realTimeVideoStarted = 0;
	private double aspectRatio = 1;
	private File videoFile;
	private Thread thread;

	/**
	 * @param videoFile Video file path
	 */
	public VideoPanel(File videoFile) {
		this.videoFile = videoFile;
	}
	
	public void stop() {
		stop = true;
	}

	/**
	 * Start playing video. Panel should be already visible.  
	 */
	public void start() {
		
		thread = new Thread(new Runnable()
		{
			public void run()
			{
				try {
					stop = false;

					video = new FFmpegFrameGrabber(videoFile);
					video.start();
					long videoLength = video.getLengthInTime()/1000;
					long initialVideoTimeMs = 0;

					System.out.println("Video length: "+videoLength);

					int width = video.getImageWidth();
					int height = video.getImageHeight();
					aspectRatio = ((double)width)/height;
					revalidate();
					
					
					long videoTimeMs = 0;

					while (!stop) {

						try {
							Frame videoFrame = video.grabImage();

							if (useVideo) {
								videoTimeMs = video.getTimestamp()/1000;

								if (videoFrame==null) {
									if (initialVideoTimeMs>0 && videoTimeMs - initialVideoTimeMs > videoLength - 1000 ) {
										video.restart();
										realTimeVideoStarted = System.currentTimeMillis();
									}
									continue;
								} else {
									if (initialVideoTimeMs == 0) {
										initialVideoTimeMs = video.getTimestamp()/1000;
									}
								}
							}

							showImage(videoFrame);

							long deltaTime = (videoTimeMs - initialVideoTimeMs) - (System.currentTimeMillis() - realTimeVideoStarted) + 10;

							if (deltaTime > 0) {
								Thread.sleep(deltaTime);
							} 

						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (video!=null)
							video.stop();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();		
	}

	@Override
    public Dimension getPreferredSize() {
        Dimension d = this.getParent().getSize();
        double parentAspectRation = ((double)d.width) / d.height; 
        
        int w, h;
        if (parentAspectRation < aspectRatio) {
        	// video is wider that parent -> use width as a base size 
        	w = d.width; h = (int) (d.width/aspectRatio);
        } else {
        	// video is narrower that parent -> use heigh as a base size 
        	h = d.height; w = (int) (d.height*aspectRatio);
        } 
        return new Dimension(w, h);
    }
}
