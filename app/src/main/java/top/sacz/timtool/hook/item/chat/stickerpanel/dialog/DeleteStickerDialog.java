package top.sacz.timtool.hook.item.chat.stickerpanel.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.util.TextInfo;

import java.io.File;

import top.sacz.timtool.R;
import top.sacz.timtool.hook.HookEnv;
import top.sacz.timtool.hook.item.chat.stickerpanel.StickerInfo;
import top.sacz.timtool.hook.util.ToastTool;
import top.sacz.timtool.util.ScreenParamUtils;
import top.sacz.xphelper.util.ActivityTools;

public class DeleteStickerDialog {
    /**
     * 创建删除表情dialog
     *
     * @param stickerInfo 表情信息
     * @param callback    删除成功回调
     */
    public void show(StickerInfo stickerInfo, Runnable callback) {
        Activity activity = ActivityTools.getTopActivity();
        int padding = ScreenParamUtils.dpToPx(activity, 40);
        ImageView stickerImageView = new ImageView(activity);
        stickerImageView.setPadding(padding, padding, padding, 0);
        Glide.with(HookEnv.getHostAppContext())
                .load(new File(stickerInfo.getPath()))
                .fitCenter()
                .into(stickerImageView);
        int warningColor = HookEnv.getHostAppContext().getColor(R.color.warning);
        MessageDialog.show("删除贴纸", "确定要删除该贴纸吗?这将无法还原")
                .setCustomView(new OnBindView<>(stickerImageView) {
                    @Override
                    public void onBind(MessageDialog dialog, View v) {

                    }
                })
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
