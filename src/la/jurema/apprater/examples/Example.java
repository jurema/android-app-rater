package la.jurema.apprater.examples;

import la.jurema.android.app.rater.R;
import la.jurema.apprater.manager.Rate;
import android.app.Activity;
import android.os.Bundle;

public class Example extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Rate.configure(this, 0, 0,0,0, "br.com.icarros.androidapp", null, "Mensagem",
				"Vote", "Lembreme Depois", "Não votar", true);
		Rate.onCreate();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Rate.onResume();
	}
	
	@Override
	protected void onDestroy() {
		Rate.onDestroy();
		super.onDestroy();
	}
}