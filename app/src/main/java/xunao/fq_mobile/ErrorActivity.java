package xunao.fq_mobile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import xunao.fq_mobile.util.Network;

public class ErrorActivity extends Activity {
	private MyApplication myApp;
	private Context context = this;
	private Handler handler = new Handler();

	private LinearLayout reload;
	private TextView words;
	private ProgressBar pb;

	private String url = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error);

		myApp = (MyApplication) getApplication();
		url = getIntent().getStringExtra("url");

		bindView();
		bindEvent();
	}

	@Override
	public void onResume() {
		super.onResume();
		check();
	}

	private void bindView() {
		reload = (LinearLayout) findViewById(R.id.bt_reload);
		words = (TextView) findViewById(R.id.txt_words);

		pb = (ProgressBar) findViewById(R.id.pb);
		pb.setIndeterminateDrawable(getResources().getDrawable(R.anim.loading_l));
	}

	private void bindEvent() {
		reload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				check();
			}
		});
	}

	private void check() {
		words.setText("重试中");
		new Thread() {
			@Override
			public void run() {
				int status = Network.getRespStatus(url);
				if (Network.isNetworkAvailable(context) && status != 404 && status != 500 && status != -1) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							myApp.getMa().videowebview.reload();
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							finish();
						}
					});
				} else {
					handler.post(new Runnable() {
						@Override
						public void run() {
							words.setText("请点击重试");
						}
					});
				}
			}
		}.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return true;
	}
}
