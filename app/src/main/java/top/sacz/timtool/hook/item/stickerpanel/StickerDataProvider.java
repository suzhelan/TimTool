package top.sacz.timtool.hook.item.stickerpanel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.sacz.timtool.hook.util.PathTool;
import top.sacz.timtool.hook.util.ToastTool;
import top.sacz.xphelper.util.KvHelper;


/**
 * 表情面板数据提供者
 */
public class StickerDataProvider {

    private final static KvHelper kvHelper = new KvHelper("表情面板");

    public static String getStickerStorageDirectory() {
        return PathTool.getModuleDataPath() + "/表情";
    }

    public static String getCurrentSelectionDir() {
        return kvHelper.getString("currentSelection", "");
    }

    public static void setCurrentSelectionDir(String currentSelection) {
        kvHelper.put("currentSelection", currentSelection);
    }

    public static String getCacheDir() {
        return PathTool.getModuleCachePath("img");
    }

    public static List<String> searchStickerDirectory() {
        File dirFile = new File(getStickerStorageDirectory());
        if (!dirFile.exists() && dirFile.mkdirs()) {
            ToastTool.show("创建表情文件夹成功");
        }
        String[] names = dirFile.list((dir, name) -> dir.isDirectory());
        if (names != null) {
            return List.of(names);
        }
        return List.of();
    }

    public static List<StickerInfo> searchStickerFile(String stickerDirName) {
        List<StickerInfo> stickerInfoList = new ArrayList<>();
        File dirFile = new File(getStickerStorageDirectory(), stickerDirName);
        File[] files = dirFile.listFiles();
        if (files == null) {
            return stickerInfoList;
        }
        for (File file : files) {
            StickerInfo stickerInfo = new StickerInfo();
            stickerInfo.setPath(file.getAbsolutePath());
            stickerInfo.setName(file.getName());
            stickerInfo.setType(1);
            stickerInfo.setTime(file.lastModified());
            stickerInfoList.add(stickerInfo);
        }
        stickerInfoList.sort((o1, o2) -> {
            long result = o2.getTime() - o1.getTime();
            if (result == 0) return 0;
            return (result < 0) ? -1 : 1;
        });
        return stickerInfoList;
    }
}
