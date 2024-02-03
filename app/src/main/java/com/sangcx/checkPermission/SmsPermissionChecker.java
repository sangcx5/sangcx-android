package com.sangcx.checkPermission;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class SmsPermissionChecker {

    public static String text = "";

    public static CharSequence checkSmsPermissions(Context context) {
        PackageManager packageManager = context.getPackageManager();

        final int flags = PackageManager.GET_META_DATA;
        final int requestedPermissions = PackageManager.GET_PERMISSIONS;

        try {
            ApplicationInfo[] appList = packageManager.getInstalledApplications(flags).toArray(new ApplicationInfo[0]);

            for (ApplicationInfo appInfo : appList) {
                PackageInfo packageInfo = packageManager.getPackageInfo(
                        appInfo.packageName,
                        requestedPermissions
                );

                if (hasSmsPermission(packageInfo) && !isSystemApp(context, appInfo.packageName)) {
                    text += appInfo.packageName + "\n";
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return text;
    }

    private static boolean hasSmsPermission(PackageInfo packageInfo) {
        String[] permissions = packageInfo.requestedPermissions;

        if (permissions != null) {
            for (String permission : permissions) {
                if ("android.permission.SEND_SMS".equals(permission)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isSystemApp(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

}

