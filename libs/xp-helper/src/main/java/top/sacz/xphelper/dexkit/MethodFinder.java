package top.sacz.xphelper.dexkit;

import org.luckypray.dexkit.query.FindMethod;
import org.luckypray.dexkit.query.enums.MatchType;
import org.luckypray.dexkit.query.matchers.MethodMatcher;
import org.luckypray.dexkit.result.MethodData;
import org.luckypray.dexkit.result.MethodDataList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import top.sacz.xphelper.dexkit.cache.DexKitCache;
import top.sacz.xphelper.reflect.ClassUtils;

public class MethodFinder {

    public static MethodBridge build() {
        return new MethodBridge();
    }

    public static class MethodBridge {

        private Class<?> declaredClass;//方法声明类

        private Class<?>[] parameters;//方法的参数列表

        private String methodName;//方法名称

        private Class<?> returnType;//方法的返回值类型

        private String[] stringContent;//方法中使用的字符串列表

        private Method[] invokeMethods;//方法中调用的方法列表

        private Method[] callMethods;//调用了该方法的方法列表

        private long[] usingNumbers;//方法中使用的数字列表

        private int paramCount;//参数数量

        private boolean isParamCount = false;

        private int modifiers;//修饰符

        private boolean isModifiers = false;

        private MatchType matchType;

        private String[] searchPackages;

        private String[] excludePackages;


        public MethodBridge declaredClass(Class<?> declaredClass) {
            this.declaredClass = declaredClass;
            return this;
        }

        public MethodBridge parameters(Class<?>... parameters) {
            this.parameters = parameters;
            return this;
        }

        public MethodBridge methodName(String name) {
            methodName = name;
            return this;
        }

        public MethodBridge returnType(Class<?> returnTypeClass) {
            returnType = returnTypeClass;
            return this;
        }

        public MethodBridge invokeMethods(Method... methods) {
            invokeMethods = methods;
            return this;
        }

        public MethodBridge callMethods(Method... methods) {
            this.callMethods = methods;
            return this;
        }

        public MethodBridge usingNumbers(long... numbers) {
            this.usingNumbers = numbers;
            return this;
        }

        public MethodBridge paramCount(int count) {
            this.paramCount = count;
            this.isParamCount = true;
            return this;
        }


        public MethodBridge useString(String... strings) {
            this.stringContent = strings;
            return this;
        }

        public MethodBridge modifiers(int modifiers, MatchType matchType) {
            this.modifiers = modifiers;
            this.isModifiers = true;
            this.matchType = matchType;
            return this;
        }

        public MethodBridge searchPackages(String... strings) {
            this.searchPackages = strings;
            return this;
        }

        public MethodBridge excludePackages(String... strings) {
            this.excludePackages = strings;
            return this;
        }


        private FindMethod buildFindMethod() {
            FindMethod findMethod = FindMethod.create();
            if (searchPackages != null) {
                findMethod.searchPackages(searchPackages);
            }
            if (excludePackages != null) {
                findMethod.excludePackages(excludePackages);
            }
            return findMethod.matcher(buildMethodMatcher());
        }

        private MethodMatcher buildMethodMatcher() {
            MethodMatcher methodMatcher = MethodMatcher.create();
            if (declaredClass != null) {
                methodMatcher.declaredClass(declaredClass);
            }
            if (methodName != null && !methodName.isEmpty()) {
                methodMatcher.name(methodName);
            }
            if (returnType != null) {
                methodMatcher.returnType(returnType);
            }
            if (stringContent != null && stringContent.length != 0) {
                methodMatcher.usingStrings(stringContent);
            }
            if (parameters != null) {
                for (Class<?> parameterClass : parameters) {
                    methodMatcher.addParamType(parameterClass);
                }
            }
            if (invokeMethods != null) {
                for (Method invokeMethod : invokeMethods) {
                    methodMatcher.addInvoke(MethodMatcher.create(invokeMethod));
                }
            }
            if (callMethods != null) {
                for (Method callMethod : callMethods) {
                    methodMatcher.addCaller(MethodMatcher.create(callMethod));
                }
            }
            if (usingNumbers != null) {
                for (long usingNumber : usingNumbers) {
                    methodMatcher.addUsingNumber(usingNumber);
                }
            }
            if (isParamCount) {
                methodMatcher.paramCount(paramCount);
            }
            if (isModifiers) {
                methodMatcher.modifiers(modifiers, matchType);
            }
            return methodMatcher;
        }

        public List<Method> find() throws Exception {
            //先查缓存
            List<Method> cache = DexKitCache.getMethodList(toString());
            if (!cache.isEmpty()) {
                return cache;
            }
            ArrayList<Method> methods = new ArrayList<>();
            //使用dexkit查找方法
            MethodDataList methodDataList = DexFinder.getDexKitBridge().findMethod(buildFindMethod());
            if (methodDataList.isEmpty()) {
                return methods;
            }
            for (MethodData methodData : methodDataList) {
                Method method = methodData.getMethodInstance(ClassUtils.getClassLoader());
                method.setAccessible(true);
                methods.add(method);
            }
            //写入缓存
            DexKitCache.putMethodList(toString(), methods);
            return methods;
        }

        public Method firstOrNull() throws Exception {
            List<Method> methods = find();
            if (methods.isEmpty()) {
                return null;
            }
            return methods.get(0);
        }

        public Method first() throws Exception {
            List<Method> methods = find();
            if (methods.isEmpty()) {
                throw new NoSuchMethodException("No method found :" + this);
            }
            return methods.get(0);
        }


        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            //拼入字段值 无需拼入字段名 如果为空则不拼入
            if (declaredClass != null) {
                builder.append(declaredClass.getName());
            }
            if (methodName != null && !methodName.isEmpty()) {
                builder.append(methodName);
            }
            if (returnType != null) {
                builder.append(returnType.getName());
            }
            if (parameters != null) {
                builder.append(Arrays.toString(parameters));
            }
            if (invokeMethods != null) {
                builder.append(Arrays.toString(invokeMethods));
            }
            if (callMethods != null) {
                builder.append(Arrays.toString(callMethods));
            }
            if (usingNumbers != null) {
                builder.append(Arrays.toString(usingNumbers));
            }
            if (isParamCount) {
                builder.append(paramCount);
            }
            if (isModifiers) {
                builder.append(modifiers);
            }
            if (stringContent != null && stringContent.length != 0) {
                builder.append(Arrays.toString(stringContent));
            }
            if (searchPackages != null) {
                builder.append(Arrays.toString(searchPackages));
            }
            if (excludePackages != null) {
                builder.append(Arrays.toString(excludePackages));
            }
            return builder.toString();
        }
    }
}
