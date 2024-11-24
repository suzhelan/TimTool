package top.sacz.timtool.hook.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import top.sacz.timtool.hook.HookEnv;


public class PathTool {


    public static String getDataSavePath(Context context, String dirName) {
        //getExternalFilesDir()：SDCard/Android/data/你的应用的包名/files/dirName
        return context.getExternalFilesDir(dirName).getAbsolutePath();
    }

    public static String getStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getModuleDataPath() {
//        String directory = getStorageDirectory() + "/Download/QStory";//只有创建该文件的进程才能访问文件 不适用
//        String directory = getStorageDirectory() + "/Android/media/" + HookEnv.getCurrentHostAppPackageName() + "/QStory";//LSPatch在某些机型上无法使用media文件夹
        String directory = getStorageDirectory() + "/Android/data/" + HookEnv.getInstance().getCurrentHostAppPackageName() + "/Tim小助手";
        File file = new File(directory);
        if (!file.exists()) {
            file.mkdirs();
        }
        return directory;
    }

    public static String getModuleCachePath(String dirName) {
        File cache = new File(getModuleDataPath() + "/cache/" + dirName);
        if (!cache.exists()) cache.mkdirs();
        return cache.getAbsolutePath();
    }


}
