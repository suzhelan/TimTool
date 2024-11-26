package top.sacz.timtool.hook;

public class TimVersion {
    public static long getTimVersion() {
        return HookEnv.getVersionCode();
    }

    public static String getTimVersionName() {
        return HookEnv.getVersionName();
    }

}
