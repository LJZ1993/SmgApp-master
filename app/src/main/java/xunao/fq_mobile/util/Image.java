package xunao.fq_mobile.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;

import xunao.fq_mobile.util.imgLoad.ImageFileCache;
import xunao.fq_mobile.util.imgLoad.ImageGetFromHttp;
import xunao.fq_mobile.util.imgLoad.ImageMemoryCache;

/** 图片处理类，Bitmap与Drawable的获取、转换、缩放、特效 */
@SuppressLint("DefaultLocale")
public class Image {
    private static Boolean showLog = false;

    public static void setLog(Boolean bool) {
        showLog = bool;
    }

    /** 通过id获取项目内图片 */
    public static Bitmap getBitmap(Context context, int rId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(rId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /** 获得一张图片,从三个地方获取,首先是内存缓存,然后是文件缓存,最后从网络获取 */
    public static Bitmap getBitmap(Context context, String url, int divide) {
        if (url == null) {
            return null;
        }
        ImageFileCache fileCache = new ImageFileCache();
        ImageMemoryCache memoryCache = new ImageMemoryCache(context);
        if (showLog) {
            Out.log(url);
        }
        url = url.replaceAll(" ", "%20");
        try {
            Bitmap result = memoryCache.getBitmapFromCache(url);// 从内存缓存中获取图片
            if (result == null) {
                result = fileCache.getImage(url);// 文件缓存中获取
                if (result == null) {
                    try {
                        if (!url.equals("")) {
                            result = ImageGetFromHttp.downloadBitmap(url, Screen.width(context).px / divide);// 从网络获取
                        }
                    } catch (IllegalArgumentException e) {
                        if (showLog) {
                            Out.log(url + "url异常，图片获取失败");
                        }
                    }
                    if (result != null) {
                        if (showLog) {
                            Out.log("网络获取:" + url);
                        }
                        fileCache.saveBitmap(result, url);
                        memoryCache.addBitmapToCache(url, result);
                    }
                } else {
                    if (showLog) {
                        Out.log("文件缓存:" + url);
                    }
                    memoryCache.addBitmapToCache(url, result);// 添加到内存缓存
                }
            } else {
                if (showLog) {
                    Out.log("内存缓存:" + url);
                }
            }
            return result;
        } catch (OutOfMemoryError e) {
            Out.log("内存溢出:" + url);
            return null;
        }
    }

    public static Boolean saveImageWithUrl(Context context, String url, String path, int divide) {
        Bitmap bitmap = Image.getBitmapWithoutMemory(context, url, divide);
        if (bitmap == null) {
            return false;
        }
        File file = new File(path);
        if (file.exists()) {
            Out.log(path + "已存在");
            return true;
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(path.toUpperCase().contains("PNG") ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 80, out)) {
                out.flush();
                out.close();
            }
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                System.gc();
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Boolean saveImage2Project(Context context, String url, int divide) {
        return saveImageWithUrl(context, url, getImagePath(context, url), divide);
    }

    public static String getImagePath(Context context, String url) {
        String dir = context.getFilesDir().getAbsolutePath() + "/" + Info.getProjectName() + "/images/";
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
            Phone.root(file);
        }
        return dir + Local.getName(url);
    }

    public static Bitmap getBitmapWithoutMemory(Context context, String url, int divide) {
        Bitmap result = null;
        try {
            result = ImageGetFromHttp.downloadBitmap(url, Screen.width(context).px / divide);// 从网络获取
        } catch (IllegalArgumentException e) {
            if (showLog) {
                Out.log(url + "url异常，图片获取失败");
            }
        }
        return result;
    }

    public static void recycle(Bitmap bm) {
        if (bm != null) {
            if (bm.isRecycled()) {
                bm.recycle();
                bm = null;
            }
        }
    }

    /** 软引用图片 */
    public static SoftReference<Bitmap> softBitmap(Bitmap bm) {
        return new SoftReference<Bitmap>(bm);
    }

    public static void recycle(SoftReference<Bitmap> bm) {
        if (bm != null) {
            if (bm.get() != null && !bm.get().isRecycled()) {
                bm.get().recycle();
                bm = null;
            }
        }
    }

    public static Bitmap loadpic(Context context, String path) {
        Bitmap bitmap = null;
        path = getDirectory(context) + path;
        try {
            File file = new File(path);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(path);
            }
        } catch (Exception e) {

        }
        return bitmap;
    }

    private static String getDirectory(Context context) {
        String dir = context.getFilesDir().getAbsolutePath() + "/images/";
        createPath(dir);
        return dir;
    }

    private static void createPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /** Drawable转Bitmap */
    public static Bitmap Drawable2Bitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    /** Bitmap缩放 */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    /** Bitmap圆角 */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /** Bitmap添加阴影 */
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w, h / 2, matrix, false);
        Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);
        canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0, h, w, bitmapWithReflection.getHeight() + reflectionGap, paint);
        return bitmapWithReflection;
    }

    /** Bitmap转字节 */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static byte[] Bitmap2Bytes(Bitmap bm, int cos) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        try {
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Options options = new Options();
        options.inSampleSize = 4;
        bm.compress(Bitmap.CompressFormat.PNG, cos, baos);
        return baos.toByteArray();
    }

    /** 字节转Bitmap */
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /** 读取网络地址保存为Drawable */
    public static Drawable loadDrawableFromNetwrok(String imageUrl, String name) throws MalformedURLException, IOException {
        return Drawable.createFromResourceStream(null, null, new URL(imageUrl).openStream(), name);
    }

    /** Bitmap转Drawable */
    public static Drawable getDrawable(Context context, Bitmap bm) {
        return new BitmapDrawable(context.getResources(), bm);
    }

    /** Drawable缩放 */
    public static Drawable zoomDrawable(Context context, Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = Drawable2Bitmap(drawable);
        Matrix matrix = new Matrix();
        float sx = ((float) w / width);
        float sy = ((float) h / height);
        matrix.postScale(sx, sy);
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(context.getResources(), newbmp);
    }

    public static Bitmap scalePicture(String filename, int maxWidth, int maxHeight) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            BitmapFactory.decodeFile(filename, opts);
            int srcWidth = opts.outWidth;
            int srcHeight = opts.outHeight;
            int desWidth = 0;
            int desHeight = 0;
            // 缩放比例
            double ratio = 0.0;
            if (srcWidth > srcHeight) {
                ratio = srcWidth / maxWidth;
                desWidth = maxWidth;
                desHeight = (int) (srcHeight / ratio);
            } else {
                ratio = srcHeight / maxHeight;
                desHeight = maxHeight;
                desWidth = (int) (srcWidth / ratio);
            }
            // 设置输出宽度、高度
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
            newOpts.inSampleSize = (int) (ratio) + 1;
            newOpts.inJustDecodeBounds = false;
            newOpts.outWidth = desWidth;
            newOpts.outHeight = desHeight;
            bitmap = BitmapFactory.decodeFile(filename, newOpts);
        } catch (Exception e) {

        }
        return bitmap;
    }

    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {

        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }
}