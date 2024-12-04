package top.sacz.timtool.hook;

import android.content.Context;

/**
 * 单例模式 存储全局变量 包含app的context和版本号等信息
 */
public class HookEnv {

    public static String TIM_PACKAGE = "com.tencent.tim";
    public static String QQ_PACKAGE = "com.tencent.mobileqq";

    /**
     * 当前宿主包名
     */
    private static String currentHostAppPackageName;
    /**
     * 当前宿主进程名称
     */
    private static String processName;
    /**
     * 模块路径
     */
    private static String moduleApkPath;
    /**
     * 宿主apk路径
     */
    private static String hostApkPath;
    /**
     * 宿主应用名
     */
    private static String appName;
    /**
     * 宿主版本名称
     */
    private static String versionName;
    /**
     * 宿主版本号
     */
    private static int versionCode;
    /**
     * 全局的Context
     */
    private static Context hostAppContext;
    private static ClassLoader hostClassLoader;

    private HookEnv() {
    }

    public static boolean isTim() {
        return TIM_PACKAGE.equals(currentHostAppPackageName);
    }

    public static boolean isQQ() {
        return QQ_PACKAGE.equals(currentHostAppPackageName);
    }

    public static String getAppName() {
        return appName;
    }


    public static void setAppName(String appName) {
        HookEnv.appName = appName;
    }

    public static ClassLoader getHostClassLoader() {
        return hostClassLoader;
    }

    public static void setHostClassLoader(ClassLoader classLoader) {
        hostClassLoader = classLoader;

    }

    public static String getHostApkPath() {
        return hostApkPath;
    }

    public static void setHostApkPath(String apkPath) {
        hostApkPath = apkPath;

    }

    public static String getVersionName() {
        return versionName;
    }

    public static void setVersionName(String hostVersionName) {
        versionName = hostVersionName;

    }

    public static int getVersionCode() {
        return versionCode;
    }

    public static void setVersionCode(int hostVersionCode) {
        versionCode = hostVersionCode;

    }

    public static Context getHostAppContext() {
        return hostAppContext;
    }

    public static void setHostAppContext(Context appContext) {
        hostAppContext = appContext;

    }

    public static String getModuleApkPath() {
        return moduleApkPath;
    }

    public static void setModuleApkPath(String path) {
        moduleApkPath = path;

    }

    public static String getProcessName() {
        return processName;
    }

    public static void setProcessName(String currentProcessName) {
        processName = currentProcessName;
    }

    public static String getCurrentHostAppPackageName() {
        return currentHostAppPackageName;
    }

    public static void setCurrentHostAppPackageName(String packageName) {
        currentHostAppPackageName = packageName;

    }

    public static boolean isMainProcess() {
        return processName.equals(currentHostAppPackageName);
    }
}
