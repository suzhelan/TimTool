package top.sacz.timtool.hook.core.factory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.sacz.timtool.hook.base.BaseHookItem;
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem;
import top.sacz.timtool.hook.gen.HookItemEntryList;

public class HookItemFactory {
    private static final Map<Class<? extends BaseHookItem>, BaseHookItem> ITEM_MAP = new HashMap<>();

    static {
        List<BaseHookItem> items = HookItemEntryList.getAllHookItems();
        //添加到map中
        for (BaseHookItem item : items) {
            ITEM_MAP.put(item.getClass(), item);
        }
        items.sort(Comparator.comparing(BaseHookItem::getSimpleName));
    }

    public static BaseSwitchFunctionHookItem findHookItemByPath(String path) {
        for (BaseHookItem item : ITEM_MAP.values()) {
            if (item.getPath().equals(path)) {
                return (BaseSwitchFunctionHookItem) item;
            }
        }
        return null;
    }

    public static List<BaseSwitchFunctionHookItem> getAllSwitchFunctionItemList() {
        ArrayList<BaseSwitchFunctionHookItem> result = new ArrayList<>();
        for (BaseHookItem item : ITEM_MAP.values()) {
            if (item instanceof BaseSwitchFunctionHookItem) {
                result.add((BaseSwitchFunctionHookItem) item);
            }
        }
        return result;
    }

    public static List<BaseHookItem> getAllItemList() {
        return List.copyOf(ITEM_MAP.values());
    }

    public static <T extends BaseHookItem> T getItem(Class<T> clazz) {
        BaseHookItem item = ITEM_MAP.get(clazz);
        return clazz.cast(item);
    }
}
