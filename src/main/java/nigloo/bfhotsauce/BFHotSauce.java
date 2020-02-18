package nigloo.bfhotsauce;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.input.MouseButton;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BFHotSauce  extends Application
{
	private final Robot robot = new Robot();
	
	private Timeline autoPlayer = null;

	public static void main(String[] args)
	{
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.WARNING);

		// Don't forget to disable the parent handlers.
		logger.setUseParentHandlers(false);
		
		try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		initGlobalListeners();
		
		autoPlayer = new Timeline(new KeyFrame(
				Duration.millis(1000/Config.FPS),
				event -> playBFHotSauce()));
		autoPlayer.setCycleCount(Timeline.INDEFINITE);
	}
	
	
	
	
	private final int MAX_DISTANCE_SQ = Config.TARGET_COLOR_DISTANCE*Config.TARGET_COLOR_DISTANCE * 3;
	private long pauseUntil = 0;
	private boolean wasInPause;
	
	private void playBFHotSauce()
	{
		if (pauseUntil > System.currentTimeMillis())
			return;
		
		Point2D p = new ScreenPosition(Config.DETECTION_LINE_X_MIN, Config.DETECTION_LINE_Y).toFx();
		Point2D size = new ScreenPosition(Config.DETECTION_LINE_X_MAX - Config.DETECTION_LINE_X_MIN, 1).toFx();
		
		Image image = robot.getScreenCapture(null, Math.floor(p.getX()), Math.floor(p.getY()), Math.ceil(size.getX()), 1);
		
		/*
		image = robot.getScreenCapture(null, 0d, 0d, 100d, 100d);
		
		try {
			ScreenPosition realSize = new ScreenPosition((int)image.getWidth(), (int)image.getHeight());
			BufferedImage scaledImage = new BufferedImage(realSize.x, realSize.y, BufferedImage.TYPE_INT_RGB);
			scaledImage.getGraphics().drawImage(SwingFXUtils.fromFXImage(image, null).getScaledInstance(realSize.x, realSize.y, java.awt.Image.SCALE_DEFAULT),
					0, 0, null);
			ImageIO.write(scaledImage, "png", new File("C:\\Users\\sebas\\Desktop\\capture.png")) ;
			System.exit(0);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//*/
		
		int width = (int) image.getWidth();
		
		byte[] pixels = new byte[width * 4];
		int scanlineStride = width * 4;
		
		image.getPixelReader().getPixels(0, 0, width, 1, PixelFormat.getByteBgraInstance(), pixels, 0, scanlineStride);
		
		int xGreenBegin = -1;
		int xGreenEnd = -1;
		
		for (int i = 0 ; i < width * 4 ; i += 4)
		{
			int r = pixels[i+2] & 0xff;
			int g = pixels[i+1] & 0xff;
			int b = pixels[i+0] & 0xff;
			
			
			int sqrtDist = (Config.TARGET_COLOR_R - r) * (Config.TARGET_COLOR_R - r)
					+ (Config.TARGET_COLOR_G - g) * (Config.TARGET_COLOR_G - g)
					+ (Config.TARGET_COLOR_B - b) * (Config.TARGET_COLOR_B - b);

			boolean isTarget = sqrtDist <= MAX_DISTANCE_SQ;
			
			if (isTarget && xGreenBegin == -1)
			{
				xGreenBegin = i/4;
				//System.out.println("Begin : \t"+r+"\t"+g+"\t"+b);
				
			}
			else if (!isTarget && xGreenBegin != -1) {
				xGreenEnd = i/4;
				//System.out.println("End   : \t"+r+"\t"+g+"\t"+b);
				break;
			}
		}
		
		if (xGreenBegin == -1 || xGreenEnd == -1)
			return;
		
		int targetVisibleLenght = xGreenEnd - xGreenBegin;
		
		//System.out.println("\n"+xGreenBegin+" -> "+xGreenEnd+" : "+targetVisibleLenght);
		
		if (targetVisibleLenght <= (int)(new ScreenPosition(Config.TARGET_WIDTH, 0).toFx().getX() / 2))
		{
			if (wasInPause) {
				pauseUntil = System.currentTimeMillis() + 500;
			}
			else
			{
				ScreenPosition buttonPos = new ScreenPosition(Config.BUTTON_X, Config.BUTTON_Y);
				
				//System.out.println("Click "+buttonPos);
				
				robot.mouseMove(buttonPos.toFx());
				robot.mouseClick(MouseButton.PRIMARY);
				
				pauseUntil = System.currentTimeMillis() + 1000;
				wasInPause = true;
			}
		}
		else
			wasInPause = false;
	}
	
	private void initGlobalListeners()
	{
		GlobalScreen.addNativeKeyListener(new NativeKeyListener()
		{
			@Override public void nativeKeyTyped(NativeKeyEvent nativeEvent) {}
			@Override public void nativeKeyReleased(NativeKeyEvent nativeEvent) {}
			
			@Override
			public void nativeKeyPressed(NativeKeyEvent nativeEvent)
			{
				if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_ESCAPE)
					System.exit(0);
				else if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_1)
					autoPlayer.stop();
				else if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_2)
					autoPlayer.playFromStart();
			}
		});
	}
}
