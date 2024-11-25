package top.sacz.xphelper.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XposedHelpers;
import top.sacz.xphelper.exception.ReflectException;

public abstract class BaseFinder<T extends Member> {
    private static final Map<String, List<Field>> FIELD_CACHE = new HashMap<>();
    private static final Map<String, List<Method>> METHOD_CACHE = new HashMap<>();

    private static final Map<String, List<Constructor<?>>> CONSTRUCTOR_CACHE = new HashMap<>();

    protected Class<?> declaringClass;

    protected List<T> result = new ArrayList<>();

    private boolean isFind = false;

    protected BaseFinder() {

    }

    protected void writeToFieldCache(List<Field> value) {
        String sign = buildSign();
        FIELD_CACHE.put(sign, value);
    }

    protected List<Field> findFiledCache() {
        String sign = buildSign();
        if (FIELD_CACHE.containsKey(sign)) {
            return FIELD_CACHE.get(sign);
        }
        return null;
    }

    protected void writeToMethodCache(List<Method> value) {
        String sign = buildSign();
        METHOD_CACHE.put(sign, value);
    }

    protected List<Method> findMethodCache() {
        String sign = buildSign();
        if (METHOD_CACHE.containsKey(sign)) {
            return METHOD_CACHE.get(sign);
        }
        return null;
    }

    protected void writeToConstructorCache(List<Constructor<?>> value) {
        String sign = buildSign();
        CONSTRUCTOR_CACHE.put(sign, value);
    }

    protected List<Constructor<?>> findConstructorCache() {
        String sign = buildSign();
        if (CONSTRUCTOR_CACHE.containsKey(sign)) {
            return CONSTRUCTOR_CACHE.get(sign);
        }
        return null;
    }

    protected void findComplete() {
        this.isFind = true;
        for (T member : result) {
            XposedHelpers.callMethod(member, "setAccessible", true);
        }
    }

    public abstract BaseFinder<T> find();

    public abstract String buildSign();


    public List<T> getResult() {
        if (!isFind) {
            find();
        }
        return result;
    }

    public T first() {
        if (!isFind) {
            find();
        }
        if (result.isEmpty() && declaringClass != Object.class) {
            //如果查找不到 向父类查找
            declaringClass = declaringClass.getSuperclass();
            return find().first();
        }
        if (result.isEmpty()) {
            throw new ReflectException("can not find " + buildSign());
        }
        return result.get(0);
    }

    public T last() {
        if (!isFind) {
            find();
        }
        if (result.isEmpty() && declaringClass != Object.class) {
            //如果查找不到 向父类查找
            declaringClass = declaringClass.getSuperclass();
            return find().first();
        }
        if (result.isEmpty()) {
            throw new ReflectException("can not find " + buildSign());
        }
        return result.get(result.size() - 1);
    }
}
