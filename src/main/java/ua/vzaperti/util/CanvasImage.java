package ua.vzaperti.util;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_ProfileRGB;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

/**
 * Make sure OpenGL or XRender is enabled to get low latency, something like
 *      export _JAVA_OPTIONS=-Dsun.java2d.opengl=True
 *      export _JAVA_OPTIONS=-Dsun.java2d.xrender=True
 */
public class CanvasImage extends Canvas {

	private static final long serialVersionUID = -7344865276627753882L;

	public static class CanvasImageException extends java.lang.Exception {
		private static final long serialVersionUID = -8020670700292656996L;
		public CanvasImageException(String message) { super(message); }
        public CanvasImageException(String message, Throwable cause) { super(message, cause); }
    }

    public static String[] getScreenDescriptions() {
        GraphicsDevice[] screens = getScreenDevices();
        String[] descriptions = new String[screens.length];
        for (int i = 0; i < screens.length; i++) {
            descriptions[i] = screens[i].getIDstring();
        }
        return descriptions;
    }
    public static DisplayMode getDisplayMode(int screenNumber) {
        GraphicsDevice[] screens = getScreenDevices();
        if (screenNumber >= 0 && screenNumber < screens.length) {
            return screens[screenNumber].getDisplayMode();
        } else {
            return null;
        }
    }
    public static double getGamma(int screenNumber) {
        GraphicsDevice[] screens = getScreenDevices();
        if (screenNumber >= 0 && screenNumber < screens.length) {
            return getGamma(screens[screenNumber]);
        } else {
            return 0.0;
        }
    }
    public static double getDefaultGamma() {
        return getGamma(getDefaultScreenDevice());
    }

    public static double getGamma(GraphicsDevice screen) {
        ColorSpace cs = screen.getDefaultConfiguration().getColorModel().getColorSpace();
        if (cs.isCS_sRGB()) {
            return 2.2;
        } else {
            try {
                return ((ICC_ProfileRGB)((ICC_ColorSpace)cs).getProfile()).getGamma(0);
            } catch (RuntimeException e) { }
        }
        return 0.0;
    }
    public static GraphicsDevice getScreenDevice(int screenNumber) throws CanvasImageException {
        GraphicsDevice[] screens = getScreenDevices();
        if (screenNumber >= screens.length) {
            throw new CanvasImageException("CanvasFrame Error: Screen number " + screenNumber + " not found. " +
                                "There are only " + screens.length + " screens.");
        }
        return screens[screenNumber];//.getDefaultConfiguration();
    }
    public static GraphicsDevice[] getScreenDevices() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    }
    public static GraphicsDevice getDefaultScreenDevice() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    }

    public CanvasImage() {
//        this.setSize(10,10); // mac bug
        needInitialResize = true;
    }

//    private void init(final DisplayMode displayMode, final double gamma) {
//        Runnable r = new Runnable() { public void run() {
//            KeyboardFocusManager.getCurrentKeyboardFocusManager().
//                    addKeyEventDispatcher(keyEventDispatch);
//
//            GraphicsDevice gd = getGraphicsConfiguration().getDevice();
//            DisplayMode d = gd.getDisplayMode(), d2 = null;
//            if (displayMode != null && d != null) {
//                int w = displayMode.getWidth();
//                int h = displayMode.getHeight();
//                int b = displayMode.getBitDepth();
//                int r = displayMode.getRefreshRate();
//                d2 = new DisplayMode(w > 0 ? w : d.getWidth(),    h > 0 ? h : d.getHeight(),
//                                     b > 0 ? b : d.getBitDepth(), r > 0 ? r : d.getRefreshRate());
//            }
//            if (d2 != null && !d2.equals(d)) {
//                gd.setDisplayMode(d2);
//            }
//            double g = gamma == 0.0 ? getGamma(gd) : gamma;
//            inverseGamma = g == 0.0 ? 1.0 : 1.0/g;

            // Must be called after the fullscreen stuff, but before
            // getting our BufferStrategy or even creating our Canvas
//            setVisible(true);

