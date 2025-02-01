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

/**
 * 超精妙设计
 */
public abstract class BaseFinder<T extends Member> {
    private static final Map<String, List<Field>> FIELD_CACHE = new HashMap<>();
    private static final Map<String, List<Method>> METHOD_CACHE = new HashMap<>();

    private static final Map<String, List<Constructor<?>>> CONSTRUCTOR_CACHE = new HashMap<>();

    protected Class<?> declaringClass;

    protected String fromClassName;

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

    /**
     * 由子类实现的实际查找方法
     *
     * @return this
     */
    public abstract BaseFinder<T> find();

    /**
     * 构建缓存签名
     */
    public abstract String buildSign();

    /**
     * 进行查找,查找后如果没有结果会自动向父类查找
     *
     * @return this
     */
    public BaseFinder<T> findAndSuper() {
        if (this.isFind) {
            return this;
        }
        find();
        if (result.isEmpty() && getDeclaringClass() != Object.class) {
            //如果查找不到 向父类查找
            return setDeclaringClass(getDeclaringClass().getSuperclass())
                    .findAndSuper();
        }
        //彻底的查找结束
        this.isFind = true;
        //设置可访问
        for (T member : result) {
            XposedHelpers.callMethod(member, "setAccessible", true);
        }

        return this;
    }

    /**
     * 在所有获取结果的方法都需要调用 不然不会查找 {@link #findAndSuper();}
     *
     * @return 结果列表
     */
    public List<T> getResult() {
        findAndSuper();
        return result;
    }

    public T get(int index) {
        if (getResult().isEmpty()) {
            throw new ReflectException("can not find " + buildSign());
        }
        if (result.size() <= index) {
            throw new ReflectException("The resulting number is " + result.size() + " and the index is " + index + ":" + buildSign());
        }
        return result.get(index);
    }

    public T first() {
        if (getResult().isEmpty()) {
            throw new ReflectException("can not find " + buildSign());
        }
        return result.get(0);
    }

    public T last() {
        if (getResult().isEmpty()) {
            throw new ReflectException("can not find " + buildSign());
        }
        return result.get(result.size() - 1);
    }

    protected Class<?> getDeclaringClass() {
        return declaringClass;
    }

    protected BaseFinder<T> setDeclaringClass(Class<?> declaringClass) {
        this.declaringClass = declaringClass;
        if (fromClassName == null) {
            this.fromClassName = declaringClass.getName();
        }
        return this;
    }
}
