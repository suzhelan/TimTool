package top.sacz.xphelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.loader.ResourcesLoader;
import android.content.res.loader.ResourcesProvider;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import de.robv.android.xposed.IXposedHookZygoteInit;
import top.sacz.xphelper.reflect.ClassUtils;

public class XpHelper {
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static ClassLoader classLoader;

    private static String moduleApkPath;

    public static void initContext(Context application) {
        context = application;
        classLoader = application.getClassLoader();
        ClassUtils.intiClassLoader(classLoader);
    }

    public static void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
        moduleApkPath = startupParam.modulePath;
    }

    /**
     * 注入模块的Res资源到上下文中
     *
     * @param context 要注入的上下文
     */
    public static void injectResourcesToContext(Context context) {
        Resources res = context.getResources();
        String modulePath = moduleApkPath;
        if (Build.VERSION.SDK_INT >= 30) {
            injectResourcesAboveApi30(res, modulePath);
        } else {
            injectResourcesBelowApi30(res, modulePath);
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
            Handler sHandler = new Handler(Looper.getMainLooper());
            sHandler.postDelayed(task, 0L);
        }
    }

    @SuppressLint("NewApi")
    private static void injectResourcesAboveApi30(Resources res, String path) {
        if (ResourcesLoaderHolderApi30.sResourcesLoader == null) {
            try (ParcelFileDescriptor pfd = ParcelFileDescriptor.open(new File(path),
                    ParcelFileDescriptor.MODE_READ_ONLY)) {
                ResourcesProvider provider = ResourcesProvider.loadFromApk(pfd);
                ResourcesLoader loader = new ResourcesLoader();
                loader.addProvider(provider);
                ResourcesLoaderHolderApi30.sResourcesLoader = loader;
            } catch (IOException e) {
                return;
            }
        }
        runOnUiThread(() -> {
            try {
                res.addLoaders(ResourcesLoaderHolderApi30.sResourcesLoader);
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

    private static class ResourcesLoaderHolderApi30 {

        public static ResourcesLoader sResourcesLoader = null;

        private ResourcesLoaderHolderApi30() {
        }

    }
}
