package com.xunao.gr.customActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import xunao.fq_mobile.util.Image;

@SuppressLint("SimpleDateFormat")
@SuppressWarnings("deprecation")
public class CustomPhotoActivity extends Activity {
	public Context context = this;
	public Boolean needCut = false;
	public int degree = 0;
//app\src\main\java  move "C:\Users\Administrator\Desktop\google-services.json" "app\src\main\java"
	private File mCurrentPhotoFile, PHOTO_DIR;
	protected File mPhotoUploadFile;
	protected Uri photoUri;
	public String PHOTO_PATH;
	public static final int SELECT_PIC_BY_TACK_PHOTO = 1; // 拍照
	public static final int SELECT_PIC_BY_PICK_PHOTO = 2; // 相册
	public static final int CROP_PHOTO = 3; // 裁剪

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PHOTO_DIR = new File(getSDPath() + "/swapp");
		mPhotoUploadFile = new File(getPhotoZoomName());

		if (!PHOTO_DIR.exists()) {
			if (!PHOTO_DIR.mkdirs()) {
				showToast("创建文件失败！" + PHOTO_DIR);
			}
		}
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyy-MM-dd_HH-mm-ss");
		String photoName2 = dateFormat.format(date) + ".jpg";
		mCurrentPhotoFile = new File(PHOTO_DIR, photoName2);
		PHOTO_PATH = mCurrentPhotoFile.getAbsolutePath();
	}

	// 照相对话框
	protected DialogInterface.OnClickListener onDialogClick = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				startCamearPicCut(dialog);// 开启照相
				break;
			case 1:
				startImageCaptrue(dialog);// 开启图库
				break;
			default:
				break;
			}
		}

		private void startCamearPicCut(DialogInterface dialog) {
			dialog.dismiss();
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			photoUri = Uri.fromFile(mCurrentPhotoFile);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoUploadFile));
			startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
		}

		private void startImageCaptrue(DialogInterface dialog) {
			dialog.dismiss();
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			if (needCut) {
				intent.putExtra("crop", "true");
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", 480);
				intent.putExtra("outputY", 480);
				intent.putExtra("scale", true);// 黑边
				intent.putExtra("scaleUpIfNeeded", true);// 黑边
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoUploadFile));
				intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
			}
			startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (needCut) {
				if (requestCode != CROP_PHOTO) {
					doPhoto(requestCode, data);
				} else {
					upload();
				}
			} else {
				if (requestCode == SELECT_PIC_BY_TACK_PHOTO) {// 拍照
					if (PHOTO_PATH != null && (PHOTO_PATH.endsWith(".png") || PHOTO_PATH.endsWith(".PNG") || PHOTO_PATH.endsWith(".jpg") || PHOTO_PATH.endsWith(".JPG"))) {
						if (photoUri == null) {
							showToast("文件没有找到，请重新选择");
							PHOTO_PATH = null;
							return;
						}
						upload(mPhotoUploadFile.getPath());
					} else {
						Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
					}
				} else if (requestCode == SELECT_PIC_BY_PICK_PHOTO) {
					if (data != null) {
						photoUri = data.getData();
					}
					if (photoUri != null) {
						String[] proj = { MediaColumns.DATA };
						Cursor cursor = managedQuery(photoUri, proj, null, null, null);
						int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
						cursor.moveToFirst();
						PHOTO_PATH = cursor.getString(column_index);
					}
					upload(PHOTO_PATH);
				}
			}
		}
	}

	private void doPhoto(int requestCode, Intent data) {
		if (requestCode == SELECT_PIC_BY_TACK_PHOTO) {// 拍照
			if (PHOTO_PATH != null && (PHOTO_PATH.endsWith(".png") || PHOTO_PATH.endsWith(".PNG") || PHOTO_PATH.endsWith(".jpg") || PHOTO_PATH.endsWith(".JPG"))) {
				if (photoUri == null) {
					showToast("文件没有找到，请重新选择");
					PHOTO_PATH = null;
					return;
				}
				startPhotoZoom(Uri.fromFile(mPhotoUploadFile));
			} else {
				Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
			}
		} else if (requestCode == SELECT_PIC_BY_PICK_PHOTO) {
			upload();
		}
	}

	// 上传裁剪后的图片
	public void upload() {
		String p = mPhotoUploadFile.getPath();
		upload(p);
	}

	// 上传为裁剪的图片
	public void upload(String str) {
		degree = Image.getBitmapDegree(str);
	}

	private String getSDPath() {
		File sdDir = null;
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			sdDir = Environment.getExternalStorageDirectory();
		}
		if (sdDir != null) {
			return sdDir.toString();
		} else {
			return "";
		}
	}

	private void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 240);
		intent.putExtra("outputY", 240);
		intent.putExtra("output", Uri.fromFile(mPhotoUploadFile));
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, CROP_PHOTO);
	}

	public String getPhotoZoomName() {
		return PHOTO_DIR.getPath() + "/myUploadName.jpg";
	}

	private void showToast(String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
}
