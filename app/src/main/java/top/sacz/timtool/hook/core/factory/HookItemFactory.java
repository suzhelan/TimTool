package top.sacz.timtool.hook.core.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.sacz.timtool.hook.base.BaseHookItem;
import top.sacz.timtool.hook.gen.HookItemEntryListKt;

public class HookItemFactory {
    private static final Map<Class<? extends BaseHookItem>, BaseHookItem> ITEM_MAP = new HashMap<>();

    static {
        BaseHookItem[] items = HookItemEntryListKt.getAllHookItems();
        //进行一个中文排序
        /*List.of(items).sort((o1, o2) -> {
            Comparator<Object> compare = Collator.getInstance(java.util.Locale.CHINA);
            return compare.compare(o1.getItemName(), o2.getItemName());
        });*/
        //添加到map中
        for (BaseHookItem item : items) {
            ITEM_MAP.put(item.getClass(), item);
        }
    }

    public static List<BaseHookItem> getAllItems() {
        return List.copyOf(ITEM_MAP.values());
    }

    public static <T extends BaseHookItem> T getItem(Class<T> clazz) {
        BaseHookItem item = ITEM_MAP.get(clazz);
        return clazz.cast(item);
    }
}
