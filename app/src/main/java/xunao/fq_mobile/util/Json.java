package xunao.fq_mobile.util;

import org.json.JSONException;
import org.json.JSONObject;

public class Json {
    public static String getString(JSONObject jo, String value) {
        if (jo != null) {
            if (jo.has(value)) {
                try {
                    return jo.getString(value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static int getInt(JSONObject jo, String value) {
        if (jo != null) {
            if (jo.has(value)) {
                try {
                    if (jo.getString(value).equals("")) {
                        return 0;
                    }
                    return jo.getInt(value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    public static long getLong(JSONObject jo, String value) {
        if (jo != null) {
            if (jo.has(value)) {
                try {
                    if (jo.getString(value).equals("")) {
                        return 0;
                    }
                    return jo.getLong(value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    public static double getDouble(JSONObject jo, String value) {
        if (jo != null) {
            if (jo.has(value)) {
                try {
                    if (jo.getString(value).equals("")) {
                        return 0;
                    }
                    return jo.getDouble(value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }
}
