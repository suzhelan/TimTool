package top.sacz.timtool.hook;

import android.content.Context;

/**
 * 单例模式 存储全局变量 包含app的context和版本号等信息
 */
public class HookEnv {
    private static final HookEnv INSTANCE = new HookEnv();
    /**
     * 当前宿主包名
     */
    private String currentHostAppPackageName;
    /**
     * 当前宿主进程名称
     */
    private String processName;
    /**
     * 模块路径
     */
    private String moduleApkPath;
    /**
     * 宿主apk路径
     */
    private String hostApkPath;
    /**
     * 宿主版本名称
     */
    private String versionName;
    /**
     * 宿主版本号
     */
    private int versionCode;
    /**
     * 全局的Context
     */
    private Context hostAppContext;

    private HookEnv() {
    }

    /**
     * 获取当前实例
     */
    public static HookEnv getInstance() {
        return INSTANCE;
    }

    public String getHostApkPath() {
        return hostApkPath;
    }

    public HookEnv setHostApkPath(String hostApkPath) {
        this.hostApkPath = hostApkPath;
        return this;
    }

    public String getVersionName() {
        return versionName;
    }

    public HookEnv setVersionName(String versionName) {
        this.versionName = versionName;
        return this;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public HookEnv setVersionCode(int versionCode) {
        this.versionCode = versionCode;
        return this;
    }

    public Context getHostAppContext() {
        return hostAppContext;
    }

    public HookEnv setHostAppContext(Context hostAppContext) {
        this.hostAppContext = hostAppContext;
        return this;
    }

    public String getModuleApkPath() {
        return moduleApkPath;
    }

    public HookEnv setModuleApkPath(String moduleApkPath) {
        this.moduleApkPath = moduleApkPath;
        return this;
    }

    public String getProcessName() {
        return processName;
    }

    public HookEnv setProcessName(String processName) {
        this.processName = processName;
        return this;
    }

    public String getCurrentHostAppPackageName() {
        return currentHostAppPackageName;
    }

    public HookEnv setCurrentHostAppPackageName(String currentHostAppPackageName) {
        this.currentHostAppPackageName = currentHostAppPackageName;
        return this;
    }

}
