package top.sacz.xphelper.dexkit;

import org.luckypray.dexkit.DexKitBridge;

import top.sacz.xphelper.XpHelper;

public class DexFinder {

    private static DexKitBridge dexKitBridge;

    public synchronized static void create(String apkPath) {
        if (dexKitBridge != null) {
            throw new RuntimeException("DexKitBridge has been created");
        }
        System.loadLibrary("dexkit");
        dexKitBridge = DexKitBridge.create(apkPath);
    }

    public static DexKitBridge getDexKitBridge() {
        if (dexKitBridge == null) {
            create(XpHelper.context.getApplicationInfo().sourceDir);
        }
        return dexKitBridge;
    }


    public static void close() {
        dexKitBridge.close();
        dexKitBridge = null;
    }

}
