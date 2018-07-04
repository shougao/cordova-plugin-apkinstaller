package com.mycompany.installer;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class CrashCatchHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashCatchHandler";
    private static final String LOG_DIR_NAME = "exception_log";
    public static String LOG_PATH;
    private Context mContext;
    //存储设备信息和异常信息
    private Map<String, String> mInfoMap;
    private SimpleDateFormat mDateFormat;

    private static CrashCatchHandler INSTANCE = new CrashCatchHandler();

    public static CrashCatchHandler getInstance() {
        return INSTANCE;
    }

    private CrashCatchHandler() {
        mInfoMap = new LinkedHashMap<>();
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    }

    public void init(Context context) {
        mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            LOG_PATH = mContext.getExternalFilesDir(LOG_DIR_NAME).getPath();
        } else {
            LOG_PATH = mContext.getFilesDir().getPath();
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Toast.makeText(mContext, "抱歉, 程序出现异常, 即将退出.", Toast.LENGTH_LONG).show();
        //收集设备参数信息
        collectDeviceInfo(mContext);
        //保存日志文件至本地
        postService(saveCrashLog(e));
    }

    private void postService(String updateFile) {
        if (TextUtils.isEmpty(updateFile)) {
            return;
        }
    }

    public void collectDeviceInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                String appName = packageInfo.applicationInfo.packageName;
                String versionName = packageInfo.versionName + "";
                String versionCode = packageInfo.versionCode + "";
                mInfoMap.put("Package Name", appName);
                mInfoMap.put("Version Name", versionName);
                mInfoMap.put("Version Number", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error to get device info.", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mInfoMap.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                Log.e(TAG, "Error to get exception stack.", e);
            }
        }
    }

    private String saveCrashLog(Throwable ex) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : mInfoMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            stringBuilder.append(key + " = " + value + "\n");
        }
        //异常写入stringBuilder
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        //异常原因写入stringBuilder
        Throwable cause = ex.getCause();
        if (cause != null) {
            cause.printStackTrace(printWriter);
        }
        printWriter.close();
        String result = writer.toString();
        stringBuilder.append(result);
        try {
            String time = mDateFormat.format(new Date(System.currentTimeMillis()));
            String fileName = time + ".txt";
            File dir = new File(LOG_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(dir.getAbsolutePath() + File.separator + fileName);
            fos.write(stringBuilder.toString().getBytes());
            fos.close();
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "Error to wrte log file.", e);
        }
        return null;
    }
}
