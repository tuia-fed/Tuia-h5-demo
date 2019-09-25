package com.tm.demo;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 检测其他应用是否安装
 * Created by Administrator on 2018/1/25.
 */

public class AppUtil {


    public static String sortAppend(Map<String, String> paramMap) {
        TreeMap<String, String> sortedMap = new TreeMap<>(paramMap);
        StringBuilder builder = new StringBuilder();
        boolean flag = true;
        for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
            String value = entry.getValue();
            if (flag) {
                flag = false;
                builder.append(value);
            } else {
                builder.append("," + value);
            }
        }
        return builder.toString();
    }


    public static PackageInfo getAppList(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            Map<String, String> sortedMap = new TreeMap<String, String>();
            List<PackageInfo> packages = pm.getInstalledPackages(0);
            for (PackageInfo packageInfo : packages) {
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0 && packageInfo.applicationInfo.packageName.contains(packageName)) {
                    return packageInfo;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }


    public static String getAppInfo(Context context, String packageName) {
        JSONArray jsonArray = new JSONArray();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Calendar calendar = Calendar.getInstance();
            long endTime = calendar.getTimeInMillis();//结束时间
            calendar.add(Calendar.DAY_OF_WEEK, -2);//时间间隔为一周
            long startTime = calendar.getTimeInMillis();//开始时间
            android.app.usage.UsageStatsManager usageStatsManager = (android.app.usage.UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            //获取一个月内的信息
            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(android.app.usage.UsageStatsManager.INTERVAL_WEEKLY, startTime, endTime);
            for (UsageStats usageStats : queryUsageStats) {
                try {
                    if (usageStats.getPackageName().contains(packageName)) {
                        JSONObject object = new JSONObject();
                        object.put("packageName", usageStats.getPackageName());
                        long totalTime = usageStats.getTotalTimeInForeground();
                        long launchCount = usageStats.getClass().getDeclaredField("mLaunchCount").getLong(usageStats);
                        if (totalTime <= 0 || launchCount <= 0) {
                            continue;
                        }
                        object.put("totalTimeInForeground", totalTime);
                        object.put("useTimes", launchCount);
                        jsonArray.put(object);
                    }
                } catch (Exception e) {
                }
            }
        } else {
            int MAX_RECENT_TASKS = 10;
            PackageManager pm = context.getPackageManager();
            ActivityManager am = (ActivityManager)
                    context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RecentTaskInfo> list = am
                    .getRecentTasks(MAX_RECENT_TASKS, ActivityManager.RECENT_WITH_EXCLUDED);
            for (ActivityManager.RecentTaskInfo running : list) {
                Intent intent = running.baseIntent;
                ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
                if (resolveInfo != null) {
                    if (resolveInfo.activityInfo.packageName.contains(packageName)) {
                        JSONObject object = new JSONObject();//
                        try {
                            object.put("packageName", resolveInfo.activityInfo.packageName);
                            jsonArray.put(object);
                        } catch (JSONException e) {
                        }
                    }
                }
            }
        }
        return jsonArray.toString();
    }


    /**
     * 打开app
     *
     * @return
     */
    public static boolean openOtherApp(Context context, String appPackageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(appPackageName);
            if (launchIntentForPackage != null) {
                context.startActivity(launchIntentForPackage);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 检查是否安装应用
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppInstall(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 在手机上打开文件
     */
    public static void openFile(Context mContext, File f) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT < 24) {
            /* 调用getMIMEType()来取得MimeType */
            String type = "application/vnd.android.package-archive";
            /* 设置intent的file与MimeType */
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
            intent.setDataAndType(Uri.fromFile(f), type);
        } else {
            Uri uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".updatefileprovider", f);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

}
