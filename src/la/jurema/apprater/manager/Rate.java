package la.jurema.apprater.manager;

import la.jurema.apprate.R;
import la.jurema.apprater.utils.LogManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Rate implements DialogInterface.OnCancelListener{

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
	private static Dialog alertRate;
	
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
			
			alertRate = (title!=null?new Dialog(context):new Dialog(context,R.style.CustomDialogTheme));
			alertRate.setContentView(R.layout.app_rater_message);
			alertRate.setCancelable(true);
			alertRate.setOnCancelListener(new Rate());
			
			Button btnPositive = (Button) alertRate.findViewById(R.id.btn_vote);
			Button btnNeutral = (Button) alertRate.findViewById(R.id.btn_later);
			Button btnNegative = (Button) alertRate.findViewById(R.id.btn_never);
			TextView txtMessage = (TextView) alertRate.findViewById(R.id.txt_message);
			
			LinearLayout.LayoutParams lpInvisible = new LinearLayout.LayoutParams(0, 0);
			
			if(title!=null){
				alertRate.setTitle(title);
			}
			if(message!=null){
				txtMessage.setText(message);
			}else{
				txtMessage.setLayoutParams(lpInvisible);
			}
			if(rateText!=null){
				btnPositive.setText(rateText);
				btnPositive.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						LogManager.print(rateText+" selected");
						rateAnalyzer.resultToHistory(RateAnalyzer.TYPE_SHOW_RATE);
						if(packpageInMarket!=null){
							try{
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri.parse("market://details?id="+packpageInMarket));
								context.startActivity(intent);
							}catch (Exception e) {
								LogManager.print("Device dont have market app");
							}
						}else{
							LogManager.print("packpage is null");
						}
						alertRate.cancel();
					}
				});
			}else{
				btnPositive.setLayoutParams(lpInvisible);
			}
			if(remindLaterText!=null){
				btnNeutral.setText(remindLaterText);
				btnNeutral.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						LogManager.print(remindLaterText+" selected");
						rateAnalyzer.resultToHistory(RateAnalyzer.TYPE_SHOW_LATER);
						alertRate.cancel();
					}
				});
			}else{
				btnNeutral.setLayoutParams(lpInvisible);
			}
			if(noRateText!=null){
				btnNegative.setText(noRateText);
				btnNegative.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						LogManager.print(noRateText+" selected");
						rateAnalyzer.resultToHistory(RateAnalyzer.TYPE_SHOW_CANCEL);
						alertRate.cancel();
					}
				});
			}else{
				btnNegative.setLayoutParams(lpInvisible);
			}
			
			alertRate.show();
		}
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		LogManager.print("Canceled box");
		rateAnalyzer.resultToHistory(RateAnalyzer.TYPE_SHOW_BACK);
	}

}