package top.sacz.timtool.hook.item.chat.emojipanel;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kongzue.dialogx.dialogs.BottomDialog;
import com.kongzue.dialogx.util.FixContextUtil;

import java.util.ArrayList;
import java.util.List;

import top.sacz.timtool.R;
import top.sacz.timtool.hook.HookEnv;
import top.sacz.timtool.hook.item.chat.emojipanel.adapter.EmojiDirAdapter;
import top.sacz.timtool.hook.item.chat.emojipanel.adapter.EmojiPanelAdapter;
import top.sacz.timtool.ui.view.CustomRecycleView;
import top.sacz.timtool.util.ScreenParamUtils;

public class BottomEmojiPanelDialog {

    private static int currentPosition = 0;

    public static void show() {
        Context context = HookEnv.getHostAppContext();
        int height = (int) (ScreenParamUtils.getScreenHeight(context) * 0.8);
        BottomDialog dialog = BottomDialog.build()
                .setMaxHeight(height)
                .setMinHeight(height)
                .setScrollableWhenContentLargeThanVisibleRange(false)
                /*.setCustomView(new OnBindView<>(R.layout.layout_emoji_panel_dialog) {
                    @Override
                    public void onBind(BottomDialog dialog, View v) {
                        onBindView(dialog, (ViewGroup) v);
                    }
                })*/
                .show();
        ViewGroup root = (ViewGroup) FixContextUtil.getFixLayoutInflater(HookEnv.getHostAppContext()).inflate(R.layout.layout_emoji_panel_dialog, null, false);
        onBindView(dialog, root);
        dialog.getDialogImpl().boxBody.addView(root);
    }

    private static void onBindView(BottomDialog dialog, ViewGroup root) {
        //表情文件列表
        CustomRecycleView rvEmoji = root.findViewById(R.id.rv_emoji_image);
        rvEmoji.setLayoutManager(new GridLayoutManager(root.getContext(), 4));
        EmojiPanelAdapter emojiPanelAdapter = new EmojiPanelAdapter();
        rvEmoji.setAdapter(emojiPanelAdapter);
//        rvEmoji.lockScroll(true);//设置此属性 不然滚动灵敏度可能会非常低 体验非常差

        //表情目录列表
        CustomRecycleView rvEmojiDir = root.findViewById(R.id.rv_emoji_dir);
        rvEmojiDir.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.HORIZONTAL, false));
        //文件夹的adapter
        EmojiDirAdapter emojiDirAdapter = new EmojiDirAdapter();
        emojiDirAdapter.submitList(EmojiPanelDataProvider.searchEmojiDirectory());
        emojiDirAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (position == currentPosition) {
                return;
            }

            currentPosition = position;
            String dirName = emojiDirAdapter.getItem(position);

            emojiPanelAdapter.submitList(new ArrayList<>());
            List<EmojiInfo> emojiInfoList = EmojiPanelDataProvider.searchEmojiFile(dirName);
            for (EmojiInfo emojiInfo : emojiInfoList) {
                emojiPanelAdapter.add(emojiInfo);
            }
        });
        rvEmojiDir.setAdapter(emojiDirAdapter);
    }

    public static void updateImageByDirName() {

    }

}
