package top.sacz.timtool.hook.item.chat.stickerpanel;

import android.app.Activity;
import android.content.Context;

import com.kongzue.dialogx.util.FixContextUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import top.sacz.xphelper.XpHelper;

public class BottomStickerPanelDialog {

    private StickerPanelView panelView;
    private BasePopupView dialog;

    /**
     * 关闭弹窗并删除引用以释放内存
     */
    public void dismiss() {
        if (panelView != null) {
            panelView.dismiss();
            panelView = null;
        }
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public void show() {
        Activity activity = XpHelper.getTopActivity();
        XpHelper.injectResourcesToContext(activity);
        Context fixContext = FixContextUtil.getFixContext(activity);
        StickerPanelView panelView = new StickerPanelView(fixContext, this);
        BasePopupView dialog = new XPopup.Builder(fixContext)
                .isDestroyOnDismiss(true)
                .asCustom(panelView)
                .show();

        this.panelView = panelView;
        this.dialog = dialog;
    }

}
