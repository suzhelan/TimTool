package top.sacz.timtool.hook.base;

import de.robv.android.xposed.XC_MethodHook;

public abstract class BaseSwitchFunctionHookItem extends BaseHookItem {

    private boolean enabled = isLoadedByDefault();

    /**
     * 是否默认加载
     */
    public boolean isLoadedByDefault() {
        return false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected final void tryExecute(XC_MethodHook.MethodHookParam param, HookAction hookAction) {
        //只有在开启了的情况下才执行
        if (isEnabled()) {
            super.tryExecute(param, hookAction);
        }
    }

}
