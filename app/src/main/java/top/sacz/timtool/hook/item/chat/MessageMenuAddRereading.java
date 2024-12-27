package top.sacz.timtool.hook.item.chat;

import android.view.View;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;
import java.util.List;

import de.robv.android.xposed.XposedHelpers;
import top.sacz.timtool.R;
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem;
import top.sacz.timtool.hook.core.annotation.HookItem;
import top.sacz.timtool.hook.item.api.QQCustomMenu;
import top.sacz.timtool.hook.item.chat.rereading.RereadingMessageClickListener;
import top.sacz.timtool.hook.qqapi.ContactUtils;
import top.sacz.xphelper.reflect.ClassUtils;
import top.sacz.xphelper.reflect.FieldUtils;
import top.sacz.xphelper.reflect.Ignore;
import top.sacz.xphelper.reflect.MethodUtils;


@HookItem("辅助功能/聊天/消息长按菜单添加复读")
public class MessageMenuAddRereading extends BaseSwitchFunctionHookItem {

    @Override
    public void loadHook(@NonNull ClassLoader classLoader) throws Throwable {
        //qq创建长按菜单的方法
        Method setMenuMethod = MethodUtils.create("com.tencent.qqnt.aio.menu.ui.QQCustomMenuExpandableLayout")
                .methodName("setMenu")
                .params(Ignore.class, View.class)
                .returnType(void.class)
                .first();
        hookBefore(setMenuMethod, param -> {
            Object itemListWrapper = param.args[0];
            //这个参数第一个为list的字段是长按的菜单列表
            List<Object> itemList = FieldUtils.create(itemListWrapper)
                    .fieldType(List.class)
                    .firstValue(itemListWrapper);
            //获取第一个菜单
            Object item = itemList.get(0);
            //解析出msgRecord
            Object aioMsgItem = FieldUtils.getFirstType(item, ClassUtils.findClass("com.tencent.mobileqq.aio.msg.AIOMsgItem"));
            Object msgRecord = XposedHelpers.callMethod(aioMsgItem, "getMsgRecord");
            //构建长按菜单选项
            Object itemButton = QQCustomMenu.createMenuItem(aioMsgItem, R.drawable.repeat, "复读",
                    () -> {
                        RereadingMessageClickListener listener = new RereadingMessageClickListener(msgRecord, ContactUtils.getCurrentContact());
                        listener.rereading();
                        return null;
                    });
            //添加选项到菜单
            itemList.add(0, itemButton);
        });
    }
}
