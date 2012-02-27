package la.jurema.android.app.rater;

import java.util.Calendar;

public class ManipuleDate {

	private static Calendar calendar = Calendar.getInstance(); 
	
	public static long getCurrentTime(){
        return calendar.getTimeInMillis();
	}
	
	public static long getTimeMoreDays(long time,int days){
		return time + getDayInMillis(days);
	}
	
	public static long getDayInMillis(int days){
		return days*24*60*60*1000;
	}
	
	public static boolean after(long dayInMillissecunds){
		return dayInMillissecunds < getCurrentTime();
	}
	
	public static String getHumanDate(){
		return calendar.getTime().toLocaleString();
	}
}
