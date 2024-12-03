package top.sacz.timtool.hook.item.stickerpanel.dialog;

import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.dialogs.MessageMenu;
import com.kongzue.dialogx.dialogs.PopTip;

import java.io.File;
import java.util.List;

import top.sacz.timtool.hook.item.stickerpanel.StickerDataProvider;

public class CreateStickerDirDialog {
    public void show(MessageMenu dialog) {
        new InputDialog("创建新文件夹", "输入文件夹名", "创建", "取消")
                .setOkButton((baseDialog, v, inputStr) -> {
                    String newDirFile = StickerDataProvider.getStickerStorageDirectory() + "/" + inputStr;
                    File file = new File(newDirFile);
                    if (file.exists()) {
                        PopTip.show("文件夹已存在").iconError();
                        return true;
                    }
                    if (file.mkdirs()) {
                        List<String> stickerDirectory = StickerDataProvider.searchStickerDirectory();
                        dialog.setMenuList(stickerDirectory.toArray(new String[0]));
                        PopTip.show("创建成功 " + inputStr).iconSuccess();
                    }
                    return false;
                })
                .show();
    }
}
