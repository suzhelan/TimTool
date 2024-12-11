package top.sacz.timtool.hook.item.chat.test;

import androidx.annotation.NonNull;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem;
import top.sacz.timtool.hook.util.LogUtils;
import top.sacz.timtool.hook.util.OutputHookStack;

//@HookItem("辅助功能/聊天/允许预览所有链接")
// TODO LinkInfo是在Native做解析的 在Java层每次主动解析会造成不理想的性能开销 暂时搁置 等待合适的方案
public class AllowsAllLinkPreviewed extends BaseSwitchFunctionHookItem {

    @Override
    public void loadHook(@NonNull ClassLoader loader) throws Throwable {

        XposedHelpers.findAndHookConstructor("com.tencent.qqnt.kernel.nativeinterface.TextElement", loader, java.lang.String.class, int.class, long.class, long.class, java.lang.String.class, java.lang.Integer.class, java.lang.Long.class, loader.loadClass("com.tencent.qqnt.kernel.nativeinterface.LinkInfo"), java.lang.Long.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (param.args[7] != null) {
                    OutputHookStack.OutputObjectField(param.args[7]);
                    LogUtils.addRunLog("LinkInfo", LogUtils.getCallStack());
                }
            }
        });
        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.aio.msglist.holder.component.text.b.c", loader, "e", android.content.Context.class, android.widget.RelativeLayout.class, loader.loadClass("com.tencent.qqnt.kernel.nativeinterface.LinkInfo"), boolean.class, java.lang.String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.aio.utils.d", loader, "P", loader.loadClass("com.tencent.qqnt.kernel.nativeinterface.MsgRecord"), new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
    }
}