//            initCanvas(displayMode, gamma);
//        }};

//        if (EventQueue.isDispatchThread()) {
//            r.run();
//        } else {
//            try {
//                EventQueue.invokeAndWait(r);
//            } catch (java.lang.Exception ex) { }
//        }
//    }


    @Override 
    public void update(Graphics g) {
        paint(g);
    }
    
    @Override 
    public void paint(Graphics g) {
        // Calling BufferStrategy.show() here sometimes throws
        // NullPointerException or IllegalStateException,
        // but otherwise seems to work fine.
        try {
            if (getWidth() <= 0 || getHeight() <= 0) {
                return;
            }
            BufferStrategy strategy = getBufferStrategy();
            do {
                do {
                    g = strategy.getDrawGraphics();
                    if (color != null) {
                        g.setColor(color);
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                    if (image != null) {
                        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
                    }
                    if (buffer != null) {
                        g.drawImage(buffer, 0, 0, getWidth(), getHeight(), null);
                    }
                    g.dispose();
                } while (strategy.contentsRestored());
                strategy.show();
            } while (strategy.contentsLost());
        } catch (NullPointerException e) {
        } catch (IllegalStateException e) { }
    }
    
    
    // used for example as debugging console...
//    public static CanvasFrame global = null;

    // Latency is about 60 ms on Metacity and Windows XP, and 90 ms on Compiz Fusion,
    // but we set the default to twice as much to take into account the roundtrip
    // camera latency as well, just to be sure
    public static final long DEFAULT_LATENCY = 200;
    private long latency = DEFAULT_LATENCY;

//    private KeyEvent keyEvent = null;
//    private KeyEventDispatcher keyEventDispatch = new KeyEventDispatcher() {
//        public boolean dispatchKeyEvent(KeyEvent e) {
//            if (e.getID() == KeyEvent.KEY_PRESSED) {
//                synchronized (CanvasFrame.this) {
//                    keyEvent = e;
//                    CanvasFrame.this.notify();
//                }
//            }
//            return false;
//        }
//    };

//    protected Canvas canvas = null;
    protected boolean needInitialResize = false;
    protected double initialScale = 1.0;
    protected double inverseGamma = 1.0;
    private Color color = null;
    private Image image = null;
    private BufferedImage buffer = null;
    private Java2DFrameConverter converter = new Java2DFrameConverter();

    public long getLatency() {
        // if there exists some way to estimate the latency in real time,
        // add it here
        return latency;
    }
    public void setLatency(long latency) {
        this.latency = latency;
    }
    public void waitLatency() throws InterruptedException {
        Thread.sleep(getLatency());
    }

//    @Override
//    public void setSize(final int width, final int height) {
//        Dimension d = getSize();
//        if (d.width == width && d.height == height) {
//            return;
//        }
//
//        Runnable r = new Runnable() { public void run() {
//            // There is apparently a bug in Java code for Linux, and what happens goes like this:
//            // 1. Canvas gets resized, checks the visible area (has not changed) and updates
//            // BufferStrategy with the same size. 2. pack() resizes the frame and changes
//            // the visible area 3. We call Canvas.setSize() with different dimensions, to make
//            // it check the visible area and reallocate the BufferStrategy almost correctly
//            // 4. Finally, we resize the Canvas to the desired size... phew!
////            setExtendedState(NORMAL); // force unmaximization
//            setSize(width, height);
//            setSize(width+1, height+1);
//            setSize(width, height);
//            needInitialResize = false;
//        }};
//
//        if (EventQueue.isDispatchThread()) {
//            r.run();
//        } else {
//            try {
//                EventQueue.invokeAndWait(r);
//            } catch (java.lang.Exception ex) { }
//        }
//    }

    public double getCanvasScale() {
        return initialScale;
    }
    public void setCanvasScale(double initialScale) {
        this.initialScale = initialScale;
        this.needInitialResize = true;
    }

    public Graphics2D createGraphics() {
        if (buffer == null || buffer.getWidth() != getWidth() || buffer.getHeight() != getHeight()) {
            BufferedImage newbuffer = getGraphicsConfiguration().createCompatibleImage(
                    getWidth(), getHeight(), Transparency.TRANSLUCENT);
            if (buffer != null) {
                Graphics g = newbuffer.getGraphics();
                g.drawImage(buffer, 0, 0, null);
                g.dispose();
            }
            buffer = newbuffer;
        }
        return buffer.createGraphics();
    }
    
    public void releaseGraphics(Graphics2D g) {
        g.dispose();
        paint(null);
    }

    public void showColor(Color color) {
        this.color = color;
        this.image = null;
        paint(null);
    }

    // Java2D will do gamma correction for TYPE_CUSTOM BufferedImage, but
    // not for the standard types, so we need to do it manually.
    public void showImage(Frame image) {
        showImage(image, false);
    }
    
    public void showImage(Frame image, boolean flipChannels) {
        showImage(converter.getBufferedImage(image, Java2DFrameConverter.getBufferedImageType(image) ==
                BufferedImage.TYPE_CUSTOM ? 1.0 : inverseGamma, flipChannels, null));
    }
    
    public void showImage(Image image) {
        if (image == null) {
            return;
        } else if (needInitialResize) {
        	needInitialResize = false;
            setVisible(true);
            createBufferStrategy(2);
            int w = (int)Math.round(image.getWidth (null)*initialScale);
            int h = (int)Math.round(image.getHeight(null)*initialScale);
            setPreferredSize(new Dimension(w, h));
        }
        this.color = null;
        this.image = image;
        paint(null);
    }
    
//    public static Mat bufferedImageToMat(BufferedImage bi) {
//    	  Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
//    	  byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
//    	  mat.put(0, 0, data);
//    	  return mat;
//    	}

//    // This should not be called from the event dispatch thread (EDT),
//    // but if it is, it should not totally crash... In the worst case,
//    // it will simply timeout waiting for the moved events.
//    public static void tile(final CanvasFrame[] frames) {
//
//        class MovedListener extends ComponentAdapter {
//            boolean moved = false;
//            @Override public void componentMoved(ComponentEvent e) {
//                moved = true;
//                Component c = e.getComponent();
//                synchronized (c) {
//                    c.notify();
//                }
//            }
//        }
//        final MovedListener movedListener = new MovedListener();
//
//        // layout the canvas frames for the cameras in tiles
//        int canvasCols = (int)Math.round(Math.sqrt(frames.length));
//        if (canvasCols*canvasCols < frames.length) {
//            // if we don't get a square, favor horizontal layouts
//            // since screens are usually wider than cameras...
//            // and we also have title bars, tasks bar, menus, etc that
//            // takes up vertical space
//            canvasCols++;
//        }
//        int canvasX = 0, canvasY = 0;
//        int canvasMaxY = 0;
//        for (int i = 0; i < frames.length; i++) {
//            final int n = i;
//            final int x = canvasX;
//            final int y = canvasY;
//            try {
//                movedListener.moved = false;
//                EventQueue.invokeLater(new Runnable() {
//                    public void run() {
//                        frames[n].addComponentListener(movedListener);
//                        frames[n].setLocation(x, y);
//                    }
//                });
//                int count = 0;
//                while (!movedListener.moved && count < 5) {
//                    // wait until the window manager actually places our window...
//                    // wait a maximum of 500 ms since this does not work if
//                    // we are on the event dispatch thread. also some window
//                    // managers like Windows do not always send us the event...
//                    synchronized (frames[n]) {
//                        frames[n].wait(100);
//                    }
//                    count++;
//                }
//                EventQueue.invokeLater(new Runnable() {
//                    public void run() {
//                        frames[n].removeComponentListener(movedListener);
//                    }
//                });
//            } catch (java.lang.Exception ex) { }
//            canvasX = frames[i].getX()+frames[i].getWidth();
//            canvasMaxY = Math.max(canvasMaxY, frames[i].getY()+frames[i].getHeight());
//            if ((i+1)%canvasCols == 0) {
//                canvasX = 0;
//                canvasY = canvasMaxY;
//            }
//        }
//    }
}
