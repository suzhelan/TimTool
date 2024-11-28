package top.sacz.timtool.hook.item.chat.emojipanel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.sacz.timtool.hook.util.PathTool;
import top.sacz.timtool.hook.util.ToastTool;

/**
 * 表情面板数据提供者
 */
public class EmojiPanelDataProvider {

    private static final String emojiDirectory = PathTool.getModuleDataPath() + "/表情";

    public static List<String> searchEmojiDirectory() {
        File dirFile = new File(emojiDirectory);
        if (!dirFile.exists() && dirFile.mkdirs()) {
            ToastTool.show("创建表情文件夹成功");
        }
        String[] names = dirFile.list((dir, name) -> dir.isDirectory());
        if (names != null) {
            return List.of(names);
        }
        return List.of();
    }

    public static List<EmojiInfo> searchEmojiFile(String emojiDirName) {
        List<EmojiInfo> emojiInfoList = new ArrayList<>();
        File dirFile = new File(emojiDirectory, emojiDirName);
        File[] files = dirFile.listFiles();
        for (File file : files) {
            EmojiInfo emojiInfo = new EmojiInfo();
            emojiInfo.setPath(file.getAbsolutePath());
            emojiInfo.setName(file.getName());
            emojiInfo.setType(1);
            emojiInfoList.add(emojiInfo);
        }
        return emojiInfoList;
    }
}
