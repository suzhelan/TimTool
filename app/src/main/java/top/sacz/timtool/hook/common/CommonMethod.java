package top.sacz.timtool.hook.common;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 常用方法
 */
public class CommonMethod {

    /**
     * 查找context create的方法 通用方法 可以过掉大多数加固
     */
    public static Method getContextCreateMethod(XC_LoadPackage.LoadPackageParam loadParam) {
        try {
            if (loadParam.appInfo.name != null) {
                Class<?> clz = loadParam.classLoader.loadClass(loadParam.appInfo.name);
                try {
                    return clz.getDeclaredMethod("attachBaseContext", Context.class);
                } catch (Throwable i) {
                    try {
                        return clz.getDeclaredMethod("onCreate");
                    } catch (Throwable e) {
                        try {
                            return clz.getSuperclass().getDeclaredMethod("attachBaseContext", Context.class);
                        } catch (Throwable m) {
                            return clz.getSuperclass().getDeclaredMethod("onCreate");
                        }
                    }
                }

            }
        } catch (Throwable o) {
            XposedBridge.log("[error]" + Log.getStackTraceString(o));
        }
        try {
            return ContextWrapper.class.getDeclaredMethod("attachBaseContext", Context.class);
        } catch (Throwable u) {
            XposedBridge.log("[error]" + Log.getStackTraceString(u));
            return null;
        }
    }
}
