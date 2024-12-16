package top.sacz.xphelper.activity.replace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import top.sacz.xphelper.activity.ActivityProxyManager;
import top.sacz.xphelper.reflect.ClassUtils;


public class ProxyHandler implements Handler.Callback {
    private final Handler.Callback mDefault;

    public ProxyHandler(Handler.Callback defaultCallback) {
        mDefault = defaultCallback;
    }

    private static void processLaunchActivityItem(Object clientTransaction, Object item) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        Class<?> clz = item.getClass();
        Field fmIntent = clz.getDeclaredField("mIntent");
        fmIntent.setAccessible(true);
        Intent wrapper = (Intent) fmIntent.get(item);
        Log.d("ParasiticsUtils:", "handleMessage: target wrapper =" + wrapper);
        assert wrapper != null;
        //获取Bundle
        Bundle bundle = null;
        try {
            Field fExtras = Intent.class.getDeclaredField("mExtras");
            fExtras.setAccessible(true);
            bundle = (Bundle) fExtras.get(wrapper);
        } catch (Exception e) {
        }
        //设置
        if (bundle != null) {
            bundle.setClassLoader(ClassUtils.getClassLoader());
            if (wrapper.hasExtra(ActivityProxyManager.ACTIVITY_PROXY_INTENT)) {
                Intent realIntent = wrapper.getParcelableExtra(ActivityProxyManager.ACTIVITY_PROXY_INTENT);
                fmIntent.set(item, realIntent);
                // android 12
                if (Build.VERSION.SDK_INT >= 31) {
                    IBinder token = (IBinder) clientTransaction.getClass().getMethod("getActivityToken").invoke(clientTransaction);
                    Class<?> cActivityThread = Class.forName("android.app.ActivityThread");
                    Method currentActivityThread = cActivityThread.getDeclaredMethod("currentActivityThread");
                    currentActivityThread.setAccessible(true);
                    Object activityThread = currentActivityThread.invoke(null);
                    assert activityThread != null;
                    try {
                        Object acr = activityThread.getClass()
                                .getMethod("getLaunchingActivity", IBinder.class)
                                .invoke(activityThread, token);
                        if (acr != null) {
                            Field fAcrIntent = acr.getClass().getDeclaredField("intent");
                            fAcrIntent.setAccessible(true);
                            fAcrIntent.set(acr, realIntent);
                        }
                    } catch (NoSuchMethodException e) {
                        if (Build.VERSION.SDK_INT == 33) {
                            // expected behavior...?!
                        } else {
                            throw e;
                        }
                    }
                }
            }
        }
    }

    @SuppressLint({"PrivateApi", "DiscouragedPrivateApi"})
    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            // LAUNCH_ACTIVITY     sdk <= 8.0
            case 100:
                try {
                    Object record = msg.obj;
                    Field fIntent = record.getClass().getDeclaredField("intent");
                    fIntent.setAccessible(true);
                    Intent intent = (Intent) fIntent.get(record);
                    assert intent != null;
                    //获取bundle
                    Bundle bundle = null;
                    try {
                        Field fExtras = Intent.class.getDeclaredField("mExtras");
                        fExtras.setAccessible(true);
                        bundle = (Bundle) fExtras.get(intent);
                    } catch (Exception e) {
                    }
                    //设置
                    if (bundle != null) {
                        bundle.setClassLoader(ClassUtils.getClassLoader());
                        if (intent.hasExtra(ActivityProxyManager.ACTIVITY_PROXY_INTENT)) {
                            Intent rIntent = intent.getParcelableExtra(ActivityProxyManager.ACTIVITY_PROXY_INTENT);
                            fIntent.set(record, rIntent);
                        }
                    }
                } catch (Exception e) {
                }
                break;
            // EXECUTE_TRANSACTION    8.0+
            case 159:
                Object clientTransaction = msg.obj;
                try {
                    if (clientTransaction != null) {
//                        Method getCallbacksMethod = clientTransaction.getClass().getDeclaredMethod("getCallbacks");
                        Method getCallbacksMethod = Class.forName("android.app.servertransaction.ClientTransaction").getDeclaredMethod("getCallbacks");
                        getCallbacksMethod.setAccessible(true);
                        List<?> clientTransactionItems = (List<?>) getCallbacksMethod.invoke(clientTransaction);
                        if (clientTransactionItems == null && clientTransactionItems.isEmpty())
                            break;
                        for (Object item : clientTransactionItems) {
                            Class<?> clz = item.getClass();
                            if (clz.getName().contains("LaunchActivityItem")) {
                                processLaunchActivityItem(clientTransaction, item);
                            }
                        }
                    }
                } catch (Exception e) {
                }
                break;
//            default:
//                LogUtil.log("code -> " + msg.what);
        }
        return mDefault != null && mDefault.handleMessage(msg);
    }
}
