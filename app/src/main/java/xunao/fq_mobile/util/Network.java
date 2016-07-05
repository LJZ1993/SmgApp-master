package xunao.fq_mobile.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class Network {
    /** 检查网络是否连接 */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** 检查网络连接情况 0: None, 1: Wifi, 2: GPRS, 3: Other */
    public static int checkNetworkType(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager == null) {
            return 0;
        }
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable())
            return 0;
        if (State.CONNECTED == connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()) {
            return 1;
        }
        if (State.CONNECTED == connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()) {
            return 2;
        }
        return 3;
    }

    /** 获取页面信息 */
    public static int getRespStatus(String url) {
        int status = -1;
        try {
            HttpHead head = new HttpHead(url);
            HttpClient client = new DefaultHttpClient();
            HttpResponse resp = client.execute(head);
            status = resp.getStatusLine().getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }
}
