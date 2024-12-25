package top.sacz.timtool.hook.item.experiment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.system.Os;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

import kotlin.collections.SetsKt;
import top.sacz.timtool.hook.HookEnv;
import top.sacz.timtool.hook.base.ApiHookItem;
import top.sacz.timtool.hook.core.annotation.HookItem;

@HookItem("保护模块数据目录")
public class ProtectModuleDataDirectory extends ApiHookItem {

    private static final HashSet<String> FILES_TO_HIDE = new HashSet<>();

    static {
        FILES_TO_HIDE.add("Tim小助手");
    }

    private HashSet<String> mPathPrefixList;

    @Nullable
    private static String[] filterList(@Nullable String[] files) {
        if (files == null || files.length == 0) {
            return files;
        }
        ArrayList<String> names = new ArrayList<>(files.length);
        for (String fileName : files) {
            if (fileName == null) {
                continue;
            }
            boolean hide = FILES_TO_HIDE.contains(fileName);
            if (!hide) {
                names.add(fileName);
            }
        }
        return names.toArray(new String[0]);
    }


    @SuppressLint("SdCardPath")
    @Override
    public void loadHook(@NonNull ClassLoader classLoader) {
        Context ctx = HookEnv.getHostAppContext();
        String packageName = ctx.getPackageName();
        // UserHandle.PER_USER_RANGE
        int userHandleIndex = Os.geteuid() / 100000;
        // /data/user/{user:d}/{pkg:s}/files
        // /data/data/{pkg:s}/files
        // /sdcard/Android/data/{pkg:s}/files
        // /storage/emulated/{user:d}/Android/data/{pkg:s}/files
        // /storage/self/primary/Android/data/{pkg:s}/files
        mPathPrefixList = SetsKt.hashSetOf("/Android/data/" + packageName);//当file对象包含该路径时则命中
        // hook File.{list,listFiles} to hide files
        Method listMethod;
        try {
            listMethod = File.class.getDeclaredMethod("list");
        } catch (NoSuchMethodException e) {
            return;
        }
        hookAfter(listMethod, p -> {
            File thiz = (File) p.thisObject;
            String path = thiz.getAbsolutePath();
            if (p.hasThrowable()) {
                return;
            }
            String[] names = (String[]) p.getResult();
            for (String prefixPath : mPathPrefixList) {
                if (path.contains(prefixPath)) {
                    String[] newResult = filterList(names);
                    p.setResult(newResult);
                }
            }
        });
    }
}
