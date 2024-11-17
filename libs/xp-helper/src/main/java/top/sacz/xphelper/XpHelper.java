package top.sacz.xphelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.lang.reflect.Method;

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

    public static void injectResourcesToContext(Context context) {
        try {
            Resources resources = context.getResources();
            AssetManager assetManager = resources.getAssets();
            @SuppressLint("DiscouragedPrivateApi")
            Method method = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            method.setAccessible(true);
            method.invoke(assetManager, moduleApkPath);
        } catch (Exception ex) {
        }
    }
}
