package top.sacz.timtool.hook.item.chat.stickerpanel.dialog;

import android.graphics.Color;

import com.kongzue.dialogx.dialogs.MessageMenu;
import com.kongzue.dialogx.interfaces.OnMenuButtonClickListener;
import com.kongzue.dialogx.interfaces.OnMenuItemSelectListener;
import com.kongzue.dialogx.util.TextInfo;

import java.util.List;

import top.sacz.timtool.hook.item.chat.stickerpanel.StickerDataProvider;
import top.sacz.timtool.hook.qqapi.QQImageMsgUtils;

public class SaveStickerDialog {
    public static void show(Object msgRecord) {
        int selectMenuIndex = 0;
        String picUrl = QQImageMsgUtils.getMsgRecordPicUrl(msgRecord);
        List<String> dirs = StickerDataProvider.searchStickerDirectory();
        MessageMenu.showStringList(dirs)
                .setMessage("保存到")
                .setTitle("保存表情到")
                .setOnMenuItemClickListener(new OnMenuItemSelectListener<>() {
                    public void onMultiItemSelect(MessageMenu dialog, CharSequence[] text, int[] indexArray) {

                    }
                })
                .setOtherButton("新建一个文件夹", (OnMenuButtonClickListener<MessageMenu>) (dialog, v) -> false)
                .setOtherTextInfo(new TextInfo().setFontColor(Color.parseColor("#EB5545")).setBold(true))
                .setOkButton(
                        "确定",
                        (OnMenuButtonClickListener<MessageMenu>) (dialog, v) -> false)
                .setSelection(selectMenuIndex);
        //TODO 添加新建文件夹功能和保存表情到文件夹组
    }
}
