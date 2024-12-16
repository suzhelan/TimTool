package top.sacz.xphelper.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.loader.ResourcesLoader;
import android.content.res.loader.ResourcesProvider;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityTools {
    public static ResourcesLoader resourcesLoader = null;

    /**
     * 获取所有声明在AndroidManifest中已经有注册过的activityInfo
     *
     * @param context 上下文
     */
    public static ActivityInfo[] getAllActivity(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), PackageManager.GET_ACTIVITIES);
            //所有的Activity
            ActivityInfo[] activities = packageInfo.activities;
            return activities;

        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<View> getAllChildViews(Activity activity) {
        View view = activity.getWindow().getDecorView();
        return getAllChildViews(view);

    }

    public static List<View> getAllChildViews(View view) {
        List<View> allChildren = new ArrayList<>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View views = vp.getChildAt(i);
                allChildren.add(views);
                //递归调用
                allChildren.addAll(getAllChildViews(views));
            }
        }
        return allChildren;
    }
    public static void injectResourcesToContext(Context context, String moduleApkPath) {
        Resources res = context.getResources();
        if (Build.VERSION.SDK_INT >= 30) {
            ActivityTools.injectResourcesAboveApi30(res, moduleApkPath);
        } else {
            ActivityTools.injectResourcesBelowApi30(res, moduleApkPath);
        }
    }

    /**
     * 获取当前正在运行的Activity
     */
    @SuppressLint("PrivateApi")
    public static Activity getTopActivity() {
        Class<?> activityThreadClass;
        try {
            activityThreadClass = Class.forName("android.app.ActivityThread");
            //获取当前活动线程
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            @SuppressLint("DiscouragedPrivateApi")
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            //获取线程Map
            Map<?, ?> activities = (Map<?, ?>) activitiesField.get(activityThread);
            if (activities == null) return null;
            for (Object activityRecord : activities.values()) {
                Class<?> activityRecordClass = activityRecord.getClass();
                //获取暂停状态
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                //不是暂停状态的话那就是当前正在运行的Activity
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static void runOnUiThread(Runnable task) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            task.run();
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(task, 0L);
        }
    }

    @SuppressLint("NewApi")
    private static void injectResourcesAboveApi30(Resources res, String path) {
        if (resourcesLoader == null) {
            try (ParcelFileDescriptor pfd = ParcelFileDescriptor.open(new File(path),
                    ParcelFileDescriptor.MODE_READ_ONLY)) {
                ResourcesProvider provider = ResourcesProvider.loadFromApk(pfd);
                ResourcesLoader loader = new ResourcesLoader();
                loader.addProvider(provider);
                resourcesLoader = loader;
            } catch (IOException e) {
                return;
            }
        }
        runOnUiThread(() -> {
            try {
                res.addLoaders(resourcesLoader);
                injectResourcesBelowApi30(res, path);
            } catch (IllegalArgumentException e) {
                String expected1 = "Cannot modify resource loaders of ResourcesImpl not registered with ResourcesManager";
                if (expected1.equals(e.getMessage())) {
                    Log.e("ActivityProxy", Log.getStackTraceString(e));
                    // fallback to below API 30
                    injectResourcesBelowApi30(res, path);
                } else {
                    throw e;
                }
            }
        });
    }

    private static void injectResourcesBelowApi30(Resources res, String path) {
        try {
            AssetManager assetManager = res.getAssets();
            @SuppressLint("DiscouragedPrivateApi")
            Method method = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            method.setAccessible(true);
            method.invoke(assetManager, path);
        } catch (Exception ignored) {
        }
    }

}
