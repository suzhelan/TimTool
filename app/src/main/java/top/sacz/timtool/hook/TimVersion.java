package top.sacz.timtool.hook;

public class TimVersion {

    public static final long TIM_4_0_95_BETA = 4001;
    public static final long TIM_4_0_95 = 4008;
    public static final long TIM_4_0_96 = 4010;
    public static final long TIM_4_0_97 = 4011;
    public static final long TIM_4_0_98 = 4012;

    public static final int QQ_9_0_0 = 5282;
    public static final int QQ_9_0_15 = 5626;
    public static final int QQ_9_0_17 = 5714;
    public static final int QQ_9_0_20 = 5844;
    public static final long QQ_9_0_25 = 5932;
    public static final long QQ_9_0_30 = 6038;
    public static final long QQ_9_0_35 = 6148;
    public static final long QQ_9_0_50 = 6236;
    public static final long QQ_9_0_55 = 6368;
    public static final long QQ_9_0_56 = 6372;
    public static final long QQ_9_0_60 = 6458;
    public static final long QQ_9_0_65 = 6588;
    public static final long QQ_9_0_70 = 6676;
    public static final long QQ_9_0_73 = 6722;
    public static final long QQ_9_0_75 = 6792;
    public static final long QQ_9_0_95 = 7368;
    public static final long QQ_9_1_00 = 7518;
    public static final long QQ_9_1_20 = 8156;
    public static final long QQ_9_1_28 = 8398;
    public static final long QQ_9_1_30 = 8496;

    public static String getAppName() {
        return HookEnv.getAppName();
    }

    public static String getVersionName() {
        return HookEnv.getVersionName();
    }

    public static int getVersionCode() {
        return HookEnv.getVersionCode();
    }

    public static boolean isQQ() {
        return HookEnv.isQQ();
    }

    public static boolean isTim() {
        return HookEnv.isTim();
    }

    public static long getQQVersion() {
        if (HookEnv.isQQ()) {
            return HookEnv.getVersionCode();
        }
        return 0;
    }

    public static String getQQVersionName() {
        if (HookEnv.isQQ()) {
            return HookEnv.getVersionName();
        }
        return "";
    }

    public static long getTimVersion() {
        if (HookEnv.isTim()) {
            return HookEnv.getVersionCode();
        }
        return 0;
    }

    public static String getTimVersionName() {
        if (HookEnv.isTim()) {
            return HookEnv.getVersionName();
        }
        return "";
    }

}
