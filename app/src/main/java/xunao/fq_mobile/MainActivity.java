package xunao.fq_mobile;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.common.SocializeConstants;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xunao.gr.customActivity.CustomPhotoActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import xunao.fq_mobile.util.Data;

/**
 * @author tanwenwei
 */
@SuppressLint({ "InflateParams", "DefaultLocale", "SetJavaScriptEnabled", "SdCardPath" })
public class MainActivity extends CustomPhotoActivity {
	private MyApplication myApp;
	private Context context = this;

	protected UMSocialService mController;

	private FrameLayout videoview = null;// 全屏时视频加载view
	private RelativeLayout btShare;
	private Button videolandport;
	public WebView videowebview;
	private Boolean islandport = true;// true表示此时是竖屏，false表示此时横屏。
	private View xCustomView = null;
	private Context myContext = MainActivity.this;
	private xWebChromeClient xwebchromeclient;
	private String url = "http://180.166.160.17:10000/index.php?r=smgUserReal/login"; // 正式
//	private String url = "http://172.27.241.33:81/index.php?r=smgUserReal/login"; // 测试
//	private String url = "http://fq.xun-ao.com/index.php?r=smgUserReal/login"; // 42
	private WebChromeClient.CustomViewCallback xCustomViewCallback;
	private FrameLayout mContentView;
	private ValueCallback<Uri> mUploadMessage;
	final private int FILECHOOSER_RESULTCODE_GETCONTENT = 998;
	final private int FILECHOOSER_RESULTCODE_CAPTURE = 999;
	private Uri captureUri;
	private String[] arrayString = { "拍照", "相册" };
	private String title = "上传照片";
	private Handler handler = new Handler();
	private AudioManager audioMgr;
	private int stepVolume = 2;

	private void addMediaVolume(int current) {
		current = current + stepVolume;
		if (current >= maxVolume)
			current = maxVolume;
		setMediaVolume(current);
		// volumeSeekBar.setProgress(current);
	}

