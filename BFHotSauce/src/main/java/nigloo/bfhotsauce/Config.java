package nigloo.bfhotsauce;

import java.io.FileInputStream;
import java.util.Properties;

public class Config {

	public static final int FPS;
	
	public static final int DETECTION_LINE_X_MIN;
	public static final int DETECTION_LINE_X_MAX;
	public static final int DETECTION_LINE_Y;
	
	public static final int TARGET_WIDTH;
	public static final int TARGET_COLOR_R;
	public static final int TARGET_COLOR_G;
	public static final int TARGET_COLOR_B;
	public static final int TARGET_COLOR_DISTANCE;
	
	public static final int BUTTON_X;
	public static final int BUTTON_Y;

	static {
		Properties config = new Properties();
		try {
			config.load(new FileInputStream("config.properties"));

			FPS                   = Integer.parseInt( config.getProperty("fps"));
			
			DETECTION_LINE_X_MIN  = Integer.parseInt(config.getProperty("detection_line.x.min"));
			DETECTION_LINE_X_MAX  = Integer.parseInt(config.getProperty("detection_line.x.max"));
			DETECTION_LINE_Y      = Integer.parseInt(config.getProperty("detection_line.y"));

			TARGET_WIDTH          = Integer.parseInt(config.getProperty("target.width"));
			TARGET_COLOR_R        = Integer.parseInt(config.getProperty("target.color.red"));
			TARGET_COLOR_G        = Integer.parseInt(config.getProperty("target.color.green"));
			TARGET_COLOR_B        = Integer.parseInt(config.getProperty("target.color.blue"));
			TARGET_COLOR_DISTANCE = Integer.parseInt(config.getProperty("target.color.distance"));
			
			BUTTON_X              = Integer.parseInt(config.getProperty("button.x"));
			BUTTON_Y              = Integer.parseInt(config.getProperty("button.y"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private Config() {
		throw new UnsupportedOperationException();
	}
}
