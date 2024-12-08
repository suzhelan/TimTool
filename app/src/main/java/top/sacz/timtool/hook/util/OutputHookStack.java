package top.sacz.timtool.hook.util;

import android.annotation.SuppressLint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

/**
 * XPosed Hook Java分析方法 对象等
 *
 * @author 言子 , asmclk@163.com
 * @version 2.0
 */
public class OutputHookStack {
    /*
     * 输出的日志路径 需要以/结尾的路径
     */
    private static final String OUTPUT_LOG_DIR = PathTool.getModuleDataPath() + "/";

    /**
     * 输出一个对象所有的字段
     *
     * @param obj 对象
     */
    public static void OutputObjectField(Object obj) {
        if (obj == null) throw new RuntimeException("对象为空 无法输出字段");
        StringBuilder stringBuffer = new StringBuilder(getTime());
        stringBuffer.append("\n-------------------------------------------------------------------");
        stringBuffer.append("\n属性 : \n\n");
        Class<?> ThisClass = obj.getClass();

        for (Field f : ThisClass.getDeclaredFields()) {
            f(stringBuffer, f);
            try {
                Object res;
                if (Modifier.toString(f.getModifiers()).contains("static")) {
                    res = f.get(null);
                    if (res != null && res.getClass().getSimpleName().matches("String|CharSequence")) {
                        stringBuffer.append("\"").append(res).append("\"");
                    } else {
                        stringBuffer.append(res);
                    }
                } else {

                    res = f.get(obj);

                    if (res != null && res.getClass().getSimpleName().matches("String|CharSequence")) {
                        stringBuffer.append("\"").append(res).append("\"");
                    } else {
                        if (f.getType().isArray()) {
                            stringBuffer.append("length:").append(Array.getLength(res));

                        }
                        stringBuffer.append(res);
                    }
                }

            } catch (Exception e) {
                stringBuffer.append("动态反射属性异常 : ").append(e);
            }
            stringBuffer.append(";");
            stringBuffer.append("\n");
        }

        stringBuffer.append("\n-------------------------------------------------------------------\n\n");

        writeFileText(OUTPUT_LOG_DIR + "对象反射记录/" + ThisClass.getName() + ".java", stringBuffer.toString());
    }

    /**
     * Xposed hook 获取静态方法调用栈和运行时静态字段信息
     * 静态方法无运行时对象(param.thisObject) 不能获取运行时当前对象静态字段
     */
    public static void OutputStaticMethodStack(XC_MethodHook.MethodHookParam param) {
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        StringBuilder stringBuffer = new StringBuilder(getTime());
        stringBuffer.append("\n-------------------------------------------------------------------");
        stringBuffer.append("\n属性 : \n\n");
        Class<?> ThisClass = param.method.getDeclaringClass();
        for (Field f : ThisClass.getDeclaredFields()) {
            f(stringBuffer, f);
            try {
                if (Modifier.toString(f.getModifiers()).contains("static")) {
                    Object res = f.get(null);
                    if (res != null && res.getClass().getSimpleName().matches("String|CharSequence")) {
                        stringBuffer.append("\"").append(res).append("\"");
                    } else {
                        stringBuffer.append(res);
                    }
                } else {
                    if (f.getType().getSimpleName().matches("String|CharSequence")) {
                        stringBuffer.append("\"").append("{静态方法无运行时对象}").append("\"");
                    } else {
                        stringBuffer.append("静态方法无运行时对象");
                    }
                }
            } catch (Exception e) {
                stringBuffer.append("动态反射字段异常 ").append(e);
            }
            stringBuffer.append(";");
            stringBuffer.append("\n");
        }
        getHookMethodRunTimeParam(param, stringBuffer);
        stringBuffer.append("调用栈(从上往下调用) : \n");
        e(param, stackElements, stringBuffer, ThisClass);
    }

    private static void e(XC_MethodHook.MethodHookParam param, StackTraceElement[] stackElements, StringBuilder stringBuffer, Class<?> thisClass) {
        if (stackElements.length != 0) {
            getMethodStack(param, stackElements, stringBuffer, thisClass);
        } else {
            stringBuffer.append("StackTraceElement[]为空");
        }
        stringBuffer.append("\n\n-------------------------------------------------------------------");
        stringBuffer.append("\n\n\n");

        writeFileText(OUTPUT_LOG_DIR + "动态反射记录/" + thisClass.getName() + "." + param.method.getName() + ".java", stringBuffer.toString());
    }

    private static void f(StringBuilder stringBuffer, Field f) {
        f.setAccessible(true);
        stringBuffer.append("   ");
        stringBuffer.append(Modifier.toString(f.getModifiers())).append(" ");
        String type = f.getType().getName().startsWith("java.lang.") ? f.getType().getSimpleName() : f.getType().getName();
        stringBuffer.append(type).append(" ").append(f.getName());
        stringBuffer.append(" = ");
    }

