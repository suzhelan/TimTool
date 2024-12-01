package top.sacz.timtool.hook.item.chat.stickerpanel.dialog;


import android.graphics.Color;

import com.kongzue.dialogx.dialogs.MessageMenu;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.interfaces.OnMenuButtonClickListener;
import com.kongzue.dialogx.interfaces.OnMenuItemSelectListener;
import com.kongzue.dialogx.util.TextInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import top.sacz.timtool.hook.item.chat.stickerpanel.StickerDataProvider;
import top.sacz.timtool.hook.qqapi.QQImageMsgUtils;
import top.sacz.timtool.net.DownloadManager;
import top.sacz.timtool.util.FileUtils;

public class SaveStickerDialog {

    public void show(Object msgRecord) {
        String picUrl = QQImageMsgUtils.getMsgRecordPicUrl(msgRecord);
        String picMD5 = QQImageMsgUtils.getMsgRecordPicMd5(msgRecord).toUpperCase();
        List<String> dirs = StickerDataProvider.searchStickerDirectory();
        List<CharSequence> savedDirs = new ArrayList<>();
        MessageMenu.showStringList(dirs)
                .setMessage("保存到")
                .setTitle("保存表情到")
                .setOnMenuItemClickListener(new OnMenuItemSelectListener<>() {
                    public void onMultiItemSelect(MessageMenu dialog, CharSequence[] text, int[] indexArray) {
                        savedDirs.clear();
                        savedDirs.addAll(Arrays.asList(text));
                    }
                })
                .setOtherButton("新建一个文件夹", (OnMenuButtonClickListener<MessageMenu>) (dialog, v) -> {
                    new CreateStickerDirDialog().show(dialog);
                    return true;
                })
                .setOtherTextInfo(new TextInfo().setFontColor(Color.parseColor("#FF6699")).setBold(true))
                .setOkButton("确定",
                        (OnMenuButtonClickListener<MessageMenu>) (dialog, v) -> {
                            saveStickerToDir(picMD5, picUrl, savedDirs);
                            return false;
                        })
                .setMultiSelection();
    }

    private void saveStickerToDir(String md5, String picUrl, List<CharSequence> dirNameList) {
        //下载到缓存目录先
        String cachePath = StickerDataProvider.getCacheDir() + "/" + md5;
        DownloadManager.downloadAsync(picUrl, cachePath, () -> {
            //下载好了 一个一个复制到其他目录
            for (CharSequence dirName : dirNameList) {
                try {
                    String targetPath = StickerDataProvider.getStickerStorageDirectory() + "/" + dirName + "/" + md5;
                    FileUtils.copyFile(cachePath, targetPath);
                    PopTip.show("成功保存到:" + dirName).iconSuccess();
                } catch (IOException e) {
                    PopTip.show("保存失败" + e).iconError();
                }
            }
        });
    }

}
