package top.sacz.timtool.hook;

public class TimVersion {
    public static long getTimVersion() {
        return HookEnv.getInstance().getVersionCode();
    }

    public static String getTimVersionName() {
        return HookEnv.getInstance().getVersionName();
    }

}
