package top.sacz.xphelper.activity;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;

import dalvik.system.DexClassLoader;
import top.sacz.xphelper.activity.replace.IActivityManagerHandler;
import top.sacz.xphelper.activity.replace.ProxyHandler;
import top.sacz.xphelper.activity.replace.ProxyInstrumentation;
import top.sacz.xphelper.reflect.ClassUtils;
import top.sacz.xphelper.util.ActivityTools;

@SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
public class ActivityProxyManager {
    private static final AtomicBoolean Initialized = new AtomicBoolean();

    public static String HostActivityClassName;
    public static String ACTIVITY_PROXY_INTENT = "lin_proxy_intent";

    public static boolean isModuleActivity(String className) {
        try {
            return BaseActivity.class.isAssignableFrom(ClassUtils.getModuleClassLoader().loadClass(className));
//            ClassUtils.getModuleClassLoader().loadClass(className);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 用于启动未注册在AndroidManifest的Activity(也就是模块自身的activity)
     * 模块自身的Activity需要继承本库的 {@link BaseActivity} 才能启动
     *
     * @param hostContext 宿主的上下文
     */
    public static void initActivityProxyManager(Context hostContext) {

        HostActivityClassName = ActivityTools.getAllActivity(hostContext)[0].name;
        if (Initialized.getAndSet(true)) return;
        try {
            Class<?> cActivityThread = Class.forName("android.app.ActivityThread");
            // 获取sCurrentActivityThread对象
            Field fCurrentActivityThread = cActivityThread.getDeclaredField("sCurrentActivityThread");
            fCurrentActivityThread.setAccessible(true);
            Object currentActivityThread = fCurrentActivityThread.get(null);

            replaceInstrumentation(currentActivityThread);
            replaceHandler(currentActivityThread);
            replaceIActivityManager();
            try {
                replaceIActivityTaskManager();
            } catch (Exception ignored) {
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取插件的随机id
     *
     * @param context       宿主上下文
     * @param pluginApkPath 插件路径
     * @return 随机id
     */
    private static int getModuleRandomID(Context context, String pluginApkPath) {
        try {
            //在应用安装目录下创建一个名为app_dex文件夹目录,如果已经存在则不创建 /data/data/hostPackageName/app_LActivity_DEXHotLoad
            File optimizedDirectoryFile = context.getDir("LActivity_DEXHotLoad", Context.MODE_PRIVATE);
            // 构建插件的DexClassLoader类加载器，参数：
            // 1、包含dex的apk文件或jar文件的路径，
            // 2、apk、jar解压缩生成dex存储的目录，
            // 3、本地library库目录，一般为null，
            // 4、父ClassLoader
            DexClassLoader dexClassLoader = new DexClassLoader(pluginApkPath, optimizedDirectoryFile.getPath(),
                    null, ClassLoader.getSystemClassLoader());
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(pluginApkPath, PackageManager.GET_ACTIVITIES);
            if (info == null)
                throw new RuntimeException("Package.getPackageArchiveInfo(pluginApkPath, PackageManager.GET_ACTIVITIES) ");

            ApplicationInfo appInfo = info.applicationInfo;
//                String appName = pm.getApplicationLabel(appInfo).toString();
            String packageName = appInfo.packageName;

            //通过使用apk自己的类加载器，反射出R类中相应的内部类进而获取我们需要的资源id
            Class<?> resClz = dexClassLoader.loadClass(packageName + ".R");
            for (Class<?> res : resClz.getDeclaredClasses()) {
                for (Field idField : res.getDeclaredFields()) {
                    idField.setAccessible(true);
                    return (int) idField.get(null);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("LActivity_DEXHotLoad getModuleRandomID Error");
    }

    private static void replaceInstrumentation(Object activityThread) throws Exception {
        Field fInstrumentation = activityThread.getClass().getDeclaredField("mInstrumentation");
        fInstrumentation.setAccessible(true);
        Instrumentation mInstrumentation = (Instrumentation) fInstrumentation.get(activityThread);
        fInstrumentation.set(activityThread, new ProxyInstrumentation(mInstrumentation));
    }

    private static void replaceHandler(Object activityThread) throws Exception {
        Field fHandler = activityThread.getClass().getDeclaredField("mH");
        fHandler.setAccessible(true);
        Handler mHandler = (Handler) fHandler.get(activityThread);

        Class<?> chandler = Class.forName("android.os.Handler");
        Field fCallback = chandler.getDeclaredField("mCallback");
        fCallback.setAccessible(true);
        Handler.Callback mCallback = (Handler.Callback) fCallback.get(mHandler);
        fCallback.set(mHandler, new ProxyHandler(mCallback));
    }

    private static void replaceIActivityManager() throws Exception {
        Class<?> activityManagerClass;
        Field gDefaultField;
        try {
            activityManagerClass = Class.forName("android.app.ActivityManagerNative");
            gDefaultField = activityManagerClass.getDeclaredField("gDefault");
        } catch (Exception err1) {
            try {
                activityManagerClass = Class.forName("android.app.ActivityManager");
                gDefaultField = activityManagerClass.getDeclaredField("IActivityManagerSingleton");
            } catch (Exception err2) {
                return;
            }
        }
        gDefaultField.setAccessible(true);
        Object gDefault = gDefaultField.get(null);
        Class<?> singletonClass = Class.forName("android.util.Singleton");
        Field mInstanceField = singletonClass.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);
        Object mInstance = mInstanceField.get(gDefault);
        Object amProxy = Proxy.newProxyInstance(
                ClassUtils.getModuleClassLoader(),
                new Class[]{Class.forName("android.app.IActivityManager")},
                new IActivityManagerHandler(mInstance));
        mInstanceField.set(gDefault, amProxy);
    }

    private static void replaceIActivityTaskManager() throws Exception {
        Class<?> activityTaskManagerClass = Class.forName("android.app.ActivityTaskManager");
        Field fIActivityTaskManagerSingleton = activityTaskManagerClass.getDeclaredField("IActivityTaskManagerSingleton");
        fIActivityTaskManagerSingleton.setAccessible(true);
        Object singleton = fIActivityTaskManagerSingleton.get(null);
        Class<?> activityManagerClass;
        Field gDefaultField;
        try {
            activityManagerClass = Class.forName("android.app.ActivityManagerNative");
            gDefaultField = activityManagerClass.getDeclaredField("gDefault");
        } catch (Exception err1) {
            try {
                activityManagerClass = Class.forName("android.app.ActivityManager");
                gDefaultField = activityManagerClass.getDeclaredField("IActivityManagerSingleton");
            } catch (Exception err2) {
                return;
            }
        }
        gDefaultField.setAccessible(true);
        Object gDefault = gDefaultField.get(null);
        Class<?> singletonClass = Class.forName("android.util.Singleton");
        Field mInstanceField = singletonClass.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);
        singletonClass.getMethod("get").invoke(singleton);
        Object mDefaultTaskMgr = mInstanceField.get(singleton);
        Object proxy2 = Proxy.newProxyInstance(
                ClassUtils.getModuleClassLoader(),
                new Class[]{Class.forName("android.app.IActivityTaskManager")},
                new IActivityManagerHandler(mDefaultTaskMgr));
        mInstanceField.set(singleton, proxy2);
    }

}
