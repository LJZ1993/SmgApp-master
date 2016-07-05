package xunao.fq_mobile.util;

import android.content.Context;

public class Info {
    private static String ProjectName;

    public static String getProjectName() {
        return ProjectName;
    }

    public static void setProjectName(String projectName) {
        ProjectName = projectName;
    }

    public static void setProjectName(Context context) {
        String packageName = context.getPackageName();
        String[] arr = packageName.split(".");
        if (arr.length > 1) {
            ProjectName = arr[arr.length - 1];
        } else {
            ProjectName = packageName;
        }
    }
}