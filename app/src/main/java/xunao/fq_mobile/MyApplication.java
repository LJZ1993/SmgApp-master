package xunao.fq_mobile;

import android.app.Application;

public class MyApplication extends Application {
	private MainActivity ma;

	public MainActivity getMa() {
		return ma;
	}

	public void setMa(MainActivity ma) {
		this.ma = ma;
	}
}
