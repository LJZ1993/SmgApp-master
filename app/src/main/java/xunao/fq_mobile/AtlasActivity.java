package xunao.fq_mobile;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xunao.fq_mobile.util.Image;
import xunao.fq_mobile.util.Out;
import xunao.photoView.PhotoViewAttacher;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AtlasActivity extends Activity {
	private Context context = this;

	private ViewPager viewpager;
	private ImageView savepic;
	private TextView showpage;
	private Handler handler=new Handler();
	private int num, index = 1;
	private ArrayList<String> listS = new ArrayList<String>();
	private List<Bitmap> listB = new ArrayList<Bitmap>();
	private List<ImageView> imvarray=new ArrayList<ImageView>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentView());

		Intent intent = getIntent();
		String data = intent.getStringExtra("pics");
		try {
			JSONObject jo = new JSONObject(data);
			index = jo.getInt("index");
			JSONArray ja = new JSONArray(jo.getString("src"));
			int count = ja.length();
			for (int i = 0; i < count; i++) {
				String tmp = ja.get(i).toString();
				listS.add(tmp);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		index ++;
		bindView();
		initData();
		bindEvent();
	}

	private int getContentView() {
		return R.layout.activity_atlas;
	}

	private void bindView() {
		viewpager = (ViewPager) findViewById(R.id.viewpager);
		savepic = (ImageView) findViewById(R.id.savepic);
		showpage = (TextView) findViewById(R.id.showpage);
	}

	private void initData() {
		new Thread(){
			@Override
			public void run() {
				Bitmap def = Image.getBitmap(context, R.drawable.default_shop);
				num = listS.size();
				showpage.setText(index + "/" + num);
				final List<View> vs = new ArrayList<View>();
				for (int i = 0; i < num; i++) {
					final ImageView iv = new ImageView(context);
					iv.setImageBitmap(def);
					Bitmap bm = Image.getBitmap(context, listS.get(i), 2);
					if (bm != null) {
						listB.add(bm);
						iv.setImageBitmap(bm);
					}
					handler.post(new Runnable(){
						@Override
						public void run() {
							new PhotoViewAttacher(iv, context);
							vs.add(iv);
						}
					});
				}
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						viewpager.setAdapter(new ViewAdapter(vs));
						viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
							@Override
							public void onPageSelected(int arg0) {
								showpage.setText((arg0 + 1) + "/" + num);
								index = arg0;
							}
				
							@Override
							public void onPageScrolled(int arg0, float arg1, int arg2) {
				
							}
				
							@Override
							public void onPageScrollStateChanged(int arg0) {
				
							}
						});
						viewpager.setCurrentItem(index - 1);
					}
				});
				
			}
				
		}.start();
	}

	private void bindEvent() {
		savepic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = listS.get(index);
				if (!Image.saveImage2Project(context, url, 1)) {
					Out.showToast(context, "保存失败，请稍后再试");
				}
			}
		});
	}

	public class ViewAdapter extends PagerAdapter {
		private List<View> views = null;
		private float rate = 1;

		public ViewAdapter(List<View> views) {
			this.views = views;
		}

		public ViewAdapter(List<View> views, float rate) {
			this.views = views;
			this.rate = rate;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(views.get(arg1), 0);
			return views.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public float getPageWidth(int position) {
			return rate;
		}
	}
}
