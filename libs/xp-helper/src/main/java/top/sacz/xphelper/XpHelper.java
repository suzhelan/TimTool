package top.sacz.xphelper;

import android.annotation.SuppressLint;
import android.content.Context;

import de.robv.android.xposed.IXposedHookZygoteInit;
import top.sacz.xphelper.reflect.ClassUtils;
import top.sacz.xphelper.util.ActivityTools;

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
        ActivityTools.injectResourcesToContext(context, moduleApkPath);
    }


}
