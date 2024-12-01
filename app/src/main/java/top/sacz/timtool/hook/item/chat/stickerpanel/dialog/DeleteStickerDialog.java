package top.sacz.timtool.hook.item.chat.stickerpanel.dialog;

import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.util.TextInfo;

import java.io.File;

import top.sacz.timtool.R;
import top.sacz.timtool.hook.HookEnv;
import top.sacz.timtool.hook.item.chat.stickerpanel.StickerInfo;
import top.sacz.timtool.hook.util.ToastTool;

public class DeleteStickerDialog {
    /**
     * 创建删除表情dialog
     *
     * @param stickerInfo 表情信息
     * @param callback    删除成功回调
     */
    public void show(StickerInfo stickerInfo, Runnable callback) {
        int warningColor = HookEnv.getHostAppContext().getColor(R.color.warning);
        MessageDialog.show("删除贴纸", "确定要删除该贴纸吗?")
                .setOkButton("删除", (dialog, v) -> {
                    File file = new File(stickerInfo.getPath());
                    if (file.exists() && file.delete()) {
                        ToastTool.show("删除成功");
                        callback.run();
                    }
                    return false;
                })
                .setOkTextInfo(new TextInfo().setFontColor(warningColor).setBold(true))
                .setCancelButton("取消", (dialog, v) -> false);

    }
}
