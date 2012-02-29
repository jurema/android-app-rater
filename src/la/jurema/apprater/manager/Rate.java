package la.jurema.apprater.manager;

import la.jurema.apprater.utils.LogManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

public class Rate implements DialogInterface.OnCancelListener,DialogInterface.OnClickListener{

	public static Context context;
	public static String packpageInMarket;
	public static String title;
	public static String message;
	public static String rateText;
	public static String remindLaterText;
	public static String noRateText;
	public static RateAnalyzer rateAnalyzer;
	public static Integer daysInterval;
	public static Integer opensInterval;
	public static Integer daysIntervalFirst;
	public static Integer opensIntervalFirst;
	public static boolean showInSecion;
	
	/**
	 * 
	 * @param context
	 *            Context needs to open dialog box
	 * @param databaseVersion
	 *            to update database add one more, starts with 1
	 * @param daysInterval
	 *            days interval to open the dialog for not count days send -1
	 * @param opensInterval
	 *            opens interval to open the dialog for not count opens send -1
	 * @param packpageInMarket
	 *            packpage registred in market to linked the rate example
	 *            "com.jurema.moove"
	 * @param title
	 *            Title of dialog send null to dialog without title
	 * @param message
	 *            Message of dialog send null to dialog without message
	 * @param rateText
	 *            text of rate button send null to not show this option
	 * @param remindLaterText
	 *            text of remind Later button send null to not show this option
	 * @param noRateText
	 *            text of no rate button send null to not show this option
	 * @param openAfterRateOption
	 *            send true if you want open dialog even after the user select
	 *            rate option or false if never show after user select rate
	 *            option
	 * @param showLog
	 *            if you want see the logcat with tag jRate send true or send
	 *            false to disable logcat
	 * @param saveHistory
	 *            send true to save historic in sqlite default is false
	 */
	public static void configure(Context context, int daysIntervalFirst, int opensIntervalFirst,
			int daysInterval, int opensInterval,
			String packpageInMarket, String title, String message,
			String rateText, String remindLaterText, String noRateText, boolean showLog) {
		Rate.context = context;
		Rate.packpageInMarket = packpageInMarket;
		Rate.title = title;
		Rate.message = message;
		Rate.rateText = rateText;
		Rate.remindLaterText = remindLaterText;
		Rate.noRateText = noRateText;
		Rate.daysInterval = daysInterval;
		Rate.opensInterval = opensInterval;
		Rate.daysIntervalFirst = daysIntervalFirst;
		Rate.opensIntervalFirst = opensIntervalFirst;
		Rate.showInSecion = false;
		
		LogManager.setLogEnable(showLog);
		LogManager.print("Opening analyzer");
		Rate.rateAnalyzer = new RateAnalyzer(context);
	}
	
	public static void onCreate(){
		rateAnalyzer.make();
	}

	public static void onResume(){
		rateAnalyzer.make();
	}
	
	public static void onDestroy(){
		rateAnalyzer.close();
	}

	public static void showDialog() {
		if(!showInSecion){
			showInSecion = true;
			
			LogManager.print("Showing dialog");
			
			AlertDialog.Builder alertRate = new AlertDialog.Builder(context);
			if(title!=null)alertRate.setTitle(title);
			if(message!=null)alertRate.setMessage(message);
			if(rateText!=null)alertRate.setPositiveButton(rateText, new Rate());
			if(remindLaterText!=null)alertRate.setNeutralButton(remindLaterText, new Rate());
			if(noRateText!=null)alertRate.setNegativeButton(noRateText, new Rate());
			alertRate.setOnCancelListener(new Rate());
			alertRate.show();
		}
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		LogManager.print("Canceled box");
		rateAnalyzer.resultToHistory(RateAnalyzer.TYPE_SHOW_BACK);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		LogManager.print(which);
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			LogManager.print(rateText+" selected");
			rateAnalyzer.resultToHistory(RateAnalyzer.TYPE_SHOW_RATE);
			try{
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id="+packpageInMarket));
				context.startActivity(intent);
			}catch (Exception e) {
				LogManager.print("Device dont have market app");
			}
			break;
		case DialogInterface.BUTTON_NEUTRAL:
			LogManager.print(remindLaterText+" selected");
			rateAnalyzer.resultToHistory(RateAnalyzer.TYPE_SHOW_LATER);
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			rateAnalyzer.resultToHistory(RateAnalyzer.TYPE_SHOW_CANCEL);
			LogManager.print(noRateText+" selected");
			break;
		default:
			break;
		}
	}

}