    /**
     * Xposed Hook输出方法调用栈和运行时对象的所有字段
     */
    public static void OutputMethodStack(XC_MethodHook.MethodHookParam param) {
        //静态方法交给输出调用栈处理
        if (Modifier.toString(param.method.getModifiers()).contains("static")) {
            OutputStaticMethodStack(param);
            return;
        }
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        StringBuilder stringBuffer = new StringBuilder(getTime());
        stringBuffer.append("\n-------------------------------------------------------------------");
        stringBuffer.append("\n属性 : \n\n");
        Class<?> ThisClass = param.thisObject.getClass();
        for (Field f : ThisClass.getDeclaredFields()) {
            f(stringBuffer, f);
            try {
                if (Modifier.toString(f.getModifiers()).contains("static")) {
                    Object res = f.get(null);
                    if (res != null && res.getClass().getSimpleName().matches("String|CharSequence")) {
                        stringBuffer.append("\"").append(res).append("\"");
                    } else {
                        stringBuffer.append(res);
                    }
                } else {
                    Object res = f.get(param.thisObject);
                    if (res != null && res.getClass().getSimpleName().matches("String|CharSequence")) {
                        stringBuffer.append("\"").append(res).append("\"");
                    } else {
                        stringBuffer.append(res);
                    }
                }
            } catch (Exception e) {
                stringBuffer.append("动态反射属性异常 : ").append(e);
            }
            stringBuffer.append(";");
            stringBuffer.append("\n");
        }
        getHookMethodRunTimeParam(param, stringBuffer);
        stringBuffer.append("调用栈 : \n");
        e(param, stackElements, stringBuffer, ThisClass);
    }

    private static void getHookMethodRunTimeParam(XC_MethodHook.MethodHookParam param, StringBuilder stringBuffer) {
        stringBuffer.append("\n方法返回:");
        stringBuffer.append(param.getResult());
        stringBuffer.append("\n");
        Member m2 = param.method;
        if (m2 instanceof Constructor<?> constructor) {
            Class<?>[] methodParams = constructor.getParameterTypes();
            if (methodParams.length != 0) {
                stringBuffer.append("方法运行时参数 : \n");
            }
            for (int i = 0; i < methodParams.length; i++) {
                stringBuffer.append(methodParams[i].getName()).append(" = ");
                stringBuffer.append(param.args[i]);
                stringBuffer.append("\n");
            }
            stringBuffer.append("\n");
        }
        if (!(m2 instanceof Method)) {
            return;
        }
        Class<?>[] methodParams = ((Method) m2).getParameterTypes();
        if (methodParams.length != 0) {
            stringBuffer.append("方法运行时参数 : \n");
        }
        for (int i = 0; i < methodParams.length; i++) {
            stringBuffer.append(methodParams[i].getName()).append(" = ");
            stringBuffer.append(param.args[i]);
            stringBuffer.append("\n");
        }
        stringBuffer.append("\n");
    }

    private static void getMethodStack(XC_MethodHook.MethodHookParam param, StackTraceElement[] stackElements, StringBuilder stringBuffer, Class<?> ThisClass) {
        boolean isStart = false;
        for (StackTraceElement stackTraceElement : stackElements) {
            //防止记录被注册到Native层的钩子方法
            if (stackTraceElement.getMethodName().equals(param.method.getName())) isStart = true;
            if (!isStart) continue;
            stringBuffer.append("\n");
            stringBuffer.append("   ");
            if (stackTraceElement.getMethodName().equals(param.method.getName())) {
                //框架输出当前方法的栈文本可能会是LSPHooker_.g0(Unknown Source:15)
                stringBuffer.append(ThisClass.getName()).append(".").append(param.method.getName()).append("(").append(stackTraceElement.getLineNumber()).append(")");
                stringBuffer.append(" <---当前方法");
            } else if (stackTraceElement.getClassName().equals(ThisClass.getName())) {
                stringBuffer.append(stackTraceElement);
                stringBuffer.append(" <---同一类下");
            } else {
                stringBuffer.append(stackTraceElement);
            }
        }
    }

    /**
     * 向文件写入文本
     *
     * @param path    路径
     * @param content 内容
     */
    private static void writeFileText(String path, String content) {
        File file = new File(path);
        BufferedWriter writer = null;
        try {
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true), StandardCharsets.UTF_8));
            writer.write(content);
        } catch (IOException ignored) {

        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取普通的异常调用栈
     *
     * @param exception new Throwable
     * @return 调用栈文本
     */
    public static String getStackTrace(Throwable exception) {
        StringBuilder sb = new StringBuilder();
        sb.append(exception).append("\n");
        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            sb.append(stackTraceElement).append("\n");
        }
        return sb.toString();
    }

    private static String getTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df1 = new SimpleDateFormat("yyyy年MM月dd日"), df2 = new SimpleDateFormat("E", Locale.CHINA), df3 = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String TimeMsg1 = df1.format(calendar.getTime()), TimeMsg2 = df2.format(calendar.getTime()), TimeMsg3 = df3.format(calendar.getTime());
        if (TimeMsg1.contains("年0"))//去掉多余的 0
            TimeMsg1 = TimeMsg1.replace("年0", "年");
        if (TimeMsg1.contains("月0")) TimeMsg1 = TimeMsg1.replace("月0", "月");
        if (TimeMsg2.contains("周")) TimeMsg2 = TimeMsg2.replace("周", "星期");//转换为星期
        return TimeMsg1 + TimeMsg2 + TimeMsg3;
    }

    /**
     * 使用演示
     */
    public void demo() {
        //要记录的方法
        Method method = (Method) new Object();
        XposedBridge.hookMethod(method, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                //普通非静态方法调用
                OutputMethodStack(param);
                //如果是静态方法调用
//                OutputStaticMethodStack(param);
                //如果需要获取方法参数里的对象
//                OutputObjectField(param.args[0]);
            }
        });
    }
}
