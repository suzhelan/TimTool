package top.sacz.xphelper.dexkit.cache;


import android.util.Log;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import top.sacz.xphelper.reflect.ClassUtils;
import top.sacz.xphelper.reflect.MethodUtils;
import top.sacz.xphelper.util.KvHelper;

public class DexKitCache {

    public static void putMethodList(String key, List<Method> methodList) {
        KvHelper kvHelper = new KvHelper("DexKitCache");
        ArrayList<String> infoList = new ArrayList<>();
        for (Method method : methodList) {
            infoList.add(getMethodInfoJSON(method));
        }
        kvHelper.put(key, infoList);
    }

    public static List<Method> getMethodList(String key) {
        KvHelper kvHelper = new KvHelper("DexKitCache");
        ArrayList<Method> result = new ArrayList<>();
        ArrayList<String> methodInfoList = kvHelper.getObject(key, new TypeReference<>() {
        });
        Log.d("Cache", "getMethodList: " + key);
        if (methodInfoList != null) {
            for (String methodInfo : methodInfoList) {
                result.add(findMethodByJSON(methodInfo));
            }
        }
        return result;
    }

    private static Method findMethodByJSON(String methodInfoStrJSON) {
        JSONObject methodInfo = JSONObject.parseObject(methodInfoStrJSON);
        String methodName = methodInfo.getString("MethodName");
        String declareClass = methodInfo.getString("DeclareClass");
        String ReturnType = methodInfo.getString("ReturnType");
        JSONArray methodParams = methodInfo.getJSONArray("Params");
        Class<?>[] params = new Class[methodParams.size()];
        for (int i = 0; i < params.length; i++) {
            params[i] = ClassUtils.findClass(methodParams.getString(i));
        }
        return MethodUtils.create(declareClass)
                .methodName(methodName)
                .returnType(ClassUtils.findClass(ReturnType))
                .params(params)
                .first();
    }

    private static String getMethodInfoJSON(Method method) {
        method.setAccessible(true);
        JSONObject result = new JSONObject();
        String methodName = method.getName();
        String declareClass = method.getDeclaringClass().getName();
        Class<?>[] methodParams = method.getParameterTypes();
        JSONArray params = new JSONArray();
        for (Class<?> type : methodParams) {
            params.add(type.getName());
        }
        result.put("DeclareClass", declareClass);
        result.put("MethodName", methodName);
        result.put("Params", params);
        result.put("ReturnType", method.getReturnType().getName());
        return result.toString();
    }
}
