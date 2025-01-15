package top.sacz.xphelper;

import android.annotation.SuppressLint;
import android.content.Context;

import de.robv.android.xposed.IXposedHookZygoteInit;
import top.sacz.xphelper.activity.ActivityProxyManager;
import top.sacz.xphelper.reflect.ClassUtils;
import top.sacz.xphelper.util.ActivityTools;
import top.sacz.xphelper.util.KvHelper;

public class XpHelper {
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static ClassLoader classLoader;

    public static String moduleApkPath;

    public static void initContext(Context application) {
        context = application;
        classLoader = application.getClassLoader();
        ClassUtils.intiClassLoader(classLoader);
        KvHelper.initialize(application);
        ActivityProxyManager.initActivityProxyManager(application);
    }

    public static void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
        moduleApkPath = startupParam.modulePath;
    }

    /**
     * 设置配置存储路径
     */
    public static void setConfigPath(String pathDir) {
        KvHelper.initialize(pathDir);
    }

    public static void setConfigPassword(String password) {
        KvHelper.setGlobalPassword(password);
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
