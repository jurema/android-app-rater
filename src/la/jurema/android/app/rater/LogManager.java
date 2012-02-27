package la.jurema.android.app.rater;

import android.util.Log;

public class LogManager {

	private static boolean logEnable = false;
	private static String logTag = "jRate";
	
	public static void print(Object text){
        if(logEnable)Log.i(logTag, text.toString());
	}
	
	public static void setLogEnable(boolean logEnable) {
		LogManager.logEnable = logEnable;
	}
}
