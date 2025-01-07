package top.sacz.timtool.hook.item.experiment;

import androidx.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem;
import top.sacz.timtool.hook.core.annotation.HookItem;
import top.sacz.xphelper.reflect.ConstructorUtils;
import top.sacz.xphelper.reflect.FieldUtils;


@HookItem("辅助功能/实验性/取消转发9人上限")
public class CancelForwardPeopleNumberLimit extends BaseSwitchFunctionHookItem {

    @Override
    public void loadHook(@NonNull ClassLoader classLoader) throws Throwable {
        Constructor<?> forwardRecentActivityInit = ConstructorUtils.create("com.tencent.mobileqq.activity.ForwardRecentActivity")
                .first();
        hookAfter(forwardRecentActivityInit, param -> FieldUtils.create(param.thisObject)
                .fieldName("mForwardTargetMap")
                .fieldType(Map.class)
                .setFirst(param.thisObject, new UnlimitedMap<String, Object>()));
    }

    /**
     * kotlin写下面这段 会提示父类没有size方法无法重写 所以我用java写了
     * class UnlimitedMap<K, V> : LinkedHashMap<K, V>() {
     * override fun size(): Int {
     * return 0
     * }
     * }
     *
     * @param <K>
     * @param <V>
     */
    static class UnlimitedMap<K, V> extends LinkedHashMap<K, V> {

        @Override
        public int size() {
            int size = super.size();
            //qq的逻辑 this.mForwardTargetMap.size() != 9
            if (size == 9) {
                return 8;
            }
            return size;
        }
    }
}
