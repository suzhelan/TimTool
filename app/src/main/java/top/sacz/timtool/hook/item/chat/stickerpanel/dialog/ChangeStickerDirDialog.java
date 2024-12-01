package top.sacz.timtool.hook.item.chat.stickerpanel.dialog;

import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.util.TextInfo;

import java.io.File;

import top.sacz.timtool.R;
import top.sacz.timtool.hook.HookEnv;
import top.sacz.timtool.hook.item.chat.stickerpanel.StickerDataProvider;
import top.sacz.timtool.util.FileUtils;

/**
 * 比如修改文件夹名称什么的
 */
public class ChangeStickerDirDialog {

    public void show(String dirName, Runnable callback) {
        int warningColor = HookEnv.getHostAppContext().getColor(R.color.warning);
        InputDialog.show("修改文件夹名称", "输入文件夹名", "修改文件夹名称", null, "删除", dirName)
                .setOkButton((dialog, v, inputStr) -> {
                    if (inputStr.isEmpty()) {
                        PopTip.show("文件夹名不能为空").iconError();
                        return true;
                    }
                    if (inputStr.equals(dirName)) {
                        PopTip.show("文件夹名未改变").iconError();
                        return true;
                    }
                    if (StickerDataProvider.searchStickerDirectory().contains(inputStr)) {
                        PopTip.show(inputStr + "文件夹已存在").iconError();
                        return true;
                    }
                    File dir = new File(StickerDataProvider.getStickerStorageDirectory(), dirName);
                    if (!dir.exists()) {
                        PopTip.show("文件夹不存在").iconError();
                        return true;
                    }
                    if (dir.renameTo(new File(StickerDataProvider.getStickerStorageDirectory(), inputStr))) {
                        PopTip.show("修改成功 " + inputStr).iconSuccess();
                        callback.run();
                    }
                    return false;
                }).setOtherButton((dialog, v) -> {
                    showDeleteDirDialog(dirName, dialog, callback);
                    return true;
                })
                .setOtherTextInfo(new TextInfo().setFontColor(warningColor).setBold(true));
    }

    public void showDeleteDirDialog(String dirName, MessageDialog changeDirDialog, Runnable callback) {
        int warningColor = HookEnv.getHostAppContext().getColor(R.color.warning);
        MessageDialog.show("删除文件夹", "确定要删除该文件夹吗?这将无法还原")
                .setOkButton("删除", (dialog, v) -> {
                    //删除文件夹
                    File dir = new File(StickerDataProvider.getStickerStorageDirectory(), dirName);
                    FileUtils.deleteFile(dir);
                    changeDirDialog.dismiss();
                    PopTip.show("删除成功").iconSuccess();
                    callback.run();
                    return false;
                })
                .setOkTextInfo(new TextInfo().setFontColor(warningColor).setBold(true))
                .setCancelButton("取消", (dialog, v) -> false);
    }
}
