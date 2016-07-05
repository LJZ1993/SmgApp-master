package xunao.fq_mobile.util;

/**
 * Created by chenchao on 16/7/5.
 * cc@cchao.org
 */
public class Local {

    /** 获取路径中名字 */
    public static String getName(String path) {
        String[] arr = path.split("/");
        return arr[arr.length - 1];
    }

}