	private void cutMediaVolume(int current) {
		current = current - stepVolume;
		if (current <= 0)
			current = 0;
		setMediaVolume(current);
		// volumeSeekBar.setProgress(current);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		myApp = (MyApplication) getApplication();
		myApp.setMa(this);

		audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉应用标题
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		initwidget();
		// initListener();

		// 友盟社会化分享 亦非云账号
		SocializeConstants.APPKEY = "56f8fb7867e58ed72000234b";

		mController = UMServiceFactory.getUMSocialService("xunao.fq_mobile");
		addWXPlatform();
		mController.getConfig().removePlatform(SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.TENCENT);

		btShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setShareContent("img", "分享标题", "分享内容", "http://www.baidu.com");
				// mController.openShare(MainActivity.this, false);
			}
		});

		videowebview.addJavascriptInterface(new Object() {
			// 图册浏览
			public void showAtlas(final String data) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Log.d("tag", data);
						Intent intent = new Intent(context, AtlasActivity.class);
						intent.putExtra("pics", data);
						startActivity(intent);
					}
				});
			}

			// 第三方分享
			public void share(final String title, final String description, final String linkUrl, final String imageUrl) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						setShareContent(imageUrl, title, description, linkUrl);
						mController.openShare(MainActivity.this, false);
					}
				});
			}

		}, "Android");
		// 获取应用版本号，发到服务器
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			final String version = packInfo.versionName;
			Log.d("version", version);
			new Thread() {
				@Override
				public void run() {
					String html = Data.readData(api.update_update(version));
					try {
						JSONObject data = new JSONObject(html);
						String result = data.getJSONObject("data").getString("result");
						if (result.equals("1")) {
							final String msg = data.getJSONObject("data").getString("msg");
							final String new_url = data.getJSONObject("data").getString("url");
							MainActivity.this.handler.post(new Runnable() {
								@Override
								public void run() {
									new AlertDialog.Builder(MainActivity.this).setTitle("新版本提示").setMessage(msg + "\n是否立即更新？").setPositiveButton("是", new android.content.DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											Intent i = new Intent(Intent.ACTION_VIEW);
											i.setData(Uri.parse(new_url));
											startActivity(i);
										}
									}).setNegativeButton("否", null).show();
								}
							});
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}.start();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		videowebview.loadUrl(url);

		// videowebview.loadDataWithBaseURL("about:blank", url, "text/html",
		// "utf-8", "");
	}

	/**
	 * @功能描述 : 添加微信平台分享
	 * @return
	 */
	protected void addWXPlatform() {
		// 注意：在微信授权的时候，必须传递appSecret
		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appId = "wx475524160cb6d4bc";// "wx967daebe835fbeac";
		String appSecret = "affc36c9f428ededd27cfc1e452e32f9";// "5bb696d9ccd75a38c8a0bfe0675559b3";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(context, appId, appSecret);
		wxHandler.addToSocialSDK();

		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(context, appId, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	protected void setShareContent(String shareImg, String title, String content, String targetUrl) {
		UMImage urlImage = new UMImage(context, shareImg);

		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent.setShareContent(content);
		weixinContent.setTitle(title);
		weixinContent.setShareMedia(urlImage);
		weixinContent.setTargetUrl(targetUrl);
		mController.setShareMedia(weixinContent);

		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent(content);
		circleMedia.setTitle(title);
		circleMedia.setShareMedia(urlImage);
		circleMedia.setTargetUrl(targetUrl);
		mController.setShareMedia(circleMedia);
	}

	private void initListener() {
		videolandport.setOnClickListener(new Listener());
	}

	@SuppressWarnings("deprecation")
	private void initwidget() {
		videoview = (FrameLayout) findViewById(R.id.video_view);
		// videolandport = (Button) findViewById(R.id.video_landport);
		videowebview = (WebView) findViewById(R.id.video_webview);
		WebSettings ws = videowebview.getSettings();
		/**
		 * setAllowFileAccess 启用或禁止WebView访问文件数据 setBlockNetworkImage 是否显示网络图像
		 * setBuiltInZoomControls 设置是否支持缩放 setCacheMode 设置缓冲的模式
		 * setDefaultFontSize 设置默认的字体大小 setDefaultTextEncodingName 设置在解码时使用的默认编码
		 * setFixedFontFamily 设置固定使用的字体 setJavaSciptEnabled 设置是否支持Javascript
		 * setLayoutAlgorithm 设置布局方式 setLightTouchEnabled 设置用鼠标激活被选项
		 * setSupportZoom 设置是否支持变焦
		 * */
		ws.setPluginState(PluginState.ON);
		ws.setBlockNetworkImage(false);
		ws.setBuiltInZoomControls(true);// 隐藏缩放按钮
		ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 排版适应屏幕
		ws.setUseWideViewPort(true);// 可任意比例缩放
		ws.setLoadWithOverviewMode(true);// setUseWideViewPort方法设置webview推荐使用的窗口。setLoadWithOverviewMode方法是设置webview加载的页面的模式。
		ws.setSavePassword(true);
		ws.setSaveFormData(true);// 保存表单数据
		ws.setJavaScriptEnabled(true);
		ws.setGeolocationEnabled(false);// 启用地理定位
		ws.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");// 设置定位的数据库路径
		ws.setDomStorageEnabled(true);
		ws.setSupportMultipleWindows(true);
		ws.setPluginState(PluginState.ON);
		xwebchromeclient = new xWebChromeClient();
		videowebview.setWebChromeClient(xwebchromeclient);
		videowebview.setWebViewClient(new xWebViewClientent());

		btShare = (RelativeLayout) findViewById(R.id.btShare);
	}

	class Listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// switch (v.getId()) {
			// case R.id.video_landport:
			// if (islandport) {
			// Log.i("testwebview", "竖屏切换到横屏");
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			// videolandport.setText("全屏不显示该按扭，点击切换竖屏");
			// }else {
			//
			// Log.i("testwebview", "横屏切换到竖屏");
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			// videolandport.setText("全屏不显示该按扭，点击切换横屏");
			// }
			// break;
			//
			// default:
			// break;
			// }
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (inCustomView()) {
				hideCustomView();
				return true;
			} else {
				// new
				// AlertDialog.Builder(this).setTitle("退出").setMessage("是否退出番茄新闻？").setPositiveButton("是",
				// new android.content.DialogInterface.OnClickListener() {
				// @Override
				// public void onClick(DialogInterface arg0, int arg1) {
				// videowebview.loadUrl("www.baidu.com");
				// MainActivity.this.finish();
				// }
				// }).setNegativeButton("否", null).show();
				String currentUrl = videowebview.getUrl();
				if (currentUrl.contains("index.php?r=smgwebNews/index") || currentUrl.contains("index.php?r=smgUserReal/login")) {
					new AlertDialog.Builder(this).setTitle("退出").setMessage("是否退出番茄新闻？").setPositiveButton("是", new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							videowebview.loadUrl("http://www.baidu.com");
							MainActivity.this.finish();
						}
					}).setNegativeButton("否", null).show();
				} else {
					videowebview.goBack();
				}
				return false;
			}
		}
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			addMediaVolume(getMediaVolume());
			// playMedia(getMediaVolume());
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			cutMediaVolume(getMediaVolume());
			// playMedia(getMediaVolume());
			return true;
		default:
			break;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return false;
		}
		return true;
	}

	private int getMediaVolume() {
		return audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	private void setMediaVolume(int volume) {
		audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
	}

	/**
	 * 判断是否是全屏
	 * 
	 * @return
	 */
	public boolean inCustomView() {
		return (xCustomView != null);
	}

	/**
	 * 全屏时按返加键执行退出全屏方法
	 */
	public void hideCustomView() {
		xwebchromeclient.onHideCustomView();
	}

	/**
	 * 处理Javascript的对话框、网站图标、网站标题以及网页加载进度等
	 * 
	 * @author
	 */
	public class xWebChromeClient extends WebChromeClient {
		private Bitmap xdefaltvideo;
		private View xprogressvideo;

		@Override
		// 播放网络视频时全屏会被调用的方法
		public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
			// 执行到下面那句会崩溃
			if (xCustomView != null) {
				callback.onCustomViewHidden();
				return;
			}
			videoview.removeView(videowebview);
			videoview.addView(view);
			xCustomView = view;
			// videowebview.getClass().getMethod("onPause").invoke(videowebview,(Object[])null);（低版本测试正常）

			xCustomViewCallback = callback;
			// 设置横屏
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			// 设置全屏
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		@Override
		// 视频播放退出全屏会被调用的
		public void onHideCustomView() {

			// 设置竖屏
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			// 取消全屏
			final WindowManager.LayoutParams attrs = getWindow().getAttributes();
			attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().setAttributes(attrs);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			if (xCustomView == null) {
				return;
			}
			videoview.removeView(xCustomView);
			xCustomView = null;
			videoview.addView(videowebview);
			xCustomViewCallback.onCustomViewHidden();
		}

		// 视频加载添加默认图标
		@Override
		public Bitmap getDefaultVideoPoster() {
			// Log.i(LOGTAG, "here in on getDefaultVideoPoster");
			if (xdefaltvideo == null) {
				xdefaltvideo = BitmapFactory.decodeResource(getResources(), R.drawable.videoicon);
			}
			return xdefaltvideo;
		}

		// 视频加载时进程loading
		@Override
		public View getVideoLoadingProgressView() {
			// Log.i(LOGTAG, "here in on getVideoLoadingPregressView");

			if (xprogressvideo == null) {
				LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
				xprogressvideo = inflater.inflate(R.layout.video_loading_progress, null);
			}
			return xprogressvideo;
		}

		@Override
		public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg) {
			WebView.HitTestResult result = view.getHitTestResult();
			if (result == null)
				return false;
			String data = result.getExtra();
			if (data == null)
				return false;
			if(data.contains("smgqy.xun-ao.com")){
				view.loadUrl(data);
			}else{
				Context context = view.getContext();
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
				context.startActivity(browserIntent);
			}
			return false;
		}

		// 网页标题
		@Override
		public void onReceivedTitle(WebView view, String title) {
			(MainActivity.this).setTitle(title);
		}

		// @Override
		// //当WebView进度改变时更新窗口进度
		// public void onProgressChanged(WebView view, int newProgress) {
		// (MainActivity.this).getWindow().setFeatureInt(Window.FEATURE_PROGRESS,
		// newProgress*100);
		// }
		// For Android 3.0+
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
			if (mUploadMessage != null)
				return;
			mUploadMessage = uploadMsg;
			getListDialogBuilder(MainActivity.this, arrayString, title, customOnDialogClick).show();
		}

		// For Android < 3.0
		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
			openFileChooser(uploadMsg, "");
		}

		// For Android > 4.1.1
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
			openFileChooser(uploadMsg, acceptType);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == FILECHOOSER_RESULTCODE_CAPTURE) {
			if (null == mUploadMessage) {
				return;
			}
			Uri result = captureUri;
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		} else if (requestCode == FILECHOOSER_RESULTCODE_GETCONTENT) {
			if (null == mUploadMessage) {
				return;
			}
			Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
			if (result != null) {

				String filePath = null;

				if ("content".equals(result.getScheme())) {

					Cursor cursor = this.getContentResolver().query(result, new String[] { MediaColumns.DATA }, null, null, null);
					cursor.moveToFirst();
					filePath = cursor.getString(0);
					cursor.close();

				} else {
					filePath = result.getPath();
				}
				Uri myUri = result;
				if (filePath != null) {
					if (!filePath.startsWith("file://")) {
						filePath = "file://" + filePath;
					}
					myUri = Uri.parse(filePath);
				}
				mUploadMessage.onReceiveValue(myUri);

			} else {
				mUploadMessage.onReceiveValue(result);
			}

			mUploadMessage = null;
		}
	}

	public static AlertDialog.Builder getListDialogBuilder(Context c, String[] s, String t, android.content.DialogInterface.OnClickListener o) {
		final String[] items = s;
		return new AlertDialog.Builder(c).setTitle(t).setItems(items, o).setCancelable(false);
	}

	private DialogInterface.OnClickListener customOnDialogClick = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				customStartCamearPicCut(dialog); // 开启照相
				break;
			case 1:
				customSImageCaptrue(dialog); // 开启图库
				break;
			default:
				break;
			}
		}
	};

	private int maxVolume;

	private void customStartCamearPicCut(DialogInterface dialog) {
		dialog.dismiss();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		captureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/swApp", "capture.jpg"));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, captureUri);
		startActivityForResult(intent, FILECHOOSER_RESULTCODE_CAPTURE);
	}

	private void customSImageCaptrue(DialogInterface dialog) {
		dialog.dismiss();
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "选择操作"), FILECHOOSER_RESULTCODE_GETCONTENT);
	}

	private String lastUrl = "";

	private Boolean checkSuffix(String url) {
		String[] suffix = new String[] { "doc", "docx", "xls", "xlsx", "pdf", "ppt", "pptx" };
		int count = suffix.length;
		for (int i = 0; i < count; i++) {
			if (url.toLowerCase().endsWith(suffix[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 处理各种通知、请求等事件
	 */
	public class xWebViewClientent extends WebViewClient {
		private static final boolean DEBUG = true;
		private static final String TAG = "gqz.debug";

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.i("webviewtest", "shouldOverrideUrlLoading: " + url);
			if (!url.equals(lastUrl)) {
				if (url.startsWith("tel:")) {
					lastUrl = url;
					Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
					startActivity(intent);
					return true;
				} else if (checkSuffix(url)) {
					Uri uri = Uri.parse(url);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
					return true;
				}
				return false;
			} else {
				lastUrl = "";
				return true;
			}
		}

		/**
		 * 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次
		 */
		@Override
		public void onLoadResource(WebView view, String url) {
			if (DEBUG) {
				Log.d(TAG, url);
			}
			super.onLoadResource(view, url);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}

		@Override
		public void onPageStarted(WebView view, final String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			// new Thread() {
			// @Override
			// public void run() {
			// int status = Network.getRespStatus(url);
			// if (!Network.isNetworkAvailable(context) || status == 404 ||
			// status == 500 || status == -1) {
			// handler.post(new Runnable() {
			// @Override
			// public void run() {
			// Intent intent = new Intent(MainActivity.this,
			// ErrorActivity.class);
			// intent.putExtra("url", url);
			// startActivity(intent);
			// }
			// });
			// }
			// }
			// }.start();
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			Intent intent = new Intent(MainActivity.this, ErrorActivity.class);
			intent.putExtra("url", failingUrl);
			startActivity(intent);
		}
	}

	/**
	 * 当横竖屏切换时会调用该方法
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i("testwebview", "=====<<<  onConfigurationChanged  >>>=====");
		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.i("webview", "   现在是横屏1");
			islandport = false;
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.i("webview", "   现在是竖屏1");
			islandport = true;
		}
	}

	public static int getPhoneAndroidSDK() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return version;
	}

	// 以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

}
