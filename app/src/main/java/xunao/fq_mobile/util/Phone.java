package xunao.fq_mobile.util;

import java.io.File;
import java.io.IOException;

public class Phone {

    /** 使指定文件获得777权限 */
    public static void root(File f) {
        try {
            Runtime.getRuntime().exec("chmod 777 " + f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
