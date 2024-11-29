package top.sacz.timtool.hook.item.chat.stickerpanel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.sacz.timtool.hook.util.PathTool;
import top.sacz.timtool.hook.util.ToastTool;
import top.sacz.timtool.util.KvHelper;

/**
 * 表情面板数据提供者
 */
public class StickerDataProvider {

    private final static KvHelper kvHelper = new KvHelper("表情面板");

    private static final String stickerDirectory = PathTool.getModuleDataPath() + "/表情";

    public static String getCurrentSelectionDir() {
        return kvHelper.getString("currentSelection", "");
    }

    public static void setCurrentSelectionDir(String currentSelection) {
        kvHelper.put("currentSelection", currentSelection);
    }

    public static List<String> searchStickerDirectory() {
        File dirFile = new File(stickerDirectory);
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
        File dirFile = new File(stickerDirectory, stickerDirName);
        File[] files = dirFile.listFiles();
        for (File file : files) {
            StickerInfo stickerInfo = new StickerInfo();
            stickerInfo.setPath(file.getAbsolutePath());
            stickerInfo.setName(file.getName());
            stickerInfo.setType(1);
            stickerInfoList.add(stickerInfo);
        }
        return stickerInfoList;
    }
}
