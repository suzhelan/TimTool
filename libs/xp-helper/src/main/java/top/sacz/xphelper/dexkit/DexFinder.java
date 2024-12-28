package top.sacz.xphelper.dexkit;

import org.luckypray.dexkit.DexKitBridge;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import top.sacz.xphelper.XpHelper;
import top.sacz.xphelper.dexkit.cache.DexKitCache;

public class DexFinder {

    private static final AtomicBoolean isLoadLibrary = new AtomicBoolean();
    private static DexKitBridge dexKitBridge;
    private static Timer timer;

    private static long autoCloseTime = 10 * 1000;

    /**
     * 设置关闭的时间 超过此时间没有使用dexkit 则自动关闭 (其实有可能查找过程中被关闭)
     * 所以确保每次调用getDexKitBridge后十秒内完成查找
     * 默认十秒 设置为0则不会自动关闭
     *
     * @param time 单位毫秒
     */
    public static void setAutoCloseTime(long time) {
        autoCloseTime = time;
    }

    /**
     * 初始化dexkit
     *
     * @param apkPath
     */
    public synchronized static void create(String apkPath) {
        if (dexKitBridge != null) {
            return;
        }
        if (!isLoadLibrary.getAndSet(true)) {
            System.loadLibrary("dexkit");
        }
        dexKitBridge = DexKitBridge.create(apkPath);
    }

    /**
     * 得到dexkit实例
     */
    public static DexKitBridge getDexKitBridge() {
        if (dexKitBridge == null) {
            create(XpHelper.context.getApplicationInfo().sourceDir);
        }
        resetTimer();
        return dexKitBridge;
    }

    /**
     * 清空缓存
     */
    public static void clearCache() {
        DexKitCache.clearCache();
    }

    private static void resetTimer() {
        if (autoCloseTime <= 0) {
            return;
        }
        //如果存在则取消 达到重置时间的效果
        if (timer != null) {
            timer.cancel();
        }
        //定时 10秒钟后关闭
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                close();
            }
        }, autoCloseTime); // 10 seconds
    }

    /**
     * 释放dexkit资源
     */
    public static void close() {
        if (dexKitBridge != null) {
            dexKitBridge.close();
            dexKitBridge = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
